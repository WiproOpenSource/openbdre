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

package com.wipro.ats.bdre.io.xml.processor.java;

import com.wipro.ats.bdre.io.xml.processor.XmlQuery;
import org.apache.log4j.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

/**
 * Creates a compiled XML query
 */
class JavaXmlQuery extends XmlQuery {

    private static final Logger LOGGER = Logger.getLogger(JavaXmlQuery.class);

    private XPathExpression expression = null;

    /**
     * Conversion constructor
     * 
     * @param xmlQuery
     */
    JavaXmlQuery(XmlQuery xmlQuery) {
        super(xmlQuery.getQuery(), xmlQuery.getName());
    }

    /**
     * Adds a compiled expression to the query
     * 
     * @param expression
     * @return
     */
    JavaXmlQuery compile(XPath xpath) {
        try {
            this.expression = xpath.compile(getQuery());
        } catch (XPathExpressionException e) {
            LOGGER.error("Cannot compile XPath query: " + getQuery(), e);
        }
        return this;
    }

    /**
     * Returns the compiled expression
     * 
     * @return
     */
    XPathExpression getExpression() {
        return this.expression;
    }
}
