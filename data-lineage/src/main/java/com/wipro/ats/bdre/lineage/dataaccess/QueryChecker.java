package com.wipro.ats.bdre.lineage.dataaccess;

import org.apache.hadoop.hive.service.ThriftHive;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;

import java.util.List;

public class QueryChecker {
	public static final String HOST = "127.0.0.1";
	public static final int PORT = 10000;
	public static final String HQL_PREFFIX = "EXPLAIN EXTENDED ";

	public void run(String hql) {
		TSocket transport = null;
		try {
			transport = new TSocket(HOST, PORT);
			transport.open();
			TBinaryProtocol protocol = new TBinaryProtocol(transport);

			if (null != hql && hql.trim().length() > 1) {
				hql = hql.trim();
				if (hql.endsWith(";"))
					hql = hql.substring(0, hql.length() - 1);
				hql = HQL_PREFFIX + hql;
				ThriftHive.Client client = new ThriftHive.Client(protocol);

				client.execute(hql);
				List<String> rst = client.fetchAll();

				System.out.println("HQL Syntax is OK!");
				for (int i = 0; i < rst.size(); i++) {
					System.out.println(rst.get(i));
				}
			} else
				System.out.println("HQL is NULL!");

		} catch (Exception e) {
			System.out.println("HQL Semantic Analysis Exception:");
			e.printStackTrace();
		} finally {
			if (null != transport)
				transport.close();
		}

	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("QueryChecker need Hive QL input");
			System.exit(1);
		}
		new QueryChecker().run(args[0]);
	}
}
