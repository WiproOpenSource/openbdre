package com.wipro.ats.bdre.lineage.dataaccess;

/**
 * Created by jayabroto on 27-04-2015.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class ConnectionChecker
{
	private static String driverName = "org.apache.hive.jdbc.HiveDriver";

	public static void main(String[] args) throws Exception{
		try {
			Class.forName(driverName);
			Connection connection = null;
			System.out.println("Before getting connection");
			connection=  DriverManager.getConnection("jdbc:hive2://127.0.0.1:10000/default", "root", "hadoop");
			System.out.println("After getting connection " + connection);

			ResultSet resultSet = connection.createStatement().executeQuery("select * from employee");

			while (resultSet.next()) {
				System.out.println(resultSet.getString(1) + " " + resultSet.getString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
}