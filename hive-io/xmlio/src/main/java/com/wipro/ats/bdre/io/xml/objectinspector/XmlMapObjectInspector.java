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

import java.util.HashMap;
import java.util.Map;

import com.wipro.ats.bdre.io.xml.processor.XmlProcessor;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.StandardMapObjectInspector;

/**
 * The map object inspector
 */
public class XmlMapObjectInspector extends StandardMapObjectInspector {

    private XmlProcessor xmlProcessor = null;

    /**
     * Creates the map object inspector
     * 
     * @param mapKeyObjectInspector
     *            the map key object inspector
     * @param mapValueObjectInspector
     *            the map value object inspector
     * @param xmlProcessor
     *            the XML processor
     */
    public XmlMapObjectInspector(ObjectInspector mapKeyObjectInspector, ObjectInspector mapValueObjectInspector, XmlProcessor xmlProcessor) {
        super(mapKeyObjectInspector, mapValueObjectInspector);
        this.xmlProcessor = xmlProcessor;
    }

    /**
     * @see org.apache.hadoop.hive.serde2.objectinspector.StandardMapObjectInspector#getMap(java.lang.Object)
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Map<?, ?> getMap(Object data) {
        Map map = this.xmlProcessor.getMap(data);
        ObjectInspector mapValueObjectInspector = getMapValueObjectInspector();
        Category mapValueCategory = mapValueObjectInspector.getCategory();
        ObjectInspector mapKeyObjectInspector = getMapKeyObjectInspector();
        if (mapValueCategory == Category.PRIMITIVE) {
            PrimitiveObjectInspector primitiveMapValueObjectInspector = (PrimitiveObjectInspector) mapValueObjectInspector;
            PrimitiveCategory primitiveMapValueCategory = primitiveMapValueObjectInspector.getPrimitiveCategory();
            PrimitiveObjectInspector primitiveMapKeyObjectInspector = (PrimitiveObjectInspector) mapKeyObjectInspector;
            PrimitiveCategory primitiveMapKeyCategory = primitiveMapKeyObjectInspector.getPrimitiveCategory();
            if (map != null) {
                Map result = new HashMap();
                for (Object object : map.entrySet()) {
                    Map.Entry entry = (Map.Entry) object;
                    Object key = this.xmlProcessor.getPrimitiveObjectValue(entry.getKey(), primitiveMapKeyCategory);
                    if (key != null) {
                        Object value = this.xmlProcessor.getPrimitiveObjectValue(entry.getValue(), primitiveMapValueCategory);
                        result.put(key, value);
                    }
                }
                return result;
            }
        }
        return map;
    }

    /**
     * @see org.apache.hadoop.hive.serde2.objectinspector.StandardMapObjectInspector#getMapValueElement(java.lang.Object, java.lang.Object)
     */
    @Override
    @SuppressWarnings({"rawtypes"})
    public Object getMapValueElement(Object data, Object key) {
        ObjectInspector mapKeyObjectInspector = getMapValueObjectInspector();
        Category mapKeyCategory = mapKeyObjectInspector.getCategory();
        if (mapKeyCategory == Category.PRIMITIVE) {
            PrimitiveObjectInspector primitiveMapKeyObjectInspector = (PrimitiveObjectInspector) mapKeyObjectInspector;
            PrimitiveCategory primitiveMapKeyCategory = primitiveMapKeyObjectInspector.getPrimitiveCategory();
            key = this.xmlProcessor.getPrimitiveObjectValue(key, primitiveMapKeyCategory);
        }
        Map map = getMap(data);
        return map == null ? null : map.get(key);
    }

    /**
     * @see org.apache.hadoop.hive.serde2.objectinspector.StandardMapObjectInspector#getMapSize(java.lang.Object)
     */
    @Override
    @SuppressWarnings({"rawtypes"})
    public int getMapSize(Object data) {
        Map map = getMap(data);
        return map == null ? -1 : map.size();
    }
}