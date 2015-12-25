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

package com.wipro.ats.bdre.io.xml.objectinspector;

import com.wipro.ats.bdre.io.xml.processor.SerDeArray;
import com.wipro.ats.bdre.io.xml.processor.XmlProcessor;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructField;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The struct object inspector
 */
public class XmlStructObjectInspector extends StandardStructObjectInspector {
    private static final Logger LOGGER = Logger.getLogger(XmlStructObjectInspector.class);

    private XmlProcessor xmlProcessor = null;

    /**
     * Creates the struct object inspector
     * 
     * @param structFieldNames
     *            the struct field names
     * @param structFieldObjectInspectors
     *            the struct field object inspectors
     * @param xmlProcessor
     *            the XML processor
     */
    public XmlStructObjectInspector(List<String> structFieldNames,
        List<ObjectInspector> structFieldObjectInspectors,
        XmlProcessor xmlProcessor) {
        super(structFieldNames, structFieldObjectInspectors);
        this.xmlProcessor = xmlProcessor;
    }

    /**
     * @see org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector#getStructFieldData(java.lang.Object,
     *      org.apache.hadoop.hive.serde2.objectinspector.StructField)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object getStructFieldData(Object data, StructField structField) {
        if ((data instanceof List) && !(data instanceof SerDeArray)) {
            MyField f = (MyField) structField;
            int fieldID = f.getFieldID();
            return ((List<Object>) data).get(fieldID);
        } else {
            ObjectInspector fieldObjectInspector = structField.getFieldObjectInspector();
            Category category = fieldObjectInspector.getCategory();
            Object fieldData = this.xmlProcessor.getObjectValue(data, structField.getFieldName());
            switch (category) {
                case PRIMITIVE: {
                    PrimitiveObjectInspector primitiveObjectInspector = (PrimitiveObjectInspector) fieldObjectInspector;
                    PrimitiveCategory primitiveCategory = primitiveObjectInspector.getPrimitiveCategory();
                    return this.xmlProcessor.getPrimitiveObjectValue(fieldData, primitiveCategory);
                }
                default:
                    return fieldData;
            }
        }
    }

    /**
     * @see org.apache.hadoop.hive.serde2.objectinspector.StandardStructObjectInspector#getStructFieldsDataAsList(java.lang.Object)
     */
    @Override
    public List<Object> getStructFieldsDataAsList(Object data) {
        List<Object> values = new ArrayList<Object>();
        for (StructField structField : this.fields) {
            values.add(getStructFieldData(data, structField));
        }
        return values;
    }
}