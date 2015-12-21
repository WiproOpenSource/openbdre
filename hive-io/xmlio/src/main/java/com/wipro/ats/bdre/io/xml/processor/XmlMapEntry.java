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

/**
 * Defines a specification for the custom XML-to-Map transformation
 */
public class XmlMapEntry {

    private XmlMapFacet key = null;
    private XmlMapFacet value = null;

    /**
     * Creates an XML map entry specification for the custom XML to Map transformation
     * 
     * @param fieldName
     *            the field name
     * @param key
     *            the key specification
     * @param value
     *            the value specification
     */
    public XmlMapEntry(XmlMapFacet key, XmlMapFacet value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns the key
     * 
     * @return the key
     */
    public XmlMapFacet getKey() {
        return this.key;
    }

    /**
     * Returns the value
     * 
     * @return the value
     */
    public XmlMapFacet getValue() {
        return this.value;
    }
}
