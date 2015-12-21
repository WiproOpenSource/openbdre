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

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import com.wipro.ats.bdre.io.xml.processor.XmlMapFacet;
import com.wipro.ats.bdre.io.xml.processor.XmlUtils;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.wipro.ats.bdre.io.xml.processor.XmlMapEntry;
import com.wipro.ats.bdre.io.xml.processor.XmlProcessor;
import com.wipro.ats.bdre.io.xml.processor.XmlProcessorContext;
import com.wipro.ats.bdre.io.xml.processor.XmlQuery;

/**
 * The XML processor implementation based on the javax.xml.xpath.XPath
 */
public class JavaXmlProcessor implements XmlProcessor {

    private static TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    protected static DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = null;
    private DocumentBuilder builder = null;

    private static XPathFactory XPATH_FACTORY = null;

    static {
        DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
        DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
        DOCUMENT_BUILDER_FACTORY.setIgnoringComments(true);
        // Theoretically we could use setIgnoringElementContentWhitespace(true)
        // but that would require a validating parser and the schema which we do not always have.
        // As a workaround we'll use custom solution to trim the whitespace from the text nodes
        // and drop them if all the text is just whitespace.
        // See also http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6545684
        XPATH_FACTORY = XPathFactory.newInstance();
    }

    private List<JavaXmlQuery> queries = new ArrayList<JavaXmlQuery>();
    private Map<String, XmlMapEntry> mapSpecification = null;

    /**
     * @see com.wipro.ats.bdre.io.xml.processor.XmlProcessor#initialize(com.wipro.ats.bdre.io.xml.processor.XmlProcessorContext)
     */
    @Override
    public void initialize(XmlProcessorContext xmlProcessorContext) {
        try {
            this.builder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
            XPath xpath = XPATH_FACTORY.newXPath();
            for (XmlQuery xmlQuery : xmlProcessorContext.getXmlQueries()) {
                this.queries.add(new JavaXmlQuery(xmlQuery).compile(xpath));
            }
            this.mapSpecification = xmlProcessorContext.getXmlMapSpecification();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see com.wipro.ats.bdre.io.xml.processor.XmlProcessor#parse(java.lang.String)
     */
    @Override
    public Map<String, NodeArray> parse(String value) {
        Map<String, NodeArray> result = null;
        Document document = null;
        try {
            result = new HashMap<String, NodeArray>();
            document = this.builder.parse(new InputSource(new StringReader(value)));
            for (JavaXmlQuery query : this.queries) {
                XPathExpression expression = query.getExpression();
                String name = query.getName();
                NodeArray nodeArray = new NodeArray().withName(name);
                if (expression != null) {
                    NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
                    for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); ++nodeIndex) {
                        Node node = nodeList.item(nodeIndex);
                        if (node.getNodeType() == Node.TEXT_NODE) {
                            Node text = trimTextNode(node);
                            if (text != null) {
                                nodeArray.add(node);
                            }
                        } else {
                            trimWhitespace(node);
                            nodeArray.add(node);
                        }
                    }
                }
                result.put(name, nodeArray);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * @see com.ibm.spss.hive.serde2.xml.processor.java.XmlProcessor.XPathProcessor#getObjectValue(java.lang.Object, java.lang.String)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getObjectValue(Object o, String fieldName) {
        if (o instanceof Map<?, ?>) {
            NodeArray nodeArray = (NodeArray) ((Map) o).get(fieldName);
            return nodeArray.size() == 0 ? null : nodeArray;
        } else if (o instanceof Node) {
            return getObjectValue((Node) o, fieldName);
        } else if (o instanceof NodeArray) {
            NodeArray array = (NodeArray) o;
            List<Node> nodes = new ArrayList<Node>();
            for (Node node : array) {
                Object value = getObjectValue(node, fieldName);
                if (value instanceof Node) {
                    nodes.add((Node) value);
                } else if (value instanceof NodeArray) {
                    nodes.addAll((NodeArray) value);
                }
            }
            return nodes.size() == 0 ? null : new NodeArray(nodes);
        }
        return null;
    }

    /**
     * Returns the object value for the given field name and node
     * 
     * @param node
     *            the node
     * @param fieldName
     *            the field name
     * @return the object value for the given field name and node
     */
    private Object getObjectValue(Node node, String fieldName) {
        // we have to take into account the fact that fieldName will be in the lower case
        if (node != null) {
            String name = node.getLocalName();
            switch (node.getNodeType()) {
                case Node.ATTRIBUTE_NODE:
                    return name.equalsIgnoreCase(fieldName) ? node : null;
                case Node.ELEMENT_NODE: {
                    if (name.equalsIgnoreCase(fieldName)) {
                        return new NodeArray(node.getChildNodes());
                    } else {
                        NamedNodeMap namedNodeMap = node.getAttributes();
                        for (int attributeIndex = 0; attributeIndex < namedNodeMap.getLength(); ++attributeIndex) {
                            Node attribute = namedNodeMap.item(attributeIndex);
                            if (attribute.getLocalName().equalsIgnoreCase(fieldName)) {
                                return attribute;
                            }
                        }
                        return null;
                    }
                }
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * Returns the string value for the object
     * 
     * @param o
     *            the object
     * @return the string value for the object
     */
    private String getStringValue(Object o) {
        if (o instanceof String) {
            return (String) o;
        } else if (o instanceof NodeArray) {
            NodeArray array = (NodeArray) o;
            switch (array.size()) {
                case 0:
                    return null;
                case 1: {
                    return getStringValue(array.get(0));
                }
                default:
                    return getStringValue(array);
            }
        } else if (o instanceof Node) {
            return getStringValue((Node) o);
        } else if (o != null) {
            return o.toString();
        }
        return null;
    }

    /**
     * Returns the string value for the node
     * 
     * @param node
     *            the node
     * @return the string value for the node
     */
    private String getStringValue(Node node) {
        switch (node.getNodeType()) {
            case Node.ATTRIBUTE_NODE:
            case Node.TEXT_NODE:
                return node.getNodeValue();
            default: {
                try {
                    Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
                    StringWriter buffer = new StringWriter();
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    transformer.transform(new DOMSource(node), new StreamResult(buffer));
                    return buffer.toString();
                } catch (Exception e) {
                }
                return null;
            }
        }
    }

    /**
     * Returns the string value for the node array
     * 
     * @param node
     *            the node array
     * @return the string value for the node array
     */
    private String getStringValue(NodeArray nodes) {
        StringBuilder stringBuilder = new StringBuilder();
        // If all we have is just a bunch of nodes and the user wants a string
        // we'll use a parent element called <string> to have a valid XML document
        stringBuilder.append("<string>");
        for (Node node : nodes) {
            stringBuilder.append(getStringValue(node));
        }
        stringBuilder.append("</string>");
        return stringBuilder.toString();
    }

    /**
     * @see com.ibm.spss.hive.serde2.xml.processor.java.XmlProcessor.XPathProcessor#getPrimitiveObjectValue(java.lang.Object,
     *      org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory)
     */
    @Override
    public Object getPrimitiveObjectValue(Object o, PrimitiveCategory primitiveCategory) {
        return XmlUtils.getPrimitiveValue(getStringValue(o), primitiveCategory);
    }

    /**
     * The given node returns a map entry or null
     * 
     * @param node
     *            the node
     * @return a map entry for the given node or null
     */
    @SuppressWarnings("rawtypes")
    private Map.Entry getMapEntry(Node node) {
        Object value = null;
        String keyName = node.getLocalName();
        XmlMapEntry xmlMapEntry = this.mapSpecification.get(keyName);
        switch (node.getNodeType()) {
            case Node.ATTRIBUTE_NODE:
                value = node.getNodeValue();
                break;
            case Node.ELEMENT_NODE: {
                if (xmlMapEntry == null) {
                    value = new NodeArray(node.getChildNodes());
                } else {
                    XmlMapFacet valueFacet = xmlMapEntry.getValue();
                    switch (valueFacet.getType()) {
                        case ELEMENT:
                            value = keyName;
                            break;
                        case CONTENT: {
                            NodeList nodeList = node.getChildNodes();
                            if (nodeList.getLength() > 0) {
                                value = new NodeArray(nodeList);
                            }
                        }
                            break;
                        case ATTRIBUTE: {
                            NamedNodeMap attributes = node.getAttributes();
                            Node attribute = attributes.getNamedItem(valueFacet.getName());
                            if (attribute != null) {
                                value = attribute.getNodeValue();
                            }
                        }
                            break;
                        default:
                            throw new IllegalStateException();
                    }
                    XmlMapFacet keyFacet = xmlMapEntry.getKey();
                    switch (keyFacet.getType()) {
                        case ELEMENT:
                            break;
                        case CONTENT: {
                            NodeList nodeList = node.getChildNodes();
                            if (nodeList.getLength() > 0) {
                                keyName = getStringValue((Node) nodeList.item(0));
                            } else {
                                keyName = null;
                            }
                        }
                            break;
                        case ATTRIBUTE: {
                            NamedNodeMap attributes = node.getAttributes();
                            Node attribute = attributes.getNamedItem(keyFacet.getName());
                            if (attribute != null) {
                                keyName = attribute.getNodeValue();
                            } else {
                                keyName = null;
                            }
                            break;
                        }
                        default:
                            throw new IllegalStateException();
                    }
                }
            }
        }
        if (keyName == null) {
            return null;
        } else {
            final Object _key = keyName;
            final Object _value = value;
            return new Map.Entry() {

                @Override
                public Object getKey() {
                    return _key;
                }

                @Override
                public Object getValue() {
                    return _value;
                }

                @Override
                public Object setValue(Object object) {
                    return null;
                }

            };
        }
    }

    /**
     * Given the node populates the map
     * 
     * @param map
     *            the map
     * @param node
     *            the node
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void populateMap(Map map, Node node) {
        Map.Entry entry = getMapEntry(node);
        if (entry != null) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 
     * @param node
     */
    protected void trimWhitespace(Node node) {
        List<Node> doomedChildren = new ArrayList<Node>();
        NodeList children = node.getChildNodes();
        for (int childIndex = 0; childIndex < children.getLength(); ++childIndex) {
            Node child = children.item(childIndex);
            short nodeType = child.getNodeType();
            if (nodeType == Node.ELEMENT_NODE) {
                trimWhitespace(child);
            } else if (nodeType == Node.TEXT_NODE) {
                String trimmedValue = child.getNodeValue().trim();
                if (trimmedValue.length() == 0) {
                    doomedChildren.add(child);
                } else {
                    child.setNodeValue(trimmedValue);
                }
            } else if (nodeType == Node.COMMENT_NODE) {
                node.removeChild(child);
            }
        }
        for (Node doomed : doomedChildren) {
            node.removeChild(doomed);
        }
    }

    /**
     * @param node
     * @return
     */
    private Node trimTextNode(Node node) {
        String trimmedValue = node.getNodeValue().trim();
        if (trimmedValue.length() == 0) {
            return null;
        } else {
            node.setNodeValue(trimmedValue);
            return node;
        }
    }

    /**
     * @see com.wipro.ats.bdre.io.xml.processor.XmlProcessor#getMap(java.lang.Object)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Map<?, ?> getMap(Object o) {
        Map map = null;
        if (o != null) {
            map = new HashMap();
            if (o instanceof Node) {
                Node node = (Node) o;
                populateMap(map, node);
            } else if (o instanceof NodeArray) {
                NodeArray array = (NodeArray) o;
                for (Node node : array) {
                    populateMap(map, node);
                }
            } else if (o instanceof Map) {
                map.putAll((Map) o);
            }
        }
        return map;
    }

    /**
     * @see com.wipro.ats.bdre.io.xml.processor.XmlProcessor#getList(java.lang.Object)
     */
    @Override
    public List<?> getList(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof List) {
            return (List<?>) o;
        }
        return null;
    }
}
