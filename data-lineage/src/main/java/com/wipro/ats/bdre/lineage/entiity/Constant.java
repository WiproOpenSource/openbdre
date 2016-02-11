/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.lineage.entiity;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by jayabroto on 29-04-2015.
 */
public class Constant extends Node {
    private static final Logger LOGGER = Logger.getLogger(Constant.class);

    private String label;
    private Node outNode;

    private String outAlias;

    // Convenience constructor for use in LineageMain
    public Constant(String value, String outAlias) {
        super(value, "Constant", "none", value);
        if(value.toLowerCase().equals("\\n")) this.setLabel("'new line'");
        else this.setLabel("'"+value+"'");
        this.outAlias = outAlias;
    }


    public Node getOutNode() {return outNode;}
    public void setOutNode(Node outNode) {this.outNode = outNode;}
    public String getOutAlias() {return outAlias;}
    public void setOutAlias(String outAlias) {this.outAlias = outAlias;}

    public static void updateConstants(List<Table> inTableNodes, List<Table> outTableNodes, List<Column> inColumnNodes,
                                       List<Column> outColumnNodes, List<Function> functions,List<Constant> constants, List<Relation> relations) {
        // update inNodes and outNode for each Constant
        for (Constant constant : constants) {
            System.out.println("updateConstants: Constant : " + constant);
            if (constant.getOutNode() == null) {
                String outAlias = constant.getOutAlias();
                System.out.println("outAlias = " + outAlias);
                for (Column outColumn : outColumnNodes) {
                    if ((outColumn.getColumnName() != null && outColumn.getColumnName().equalsIgnoreCase(outAlias)) ) {
                        System.out.println("updateConstants: Constant Alias match found : " + outAlias);
                        constant.setOutNode(outColumn);
                    }
                }
            }
        }
    }

    @Override
    public String toDotString() {
        return "\n\"" + getLabel() + getId() + "\" [\n" +
                "label = \"" + getLabel() + "\"\n" +
                "shape = \"" + getShape() + "\"\n" +
                "width= 0.1\n"+
                "height= 0.025\n"+
                "];";
    }

    public String toDotEdge() {             // used for Relation generation
        String value = "\"" + getLabel() + getId() + "\"";
        return value;
    }

    @Override
    public String toString() {
        return "Constant{" +
                "label=" + label +
                ", outAlias='" + outAlias + '\'' +
                '}';
    }


}