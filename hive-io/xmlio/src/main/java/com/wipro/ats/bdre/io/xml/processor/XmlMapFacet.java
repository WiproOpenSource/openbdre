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
 * The XML map facet
 */
public class XmlMapFacet {

    private String name = null;
    private Type type = null;

    /**
     * Creates XML map facet
     * 
     * @param name
     *            the name
     * @param type
     *            the type
     */
    public XmlMapFacet(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public enum Type {
        ELEMENT,
        CONTENT,
        ATTRIBUTE
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return this.type;
    }
}
