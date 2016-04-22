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

package com.wipro.ats.bdre.lineage.dataaccess;

import com.wipro.ats.bdre.IMConfig;
import com.wipro.ats.bdre.im.IMConstant;
import com.wipro.ats.bdre.im.etl.api.exception.ETLException;
import com.wipro.ats.bdre.lineage.LineageConstants;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by jayabroto on 24-04-2015.
 */
public class LineageDao {
//	private static String jdbcUrl = "jdbc:hive2://127.0.0.1:10000/";
//	private static String thriftUrl = "thrift://127.0.0.1:9083";

	private static final Logger LOGGER = Logger.getLogger(LineageDao.class);
	private static Connection connection;
//	private static HiveMetaStoreClient hiveClient;

//	public static void main (String[] args) throws Exception {
//		Map<Integer, String> columnsMap = getAllColumnsMap("default", "sample_07");
//	}

	protected static Connection getHiveJDBCConnection(String dbName) throws SQLException {
		if (connection == null || connection.isClosed()) {
			try {
				Class.forName(IMConstant.HIVE_DRIVER_NAME);
//				String hiveConnectionString = MDConfig.getProperty("hive.hive-connection", null);
				String hiveConnectionString = IMConfig.getProperty("etl.hive-connection");
				connection = DriverManager.getConnection(hiveConnectionString + "/" + dbName, "", "");

			} catch (Exception e) {
				LOGGER.error(e);
				throw new ETLException(e);
			}
			LOGGER.info("Connection successful");
		}
		return connection;
	}

//	protected static HiveMetaStoreClient getMetaStoreClient() {
//		if (hiveClient == null) {
//			try {
//				HiveConf hiveConf = new HiveConf();
////				hiveConf.set("hive.metastore.uris", IMConfig.getProperty("etl.hive-metastore-uris", env));
//				hiveConf.set("hive.metastore.uris", thriftUrl);
//				hiveConf.set("hive.exec.dynamic.partition.mode", "nonstrict");
//				hiveConf.set("hive.exec.dynamic.partition", "true");
//				hiveConf.set("hive.exec.max.dynamic.partitions.pernode", "1000");
//				hiveClient = new HiveMetaStoreClient(hiveConf);
//
//			} catch (Exception e) {
//				LOGGER.error(e);
//				throw new ETLException(e);
//			}
//		}
//		return hiveClient;
//	}

	public static void closeResultset(ResultSet resultSet) throws SQLException {
		if (resultSet != null)
			resultSet.close();
	}

	public static void closeConnection() throws SQLException {
		if (connection != null)
			connection.close();
	}

	public static Map<Integer, String> getAllColumnsMap(String dbName, String tableName) throws ETLException {
		Map<Integer, String> columnsMap = new TreeMap<Integer, String>();

		if (LineageConstants.mockHive) {
			mock(dbName, tableName, columnsMap);

		} else {
			try {
//		    Table table = getMetaStoreClient().getTable(dbName, tableName);
				DatabaseMetaData metaData = getHiveJDBCConnection(dbName).getMetaData();
				ResultSet rs = metaData.getColumns(null, null, tableName, null);

				while (rs.next()) {
//			LOGGER.debug(res.getString("COLUMN_NAME") + ", " + res.getString("TYPE_NAME") +
//					", " + res.getInt("COLUMN_SIZE") + ", " + res.getString("NULLABLE") +
//					", " + res.getString("ORDINAL_POSITION"));
					Integer ordinalPosition = Integer.valueOf(rs.getString("ORDINAL_POSITION"));
					String columnName = rs.getString("COLUMN_NAME");
					columnsMap.put(ordinalPosition, columnName.toUpperCase());
				}

				try {
					closeResultset(rs);
					closeConnection();
				} catch (SQLException ex) {
					LOGGER.error("Error in close");
				} finally {
					connection = null;
				}

			} catch (SQLException ex) {
				LOGGER.error("Error in Hive DB operation", ex);
				throw new ETLException(ex);
			}
		}

		if (columnsMap.size() == 0) {
			LOGGER.warn("Table " + tableName + " does not exist in Hive db; inferring it to be a Temp table");
//			throw new ETLException("Table " + tableName + " does not exist in Hive db");
		}

		System.out.println("List of columns: ");
		for (Map.Entry<Integer, String> entry : columnsMap.entrySet())
			System.out.println(entry.getKey() + " ::: " + entry.getValue());
		return columnsMap;
	}


	public static boolean isValidTable(String tableName) {
		return true;
	}


	private static void mock(String dbName, String tableName, Map<Integer, String> columnsMap) {
		LOGGER.warn("***** Hive Mocking activated *****");

		if ("testdb".equalsIgnoreCase(dbName)) {
			if ("employee".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[]{
						"empno", "name", "deptno", "salary"
				});
			}
			if ("department".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[]{
						"deptno", "deptname"
				});
			}
			if ("current_employee".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[]{
						"empname", "deptname"
				});
			}

			if ("mytable".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[]{
						"t1col1", "t1col2", "t1col3", "t1col4"
				});
			}

			if ("table0".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[]{
						"year", "month", "destination", "newflight"
				});
			}
			if ("myflight00".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[]{
						"year", "month", "flight", "t1col4"
				});
			}
			if ("myflight07".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[]{
						"dest", "flight", "t1col3", "t1col4"
				});
			}

			if ("bank.final".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[]{
						"ACCOUNT_ID", "CITY", "col3", "col4"
				});
			}
			if ("bank.secondary".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[]{
						"year", "month", "creation_timestamp", "column4"
				});
			}
			if ("bank.another_table".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[] {
						"account_id", "city"
				});
			}

			if ("employee_details".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[]{
					"emp_id", "emp_name", "email_id", "country", "state",
							"city", "mob_num", "address", "is_new_joinee", "band"
				});
			}
			if ("project_details".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[] {
						"emp_id", "emp_name", "email_id", "project_code", "project_name",
						"project_manager", "is_billable", "band", "location"
				});
			}
			if ("Appraisal_details".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[] {
						"Appraisee_Name", "email_id", "first_level_apprasier", "second_level_appraiser", "Appraisal_no",
						"Appraisal_year", "Appraisal_Score", "Band", "allocation_ind", "city"
				});
			}
			if ("additional_achievements".equalsIgnoreCase(tableName)) {
				buildColumnMap(columnsMap, new String[] {
						"emp_name", "email_id", "band", "city", "certification_name"
				});
			}
		}
	}

	private static void buildColumnMap(Map<Integer, String> columnsMap, String[] columnNames) {
		for (int i=0; i<columnNames.length; i++) {
			columnsMap.put(i+1, columnNames[i]);
		}
	}
}