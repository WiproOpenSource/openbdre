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
import com.wipro.ats.bdre.lineage.type.EntityType;
import com.wipro.ats.bdre.lineage.type.UniqueList;
import com.wipro.ats.bdre.md.dao.jpa.LineageQuery;
import org.apache.hadoop.hive.ql.lib.*;
import org.apache.hadoop.hive.ql.parse.*;
import org.apache.log4j.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	private String defaultHiveDbName;
	private LineageQuery lineageQuery;
	private int subquerySeq = 0;
	private PersistenceUnit persistenceUnit;

	public static void lineageMain(LineageQuery lineageQuery,String args1, String args2, String args3) throws ParseException, SemanticException {
		LineageMain lineageMain = new LineageMain();
		lineageMain.defaultHiveDbName = args1;
		lineageMain.query = lineageQuery.getQueryString();
		lineageMain.lineageQuery = lineageQuery;

		lineageMain.getLineageInfo();

		// populate beans and persist in Database
		lineageMain.populateBeansAndPersist();

		LOGGER.info("End of program");
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


	/**
	 * Implements the process method for the NodeProcessor interface.
	 */
	@Override
	public Object process(org.apache.hadoop.hive.ql.lib.Node nd, Stack<org.apache.hadoop.hive.ql.lib.Node> stack, NodeProcessorCtx procCtx,
	                      Object... nodeOutputs) throws SemanticException {
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
				LOGGER.info("\nQuery " + ++subquerySeq + " parsed");
				getTableLineage(pt);

				for (String inputTable : inputTableList) {
					LOGGER.info("inputTable = " + inputTable);
				}

				//adding to outputTableList and outputNodes
				if (outputTableList.isEmpty()) {
					String outputTable = "TEMP_" + UUID.randomUUID();
					outputTableList.add(outputTable);
					if (outTableNodes.isEmpty()) {
						Table table = new Table(outputTable, defaultHiveDbName, null, EntityType.OUTTYPE, false);
						outTableNodes.add(table);
						LOGGER.info("Output temp Table added : " + table.getTableName());
					}
				}

				for (String outputTable : outputTableList) {
					if (outputTable == null || outputTable.equalsIgnoreCase("<EOF>")) {
						if (outputTableList.remove(null) || outputTableList.remove("<EOF>")) {
							outputTable = "TEMP_" + UUID.randomUUID();
							outputTableList.add(outputTable);
						}
						if (outTableNodes.isEmpty()) {
							Table table = new Table(outputTable, defaultHiveDbName, null, EntityType.OUTTYPE, false);
							outTableNodes.add(table);
							LOGGER.info("Output temp Table added : " + table.getTableName());
						}
					}
					LOGGER.info("outputTable = " + outputTable);
				}
				// Update properties of Columns and Edges after processing all nodes in a query
				// populate output table columns
				Table.populateOutputTableColumns(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, constants,relations);
				Column.updateInputColumns(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, constants, relations);
				Function.updateFunctions(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, constants, relations);
				Constant.updateConstants(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, constants, relations);
				Relation.updateRelations(inTableNodes, outTableNodes, inColumnNodes, outColumnNodes, functions, constants, relations);
				break;
			default:
				LOGGER.info("Default clause selected: error in the switch case value given for HiveParser.TOK_QUERY");
				break;
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

		LOGGER.info("nextNodeOfFrom to process : " + nextNodeOfFrom.toString());
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

		LOGGER.info("nextToDestinationNode to process : " + nextToDestinationNode.toString());
		if (nextToDestinationNode.getType() == HiveParser.TOK_DIR)
			handleTokDir(queryNode);
		else if (nextToDestinationNode.getType() == HiveParser.TOK_TAB)
			handleTokTab(nextToDestinationNode);

		LOGGER.info("selectNode to process : " + selectNode.toString());
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
		String tableAlias = null;
		String label = null;
		String constant = null;
		String constant_alias= null;
		if (nodeSelExpr.getChild(0).getChild(0) != null
				&& nodeSelExpr.getChild(0).getChild(0).getType() == HiveParser.TOK_TABLE_OR_COL) {
			tableAlias = nodeSelExpr.getChild(0).getChild(0).getChild(0).toString();
			 {
			col = colName = nodeSelExpr.getChild(0).getChild(1).toString();
			col = col + "->" + tableAlias;
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
			LOGGER.info("nodeSelExpr = " + nodeSelExpr.getChild(0).getText());
			Double.parseDouble(nodeSelExpr.getChild(0).toString());
			constant = nodeSelExpr.getChild(0).toString();
			constant_alias = nodeSelExpr.getChild(1).toString();
			Constant conNumeric = new Constant("\'"+constant+"\'",constant_alias);
			constants.add(conNumeric);
			Relation relation = new Relation(conNumeric, null);
			relations.add(relation);
		} catch (NumberFormatException e) {
			LOGGER.info("Unrecognizable column type");
		}
		}
		else
			col = colName = nodeSelExpr.getChild(0).getChild(0).toString();
if(col != null && colName != null) {
	LOGGER.info("Input column = " + col);
	columnList.addToList("input->" + col);
	Column column = new Column(null, colName, null, tableAlias, EntityType.INTYPE, null, true);
	if (label != null)
		column.setLabel(label);
	column = inColumnNodes.addToList(column);
	Relation relation = new Relation(column, null);
	relations.add(relation);
}
	}

	private void handleTokAllColRef(ASTNode nodeSelExpr) {
		LOGGER.info("Input column = *");

		// for doing select *
		Table table = null;

		if (nodeSelExpr.getChildCount() == 1 && nodeSelExpr.getChild(0).getType() == HiveParser.TOK_TABNAME
				&& nodeSelExpr.getChild(0).getChildCount() == 1) {
			String alias = nodeSelExpr.getChild(0).getChild(0).toString();
			if (findInputTableNameByAlias(alias) != null) {             // if its an alias
				table = findInputTableNameByAlias(alias);
			}
		} else if (nodeSelExpr.getChildCount() == 0) {                      // no table specified means default table
			table = inTableNodes.get(0);
		}
		LOGGER.info("Table in * = " + table.getTableName());

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
		LOGGER.info("Function = " + functionString);

		if (nodeSelExpr.getChild(1) != null) {
			LOGGER.info("Function output alias = " + nodeSelExpr.getChild(1));
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

				LOGGER.info("Input column(infunction) = " + colName);
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

	}

	private void handleTokTab(ASTNode nextToDestinationNode) {
		ASTNode node = null;
		// handle tok_tabname
		if (nextToDestinationNode.getChild(0).getType() == HiveParser.TOK_TABNAME)
			node = (ASTNode) (nextToDestinationNode.getChild(0));
		else
			node  = nextToDestinationNode;
		String tableName = (node.getChildCount() == 1) ?
				getUnescapedName((ASTNode) node.getChild(0)) : getUnescapedName((ASTNode) node.getChild(1));
		outputTableList.add(tableName);
		LOGGER.info("Output Table name in handleTokTab = " + tableName);
		Table table = new Table(tableName, defaultHiveDbName, null, EntityType.OUTTYPE);
		outTableNodes.add(table);
	}

	private void handleTokDir(ASTNode queryNode) {
		for (int i=0; i<queryNode.getParent().getChildCount(); i++) {
			ASTNode outputTempTableNode = (ASTNode)queryNode.getParent().getChild(i);
			if (outputTempTableNode.getType() == HiveParser.TOK_TABNAME) {
				outputTableList.add(getUnescapedName((ASTNode) outputTempTableNode.getChild(0)));
				LOGGER.info("Output Table name in handleTokDir = " + getUnescapedName((ASTNode) outputTempTableNode.getChild(0)));
				Table table = new Table(getUnescapedName((ASTNode) outputTempTableNode.getChild(0)), defaultHiveDbName, null, EntityType.OUTTYPE);
				outTableNodes.add(table);

			} else if (((ASTNode)queryNode.getParent()).getType() == HiveParser.TOK_SUBQUERY && i==1) {
				String tempTableName = getUnescapedName((ASTNode)queryNode.getParent().getChild(i));
				LOGGER.info("Temp output Table name found = " + tempTableName);
				Table table = new Table(tempTableName, defaultHiveDbName, null, EntityType.OUTTYPE, false);        // temp tanle
				outTableNodes.add(table);
			}
		}
	}


	private void handleTokJoin(ASTNode nextNodeOfFrom) {
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
				LOGGER.info(childAST.toString() + " ::: " + childAST.getType());
			}
		}
	}

	private void handleTokTabRef(ASTNode nextNodeOfFrom) {
		ASTNode inputTableNodeInTR = (ASTNode) nextNodeOfFrom.getChild(0);
		String tableName = (inputTableNodeInTR.getChildCount() == 1) ?
				getUnescapedName((ASTNode) inputTableNodeInTR.getChild(0)) : getUnescapedName((ASTNode) inputTableNodeInTR.getChild(1)) ;
		String tableAlias = null;
		if (nextNodeOfFrom.getChild(1) != null)
			tableAlias = getUnescapedName((ASTNode) nextNodeOfFrom.getChild(1));

		if (tableAlias != null)
			inputTableList.add(tableName + "->" + tableAlias);
		else
			inputTableList.add(tableName);

		Table table = new Table(tableName, defaultHiveDbName, tableAlias, EntityType.INTYPE);
		inTableNodes.addToList(table);
	}

	private void handleTokSubQuery(ASTNode nextNodeOfFrom) {
		ASTNode inputTableNodeInSQ = (ASTNode) nextNodeOfFrom.getChild(1);
		inputTableList.addToList(getUnescapedName(inputTableNodeInSQ));
		Table table = new Table(getUnescapedName(inputTableNodeInSQ), defaultHiveDbName, null, EntityType.INTYPE);
		inTableNodes.addToList(table);
	}


	public String generateLineageDot(String relationDotString, String tableNode, String targetNode) {
		// generate Dot from all tables, columns and functions
		StringBuilder first = new StringBuilder("digraph g {\n" +
				"graph [\n" +
				"rankdir = \"LR\"\n" +
				"];\n");
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
		middle.append("\n" + tableNode);
		middle.append("\n");
		if(targetNode != null) {
			middle.append("\n" + targetNode);
			middle.append("\n");
		}
		StringBuilder last = new StringBuilder();           // relations

		if (relationDotString != null) {
			last.append("\n" + relationDotString);
			last.append("\n}");
		} else {
			last.append("\n}");
		}

		dotString = first.append(middle).append(last).toString();
		LOGGER.info("printing dot string");
		LOGGER.info("\n" + dotString + "\n");
		return dotString;
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

	private void populateBeansAndPersist() {
		persistenceUnit = new PersistenceUnit();
		persistenceUnit.populateBeans(finalInTableNodes, finalOutTableNodes, finalFunctions, finalConstants, finalRelations, lineageQuery);
		persistenceUnit.persist();
	}

}