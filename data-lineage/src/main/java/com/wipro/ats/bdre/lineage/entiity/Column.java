package com.wipro.ats.bdre.lineage.entiity;

import com.wipro.ats.bdre.lineage.type.EntityType;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by jayabroto on 07-05-2015.
 */
public class Column extends Node {
	private static final Logger LOGGER = Logger.getLogger(Column.class);
//	private static int counter = 0;

	private String columnName;
	private String columnType;
	private Boolean isUsedInQuery;
	private Table table;
	private String alias;
	private EntityType entityType;
	private Integer ordinalPosition;

	public Column(Table table, String columnName, String columnType, String alias,
	              EntityType entityType, Integer ordinalPosition, Boolean isUsedInQuery) {
		super(columnName, "Column", "record", columnName);
//		++counter;
//		System.out.println("Column Id generated = " + getId());
		this.columnName = columnName.toUpperCase();
		this.columnType = columnType;
		this.alias = alias;
		this.entityType = entityType;
		this.ordinalPosition = ordinalPosition;
		this.isUsedInQuery = isUsedInQuery;
		setTable(table);
	}

	public String getColumnName() {return columnName;}
	public void setColumnName(String columnName) {this.columnName = columnName.toUpperCase();}
	public String getColumnType() {return columnType;}
	public void setColumnType(String columnType) {this.columnType = columnType;}
	public Boolean isUsedInQuery() {return isUsedInQuery;}
	public void setUsedInQuery(Boolean isUsedInQuery) {this.isUsedInQuery = isUsedInQuery;}
	public String getAlias() {return alias;}
	public void setAlias(String alias) {this.alias = alias;}
	public EntityType getEntityType() {return entityType;}
	public void setEntityType(EntityType entityType) {this.entityType = entityType;}
	public Integer getOrdinalPosition() {return ordinalPosition;}
	public void setOrdinalPosition(Integer ordinalPosition) {this.ordinalPosition = ordinalPosition;}

	public Table getTable() {return table;}
	public void setTable(Table table) {
		this.table = table;
		if (table != null)
			table.addColumn(this);
	}

	@Override
	public String toDotString() {           // used for call from parent Table
			return "<" + getColumnName() + ">" + getColumnName();
//			"node1":f1
//		String value = "\"" + table.getDataBase() + "." + table.getTableName() + "\":f" + getId();
//		return value;
	}

	public String toDotEdge() {             // used for Relation generation
//			return "<f" + getId() + "> " + getColumnName();
//			"node1":f1
		String value = "\"" + table.getDataBase() + "." + table.getTableName() + "\":" + getColumnName();
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		//if (!super.equals(o)) return false;
		Column column = (Column) o;
		if (columnName != null ? !columnName.equals(column.columnName) : column.columnName != null) return false;
//		if both the aliases are non null then they must be equal
 		if (table != null ? !table.equals(column.table) : column.table != null) return false;
//		if(this.getLabel()!=column.getLabel()) return false;
		if(alias!=null && column.alias!=null){
				return alias.equals(column.alias);
			}
// 		if one of the aliases is null then they are equal. Occurs when a column is present both as a column and inside a function
	 	if (alias==null || column.alias==null) { return true; }
		 return false;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (columnName != null ? columnName.hashCode() : 0);
		result = 31 * result + (table != null ? table.hashCode() : 0);
		return result;
	}

	public static void updateInputColumns(List<Table> inTableNodes, List<Table> outTableNodes, List<Column> inColumnNodes,
	                          List<Column> outColumnNodes, List<Function> functions, List<Constant> constants, List<Relation> relations) {

		// associate columns with respective tables
		for (int i=0; i<inColumnNodes.size(); i++) {
			Column inColumn = inColumnNodes.get(i);
			if (inColumn.getTable() == null) {
				String colAlias = inColumn.getAlias();
//				System.out.println("colAlias = " + colAlias);
				if (colAlias == null || inTableNodes.size() == 1) {         // only one input table
					inColumn.setTable(inTableNodes.get(0));

				} else {                                                    // multiple input tables, matched by alias
					for (int j=0; j<inTableNodes.size(); j++) {
						Table table = inTableNodes.get(j);
						String tableAlias = table.getAlias();
						if (colAlias.equalsIgnoreCase(tableAlias)
								|| colAlias.equalsIgnoreCase(table.getTableName())) {
//							System.out.println("Match found = " + colAlias + " ::: " + table.getTableName());
							inColumn.setTable(table);
						}
					}
					if (inColumn.getTable() == null)
						LOGGER.error("Error: Column association is still null");
				}
			}

		}
	}

	@Override
	public String toString() {
		return "Column{" +
				"columnName='" + columnName + '\'' +
				", columnType='" + columnType + '\'' +
				", isUsedInQuery=" + isUsedInQuery +
				", table=" + table +
				", alias='" + alias + '\'' +
				", entityType=" + entityType +
				'}';
	}
}