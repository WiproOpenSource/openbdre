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

import com.wipro.ats.bdre.lineage.type.UniqueList;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by jayabroto on 29-04-2015.
 */
public class Function extends Node {
	private static final Logger LOGGER = Logger.getLogger(Function.class);
//	private static int counter = 0;

	private String name;
	private List<Node> inNodes = new UniqueList<Node>();       // multiple inputs possible
	private Node outNode;                                    // handling one output per function

	private List<String> inColumnNameList;
	private String outAlias;

	public Function(String label, List<Node> inNodes, Node outNode, String displayName) {
		super(label, "Function", "ellipse", displayName);
//		++counter;
		this.name = label.toUpperCase();
		this.inNodes = inNodes;
		this.outNode = outNode;
	}

	// Convenience constructor for use in LineageMain
	public Function(String label, List<String> inColumnNameList, String outAlias) {
		super(label, "Function", "ellipse", label);
//		++counter;
		this.name = label.toUpperCase();
		this.inColumnNameList = inColumnNameList;
		if(outAlias==null){
			this.outAlias = label.toUpperCase();
		}
		else

		this.outAlias = outAlias;
	}

	public String getName() {return name;}
	public void setName(String name) {this.name = name.toUpperCase();}
	public List<Node> getInNodes() {return inNodes;}
	public void setInNodes(List<Node> inNodes) {this.inNodes = inNodes;}
	public void addInNodes(Node inNode) {this.inNodes.add(inNode);}
	public Node getOutNode() {return outNode;}
	public void setOutNode(Node outNode) {this.outNode = outNode;}
	public List<String> getInColumnNameList() {return inColumnNameList;}
	public void setInColumnNameList(List<String> inColumnNameList) {this.inColumnNameList = inColumnNameList;}
	public String getOutAlias() {
		return outAlias;
	}
	public void setOutAlias(String outAlias) {this.outAlias = outAlias;}

	public static void updateFunctions(List<Table> inTableNodes, List<Table> outTableNodes, List<Column> inColumnNodes,
	                                   List<Column> outColumnNodes, List<Function> functions,List<Constant> constants, List<Relation> relations) {
		// update inNodes and outNode for each Function
		for (Function function : functions) {
			System.out.println("updateFunctions: Function : " + function);
			if (function.getInNodes() == null || function.getInNodes().size() == 0) {
				for (String inColumnName : function.getInColumnNameList()) {
					System.out.println("updateFunctions: inColumnName = " + inColumnName);
					for (Column inColumn : inColumnNodes) {
						if (inColumnName.equalsIgnoreCase(inColumn.getColumnName())) {
							System.out.println("updateFunctions: Function inColumnName match found : " + inColumnName);
							function.addInNodes(inColumn);
						}
					}
				}
			}

			if (function.getOutNode() == null) {
				String outAlias = function.getOutAlias();
				for (Column outColumn : outColumnNodes) {
					if ((outColumn.getAlias() != null && outColumn.getAlias().equalsIgnoreCase(outAlias)) ||
							((outColumn.getTable() != null && outColumn.getTable().getAlias() != null &&
									outColumn.getTable().getAlias().equalsIgnoreCase(outAlias)))) {
						System.out.println("updateFunctions: Function Alias match found : " + outAlias);
						function.setOutNode(outColumn);
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
		return "Function{" +
				"name='" + name + '\'' +
				", inNodes=" + inNodes +
				", outNode=" + outNode +
				", outAlias='" + outAlias + '\'' +
				", inColumnNameList=" + inColumnNameList +
				'}';
	}
}