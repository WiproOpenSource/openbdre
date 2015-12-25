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

package com.wipro.ats.bdre.io.xml.processor;

import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;

import java.util.List;
import java.util.Map;

/**
 * Defines an interface for the XML processors
 */
public interface XmlProcessor {

    /**
     * Initializes the processor
     * 
     * @param xmlProcessorContext
     */
    public void initialize(XmlProcessorContext xmlProcessorContext);

    /**
     * Returns the mapping of the column names to the parsed objects
     * 
     * @param text
     *            the text to parse
     * @return the mapping of the column names to the parsed objects
     */
    public Map<String, ?> parse(String value);

    /**
     * Returns the value for the given object and field name
     * 
     * @param o
     *            the object
     * @param fieldName
     *            the field name
     * @return the value for the given object and field name
     */
    public Object getObjectValue(Object o, String fieldName);

    /**
     * Returns the primitive object value
     * 
     * @param o
     *            the object
     * @param primitiveCategory
     *            the primitive category
     * @return the primitive object value
     */
    public Object getPrimitiveObjectValue(Object o, PrimitiveCategory primitiveCategory);

    /**
     * Converts the given object into a map
     * 
     * @param o
     *            the object
     * @return the map for the given object
     */
    public Map<?, ?> getMap(Object o);

    /**
     * Returns the list representation for the given object
     * 
     * @param o
     *            the object
     * @return the list representation for the given object
     */
    public List<?> getList(Object o);
}
