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

package com.wipro.ats.bdre.lineage;

import com.wipro.ats.bdre.lineage.api.PersistenceUnit;
import com.wipro.ats.bdre.lineage.entiity.*;
import com.wipro.ats.bdre.lineage.entiity.Node;
import com.wipro.ats.bdre.lineage.type.EntityType;
import com.wipro.ats.bdre.lineage.type.UniqueList;
import com.wipro.ats.bdre.md.dao.jpa.LineageNode;
import org.antlr.runtime.tree.DOTTreeGenerator;
import org.antlr.runtime.tree.Tree;
import org.antlr.stringtemplate.StringTemplate;
import org.apache.hadoop.hive.ql.lib.*;
import org.apache.hadoop.hive.ql.parse.*;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.apache.hadoop.hive.ql.parse.BaseSemanticAnalyzer.getUnescapedName;

/**
 * Created by jayabroto on 22-04-2015.
 */
/**
 * This class prints out the lineage info. It takes sql as input and prints
 * lineage info. Currently this prints only input and output tables for a given
 * sql. Later we can expand to add join tables etc.
 */
public class LineageMain implements NodeProcessor {
	private static final Logger LOGGER = Logger.getLogger(LineageMain.class);

	private UniqueList<String> inputTableList = new UniqueList<String>();
	private UniqueList<String> outputTableList = new UniqueList<String>();
	private UniqueList<String> columnList = new UniqueList<String>();
	private UniqueList<String> functionList = new UniqueList<String>();
	private UniqueList<Table> inTableNodes = new UniqueList<Table>();
	private UniqueList<Table> outTableNodes = new UniqueList<Table>();
	private UniqueList<Column> inColumnNodes = new UniqueList<Column>();
	private UniqueList<Column> outColumnNodes = new UniqueList<Column>();
	private List<Function> functions = new ArrayList<Function>();
	private List<Relation> relations = new ArrayList<Relation>();
	private List<Constant> constants = new ArrayList<Constant>();

	private UniqueList<Table> finalInTableNodes = new UniqueList<Table>();
	private UniqueList<Table> finalOutTableNodes = new UniqueList<Table>();
	private UniqueList<Column> finalInColumnNodes = new UniqueList<Column>();
	private UniqueList<Column> finalOutColumnNodes = new UniqueList<Column>();
	private List<Function> finalFunctions = new ArrayList<Function>();
	private UniqueList<Constant> finalConstants = new UniqueList<Constant>();
	private List<Relation> finalRelations = new ArrayList<Relation>();
	private String query;
	private String dotString;
	private Integer processId;
	private Long instanceId;
	private String defaultHiveDbName;

	//	private int nextId = 0;
	private int subquerySeq = 0;
	private PersistenceUnit persistenceUnit;

	/*public LineageMain(String query, Integer processId, Long instanceId, String defaultHiveDbName) {
		this.query = query;
		this.processId = processId;
		this.instanceId = instanceId;
		this.defaultHiveDbName = defaultHiveDbName;
	}*/
	public static void main(String[] args) throws IOException, ParseException,
			SemanticException, Exception {
		LineageMain lineageMain = new LineageMain();

		if (args.length == 4) {
			lineageMain.instanceId = Long.parseLong(args[3]);
			lineageMain.processId = Integer.parseInt(args[2]);
			lineageMain.defaultHiveDbName = args[1];
			lineageMain.query = args[0];

		} else {
//			lineageMain.instanceId = LineageConstants.instanceId;
//			lineageMain.processId = LineageConstants.processId;
//			lineageMain.defaultHiveDbName = LineageConstants.defaultHiveDbName;
//			lineageMain.query = LineageConstants.query;
			System.out.println("Error: Invalid inputs for LineageMain");
			throw new Exception("Invalid inputs for LineageMain");
		}

		lineageMain.getLineageInfo();
//		lineageMain.generateOperatorTree(new HiveConf(), query);

		// populate beans and persist in Database
		lineageMain.populateBeansAndPersist();

		// generate Dot
//		lineageMain.generateLineageDotFromDB();
		//lineageMain.generateLineageDot();

		System.out.println("End of program");
	}


	/**
	 * Parses given query and gets the lineage info.
	 */
	public void getLineageInfo() throws ParseException, SemanticException {
		query = query.replaceAll(";", " ");             // remove semicolon from query

		ParseDriver pd = new ParseDriver();
		ASTNode tree = pd.parse(query);
		while ((tree.getToken() == null) && (tree.getChildCount() > 0)) {
			tree = (ASTNode) tree.getChild(0);
		}

		// create a walker which walks the tree in a DFS manner while
		// maintaining
		// the operator stack. The dispatcher
		// generates the plan from the operator tree
		Map<Rule, NodeProcessor> rules = new LinkedHashMap<Rule, NodeProcessor>();
		// The dispatcher fires the processor corresponding to the closest
		// matching
		// rule and passes the context along
		Dispatcher disp = new DefaultRuleDispatcher(this, rules, null);
		GraphWalker ogw = new DefaultGraphWalker(disp);
		// Create a list of topop nodes
		ArrayList<org.apache.hadoop.hive.ql.lib.Node> topNodes = new ArrayList<org.apache.hadoop.hive.ql.lib.Node>();
		topNodes.add(tree);
		ogw.startWalking(topNodes, null);
	}

/*	private ASTNode search(int type, ASTNode node) {
		System.out.println("searching node = " + node);
		if (node != null) {
			if (node.getType() == type) {
				return node;
			} else {
				for (int i = 0; i < node.getChildCount(); i++) {
					ASTNode foundNode = search(type, (ASTNode) node.getChild(i));
					return foundNode;
				}
			}
		} else {
			return null;
		}
		return null;
	}*/

	/**
	 * Implements the process method for the NodeProcessor interface.
	 */
	public Object process(org.apache.hadoop.hive.ql.lib.Node nd, Stack<org.apache.hadoop.hive.ql.lib.Node> stack, NodeProcessorCtx procCtx,
	                      Object... nodeOutputs) throws SemanticException {
//		System.out.println("process: " + nd.getName());
		// Empty table and column lists initialized
		inputTableList = new UniqueList<String>();
		outputTableList = new UniqueList<String>();
		functionList = new UniqueList<String>();
		columnList = new UniqueList<String>();
		inTableNodes = new UniqueList<Table>();
		outTableNodes = new UniqueList<Table>();
		inColumnNodes = new UniqueList<Column>();
		outColumnNodes = new UniqueList<Column>();
		functions = new ArrayList<Function>();
		relations = new ArrayList<Relation>();
		constants = new ArrayList<Constant>();
		ASTNode pt = (ASTNode) nd;
		switch (pt.getToken().getType()) {
			case HiveParser.TOK_QUERY:
//				System.out.println("\"got query\" = " + pt);
				System.out.println("\nQuery " + ++subquerySeq + " parsed");
				//printDot(pt);

				getTableLineage(pt);

				for (String inputTable : inputTableList) {
					System.out.println("inputTable = " + inputTable);
				}

				//adding to outputTableList and outputNodes
				if (outputTableList.size() == 0) {
					String outputTable = "TEMP_" + UUID.randomUUID();
					outputTableList.add(outputTable);
					if (outTableNodes.size() == 0) {
						Table table = new Table(outputTable, defaultHiveDbName, null, EntityType.OUTTYPE, false);
						outTableNodes.add(table);
						System.out.println("Output temp Table added : " + table.getTableName());
					}
				}

				for (String outputTable : outputTableList) {
					if (outputTable == null || outputTable.equalsIgnoreCase("<EOF>")) {
						if (outputTableList.remove(null) || outputTableList.remove("<EOF>")) {
							outputTable = "TEMP_" + UUID.randomUUID();
							outputTableList.add(outputTable);
						}
						if (outTableNodes.size() == 0) {
							Table table = new Table(outputTable, defaultHiveDbName, null, EntityType.OUTTYPE, false);
							outTableNodes.add(table);
							System.out.println("Output temp Table added : " + table.getTableName());
						}
					}
					System.out.println("outputTable = " + outputTable);
				}

				// Update properties of Columns and Edges after processing all nodes in a query
				// populate output table columns
				Table.populateOutputTableColumns(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, constants,relations);
				Column.updateInputColumns(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, constants, relations);
				Function.updateFunctions(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, constants, relations);
				Constant.updateConstants(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, constants, relations);
				Relation.updateRelations(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, constants, relations);
				//	Table.updateAllTables(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, relations,
			//			finalInTableNodes, finalOutTableNodes, finalFunctions);

//				LOGGER.info("All Edges :::");
//				for (Relation edge : relations)
//					LOGGER.info("Relation = " + edge.toDotString());
				break;

//			case HiveParser.TOK_TAB:
//				outputTableList.add(BaseSemanticAnalyzer.getUnescapedName((ASTNode)pt.getChild(0)));
//				break;
//
//			case HiveParser.TOK_TABREF:
//				ASTNode tabTree = (ASTNode) pt.getChild(0);
//				String table_name = (tabTree.getChildCount() == 1) ?
//						BaseSemanticAnalyzer.getUnescapedName((ASTNode) tabTree.getChild(0)) :
//						BaseSemanticAnalyzer.getUnescapedName((ASTNode) tabTree.getChild(0)) + "." + tabTree.getChild(1);
//				String table_alias = null;
//				if (pt.getChild(1) != null)
//					table_alias = pt.getChild(1).toString();
//
//				if (table_alias != null)
//					inputTableList.add(table_name + "->" + table_alias);
//				else
//					inputTableList.add(table_name);
//				break;
		}

		finalInTableNodes.addAll(inTableNodes);
		finalOutTableNodes.addAll(outTableNodes);
		finalInColumnNodes.addAll(inColumnNodes);
		finalOutColumnNodes.addAll(outColumnNodes);
		finalFunctions.addAll(functions);
		finalConstants.addAll(constants);
		finalRelations.addAll(relations);

		return null;
	}

	private void getTableLineage(ASTNode queryNode) {
//		System.out.println("tableLineage: " + nd.getName());
//		ASTNode tn = (ASTNode) nd;
		ASTNode fromNode = (ASTNode) queryNode.getFirstChildWithType(HiveParser.TOK_FROM);
		ASTNode insertNode = (ASTNode) queryNode.getFirstChildWithType(HiveParser.TOK_INSERT);
		ASTNode insertDestinationNode = (ASTNode) insertNode.getFirstChildWithType(HiveParser.TOK_DESTINATION);
		if (insertDestinationNode == null)
			insertDestinationNode = (ASTNode) insertNode.getFirstChildWithType(HiveParser.TOK_INSERT_INTO);
		ASTNode nextToDestinationNode = (ASTNode) insertDestinationNode.getChild(0);
		ASTNode selectNode = (ASTNode) insertNode.getFirstChildWithType(HiveParser.TOK_SELECT);
		if (selectNode == null)
			selectNode = (ASTNode) insertNode.getFirstChildWithType(HiveParser.TOK_SELECTDI);
		ASTNode nextNodeOfFrom = (ASTNode) fromNode.getChild(0);

		System.out.println("nextNodeOfFrom to process : " + nextNodeOfFrom.toString());
		if (nextNodeOfFrom.getType() == HiveParser.TOK_SUBQUERY)
			handleTokSubQuery(nextNodeOfFrom);
		else if (nextNodeOfFrom.getType() == HiveParser.TOK_TABREF)
			handleTokTabRef(nextNodeOfFrom);
		else if (nextNodeOfFrom.getType() == HiveParser.TOK_JOIN
				|| nextNodeOfFrom.getType() == HiveParser.TOK_LEFTOUTERJOIN
				|| nextNodeOfFrom.getType() == HiveParser.TOK_RIGHTOUTERJOIN
				|| nextNodeOfFrom.getType() == HiveParser.TOK_FULLOUTERJOIN
				|| nextNodeOfFrom.getType() == HiveParser.TOK_LEFTSEMIJOIN
				|| nextNodeOfFrom.getType() == HiveParser.TOK_UNIQUEJOIN)
			handleTokJoin(nextNodeOfFrom);

		System.out.println("nextToDestinationNode to process : " + nextToDestinationNode.toString());
		if (nextToDestinationNode.getType() == HiveParser.TOK_DIR)
			handleTokDir(nextToDestinationNode, queryNode);
		else if (nextToDestinationNode.getType() == HiveParser.TOK_TAB)
			handleTokTab(nextToDestinationNode);

		System.out.println("selectNode to process : " + selectNode.toString());
		for (int j = 0; j < selectNode.getChildCount(); j++) {
			ASTNode nodeSelExpr = (ASTNode) selectNode.getChild(j);
			if (nodeSelExpr.getChild(0).getType() == HiveParser.TOK_FUNCTION)
				handleTokFunction(nodeSelExpr);
			else if (nodeSelExpr.getChild(0).getType() == HiveParser.TOK_ALLCOLREF)
				handleTokAllColRef((ASTNode)nodeSelExpr.getChild(0));
			else
				handleTokTabOrColOrElse(nodeSelExpr);
		}
	}

	private void handleTokTabOrColOrElse(ASTNode nodeSelExpr) {
		String col = null;
		String colName = null;
		String table_alias = null;
		String label = null;
		String constant = null;
		String constant_alias= null;
		if (nodeSelExpr.getChild(0).getChild(0) != null
				&& nodeSelExpr.getChild(0).getChild(0).getType() == HiveParser.TOK_TABLE_OR_COL) {
			table_alias = nodeSelExpr.getChild(0).getChild(0).getChild(0).toString();
			 {
			col = colName = nodeSelExpr.getChild(0).getChild(1).toString();
			col = col + "->" + table_alias;
		    if(nodeSelExpr.getChild(1)!=null) label=nodeSelExpr.getChild(1).toString();
			 }
		}else if(nodeSelExpr.getChild(0).getText()=="TOK_NULL"){
			constant = "null";
			constant_alias = nodeSelExpr.getChild(1).toString();
			Constant conNull = new Constant(constant,constant_alias);
			constants.add(conNull);
			Relation relation = new Relation(conNull, null);
			relations.add(relation);
		}
		else if(nodeSelExpr.getChild(0).toString().startsWith("'")){
			constant = nodeSelExpr.getChild(0).toString().replace("'","");
			constant_alias = nodeSelExpr.getChild(1).toString();
			Constant conSingleQuote = new Constant(constant,constant_alias);
			constants.add(conSingleQuote);
			Relation relation = new Relation(conSingleQuote, null);
			relations.add(relation);
		}
		else if(nodeSelExpr.getChild(0).toString().startsWith("\"")) {
			constant = nodeSelExpr.getChild(0).toString().replace("\"", "");
			constant_alias = nodeSelExpr.getChild(1).toString();
			Constant conDoubleQuote = new Constant(constant,constant_alias);
			constants.add(conDoubleQuote);
			Relation relation = new Relation(conDoubleQuote, null);
			relations.add(relation);
		}

		else if(nodeSelExpr.getChildCount()==2 || nodeSelExpr.getChildCount()==1 && nodeSelExpr.getChild(0).getChildCount()==0){
		try {
			System.out.println("nodeSelExpr = " + nodeSelExpr.getChild(0).getText());
			Double.parseDouble(nodeSelExpr.getChild(0).toString());
			constant = nodeSelExpr.getChild(0).toString();
			constant_alias = nodeSelExpr.getChild(1).toString();
			Constant conNumeric = new Constant("\'"+constant+"\'",constant_alias);
			constants.add(conNumeric);
			Relation relation = new Relation(conNumeric, null);
			relations.add(relation);
		} catch (NumberFormatException e) {
			System.out.println("Unrecognizable column type");
		}
		}
		else
			col = colName = nodeSelExpr.getChild(0).getChild(0).toString();
if(col != null && colName != null) {
	System.out.println("Input column = " + col);
	columnList.addToList("input->" + col);
	Column column = new Column(null, colName, null, table_alias, EntityType.INTYPE, null, true);
	if (label != null) column.setLabel(label);
//		if(nodeSelExpr.getChild(1)!=null)
//		column.setLabel(nodeSelExpr.getChild(1).toString());
	column = inColumnNodes.addToList(column);
	Relation relation = new Relation(column, null);
	relations.add(relation);
}
	}

	private void handleTokAllColRef(ASTNode nodeSelExpr) {
		System.out.println("Input column = *");;
//		columnList.add("input->*");

		// select *
		Table table = null;
//		Column column = new Column(null, "*", null, null, EntityType.INTYPE, null, true);
		if (nodeSelExpr.getChildCount() == 1 && nodeSelExpr.getChild(0).getType() == HiveParser.TOK_TABNAME
				&& nodeSelExpr.getChild(0).getChildCount() == 1) {
			String alias = nodeSelExpr.getChild(0).getChild(0).toString();
			if (findInputTableNameByAlias(alias) != null) {             // if its an alias
				table = findInputTableNameByAlias(alias);
			}
		} else if (nodeSelExpr.getChildCount() == 0) {                      // no table specified means default table
			table = inTableNodes.get(0);
		}
		System.out.println("Table in * = " + table.getTableName());

		// create all column nodes (with usedinquery flag) and their relationss
		List<Column> columns = table.findColumnsFromHive();
		for (Column column : columns) {
			column.setUsedInQuery(true);
			columnList.addToList("input->" + column.getColumnName());
			column=inColumnNodes.addToList(column);
			Relation relation = new Relation(column, null);
			relations.add(relation);
		}
	}

	private Table findInputTableNameByAlias(String alias) {
		for (Table table : inTableNodes) {
			if (table.isDataBaseTable() && alias.equalsIgnoreCase(table.getAlias()))
				return table;
		}
		return null;
	}

	private void handleTokFunction(ASTNode nodeSelExpr) {
		String functionString = nodeSelExpr.getChild(0).getChild(0).toString();
		System.out.println("Function = " + functionString);

		if (nodeSelExpr.getChild(1) != null) {
			System.out.println("Function output alias = " + nodeSelExpr.getChild(1));
//			columnList.add("alias->" + nodeSelExpr.getChild(1));
			functionString += ";outputalias->" + nodeSelExpr.getChild(1);
		}

		// Get all tok_table_or_col
		List<String> inColumnList = new UniqueList<String>();
		fetchAllTokTableOrCol(nodeSelExpr, functionString, inColumnList);

		functionList.addToList(functionString);
		// Add to functions
		Function function = new Function(nodeSelExpr.getChild(0).getChild(0).toString(), inColumnList,
				nodeSelExpr.getChild(1)!=null ? nodeSelExpr.getChild(1).toString() : null);
		functions.add(function);
		Relation relation = new Relation(function, null);
		relations.add(relation);
	}

	// Get all tok_table_or_col
	private void fetchAllTokTableOrCol(ASTNode nodeSelExpr, String functionString, List<String> inColumnList) {
		for (int j = 0; j < nodeSelExpr.getChildCount(); j++) {

			if (nodeSelExpr.getChild(j) != null && nodeSelExpr.getChild(j).getType() == HiveParser.TOK_TABLE_OR_COL) {
				// if "." is present one step up then it is a table else a column
				String tableName = null;
				String colName = null;
				if (nodeSelExpr.getChild(j).getParent().getType() == HiveParser.DOT) {
					tableName = nodeSelExpr.getChild(j).getChild(0).toString();
					colName = nodeSelExpr.getChild(j+1).toString();
				} else {
					colName = nodeSelExpr.getChild(j).getChild(0).toString();
				}
				String columnFullName = (tableName!=null ? tableName+"." : "") + colName;

				System.out.println("Input column(infunction) = " + colName);
				columnList.addToList("inputcol(infunction)->" + colName);
				inColumnList.add(colName);
				functionString += ";inputcolumn->" + colName;

				// Add to ColumnList
				columnList.addToList("input->" + colName);
				Column column = new Column(null, colName, null, null, EntityType.INTYPE, null, true);
				column =inColumnNodes.addToList(column);
				Relation relation = new Relation(column, null);
				relations.add(relation);
			}

			if (nodeSelExpr.getChild(j) != null && nodeSelExpr.getChild(j).getChildCount() > 0) {
				fetchAllTokTableOrCol((ASTNode) nodeSelExpr.getChild(j), functionString, inColumnList);
			}
		}

//		for (int i = 0; i < nodeSelExpr.getChild(0).getChildCount(); i++) {
//
//			if (nodeSelExpr.getChild(0).getChild(i) != null && nodeSelExpr.getChild(0).getChild(i).getType() == HiveParser.TOK_TABLE_OR_COL) {
//				String colName = nodeSelExpr.getChild(0).getChild(i).getChild(0).toString();
//				System.out.println("Input column(infunction) = " + colName);
//				columnList.add("inputcol(infunction)->" + colName);
//				inColumnList.add(colName);
//				functionString += ";inputcolumn->" + colName;
//
//				// Add to ColumnList
//				columnList.add("input->" + colName);
//				Column column = new Column(null, colName, null, null, EntityType.INTYPE);
//				inColumnNodes.add(column);
//				Relation relation = new Relation(column, null);
//				relations.add(relation);
//			}
//
//			else if (nodeSelExpr.getChild(0).getChild(i) != null && nodeSelExpr.getChild(0).getChild(i).getChildCount() == 2) {
//				// go through the children nodes
//				if (nodeSelExpr.getChild(0).getChild(i).getChild(1) != null && nodeSelExpr.getChild(0).getChild(i).getChild(1).getType() == HiveParser.TOK_TABLE_OR_COL) {
//					String colNameAndAlias = nodeSelExpr.getChild(0).getChild(i).getChild(1).toString() + "." + nodeSelExpr.getChild(0).getChild(i).getChild(0).toString();
//					System.out.println("input alias.column for function = " + colNameAndAlias);
//					columnList.add("inputcol(infunction)->" + colNameAndAlias);
//					inColumnList.add(colNameAndAlias);
//					functionString += ";inputcolumn->" + colNameAndAlias;
//					columnList.add("input->" + colNameAndAlias);
//					Column column = new Column(null, colName, null, null, EntityType.INTYPE);
//					inColumnNodes.add(column);
//					Relation relation = new Relation(column, null);
//					relations.add(relation);
//				}
//			}
//		}
//
//		return inColumnList;
	}

	private void handleTokTab(ASTNode nextToDestinationNode) {
		ASTNode node = null;
		// handle tok_tabname
		if (nextToDestinationNode.getChild(0).getType() == HiveParser.TOK_TABNAME)
			node = (ASTNode) (nextToDestinationNode.getChild(0));
		else
			node  = nextToDestinationNode;
//		String table_name = (nextToDestinationNode.getChildCount() == 1) ?
//				getUnescapedName((ASTNode) nextToDestinationNode.getChild(0)) :
//				getUnescapedName((ASTNode) nextToDestinationNode.getChild(0)) +
//						"." + getUnescapedName((ASTNode) nextToDestinationNode.getChild(1));
		String table_name = (node.getChildCount() == 1) ?
				getUnescapedName((ASTNode) node.getChild(0)) : getUnescapedName((ASTNode) node.getChild(1));
		outputTableList.add(table_name);
		System.out.println("Output Table name in handleTokTab = " + table_name);
		Table table = new Table(table_name, defaultHiveDbName, null, EntityType.OUTTYPE);
		outTableNodes.add(table);
	}

	private void handleTokDir(ASTNode nextToDestinationNode, ASTNode queryNode) {
//		System.out.println(((ASTNode) queryNode.getParent()).dump());
//		ASTNode outputTempTableNode = (ASTNode) queryNode.getParent().getChild(1);
//		outputTableList.add(BaseSemanticAnalyzer.getUnescapedName(outputTempTableNode));
		for (int i=0; i<queryNode.getParent().getChildCount(); i++) {
			ASTNode outputTempTableNode = (ASTNode)queryNode.getParent().getChild(i);
			if (outputTempTableNode.getType() == HiveParser.TOK_TABNAME) {
				outputTableList.add(getUnescapedName((ASTNode) outputTempTableNode.getChild(0)));
				System.out.println("Output Table name in handleTokDir = " + getUnescapedName((ASTNode) outputTempTableNode.getChild(0)));
				Table table = new Table(getUnescapedName((ASTNode) outputTempTableNode.getChild(0)), defaultHiveDbName, null, EntityType.OUTTYPE);
				outTableNodes.add(table);

			} else if (((ASTNode)queryNode.getParent()).getType() == HiveParser.TOK_SUBQUERY && i==1) {
				String temp_table_name = getUnescapedName((ASTNode)queryNode.getParent().getChild(i));
				System.out.println("Temp output Table name found = " + temp_table_name);
				Table table = new Table(temp_table_name, defaultHiveDbName, null, EntityType.OUTTYPE, false);        // temp tanle
				outTableNodes.add(table);
			}
		}
	}

//	private void handleTokDir(ASTNode nextToDestinationNode, ASTNode queryNode) {
//		for (int i=0; i<queryNode.getParent().getChildCount(); i++) {
//			ASTNode outputTempTableNode = (ASTNode) queryNode.getParent().getChild(i);
//			if (outputTempTableNode.getType() == HiveParser.TOK_TABNAME) {
//				outputTableList.add(BaseSemanticAnalyzer.getUnescapedName((ASTNode)outputTempTableNode.getChild(0)));
//
//				Table table = new Table(((ASTNode)outputTempTableNode.getChild(0)).toString(), null, null, EntityType.OUTTYPE);
//				outTableNodes.add(table);
//			}
//		}
//	}

	private void handleTokJoin(ASTNode nextNodeOfFrom) {
		// TODO handle multiple input tables without alias
		List<org.apache.hadoop.hive.ql.lib.Node> children = nextNodeOfFrom.getChildren();
		for (org.apache.hadoop.hive.ql.lib.Node child : children) {
			ASTNode childAST = (ASTNode) child;
			if (childAST.getType() == HiveParser.TOK_TABREF) {
				handleTokTabRef(childAST);

			} else if (childAST.getType() == HiveParser.TOK_JOIN
					|| childAST.getType() == HiveParser.TOK_LEFTOUTERJOIN
					|| childAST.getType() == HiveParser.TOK_RIGHTOUTERJOIN
					|| childAST.getType() == HiveParser.TOK_FULLOUTERJOIN
					|| childAST.getType() == HiveParser.TOK_LEFTSEMIJOIN
					|| childAST.getType() == HiveParser.TOK_UNIQUEJOIN) {
				handleTokJoin(childAST);

			} else {
				System.out.println(childAST.toString() + " ::: " + childAST.getType());
			}
		}
	}

	private void handleTokTabRef(ASTNode nextNodeOfFrom) {
		ASTNode inputTableNodeInTR = (ASTNode) nextNodeOfFrom.getChild(0);
//		String table_name = (inputTableNodeInTR.getChildCount() == 1) ?
//				getUnescapedName((ASTNode) inputTableNodeInTR.getChild(0)) :
//				getUnescapedName((ASTNode) inputTableNodeInTR.getChild(0)) + "." + inputTableNodeInTR.getChild(1);
		String table_name = (inputTableNodeInTR.getChildCount() == 1) ?
				getUnescapedName((ASTNode) inputTableNodeInTR.getChild(0)) : getUnescapedName((ASTNode) inputTableNodeInTR.getChild(1)) ;
		String table_alias = null;
		if (nextNodeOfFrom.getChild(1) != null)
			table_alias = getUnescapedName((ASTNode) nextNodeOfFrom.getChild(1));

		if (table_alias != null)
			inputTableList.add(table_name + "->" + table_alias);
		else
			inputTableList.add(table_name);

		Table table = new Table(table_name, defaultHiveDbName, table_alias, EntityType.INTYPE);
		inTableNodes.addToList(table);
	}

	private void handleTokSubQuery(ASTNode nextNodeOfFrom) {
		ASTNode inputTableNodeInSQ = (ASTNode) nextNodeOfFrom.getChild(1);
		inputTableList.addToList(getUnescapedName(inputTableNodeInSQ));
		Table table = new Table(getUnescapedName(inputTableNodeInSQ), defaultHiveDbName, null, EntityType.INTYPE);
		inTableNodes.addToList(table);
	}

//	private int nextId() {
//		return ++nextId;
//	}

	/*private void printDot(Tree ct) {
//		System.out.println("Top tree = " + ct);
		DOTTreeGenerator gen = new DOTTreeGenerator();
		StringTemplate st = gen.toDOT(ct);
		System.out.println(st);
		try {
			File dotFile = new File("sqlDot.dot");
			BufferedWriter bw = new BufferedWriter(new FileWriter(dotFile));
			bw.write(st.toString());
			Runtime.getRuntime().exec("dot -Tsvg sqlDot.dot -o sqlGraph.svg");
			bw.close();
		} catch (Exception e) {
			LOGGER.error("Error in writing Dot visualization", e);
		}
		System.out.println();
	}*/

	public void generateLineageDot(String relationDotString, LineageNode tableNode, String targetNode) {
		// generate Dot from all tables, columns and functions
		StringBuilder first = new StringBuilder("digraph g {\n" +
				"graph [\n" +
				"rankdir = \"LR\"\n" +
				"];\n" );
		first.append("node [\n" +
				"fontsize = \"16\"\n" +
				"shape = \"ellipse\"\n" +
				"];\n" +
				"\n" +
				"edge [\n" +
				"color=\"blue\"\n" +
				"];\n");

		StringBuilder middle = new StringBuilder();         // all nodes definitions
		// input tables to override output tables if duplicates are found
		/*for (Node node : finalOutTableNodes) {
			middle.append("\n" + node.toDotString());
		}
		for (Node node : finalInTableNodes) {
			middle.append("\n" + node.toDotString());
		}
		for (Node node : finalFunctions) {
			middle.append("\n" + node.toDotString());
		}
		for (Node node : finalConstants) {
			middle.append("\n" + node.toDotString());
		}*/
		middle.append("\n" + tableNode.getDotString());
		middle.append("\n");
		LOGGER.info("node id------"+tableNode.getNodeId());
		middle.append("\n" + targetNode);
		middle.append("\n");

		StringBuilder last = new StringBuilder();           // relations
		/*for (Relation relation : finalRelations) {
			if(relation.getSource() instanceof Column && relation.getDestination() instanceof Column) {
			if (!(((Column) relation.getSource()).getTable().getTableName().equals(((Column) relation.getDestination()).getTable().getTableName()) && ((Column) relation.getSource()).getColumnName().equals(((Column) relation.getDestination()).getColumnName()))){
				last.append("\n" + relation.toDotString());}
			}
			else
				last.append("\n" + relation.toDotString());
		}*/
		last.append("\n" + relationDotString);
		last.append("\n}");

		dotString = first.append(middle).append(last).toString();
		System.out.println("printing dot string");
		System.out.println("\n" + dotString + "\n");
		//printLineageDot(dotString);
	}

	private void printLineageDot(String dotString) {
		try {
			File dotFile = new File("lineageDot.dot");
			BufferedWriter bw = new BufferedWriter(new FileWriter(dotFile));
			bw.write(dotString);
			Runtime.getRuntime().exec("dot -Tsvg lineageDot.dot -o lineageGraph.svg");
			bw.close();
		} catch (Exception e) {
			LOGGER.error("Error in writing Dot visualization", e);
		}
	}


//		private void generateEntities() {
//		for (String tableName : inputTableList) {
//			System.out.println("generateEntities: inputTableName = " + tableName);
//			String[] split = tableName.split("->");
//			Table table = new Table(split[0], null, (split.length==2) ? split[1] : null, EntityType.INTYPE);
//			nodes.add(table);
//		}
//
//		for (String tableName : outputTableList) {
//			System.out.println("generateEntities: outputTableName = " + tableName);
//			String[] split = tableName.split("->");
//			Table table = new Table(split[0], null, (split.length==2) ? split[1] : null, EntityType.INTYPE);
//			nodes.add(table);
//		}
//
//		for (String functionName : functionList) {
//			Function function = null;
//			System.out.println("generateEntities: functionName = " + functionName);
//			String[] split = functionName.split(";");
//			if (split.length == 1)
//				function = new Function(functionName, null, null);
//			else {
//				String outputalias = null;
//				Set<String> inputcolumns = new LinkedHashSet<String>();
//				String name = split[0];
//				for (int i = 1; i < split.length; i++) {
//					String[] split2 = split[i].split("->");
//					if ("outputalias".equalsIgnoreCase(split2[0]))
//						outputalias = split2[1];
//					if ("inputcolumn".equalsIgnoreCase(split2[0]))
//						inputcolumns.add(split2[1]);
//				}
//				function = new Function(functionName, inputcolumns, outputalias);
//			}
//			nodes.add(function);
//		}
//
//		for (String columnName : columnList) {
//			Column column = null;
//			System.out.println("generateEntities: columnName = " + columnName);
//			String[] split = columnName.split("->");
//			if (split.length == 3 && split[0].equalsIgnoreCase("input")) {
//				for (Node node : nodes) {
//					if (node instanceof Table && split[2].equalsIgnoreCase(((Table) node).getAlias())) {
//						LOGGER.info("Found table for column : " + ((Table)node).getTableName() + " : " + split[1]);
////						column = ((Table)node).new Column(null, split[1], null);
//						column = new Column((Table)node, split[1], null, split[2], EntityType.INTYPE);
////						((Table)node).addColumn(column);
//						nodes.add(column);
//					}
//				}
//
//			} else if (split.length == 2 && split[0].equalsIgnoreCase("input") && split[1].equalsIgnoreCase("*")) {
//				// all input columns
//
//			} else if (split[0].equalsIgnoreCase("input")) {
//				for (String tableName : inputTableList) {       // loop not needed as there is only one input table in this case
//					String[] split2 = tableName.split("->");
//					for (Node node : nodes) {
//						if (node instanceof Table && ((Table) node).getTableName().equalsIgnoreCase(split2[0])) {
//							LOGGER.info("Found table for column : " + ((Table)node).getTableName() + " : " + split[1]);
//							column = new Column((Table)node, split[1], null, null, EntityType.INTYPE);
////							((Table)node).addColumn(column);
//							nodes.add(column);
//						}
//					}
//				}
//
//			} else if (split[0].equalsIgnoreCase("inputcol(infunction)")) {
//				for (String tableName : inputTableList) {
//					String[] split2 = tableName.split("->");
//					for (Node node : nodes) {
//						if (node instanceof Table && ((Table) node).getTableName().equalsIgnoreCase(split2[0])) {
//							LOGGER.info("Found table for column : " + ((Table)node).getTableName() + " : " + split[1]);
//							column = new Column((Table)node, split[1], null, null, EntityType.INTYPE);
////							((Table)node).addColumn(column);
//							nodes.add(column);
//						}
//					}
//				}
//			}
//		}
//	}

	/**
	 * Parse the input command and generate a ASTNode tree.
	 */
//	public static ParseContext generateOperatorTree(HiveConf conf, String command) throws SemanticException {
//		Context ctx;
//		ParseContext subPCtx=null;
//		try {
//			ctx=new Context(conf);
//			ParseDriver pd=new ParseDriver();
//			ASTNode tree=pd.parse(command,ctx);
//			tree=ParseUtils.findRootNonNullToken(tree);
//			BaseSemanticAnalyzer sem=SemanticAnalyzerFactory.get(conf,tree);
//			assert(sem instanceof SemanticAnalyzer);
//			doSemanticAnalysis((SemanticAnalyzer)sem,tree,ctx);
//			subPCtx=((SemanticAnalyzer)sem).getParseContext();
//			LOGGER.info("Sub-query Semantic Analysis Completed");
//			transform(subPCtx);         //* later
//
//		} catch (Exception e) {
//			LOGGER.error("IOException in generating the operator " + "tree for input command - " + command + " ",e);
//			LOGGER.error(org.apache.hadoop.util.StringUtils.stringifyException(e));
//			throw new SemanticException(e.getMessage(),e);
//		}
//		return subPCtx;
//	}
//
//	/**
//	 * For the input ASTNode tree, perform a semantic analysis and check metadata
//	 * Generate a operator tree and return the ParseContext instance for the operator tree.
//	 */
//	private static void doSemanticAnalysis(SemanticAnalyzer sem,
//	                                       ASTNode ast, Context ctx) throws SemanticException {
//		QB qb = new QB(null, null, false);
//		ASTNode child = ast;
//		ParseContext subPCtx = ((SemanticAnalyzer) sem).getParseContext();
//		subPCtx.setContext(ctx);
//		((SemanticAnalyzer) sem).initParseCtx(subPCtx);
//
//		LOGGER.info("Starting Sub-query Semantic Analysis");
//		sem.doPhase1(child, qb, sem.initPhase1Ctx());
//		LOGGER.info("Completed phase 1 of Sub-query Semantic Analysis");
//
//		sem.getMetaData(qb);
//		LOGGER.info("Completed getting MetaData in Sub-query Semantic Analysis");
//
//		LOGGER.info("Sub-query Abstract syntax tree: " + ast.toStringTree());
//		sem.genPlan(qb);
//		LOGGER.info("Sub-query Completed plan generation");
//	}
//
//	public static ParseContext transform(ParseContext pctx) throws SemanticException {
//		// Create the lineage context
//		LineageCtx lCtx = new LineageCtx(pctx);
//
//		Map<Rule, NodeProcessor> opRules = new LinkedHashMap<Rule, NodeProcessor>();
//		opRules.put(new RuleRegExp("R1", TableScanOperator.getOperatorName() + "%"),
//				OpProcFactory.getTSProc());
//		opRules.put(new RuleRegExp("R2", ScriptOperator.getOperatorName() + "%"),
//				OpProcFactory.getTransformProc());
//		opRules.put(new RuleRegExp("R3", UDTFOperator.getOperatorName() + "%"),
//				OpProcFactory.getTransformProc());
//		opRules.put(new RuleRegExp("R4", SelectOperator.getOperatorName() + "%"),
//				OpProcFactory.getSelProc());
//		opRules.put(new RuleRegExp("R5", GroupByOperator.getOperatorName() + "%"),
//				OpProcFactory.getGroupByProc());
//		opRules.put(new RuleRegExp("R6", UnionOperator.getOperatorName() + "%"),
//				OpProcFactory.getUnionProc());
//		opRules.put(new RuleRegExp("R7",
//						CommonJoinOperator.getOperatorName() + "%|" + MapJoinOperator.getOperatorName() + "%"),
//				OpProcFactory.getJoinProc());
//		opRules.put(new RuleRegExp("R8", ReduceSinkOperator.getOperatorName() + "%"),
//				OpProcFactory.getReduceSinkProc());
//		opRules.put(new RuleRegExp("R9", LateralViewJoinOperator.getOperatorName() + "%"),
//				OpProcFactory.getLateralViewJoinProc());
//		opRules.put(new RuleRegExp("R10", PTFOperator.getOperatorName() + "%"),
//				OpProcFactory.getTransformProc());
//
//		// The dispatcher fires the processor corresponding to the closest matching rule and passes the context along
//		Dispatcher disp = new DefaultRuleDispatcher(OpProcFactory.getDefaultProc(), opRules, lCtx);
//		GraphWalker ogw = new PreOrderWalker(disp);
//
//		// Create a list of topop nodes
//		ArrayList<org.apache.hadoop.hive.ql.lib.Node> topNodes = new ArrayList<org.apache.hadoop.hive.ql.lib.Node>();
//		topNodes.addAll(pctx.getTopOps().values());
//		ogw.startWalking(topNodes, null);
//
//		// Transfer the index from the lineage context to the session state.
//		if (SessionState.get() != null) {
//			SessionState.get().getLineageState().setIndex(lCtx.getIndex());
//		}
//
//		Set<Map.Entry<LineageInfo.DependencyKey, LineageInfo.Dependency>> set = lCtx.getParseCtx().getLineageInfo().entrySet();
//		for (Map.Entry<LineageInfo.DependencyKey, LineageInfo.Dependency> item : set) {
//			System.out.println("item = " + item.getKey() + " ::: " + item.getValue());
//		}
//		return pctx;
//	}

	private void populateBeansAndPersist() {
		persistenceUnit = new PersistenceUnit();
		persistenceUnit.populateBeans(finalInTableNodes, finalOutTableNodes, finalFunctions, finalConstants, finalRelations, query, processId, instanceId);
		persistenceUnit.persist();
	}

//	private void generateLineageDotFromDB() {
//		// populate latest generated objects for this processId from DB
////		persistenceUnit.populateBeansFromDB(processId);
//
//		// generate Dot from all tables, columns and functions
//		String first = "digraph g {\n" +
//				"graph [\n" +
//				"rankdir = \"LR\"\n" +
//				"];\n";
//		first += "node [\n" +
//				"fontsize = \"16\"\n" +
//				"shape = \"ellipse\"\n" +
//				"];\n" +
//				"\n" +
//				"edge [\n" +
//				"color=\"blue\"\n" +
//				"];\n";
//
//		String middle = "";         // all nodes definitions
//		for (LineageNode node : persistenceUnit.getLineageTableNodes()) {
//			middle += "\n" + node.getDotString();
//		}
//		for (LineageNode node : persistenceUnit.getLineageFunctionNodes()) {
//			middle += "\n" + node.getDotString();
//		}
//		middle += "\n";
//
//		String last = "";           // relations
//		for (LineageRelation relation : persistenceUnit.getLineageRelations()) {
//			last += "\n" + relation.getDotString();
//		}
//		last += "\n}";
//
//		dotString = first + middle + last;
//		System.out.println("\n" + dotString + "\n");
//
//		try {
//			File dotFile = new File("data-lineage/lineageDot.dot");
//			BufferedWriter bw = new BufferedWriter(new FileWriter(dotFile));
//			bw.write(dotString);
//			Process process = Runtime.getRuntime().exec("dot -Tsvg data-lineage/lineageDot.dot -o data-lineage/lineageGraph.svg");
//			bw.close();
//		} catch (Exception e) {
//			LOGGER.error("Error in writing Dot visualization", e);
//		}
//	}

}