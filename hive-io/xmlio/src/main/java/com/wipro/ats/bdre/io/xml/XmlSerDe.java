/**
 * (c) Copyright IBM Corp. 2013. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.io.xml;

import com.wipro.ats.bdre.io.xml.objectinspector.XmlObjectInspectorFactory;
import com.wipro.ats.bdre.io.xml.processor.*;
import com.wipro.ats.bdre.io.xml.processor.java.JavaXmlProcessor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.SerDe;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * The XML serializer/deserializer for Apache Hive
 */
public class XmlSerDe implements SerDe {

    private static final Logger LOGGER = Logger.getLogger(XmlSerDe.class);
    private static final String XML_PROCESSOR_CLASS = "xml.processor.class";
    private static final String MAP_SPECIFICATION_PREFIX = "xml.map.specification.";
    private static final String COLUMN_XPATH_PREFIX = "column.xpath.";

    private ObjectInspector objectInspector = null;
    private XmlProcessor xmlProcessor = null;

    private static final String LIST_COLUMNS = "columns";
    private static final String LIST_COLUMN_TYPES = "columns.types";

    /**
     * @see org.apache.hadoop.hive.serde2.Deserializer#initialize(org.apache.hadoop.conf.Configuration, java.util.Properties)
     */
    @Override
    public void initialize(Configuration configuration, final Properties properties) throws SerDeException {
        // (1) workaround for the Hive issue with propagating the table properties to the InputFormat
        initialize(configuration, properties, XmlInputFormat.START_TAG_KEY, XmlInputFormat.END_TAG_KEY);
        // (2) create XML processor
        String processorClass = properties.getProperty(XML_PROCESSOR_CLASS);
        if (processorClass != null) {
            try {
                this.xmlProcessor = (XmlProcessor) Class.forName(processorClass,
                    true,
                    Thread.currentThread().getContextClassLoader() == null ? getClass().getClassLoader() : Thread.currentThread()
                        .getContextClassLoader()).newInstance();
            } catch (Throwable t) {
                t.printStackTrace();
                LOGGER.error("Cannot instantiate XPath processor " + processorClass);
                LOGGER.error("Instantiating " + JavaXmlProcessor.class.getName());
            }
        }
        if (this.xmlProcessor == null) {
            this.xmlProcessor = new JavaXmlProcessor();
        }
        // (3) create XML processor context
        List<String> columnNames = Arrays.asList(properties.getProperty(LIST_COLUMNS).split("[,:;]"));
        final List<XmlQuery> queries = new ArrayList<XmlQuery>();
        final Map<String, XmlMapEntry> mapSpecification = new HashMap<String, XmlMapEntry>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(COLUMN_XPATH_PREFIX)) {
                // create column XPath query
                // "column.xpath.result"="//result/text()"
                String columnName = key.substring(COLUMN_XPATH_PREFIX.length()).toLowerCase();
                String query = properties.getProperty(key);
                if (query != null) {
                    queries.add(new XmlQuery(query, columnName));
                }
            } else if (key.startsWith(MAP_SPECIFICATION_PREFIX)) {
                // create map specification
                // "xml.map.specification.result"="@name->#content"
                String element = key.substring(MAP_SPECIFICATION_PREFIX.length());
                String specification = properties.getProperty(key);
                String[] split = specification.split("->");
                // @attribute->#content
                // element->#content
                // @attribute1->@attribute2
                XmlMapFacet.Type keyFacetType = XmlMapFacet.Type.ELEMENT;
                String keyName = split[0];
                if (split[0].startsWith("@")) {
                    keyFacetType = XmlMapFacet.Type.ATTRIBUTE;
                    keyName = split[0].substring(1);
                } else if (split[0].startsWith("#")) {
                    keyFacetType = XmlMapFacet.Type.CONTENT;
                }
                XmlMapFacet.Type valueFacetType = XmlMapFacet.Type.ELEMENT;
                String valueName = split[1];
                if (split[1].startsWith("@")) {
                    valueFacetType = XmlMapFacet.Type.ATTRIBUTE;
                    valueName = split[1].substring(1);
                } else if (split[1].startsWith("#")) {
                    valueFacetType = XmlMapFacet.Type.CONTENT;
                }
                XmlMapFacet keyFacet = new XmlMapFacet(keyName, keyFacetType);
                XmlMapFacet valueFacet = new XmlMapFacet(valueName, valueFacetType);
                XmlMapEntry mapEntry = new XmlMapEntry(keyFacet, valueFacet);
                mapSpecification.put(element, mapEntry);
            }
        }
        if (queries.size() < columnNames.size()) {
            throw new RuntimeException("The number of XPath expressions does not much the number of columns");
        }
        // (4) initialize the XML processor
        this.xmlProcessor.initialize(new XmlProcessorContext() {

            @Override
            public List<XmlQuery> getXmlQueries() {
                return queries;
            }

            @Override
            public Map<String, XmlMapEntry> getXmlMapSpecification() {
                return mapSpecification;
            }

            @Override
            public Properties getProperties() {
                return properties;
            }
        });
        // (5) create the object inspector and associate it with the XML processor
        List<TypeInfo> typeInfos = TypeInfoUtils.getTypeInfosFromTypeString(properties.getProperty(LIST_COLUMN_TYPES));
        List<ObjectInspector> inspectors = new ArrayList<ObjectInspector>(columnNames.size());
        for (TypeInfo typeInfo : typeInfos) {
            inspectors.add(XmlObjectInspectorFactory.getStandardJavaObjectInspectorFromTypeInfo(typeInfo, this.xmlProcessor));
        }
        this.objectInspector = XmlObjectInspectorFactory.getStandardStructObjectInspector(columnNames, inspectors, this.xmlProcessor);
    }

    private static void initialize(Configuration configuration, final Properties properties, String... keys) {
        for (String key : keys) {
            String configurationValue = configuration.get(key);
            String propertyValue = properties.getProperty(key);
            if (configurationValue == null) {
                if (propertyValue != null) {
                    configuration.set(key, propertyValue);
                }
            } else {
                if (propertyValue != null && !propertyValue.equals(configurationValue)) {
                    configuration.set(key, propertyValue);
                }
            }
        }
    }

    /**
     * @see org.apache.hadoop.hive.serde2.Deserializer#deserialize(org.apache.hadoop.io.Writable)
     */
    @Override
    public Object deserialize(Writable writable) throws SerDeException {
        Text text = (Text) writable;
        if (text == null || text.getLength() == 0) {
            return (Object) null;
        }
        try {
            return this.xmlProcessor.parse(text.toString());
        } catch (Exception e) {
            throw new SerDeException(e);
        }
    }

    /**
     * @see org.apache.hadoop.hive.serde2.Deserializer#getObjectInspector()
     */
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return this.objectInspector;
    }

    /**
     * @see org.apache.hadoop.hive.serde2.Deserializer#getSerDeStats()
     */
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }

    /**
     * @see org.apache.hadoop.hive.serde2.Serializer#getSerializedClass()
     */
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return Text.class;
    }

    /**
     * @see org.apache.hadoop.hive.serde2.Serializer#serialize(java.lang.Object,
     *      org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector)
     */
    @Override
    public Writable serialize(Object object, ObjectInspector objectInspector) throws SerDeException {
        throw new UnsupportedOperationException();
    }
}
