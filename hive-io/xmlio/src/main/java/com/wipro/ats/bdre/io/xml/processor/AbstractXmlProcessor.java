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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory;

/**
 * Abstract XML processor
 */
public abstract class AbstractXmlProcessor implements XmlProcessor {

    private Map<String, XmlMapEntry> mapSpecification = null;
    private XmlTransformer transformer = null;

    /**
     * @see XmlProcessor#initialize(XmlProcessorContext)
     */
    @Override
    public void initialize(XmlProcessorContext xmlProcessorContext) {
        this.mapSpecification = xmlProcessorContext.getXmlMapSpecification();
        this.transformer = new XmlTransformer();
    }

    /**
     * @see XmlProcessor#getObjectValue(java.lang.Object, java.lang.String)
     */
    @SuppressWarnings({"rawtypes"})
    @Override
    public Object getObjectValue(Object o, String fieldName) {
        if (o instanceof Map<?, ?>) {
            XmlNodeArray nodeArray = (XmlNodeArray) ((Map) o).get(fieldName);
            return nodeArray.size() == 0 ? null : nodeArray;
        } else if (o instanceof XmlNode) {
            return getObjectValue((XmlNode) o, fieldName);
        } else if (o instanceof XmlNodeArray) {
            XmlNodeArray array = (XmlNodeArray) o;
            List<XmlNode> nodes = new ArrayList<XmlNode>();
            for (XmlNode node : array) {
                Object value = getObjectValue(node, fieldName);
                if (value instanceof XmlNode) {
                    nodes.add((XmlNode) value);
                } else if (value instanceof XmlNodeArray) {
                    nodes.addAll((XmlNodeArray) value);
                }
            }
            return nodes.size() == 0 ? null : new XmlNodeArray(nodes);
        }
        return null;
    }

    /**
     * Returns the object value for the given VTD XML node and field name
     * 
     * @param node
     *            the node
     * @param fieldName
     *            the field name
     * @return the object value for the given VTD XML node and field name
     */
    private Object getObjectValue(XmlNode node, String fieldName) {
        // we have to take into account the fact that fieldName will be in the lower case
        if (node != null) {
            String name = node.getName();
            switch (node.getType()) {
                case XmlNode.ATTRIBUTE_NODE:
                    return name.equalsIgnoreCase(fieldName) ? node : null;
                case XmlNode.ELEMENT_NODE: {
                    if (name.equalsIgnoreCase(fieldName)) {
                        return new XmlNodeArray(node.getChildren());
                    } else {
                        Map<String, XmlNode> attributes = node.getAttributes();
                        for (Map.Entry<String, XmlNode> entry : attributes.entrySet()) {
                            String attributeName = entry.getKey();
                            if (attributeName.equalsIgnoreCase(fieldName)) {
                                return entry.getValue();
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
     * @see XmlProcessor#getPrimitiveObjectValue(java.lang.Object,
     *      org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector.PrimitiveCategory)
     */
    @Override
    public Object getPrimitiveObjectValue(Object o, PrimitiveCategory primitiveCategory) {
        return XmlUtils.getPrimitiveValue(getStringValue(o), primitiveCategory);
    }

    /**
     * Returns the string value for the given object
     * 
     * @param o
     * @return the string value for the given object
     */
    private String getStringValue(Object o) {
        if (o instanceof String) {
            return (String) o;
        } else if (o instanceof XmlNodeArray) {
            XmlNodeArray array = (XmlNodeArray) o;
            switch (array.size()) {
                case 0:
                    return null;
                case 1: {
                    return getStringValue(array.get(0));
                }
                default:
                    return getStringValue(array);
            }
        } else if (o instanceof XmlNode) {
            return getStringValue((XmlNode) o);
        } else if (o != null) {
            return o.toString();
        }
        return null;
    }

    /**
     * Returns the string value for the given object
     * 
     * @param node
     * @return the string value for the given object
     */
    private String getStringValue(XmlNode node) {
        switch (node.getType()) {
            case XmlNode.ATTRIBUTE_NODE:
            case XmlNode.TEXT_NODE:
                return node.getValue();
            default: {
                StringBuilder builder = new StringBuilder();
                this.transformer.transform(node, builder);
                return builder.toString();
            }
        }
    }

    /**
     * Returns the string value for the given node array
     * 
     * @param nodes
     * @return the string value for the given node array
     */
    private String getStringValue(XmlNodeArray nodes) {
        StringBuilder stringBuilder = new StringBuilder();
        // If all we have is just a bunch of nodes and the user wants a string
        // we'll use a parent element called <string> to have a valid XML document
        stringBuilder.append("<string>");
        for (XmlNode node : nodes) {
            stringBuilder.append(getStringValue(node));
        }
        stringBuilder.append("</string>");
        return stringBuilder.toString();
    }

    /**
     * @see XmlProcessor#getMap(java.lang.Object)
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Map<?, ?> getMap(Object o) {
        Map map = null;
        if (o != null) {
            map = new HashMap();
            if (o instanceof XmlNode) {
                XmlNode node = (XmlNode) o;
                populateMap(map, node);
            } else if (o instanceof XmlNodeArray) {
                XmlNodeArray array = (XmlNodeArray) o;
                for (XmlNode node : array) {
                    populateMap(map, node);
                }
            } else if (o instanceof Map) {
                map.putAll((Map) o);
            }
        }
        return map;
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
    private void populateMap(Map map, XmlNode node) {
        Map.Entry entry = getMapEntry(node);
        if (entry != null) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * The given node returns a map entry or null
     * 
     * @param node
     *            the node
     * @return a map entry for the given node or null
     */
    @SuppressWarnings({"rawtypes"})
    private Map.Entry getMapEntry(XmlNode node) {
        String keyName = node.getName();
        Object value = null;
        XmlMapEntry xmlMapEntry = this.mapSpecification.get(keyName);
        switch (node.getType()) {
            case XmlNode.ATTRIBUTE_NODE:
                value = node.getValue();
                break;
            case XmlNode.ELEMENT_NODE: {
                if (xmlMapEntry == null) {
                    value = new XmlNodeArray(node.getChildren());
                } else {
                    XmlMapFacet valueFacet = xmlMapEntry.getValue();
                    switch (valueFacet.getType()) {
                        case ELEMENT:
                            value = keyName;
                            break;
                        case CONTENT: {
                            List<XmlNode> nodeList = node.getChildren();
                            if (nodeList.size() > 0) {
                                value = new XmlNodeArray(nodeList);
                            }
                        }
                            break;
                        case ATTRIBUTE: {
                            Map<String, XmlNode> attributes = node.getAttributes();
                            XmlNode attribute = attributes.get(valueFacet.getName());
                            if (attribute != null) {
                                value = attribute.getValue();
                            } else {
                            }
                        }
                            break;
                        default:
                            throw new IllegalStateException();
                    }
                    XmlMapFacet keyFacet = xmlMapEntry.getKey();
                    switch (keyFacet.getType()) {
                        case ELEMENT:
                            // keyName is already set
                            break;
                        case CONTENT: {
                            List<XmlNode> nodeList = node.getChildren();
                            if (nodeList.size() > 0) {
                                keyName = getStringValue((XmlNode) nodeList.get(0));
                            } else {
                                keyName = null;
                            }
                        }
                            break;
                        case ATTRIBUTE: {
                            Map<String, XmlNode> attributes = node.getAttributes();
                            XmlNode attribute = attributes.get(keyFacet.getName());
                            if (attribute != null) {
                                keyName = attribute.getValue();
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
     * @see XmlProcessor#getList(java.lang.Object)
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
