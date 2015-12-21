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

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the array of the XML nodes
 */
@SuppressWarnings("serial")
public class XmlNodeArray extends ArrayList<XmlNode> implements SerDeArray {

    private String name = null;

    /**
     * Conversion constructor
     * 
     * @param nodes
     */
    public XmlNodeArray(List<XmlNode> nodes) {
        addAll(nodes);
    }

    /**
     * Creates XML node array
     */
    public XmlNodeArray() {
    }

    /**
     * Associates the node array with the given name
     * 
     * @param name
     *            the name
     * @return this instance of the node array
     */
    public XmlNodeArray withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the name of the node array or null
     * 
     * @return the name of the node array or null
     */
    public String getName() {
        return this.name;
    }

}
