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

import com.wipro.ats.bdre.io.xml.processor.XmlProcessor;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector;

import java.util.ArrayList;
import java.util.List;

/**
 * The list object inspector
 */
public class XmlListObjectInspector extends StandardListObjectInspector {

    private XmlProcessor xmlProcessor = null;

    /**
     * Creates the list object inspector
     * 
     * @param listElementObjectInspector
     *            the list element object inspector
     * @param xmlProcessor
     *            the XML processor
     */
    XmlListObjectInspector(ObjectInspector listElementObjectInspector, XmlProcessor xmlProcessor) {
        super(listElementObjectInspector);
        this.xmlProcessor = xmlProcessor;
    }

    /**
     * @see org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector#getList(java.lang.Object)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List<?> getList(Object data) {
        List list = this.xmlProcessor.getList(data);
        ObjectInspector listElementObjectInspector = getListElementObjectInspector();
        Category category = listElementObjectInspector.getCategory();
        if (category == Category.PRIMITIVE) {
            PrimitiveObjectInspector primitiveObjectInspector = (PrimitiveObjectInspector) listElementObjectInspector;
            PrimitiveCategory primitiveCategory = primitiveObjectInspector.getPrimitiveCategory();
            if (list != null) {
                List result = new ArrayList();
                for (Object primitive : list) {
                    result.add(this.xmlProcessor.getPrimitiveObjectValue(primitive, primitiveCategory));
                }
                return result;
            }
        }
        return list;
    }

    /**
     * @see org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector#getListElement(java.lang.Object, int)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Object getListElement(Object data, int index) {
        List list = getList(data);
        return list == null ? null : list.get(index);
    }

    /**
     * @see org.apache.hadoop.hive.serde2.objectinspector.StandardListObjectInspector#getListLength(java.lang.Object)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public int getListLength(Object data) {
        List list = getList(data);
        return list == null ? -1 : list.size();
    }
}