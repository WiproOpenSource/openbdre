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

import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.lineage.LineageConstants;
import com.wipro.ats.bdre.lineage.dataaccess.LineageDao;
import com.wipro.ats.bdre.lineage.type.EntityType;
import com.wipro.ats.bdre.lineage.type.UniqueList;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by jayabroto on 24-04-2015.
 */
public class Table extends Node {
	private static final Logger LOGGER = Logger.getLogger(Table.class);
//	private static int counter = 0;

	private String tableName;
	private String dataBase = LineageConstants.defaultHiveDbName;             // Default database
	private String alias;
	private EntityType entityType;
	private UniqueList<Column> columns = new UniqueList<Column>();
	private List<String> columnAllNames = new UniqueList<String>();
	private boolean isDataBaseTable = true;       // database table or temporary table

	public Table(String tableName, String dataBase, String alias, EntityType entityType, boolean isDatabaseTable) throws ETLException {
		// eliminate "." from table name
		super((tableName.split("\\."))[tableName.split("\\.").length-1], "Table", "record", tableName);
//		++counter;

		this.tableName = getLabel().toUpperCase();
		if (dataBase != null)
			this.dataBase = dataBase.toUpperCase();
		this.alias = alias;
		this.entityType = entityType;
		this.isDataBaseTable = isDatabaseTable;

		if (isDataBaseTable()) {
			// populate column names from hive db
			Map<Integer, String> columnsMap = null;
			columnsMap = LineageDao.getAllColumnsMap(this.dataBase, this.tableName);
			if (columnsMap.size() == 0) {                   // table does not exist or table has no fields
				LOGGER.warn("Error in constructor : Table " + this.tableName + " does not exist or has no fields in Hive DB");
				LOGGER.warn("Table with warning : " + this.toString());
				this.setIsDataBaseTable(false);             // mark as temporary table
			} else {
				for (Map.Entry<Integer, String> entry : columnsMap.entrySet())
					columnAllNames.add(entry.getValue());
			}
		}
	}

	public Table(String tableName, String dataBase, String alias, EntityType entityType) throws ETLException {
		this(tableName, dataBase, alias, entityType, true);
	}

	public String getTableName() {return tableName;}
	public void setTableName(String tableName) { this.tableName = tableName.toUpperCase(); }

	public UniqueList<Column> getColumns() {return columns;}
	public void setColumns(UniqueList<Column> columns) {
		this.columns = columns;
	}
	public void addColumn(Column column) { columns.addToList(column); }

	public String getAlias() {return alias;}
	public void setAlias(String alias) { this.alias = alias; }

	public String getDataBase() {return dataBase;}
	public void setDataBase(String dataBase) {this.dataBase = dataBase.toUpperCase();}

	public EntityType getEntityType() {return entityType;}
	public void setEntityType(EntityType entityType) {this.entityType = entityType;}

	public boolean isDataBaseTable() {return isDataBaseTable;}
	public void setIsDataBaseTable(boolean isDataBaseTable) {this.isDataBaseTable = isDataBaseTable;}

	public List<String> getColumnAllNames() {return columnAllNames;}

	public Column getColumnByName(String colName) {
		for (Column col : columns)
			if (colName.equalsIgnoreCase(col.getColumnName()))
				return col;
		System.out.println("No column " + colName + " found in table " + this.getTableName());
		return null;
	}

	@Override
	public String toDotString() {
//		return "\"" + getNodeType() + getId() + "\" [\n" +
//				"label = \"" + getLabel() + "\"\n" +
//				"shape = \"" + getShape() + "\"\n" +
//				"];";
		String tableFullName = getDataBase() + "." + getTableName();
		LOGGER.info("Table " + getTableName() + " : getColumns().size = " + getColumns().size());

		StringBuilder first = new StringBuilder("\n\"" + tableFullName + "\" [\n" + "label = ");

		String tableHeader = "Table " + tableFullName;
		StringBuilder middle = new StringBuilder("\"<" + tableFullName + ">" + tableHeader + " | ");
//		for (int i = 0; i < getColumns().size(); i++) {
//			Column column = getColumns().get(i);
//			column.setId(i);
//			if (i != getColumns().size()-1)
//				middle += "<f" + (i+1) + ">" + column.getColumnName() + " | ";
//			else
//				middle += "<f" + (i+1) + ">" + column.getColumnName();
//		}
		for (Column column : getColumns()) {
			middle.append(column.toDotString() + " | ");
		}
		middle = new StringBuilder(middle.substring(0, middle.length() - 3));

		String last = "\"";
		if (!this.isDataBaseTable())
			last += "\ncolor=\"gray\"";
		last += "\nshape = \"" + getShape() + "\"\n" + "];";

		return first.append(middle).append(last).toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
	//	if (!super.equals(o)) return false;
		Table table = (Table) o;
		if (tableName != null ? !tableName.equals(table.tableName) : table.tableName != null) return false;
		return !(dataBase != null ? !dataBase.equals(table.dataBase) : table.dataBase != null);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
		result = 31 * result + (dataBase != null ? dataBase.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Table{" +
				"tableName='" + tableName + '\'' +
				", dataBase='" + dataBase + '\'' +
				", alias='" + alias + '\'' +
				", entityType=" + entityType +
				", columns=" + columns +
				", isDataBaseTable=" + isDataBaseTable +
				'}';
	}

	public static void populateOutputTableColumns(UniqueList<Table> inTableNodes, UniqueList<Table> outTableNodes, UniqueList<Column> inColumnNodes, UniqueList<Column> outColumnNodes, List<Function> functions,List<Constant> constants, List<Relation> relations) {
		for (Table outTable: outTableNodes) {
			System.out.println("outTable = " + outTable.getDataBase() + "." + outTable.getTableName());
			if (outTable.isDataBaseTable()) {           // skip Temp tables as no columns in DB
				// populate column names from hive db
				outColumnNodes.addAll(outTable.findColumnsFromHive());
//				Map<Integer, String> columnsMap = null;
//				columnsMap = LineageDao.getAllColumnsMap(outTable.getDataBase(), outTable.getTableName());
//				if (columnsMap.size() == 0) {                   // table does not exist or table has no fields
//					LOGGER.warn("Error : Table " + outTable.getTableName() + " does not exist or has no fields in Hive DB");
//					outTable.setIsDataBaseTable(false);             // mark as temporary table
//				} else {
//					for (Map.Entry<Integer, String> entry : columnsMap.entrySet()) {
//						Column column = new Column(outTable, entry.getValue(), null, null, EntityType.OUTTYPE, entry.getKey(), true);
//						outColumnNodes.add(column);
//					}
//				}
			}
		}
	}
	public static void populateInputTableColumns(UniqueList<Table> inTableNodes, UniqueList<Table> outTableNodes, UniqueList<Column> inColumnNodes, UniqueList<Column> outColumnNodes, List<Function> functions, List<Constant> constants, List<Relation> relations) {
		for (Table inTable: inTableNodes) {
			System.out.println("outTable = " + inTable.getDataBase() + "." + inTable.getTableName());
			if (inTable.isDataBaseTable()) {           // skip Temp tables as no columns in DB
				// populate column names from hive db
				outColumnNodes.addAll(inTable.findColumnsFromHive());
//				Map<Integer, String> columnsMap = null;
//				columnsMap = LineageDao.getAllColumnsMap(outTable.getDataBase(), outTable.getTableName());
//				if (columnsMap.size() == 0) {                   // table does not exist or table has no fields
//					LOGGER.warn("Error : Table " + outTable.getTableName() + " does not exist or has no fields in Hive DB");
//					outTable.setIsDataBaseTable(false);             // mark as temporary table
//				} else {
//					for (Map.Entry<Integer, String> entry : columnsMap.entrySet()) {
//						Column column = new Column(outTable, entry.getValue(), null, null, EntityType.OUTTYPE, entry.getKey(), true);
//						outColumnNodes.add(column);
//					}
//				}
			}
		}
	}
	public UniqueList<Column> findColumnsFromHive() {
		UniqueList<Column> columnList = new UniqueList<Column>();
		Map<Integer, String> columnsMap = null;
		columnsMap = LineageDao.getAllColumnsMap(this.getDataBase(), this.getTableName());
		if (columnsMap.size() == 0) {                   // table does not exist or table has no fields
			LOGGER.warn("Error in findColumnsFromHive : Table " + this.getTableName() + " does not exist or has no fields in Hive DB");
			LOGGER.warn("Table with warning : " + this.toString());
			this.setIsDataBaseTable(false);             // mark as temporary table
		} else {
			for (Map.Entry<Integer, String> entry : columnsMap.entrySet()) {
				Column column = new Column(this, entry.getValue(), null, null, this.getEntityType(), entry.getKey(), true);
				columnList.addToList(column);
			}
		}
		return columnList;
	}

	// update/overwrite all columns of input & output tables after completing all relations building, including order, using columnAllNames
	// re-order input db tables by ordinalPosition
	public static void updateAllTables(List<Table> inTableNodes, List<Table> outTableNodes, List<Column> inColumnNodes, List<Column> outColumnNodes, List<Function> functions, List<Relation> relations, List<Table> finalInTableNodes, List<Table> finalOutTableNodes, List<Function> finalFunctions) {
		// assuming output table has exactly double number of columns before this stage

		/**
		 * For each output table, divide columns by 2
		 * First half of columns is from DB; last hajf is inferred from input name
		 * Objective is to delet the last half and merge it with first half
		 * The merging is sequential; i.e. nth element of last half is merged into nth element of first half
		 * Check for existing edge of deleted node; it is to be added to first half node
		 * Check for duplicate edges; if exists delete one edge
		 */
		if (outTableNodes != null && outTableNodes.size() == 1) {
			Table outTable = outTableNodes.get(0);

			if (outTable.isDataBaseTable()) {                           // process columns of only dbTable
				for (int i = 0; i < outTable.getColumns().size()/2; i++) {
					Column dbColumn = outTable.getColumns().get(i);
					Column iColumn = outTable.getColumns().get(outTable.getColumns().size()/2 + i);

					// if any edge is attached to iColumn, update edge
					for (Relation relation : relations) {
						if (relation.getDestination() != null && relation.getDestination() instanceof Column
								&& ((Column) relation.getDestination()).getColumnName().equalsIgnoreCase(iColumn.getColumnName())) {
							relation.setDestination(dbColumn);
						}
					}
				}

				// clear the last half of columnlist
				outTable.getColumns().subList(outTable.getColumns().size()/2, outTable.getColumns().size()).clear();
			}

		} else {
			LOGGER.error("updateAllTables: output table can't be updated due to invalid output table at this stage: " + outTableNodes);
		}

		for (Table table : inTableNodes) {
			if (table.isDataBaseTable()) {
				for (String columnName : table.getColumnAllNames()) {
					boolean found = false;

					for (Column column : table.getColumns()) {
						if (column.getColumnName().equalsIgnoreCase(columnName)) {
							found = true;
							column.setUsedInQuery(true);                // not needed as isUsed is default
							column.setOrdinalPosition(table.getColumnAllNames().indexOf(columnName)+1);
						}
					}
					if (!found) {                                       // if column is not used in query
						System.out.println("Column " + columnName + " is not used in query");
						Column column = new Column(table, columnName, null, null,
								EntityType.INTYPE, table.getColumnAllNames().indexOf(columnName)+1, false);
						column.setTable(table);
					}
				}
			}

			// sort columns of table by ordinal position
			table.sortColumns();
		}

		// if input table is present in finaloutputtablelist and it is a temp table
	}


	// sort columns included in this table by ordinal position; only for DB tables
	public void sortColumns() {
		Comparator<Column> comparator = null;
		if (this.isDataBaseTable()) {               // for db table
			comparator = new Comparator<Column>() {
				public int compare(Column o1, Column o2) {
					if (o1.getOrdinalPosition() == null)
						return -1;
					else if (o2.getOrdinalPosition() == null)
						return 1;
					else if (o1.getOrdinalPosition() > o2.getOrdinalPosition())
						return 1;
					else
						return -1;
				}
			};

		} else {                                    // for temp table
			comparator = new Comparator<Column>() {
				public int compare(Column o1, Column o2) {
					return o1.getColumnName().compareToIgnoreCase(o2.getColumnName());
				}
			};
		}
		Collections.sort(columns, comparator);
	}

//	public void populateColumnsUsedInQuery(String string) {
//		if (string == null)
//			return;
//		else string = string.trim();
//
//		if (string.equals("*")) {
//			System.out.println("All columns are in use: *");
//			for (Column column : columns) {
//				column.setUsedInQuery(true);
//			}
//		} else {
//			for (Column column : columns) {
//				if (string.equalsIgnoreCase(column.getColumnName())) {
//					System.out.println("Column match found: " + string);
//					column.setUsedInQuery(true);
//				}
//			}
//		}
//	}
}