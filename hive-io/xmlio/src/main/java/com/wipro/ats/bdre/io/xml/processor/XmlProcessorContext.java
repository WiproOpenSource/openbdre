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

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Defines the context for the XML processor
 */
public interface XmlProcessorContext {

    /**
     * Returns the XML queries
     * 
     * @return the XML queries
     */
    public List<XmlQuery> getXmlQueries();

    /**
     * Returns the XML map specification
     * 
     * @return the XML map specification
     */
    public Map<String, XmlMapEntry> getXmlMapSpecification();

    /**
     * The properties
     * 
     * @return the properties
     */
    public Properties getProperties();
}
