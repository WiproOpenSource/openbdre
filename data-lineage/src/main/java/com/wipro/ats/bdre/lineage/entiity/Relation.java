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

import com.wipro.ats.bdre.lineage.type.EntityType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by jayabroto on 24-04-2015.
 */
public class Relation {
	private static final Logger LOGGER = Logger.getLogger(Relation.class);
//	private static int destinationCounter;

	private Node source;
	private Node destination;
	private HashMap<String,String> properties = new HashMap<String, String>();
	private UUID id;

	public Relation(Node source, Node destination) {
		this.source = source;
		this.destination = destination;
		id= UUID.randomUUID();
	}

	public Node getSource() {return source;}
	public void setSource(Node source) {this.source = source;}
	public Node getDestination() {return destination;}
	public void setDestination(Node destination) {this.destination = destination;}

	public HashMap<String, String> getProperties() {
		return properties;
	}
	public void setProperties(HashMap<String, String> properties) {this.properties = properties;}
	private String getProperty(String key){return properties.get(key);}
	private void addProperty(String key, String value){this.properties.put(key, value);}

	public String toDotString() {
//		"node1":f1 -> "node3":f1;
		String sourceDotString = null;
		String destinationDotString = null;
		if (source instanceof Column) {
			if (((Column) source).isUsedInQuery())
				sourceDotString = ((Column) source).toDotEdge();
		}
		else if (source instanceof Function)
			sourceDotString = ((Function)source).toDotEdge();
		else if (source instanceof Constant)
			sourceDotString = ((Constant)source).toDotEdge();
//		else if (source instanceof Table)
//			sourceDotString = "\"" + ((Table)source).getDataBase() + " ." + ((Table)source).getTableName() + "\"";

		if (destination instanceof Column) {
			if (((Column) destination).isUsedInQuery())
				destinationDotString = ((Column) destination).toDotEdge();
		} else if (destination instanceof Function)
			destinationDotString = ((Function)destination).toDotEdge();
//		else if (destination instanceof Table)
//			destinationDotString = "\"" + ((Table)destination).getDataBase() + " ." + ((Table)destination).getTableName() + "\"";

		String value = sourceDotString + " -> " + destinationDotString + "[color=grey33,id=\""+id+"\",href=\"javascript:document.getElementById('"+id+"').getElementsByTagName('path')[0]. setAttribute('stroke','blue');document.getElementById('"+id+"').getElementsByTagName('path')[0]. setAttribute('stroke-width','0.22')\" ];";
		System.out.println("value = " + value);
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Relation relation = (Relation) o;
		if (source != null ? !source.equals(relation.source) : relation.source != null) return false;
		return !(destination != null ? !destination.equals(relation.destination) : relation.destination != null);

	}

	@Override
	public int hashCode() {
		int result = source != null ? source.hashCode() : 0;
		result = 31 * result + (destination != null ? destination.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Relation{" +
				"source=" + source +
				", destination=" + destination +
				", properties=" + properties +
				'}';
	}


	public static void updateRelations(List<Table> inTableNodes, List<Table> outTableNodes, List<Column> inColumnNodes,
	                                   List<Column> outColumnNodes, List<Function> functions, List<Constant> constants, List<Relation> relations) {
		Table outTable = outTableNodes.get(0);              // assuming one destination table
		List<String> relationSourceLabelList = new ArrayList<String>();
		HashMap<String,String> relationConstantLabelMap = new HashMap<String, String>();
		for (Relation relation : relations) {
			Node source = relation.getSource();
			Node destination = relation.getDestination();

			if (source != null  && destination == null ) {
				// loop through functions; if found
				for (Function function : functions) {
					for (Node inNode : function.getInNodes()) {
						// TODO handle Function as input and alias handling
						// TODO same column in function input and input column

						// match both columnName and tableName
//						System.out.println("updateRelations: source = " + source);
//						System.out.println("updateRelations: inNode = " + inNode);
						if (source instanceof Column && inNode instanceof Column &&
								((Column) source).getColumnName().equalsIgnoreCase(((Column) inNode).getColumnName())
								&& ((Column) source).getTable().getTableName().equalsIgnoreCase
								(((Column) inNode).getTable().getTableName()) && !relationSourceLabelList.contains(relation.getSource().getLabel())) {
							System.out.println("updateRelations: relation match found in FunctionNodes : " + source.getLabel());
							relation.setDestination(function);
							relationSourceLabelList.add(source.getLabel());
						}
					}
				}
				if (source instanceof Constant) {
					Constant constant = (Constant) source;
					if(relationConstantLabelMap.containsKey(constant.getLabel())){
						constant.setId(relationConstantLabelMap.get(constant.getLabel()));
					}
					relation.setDestination(constant.getOutNode());
					relationConstantLabelMap.put(constant.getLabel(),constant.getId());
				}
				if (relation.getDestination() == null) {
//					Column outColumn = new Column(outTableNodes.get(0), (source.getLabel() + ++destinationCounter), null, null, EntityType.OUTTYPE);
					// destination column name in output table is kept same as source column or function
					String outColumnName = null;
					if (source instanceof Function) {
						Function function = (Function) source;
						if (function.getOutAlias() != null)
							outColumnName = function.getOutAlias();
						else
							outColumnName = function.getInNodes().get(0).getLabel();
					} else
						outColumnName = source.getLabel();

					Column outColumn = new Column(outTable, outColumnName, null, null, EntityType.OUTTYPE, null, true);

					for(Column ocn: outColumnNodes){
						if(ocn.equals(outColumn)){
							outColumn.setId(ocn.getId());
						}

					}

					relation.setDestination(outColumn);
					System.out.println("destination outColumn is set for relation= " + outColumn + "id= "+ outColumn.getId());
				}
			}

			System.out.println("updateRelations: " + source.getLabel() + source.getId() +
					" : " + relation.getDestination().getLabel() +  relation.getDestination().getId());
		}

	}

//		public static void generateEdges(Set<Relation> edges, Set<Node> nodes, Set<String> inputTableList, Set<String> outputTableList,
//	                                 Set<String> columnList, Set<String> functionList) {
//		// traverse all nodes(tables, functions, columns) and generate relations
//		// start with source nodes
//
//		for (Node node : nodes) {            // for input table columns
//			if (isPresentInTableList(node, inputTableList)) {
//				Table table = (Table)node;
//				List<Column> columns = table.getColumns();
//				LOGGER.info("Columns for Table " + table.getTableName() + " = " + columns.size());
//				for (Column column : columns) {
//					Relation edge = new Relation(column, null);
//					edges.add(edge);
//					LOGGER.info("Relation added : " + edge.toDotString());
//				}
//			}
//		}
//
//		// copied from LineageMain generateEntities
//		for (String columnName : columnList) {      // for input columns
//			Column column = null;
//			String[] split = columnName.split("->");
//			if (split.length == 3 && split[0].equalsIgnoreCase("input")) {
//				for (Node node : nodes) {
//					if (node instanceof Table && split[2].equalsIgnoreCase(((Table) node).getAlias())) {
//						LOGGER.info("Relation : Found table for column : " + ((Table)node).getTableName() + " : " + split[1]);
//						column = ((Table) node).getColumnByName(split[1]);
//						Relation edge = new Relation(column, null);
//						edges.add(edge);
//						LOGGER.info("Relation added : " + edge.toDotString());
//					}
//				}
//
//			} else if (split.length == 2 && split[0].equalsIgnoreCase("input") && split[1].equalsIgnoreCase("*")) {
//				// fetch all input columns from DB
//
//			} else if (split[0].equalsIgnoreCase("input")) {
//				for (String tableName : inputTableList) {       // loop not needed as there is only one input table in this case
//					String[] split2 = tableName.split("->");
//					for (Node node : nodes) {
//						if (node instanceof Table && ((Table) node).getTableName().equalsIgnoreCase(split2[0])) {
//							LOGGER.info("Relation : Found table for column : " + ((Table)node).getTableName() + " : " + split[1]);
//							column = ((Table) node).getColumnByName(split[1]);
//							Relation edge = new Relation(column, null);
//							edges.add(edge);
//							LOGGER.info("Relation added : " + edge.toDotString());
//						}
//					}
//				}
//
//			} else if (split[0].equalsIgnoreCase("inputcol(infunction)")) {
////				for (String tableName : inputTableList) {
////					String[] split2 = tableName.split("->");
////					for (Node node : nodes) {
////						if (node instanceof Table && ((Table) node).getTableName().equalsIgnoreCase(split2[0])) {
////							LOGGER.info("Relation : Found table for column : " + ((Table)node).getTableName() + " : " + split[1]);
////							column = ((Table)node).new Column(null, split[1], null);
////							((Table)node).addColumn(column);
////						}
////					}
////				}
//			}
//
//			for (Node node : nodes) {            // for function
//				if (node instanceof Function) {
//					Function function = (Function)node;
//					Set<String> inputs = function.getInputs();
//					if (inputs != null) {
//						// do something
//					}
//				}
//			}
//
//			for (Node node : nodes) {            // for output table
//				if (isPresentInTableList(node, outputTableList)) {
//					Table table = (Table)node;
//					for (Relation edge : edges) {
//						edge.setDestination(table);
//					}
//				}
//			}
//		}
//		LOGGER.info("Edges number = " + edges.size());
//	}
//
//		private static boolean isPresentInTableList(Node node, Set<String> tableList) {
//		for (String tableName : tableList) {
//			if (!(node instanceof Table))
//				return false;
//			else {
//				String[] split = tableName.split("->");
////				LOGGER.info("Node = " + ((Table)node).getTableName());
////				LOGGER.info("split = " + split[0]);
//				if (((Table)node).getTableName().equalsIgnoreCase(split[0])) {
////					LOGGER.info("return true");
//					return true;
//				}
//			}
//		}
//		return false;
//	}

}