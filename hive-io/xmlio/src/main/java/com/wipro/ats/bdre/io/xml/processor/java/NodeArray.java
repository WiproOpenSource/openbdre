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

import java.util.ArrayList;
import java.util.List;

import com.wipro.ats.bdre.io.xml.processor.SerDeArray;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Some DOM nodes implement Node and NodeList interfaces. We have to distinguish between the two.
 */
@SuppressWarnings("serial")
public class NodeArray extends ArrayList<Node> implements SerDeArray {

    private String name = null;

    public NodeArray() {
    }

    /**
     * Conversion constructor for the DOM NodeList
     * 
     * @param nodeList
     */
    public NodeArray(NodeList nodeList) {
        for (int nodeIndex = 0; nodeList != null && nodeIndex < nodeList.getLength(); ++nodeIndex) {
            add(nodeList.item(nodeIndex));
        }
    }

    /**
     * Conversion constructor for the List
     * 
     * @param nodeList
     */
    public NodeArray(List<Node> nodeList) {
        addAll(nodeList);
    }

    /**
     * Associates the node array with the given name
     * 
     * @param name
     *            the name
     * @return this instance of the node array
     */
    public NodeArray withName(String name) {
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
