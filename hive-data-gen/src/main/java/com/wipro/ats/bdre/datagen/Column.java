/*
 * Copyright (c) 2014 Wipro Limited
 * All Rights Reserved
 *
 * This code is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */

package com.wipro.ats.bdre.datagen;

public class Column {

	private String columnName;
	private String columnType;
	private String columnGenId;
	private int columnIndex;
	private String refTable;
	private String refCol;

	public Column(){}

	
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	public int getColumnIndex() {
		return columnIndex;
	}
	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public String getColumnGenId() {
		return columnGenId;
	}

	public void setColumnGenId(String columnGenId) {
		this.columnGenId = columnGenId;
	}

	public String getRefTable() {
		return refTable;
	}

	public void setRefTable(String refTable) {
		this.refTable = refTable;
	}

	public String getRefCol() {
		return refCol;
	}

	public void setRefCol(String refCol) {
		this.refCol = refCol;
	}
}
