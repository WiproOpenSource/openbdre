package com.wipro.ats.bdre.lineage;

import org.apache.hadoop.hive.ql.parse.ParseException;
import org.apache.hadoop.hive.ql.parse.SemanticException;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by jayabroto on 21-05-2015.
 */
public class LineageProcessor {
	private static final Logger LOGGER = Logger.getLogger(LineageProcessor.class);

	private String defaultHiveDbName = LineageConstants.defaultHiveDbName;              // default Hive db

	public void execute(String wholeQuery, String processId, String instanceExecId) throws ParseException, SemanticException, IOException, Exception {

		// split into queries by semicolon
		String[] queries = wholeQuery.split(";");

		for (int i=0; i<queries.length; i++) {
			String query = queries[i];

			if (query != null && !query.trim().isEmpty()) {
				query = query.trim();

				// remove comments
				if (query.contains("--")) {
					int commentStart = query.indexOf("--");
					int commentEnd = query.indexOf("\n", commentStart+2);
					String comment = query.substring(commentStart, commentEnd);
					System.out.println("comment found = " + comment);
					query = query.replaceAll(comment, " ");
				}
				// remove line breaks
				query = query.replaceAll("[\\r\\n]", " ").trim();
				System.out.println("\nLineageProcessor: Processing query # " + (i + 1) + ": \"" + query + "\"");

				if (query.toLowerCase().startsWith("use")) {
					// ignore 'use db' line for lineage
					// use last split to determine the db
					String[] splits = query.split(" ");
					defaultHiveDbName = splits[splits.length-1].toUpperCase();
					System.out.println("DefaulHiveDbName is set to " + defaultHiveDbName);

				} else if (query.toLowerCase().startsWith("insert")
						|| query.toLowerCase().startsWith("select")) {              // process only insert and select statements

					// LineageMain.main(new String[]{query, processId, instanceExecId});
					LineageMain.main(new String[]{query, defaultHiveDbName, processId, instanceExecId});
				}
			}
		}
	}
}