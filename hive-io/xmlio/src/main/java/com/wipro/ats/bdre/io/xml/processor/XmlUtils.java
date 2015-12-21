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

/**
 * The XML utilities
 */
public class XmlUtils {

    /**
     * Private constructor
     */
    private XmlUtils() {
    }

    /**
     * Converts the string value to the java object for the given primitive category
     * 
     * @param value
     *            the value
     * @param primitiveCategory
     *            the primitive category
     * @return the java object
     */
    public static Object getPrimitiveValue(String value, PrimitiveCategory primitiveCategory) {
        if (value != null) {
            try {
                switch (primitiveCategory) {
                    case BOOLEAN:
                        return Boolean.valueOf(value);
                    case BYTE:
                        return Byte.valueOf(value);
                    case DOUBLE:
                        return Double.valueOf(value);
                    case FLOAT:
                        return Float.valueOf(value);
                    case INT:
                        return Integer.valueOf(value);
                    case LONG:
                        return Long.valueOf(value);
                    case SHORT:
                        return Short.valueOf(value);
                    case STRING:
                        return value;
                    default:
                        throw new IllegalStateException(primitiveCategory.toString());
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

}
