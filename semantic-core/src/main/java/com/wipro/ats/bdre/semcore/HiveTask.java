package com.wipro.ats.bdre.semcore;

import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Created by su324335 on 7/25/16.
 */
public class HiveTask {
    private static final Logger LOGGER = Logger.getLogger(HiveTask.class);
    private static Connection connection;
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    protected static Connection getHiveJDBCConnection() throws SQLException {

        try {
            //Class.forName(IMConstant.HIVE_DRIVER_NAME);
            Class.forName("org.apache.hive.jdbc.HiveDriver");
//				String hiveConnectionString = MDConfig.getProperty("hive.hive-connection", null);
            String hiveConnectionString = "jdbc:hive2://127.0.0.1:10000/default";
            connection = DriverManager.getConnection(hiveConnectionString, "", "");

        } catch (Exception e) {
            LOGGER.error(e);

        }
        LOGGER.info("Connection successful");


        return connection;
    }

    public static void main(String[] args) {
        Statement st = null;
        try {
            Class.forName(driverName);
//				String hiveConnectionString = MDConfig.getProperty("hive.hive-connection", null);
            String hiveConnectionString = args[1];
            LOGGER.info("hiveURL is "+hiveConnectionString);
            connection = DriverManager.getConnection(hiveConnectionString, "", "");

            File file = new File(args[0]);
            LOGGER.info("hql file is "+args[0]);
            Scanner s = new Scanner(file); //TODO
            s.useDelimiter("(;(\r)?\n)|(--\n)");

            while (s.hasNext())
            {
                String line = s.next();
                if (line.startsWith("/*!") && line.endsWith("*/"))
                {
                    int i = line.indexOf(' ');
                    line = line.substring(i + 1, line.length() - " */".length());
                }

                if (line.trim().length() > 0)
                {
                    st.execute(line);
                }
            }
            st.close();

        }catch (Exception e){
            LOGGER.error(e);
        }

    }

}
