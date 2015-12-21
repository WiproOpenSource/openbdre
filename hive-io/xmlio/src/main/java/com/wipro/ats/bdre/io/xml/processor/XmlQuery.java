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
 * Defines a named XML path query
 */
public class XmlQuery {

    private String query = null;
    private String name = null;

    /**
     * Creates a query
     * 
     * @param query
     */
    public XmlQuery(String query, String name) {
        this.query = query;
        this.name = name;
    }

    /**
     * Returns the query string
     * 
     * @return
     */
    public String getQuery() {
        return this.query;
    }

    /**
     * Returns the query name
     * 
     * @return
     */
    public String getName() {
        return this.name;
    }
}
