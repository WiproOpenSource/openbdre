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
 * Transforms the XML node into a result tree
 */
public class XmlTransformer {

    /**
     * Transforms the XML node into the string
     * 
     * @param node
     *            the node to transform
     * @param builder
     *            the string builder
     */
    public void transform(XmlNode node, StringBuilder builder) {
        switch (node.getType()) {
            case XmlNode.ELEMENT_NODE: {
                builder.append("<");
                builder.append(node.getName());
                for (XmlNode attribute : node.getAttributes().values()) {
                    transform(attribute, builder);
                }
                builder.append(">");
                for (XmlNode child : node.getChildren()) {
                    transform(child, builder);
                }
                builder.append("</");
                builder.append(node.getName());
                builder.append(">");
            }
                break;
            case XmlNode.ATTRIBUTE_NODE: {
                builder.append(" ");
                builder.append(node.getName());
                builder.append("=\"");
                builder.append(node.getValue());
                builder.append("\"");
            }
                break;
            case XmlNode.TEXT_NODE: {
                builder.append(node.getValue());
            }
        }
    }
}
