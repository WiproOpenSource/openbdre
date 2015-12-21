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

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.typeinfo.ListTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.MapTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.StructTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;

import com.wipro.ats.bdre.io.xml.processor.XmlProcessor;

/**
 * The XML object inspector factory
 */
public class XmlObjectInspectorFactory {

    /**
     * Private constructor
     */
    private XmlObjectInspectorFactory() {
    }

    /**
     * Returns the standard java object inspector
     * 
     * @param typeInfo
     *            the type info
     * @param xmlProcessor
     *            the XML processor
     * @return the standard java object inspector
     */
    public static ObjectInspector getStandardJavaObjectInspectorFromTypeInfo(TypeInfo typeInfo, XmlProcessor xmlProcessor) {
        switch (typeInfo.getCategory()) {
            case PRIMITIVE: {
                return PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(((PrimitiveTypeInfo) typeInfo).getPrimitiveCategory());
            }
            case LIST: {
                ObjectInspector listElementObjectInspector = getStandardJavaObjectInspectorFromTypeInfo(((ListTypeInfo) typeInfo).getListElementTypeInfo(),
                    xmlProcessor);
                return new XmlListObjectInspector(listElementObjectInspector, xmlProcessor);
            }
            case MAP: {
                MapTypeInfo mapTypeInfo = (MapTypeInfo) typeInfo;
                ObjectInspector mapKeyObjectInspector = getStandardJavaObjectInspectorFromTypeInfo(mapTypeInfo.getMapKeyTypeInfo(),
                    xmlProcessor);
                ObjectInspector mapValueObjectInspector = getStandardJavaObjectInspectorFromTypeInfo(mapTypeInfo.getMapValueTypeInfo(),
                    xmlProcessor);
                return new XmlMapObjectInspector(mapKeyObjectInspector, mapValueObjectInspector, xmlProcessor);
            }
            case STRUCT: {
                StructTypeInfo structTypeInfo = (StructTypeInfo) typeInfo;
                List<String> structFieldNames = structTypeInfo.getAllStructFieldNames();
                List<TypeInfo> fieldTypeInfos = structTypeInfo.getAllStructFieldTypeInfos();
                List<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>(fieldTypeInfos.size());
                for (int fieldIndex = 0; fieldIndex < fieldTypeInfos.size(); ++fieldIndex) {
                    structFieldObjectInspectors.add(getStandardJavaObjectInspectorFromTypeInfo(fieldTypeInfos.get(fieldIndex), xmlProcessor));
                }
                return getStandardStructObjectInspector(structFieldNames, structFieldObjectInspectors, xmlProcessor);
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    /**
     * Returns the struct object inspector
     * 
     * @param structFieldNames
     *            the field names
     * @param structFieldObjectInspectors
     *            the object inspectors
     * @param xmlProcessor
     *            the XML processor
     * @return the struct object inspector
     */
    public static StructObjectInspector getStandardStructObjectInspector(List<String> structFieldNames,
        List<ObjectInspector> structFieldObjectInspectors,
        XmlProcessor xmlProcessor) {
        return new XmlStructObjectInspector(structFieldNames, structFieldObjectInspectors, xmlProcessor);
    }

}
