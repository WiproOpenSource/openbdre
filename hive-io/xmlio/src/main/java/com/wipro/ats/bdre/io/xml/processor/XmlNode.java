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

import java.io.InputStream;
import java.io.StringReader;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Defines an XML node
 */
public class XmlNode extends DefaultHandler {

    private static final SAXParserFactory FACTORY = SAXParserFactory.newInstance();

    static {
        FACTORY.setNamespaceAware(true);
    }

    public static final int ATTRIBUTE_NODE = 0;
    public static final int TEXT_NODE = 1;
    public static final int ELEMENT_NODE = 2;

    protected boolean valid = false;
    protected String name = null;
    protected String value = null;
    protected int type = -1;
    protected List<XmlNode> children = new LinkedList<XmlNode>();
    protected Map<String, XmlNode> attributes = new HashMap<String, XmlNode>();

    private Deque<XmlNode> stack = new LinkedList<XmlNode>();

    /**
     * Conversion constructor for SAX
     * 
     * @param inputStream
     *            the input stream
     */
    public XmlNode(InputStream inputStream) {
        initialize(inputStream);
    }

    /**
     * 
     * @param inputStream
     */
    protected void initialize(InputStream inputStream) {
        try {
            SAXParser saxParser = FACTORY.newSAXParser();
            saxParser.parse(new InputSource(inputStream), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param value
     */
    protected void initialize(String value) {
        try {
            SAXParser saxParser = FACTORY.newSAXParser();
            saxParser.parse(new InputSource(new StringReader(value)), this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Protected constructor
     */
    protected XmlNode() {
    }

    /**
     * Returns the node name
     * 
     * @return the node name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the node value
     * 
     * @return the node value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Returns the node type
     * 
     * @return the node type
     */
    public int getType() {
        return this.type;
    }

    /**
     * Returns the child nodes if any
     * 
     * @return the child nodes if any
     */
    public List<XmlNode> getChildren() {
        return this.children;
    }

    /**
     * Returns the attribute node for the given name
     * 
     * @param name
     *            the attribute name
     * @return the attribute node for the given name
     */
    public XmlNode getAttribute(String name) {
        return this.attributes.get(name);
    }

    /**
     * Adds the attribute
     * 
     * @param _name
     *            the attribute name
     * @param _value
     *            the attribute name
     */
    public void addAttribute(final String _name, final String _value) {
        this.attributes.put(_name, new XmlNode() {
            {
                this.name = _name;
                this.value = _value;
                this.valid = true;
                this.type = XmlNode.ATTRIBUTE_NODE;
            }
        });
    }

    /**
     * Adds the child node
     * 
     * @param child
     *            the child node
     */
    public void addChild(XmlNode child) {
        this.children.add(child);
    }

    /**
     * Returns the node attributes
     * 
     * @return the node attributes
     */
    public Map<String, XmlNode> getAttributes() {
        return this.attributes;
    }

    /**
     * Returns true if the node is valid
     * 
     * @return true if the node is valid
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] characters, int start, int length) throws SAXException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(characters, start, length);
        final String string = stringBuilder.toString().trim();
        if (string.length() > 0) {
            this.stack.peek().addChild(new XmlNode() {
                {
                    this.type = TEXT_NODE;
                    this.valid = true;
                    this.value = string;
                }
            });
        }
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        XmlNode node = this.stack.pop();
        XmlNode parent = this.stack.size() > 0 ? this.stack.peek() : null;
        if (parent != null) {
            parent.addChild(node);
        }
    }

    /**
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        XmlNode xmlNode = this.stack.size() == 0 ? this : new XmlNode();
        xmlNode.name = localName;
        xmlNode.type = ELEMENT_NODE;
        xmlNode.valid = true;
        if (attributes != null) {
            for (int attributeIndex = 0; attributeIndex < attributes.getLength(); ++attributeIndex) {
                xmlNode.addAttribute(attributes.getLocalName(attributeIndex), attributes.getValue(attributeIndex));
            }
        }
        this.stack.push(xmlNode);
    }

}
