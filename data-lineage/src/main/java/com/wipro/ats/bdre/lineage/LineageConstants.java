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

import java.util.Date;

/**
 * Created by jayabroto on 28-04-2015.
 */
public interface LineageConstants {

	public static final String defaultHiveDbName = "DEFAULT";
	public static final String processIdString = "bdre.lineage.processId";
	public static final String instanceExecIdString = "bdre.lineage.instanceExecId";

	public static final int processId = 2;
	public static final long instanceId = new Date().getTime();

	public static final boolean mockHive = false;                     //* mock flag for mocking hive tables

//	public static final String query = "insert overwrite table bank.tmp_table select account_id, city from "
//		+ " (select city, (row_number() over (partition by account_id order by cts desc)) "
//		+ " as ACCOUNT_ID from ( "
//		+ " select account_id, city, min(creation_timestamp) as cts from  bank.final where instanceexecid in (1,2) group by account_id, city) T1) T2 "
//		+ " where T2.rn <= 2";
//	public static final String query = "insert overwrite table bank.tmp_table select account_id, city from "
//		+ " (select city, (row_number() over (partition by account_id order by cts desc)) "
//		+ " as ACCOUNT_ID from ( "
//		+ " select account_id, city, min(creation_timestamp) as cts, arijit.name as name from  bank.final inner join arijit on (bank.final.id = arijit.id ) where instanceexecid in (1,2) group by account_id, city) T1) T2 "
//		+ " where T2.rn <= 2";
//	public static final String query = "insert overwrite table BANK.ANOTHER_TABLE select account_id, city from \n" +
//		"(select city, (row_number() over (partition by account_id order by cts desc)) \n" +
//		"as ACCOUNT_ID from ( \n" +
//		"select account_id, city, min(creation_timestamp) as cts from (\n" +
//		"select f.ACCOUNT_ID, f.CITY, s.CREATION_TIMESTAMP from bank.final f LEFT OUTER JOIN bank.secondary s on (f.ACCOUNT_ID = s.ACCOUNT_ID)\n" +
//		") T3 where instanceexecid in (1,2) group by account_id, city) T1) T2 where T2.rn <= 2";

//	public static final String query = "select boom(m.t1col1, m.t1col2) as boomcol, m.t1col3, m.t1col4 from mytable m";

//	public static final String query = "use testdb;\n" +
//			"insert into table current_employee select e.name, d.deptname from employee e, department d where e.deptno = d.deptno;";
//	public static final String query = "insert overwrite table current_employee_2 select e.salary, d.deptname, e.name from employee e, department d where e.deptno = d.deptno;";

//	public static final String query = "insert overwrite table temp_employee_table select\n" +
//			"a.emp_id,\n" +
//			"a.emp_name,\n" +
//			"a.email_id,\n" +
//			"a.country,\n" +
//			"a.state,\n" +
//			"a.city,\n" +
//			"count(a.band),\n" +
//			"b.project_code,\n" +
//			"b.project_name,\n" +
//			"b.project_manager,\n" +
//			"c.first_level_appraiser,\n" +
//			"c.second_level_appraiser,\n" +
//			"c.appraisal_year,\n" +
//			"c.appraisal_score,\n" +
//			"d.certification_name\n" +
//			"from \n" +
//			"employee_details a \n" +
//			"JOIN project_details b ON (a.emp_id=b.emp_id) \n" +
//			"JOIN appraisal_details c on (a.emp_name=c.appraisee_name and a.email_id=c.email_id and b.project_manager=c.first_level_appraiser)\n" +
//			"JOIN additional_achievements d on (a.email_id=d.email_id)\n" +
//			"WHERE\n" +
//			"a.country='India' AND \n" +
//			"c.appraisal_year='2014'\n" +
//			"GROUP BY d.email_id;";

//	public static final String query = "insert overwrite table table0 select min(year), m0.month, m7.dest, m7.flight " +
//			"from myflight00 m0 LEFT OUTER JOIN myflight07 m7 " +
//			"ON m0.flight = m7.flight;";
//	public static final String query = "SELECT a.name, a.surname, b.age, c.salary FROM atable a \n" +
//		" JOIN btable b ON (a.key = b.key)  LEFT OUTER JOIN ctable c ON (a.key = c.key)";
//	public static final String query = "INSERT OVERWRITE TABLE itunes_common_esl.ITS_IB_PLI_EXCEPTION_ENRICH  partition (status='INPROCESS', etl_process_run_id)\n" +
//			"SELECT\n" + "purch_line_item_id,\n" + "purch_id,\n" +
//			"billing_qty,\n" + "adam_id,\n" + "client_name,\n" +
//			"client_version_nr,\n" + "create_ts,\n" + "device_version_nr,\n" +
//			"estimated_tax_amt,\n" + "estimated_total_amt,\n" + "global_unique_id,\n" +
//			"ip_addr_txt,\n" + "its_prod_id,\n" + "its_prod_type_id,\n" +
//			"mkt_string_txt,\n" +"parent_adam_id,\n" +"pli_parent_type_id,\n" +
//			"platform_name,\n" +"platform_version_nr,\n" +"pricing_parameter_string_txt,\n" +
//			"royalty_price_amt,\n" +"rptg_txt,\n" +"salable_version_nr,\n" +
//			"sap_content_type_cd,\n" +"sap_line_item_id,\n" +"sap_vendor_id,\n" +
//			"single_unit_price_amt,\n" +"source_app_id,\n" +"subsumed_vendor_id,\n" +
//			"target_platform_ind,\n" +"user_agent_txt,\n" +"posting_type_cd,\n" +
//			"actl_price_amt,\n" +"posting_reason_cd,\n" +"etl_action_cd,\n" +
//			"etl_batch_sk,\n" +"etl_change_batch_sk,\n" +"etl_change_ts,\n" +
//			"etl_create_batch_sk,\n" +"etl_create_ts,\n" +"exception_ind,\n" +
//			"create_dt,\n" +"processed_cnt,\n" +"Content_Provider_Id,\n" +
//			"fp_cd_gen_id,\n" +"Family_Purch_Ind,\n" +"Client_Id,\n" +
//			"Experiment_Name,\n" +"Store_Cohort_Txt,\n" +"utc_epoch_change_ts,\n" +
//			"d1.etl_process_run_id\n" +
//			"from DB.ITS_IB_PLI_EXCEPTION_ENRICH d1\n" +
//			"where status = 'UNPROCESSED';";
//	public static final String query = "SELECT f08.Month, f08.DayOfMonth, cr.description, cr.price, f08.Origin, f08.Dest, f08.FlightNum, MIN(DepTime)\n" +
//		"FROM flightinfo2008 f08 JOIN Carriers cr ON f08.UniqueCarrier = cr.code\n" +
//		"WHERE f08.Origin = 'JFK' AND f08.Dest = 'ORD' AND f08.Month = 1 AND f08.DepTime != 0;";
//	public static final String query = "CREATE TABLE newtable2007 AS\n" +
//		"  SELECT Year, Month, DepTime, ArrTime, FlightNum, Origin, Dest FROM FlightInfo2007\n" +
//		"  WHERE (Month = 7 AND DayofMonth = 3) AND (Origin='JFK' AND Dest='ORD');";
//	public static final String query = "insert overwrite table newtable select name, surname, min(age) from employee";


//	public static final String query = "insert into table leave_ft_1\n" +
//			"select l.ABSENCE_BEGIN_DT, l.ABSENCE_END_DT, l.ABSENCE_BEGIN_TM, l.ABSENCE_END_TM,\n" +
//			"l.ORIGINAL_BEGIN_DT, l.ABSENCE_REASON_CODE, l.ABSENCE_REASON_NAME, l.ABSENCE_REASON_DESC,\n" +
//			"l.ABSENCE_TYPE_CODE, l.ABSENCE_CATEGORY_CODE, l.APPROVAL_STATUS, l.DAYS_DURATION,\n" +
//			"l.EMPLOYEE_ID, l.ASSIGNMENT_INTEGRATION_ID, l.INTEGRATION_ID, l.NOTIFIED_DT\n" +
//			"from leave l left outer join physical p on(l.EMPLOYEE_ID=p.emp_no) where p.emp_no is null;";
//	public static final String query = "insert into table leave_net\n" +
//		"select distinct ABSENCE_BEGIN_DT,\n" +
//		"ABSENCE_END_DT ,\n" +
//		"ABSENCE_BEGIN_TM ,\n" +
//		"ABSENCE_END_TM ,\n" +
//		"ORIGINAL_BEGIN_DT ,\n" +
//		"ABSENCE_REASON_CODE ,\n" +
//		"ABSENCE_REASON_NAME ,\n" +
//		"ABSENCE_REASON_DESC ,\n" +
//		"ABSENCE_TYPE_CODE ,\n" +
//		"ABSENCE_CATEGORY_CODE ,\n" +
//		"APPROVAL_STATUS ,\n" +
//		"DAYS_DURATION ,\n" +
//		"o.EMPLOYEE_ID ,\n" +
//		"ASSIGNMENT_INTEGRATION_ID ,\n" +
//		"INTEGRATION_ID  ,NOTIFIED_DT ,i.ipaddress,i.date1\n" +
//		"from nxlogs_ex3 i join leave_ft_2 o on(i.employee_id=o.employee_id) where i.date1 between o.ABSENCE_BEGIN_DT and o.ABSENCE_END_DT;";
//	public static final String query = "INSERT INTO TABLE leave_net1\n" +
//		"select distinct ABSENCE_BEGIN_DT,\n" +
//		"ABSENCE_END_DT ,\n" +
//		"ABSENCE_BEGIN_TM ,\n" +
//		"ABSENCE_END_TM ,\n" +
//		"ORIGINAL_BEGIN_DT ,\n" +
//		"ABSENCE_REASON_CODE ,\n" +
//		"ABSENCE_REASON_NAME ,\n" +
//		"ABSENCE_REASON_DESC ,\n" +
//		"ABSENCE_TYPE_CODE ,\n" +
//		"ABSENCE_CATEGORY_CODE ,\n" +
//		"APPROVAL_STATUS ,\n" +
//		"DAYS_DURATION ,\n" +
//		"EMPLOYEE_ID ,\n" +
//		"ASSIGNMENT_INTEGRATION_ID ,\n" +
//		"INTEGRATION_ID  ,NOTIFIED_DT ,ipaddress,date1,date1 FROM LEAVE_NET";

//	public static final String query = "use ood_lan_tmp;\n" +
//		"\n" +
//		"--checking for swipe logs is null while on LEAVE\n" +
//		"\n" +
//		"insert into table leave_ft_1\n" +
//		"select l.ABSENCE_BEGIN_DT, l.ABSENCE_END_DT, l.ABSENCE_BEGIN_TM, l.ABSENCE_END_TM,\n" +
//		"l.ORIGINAL_BEGIN_DT, l.ABSENCE_REASON_CODE, l.ABSENCE_REASON_NAME, l.ABSENCE_REASON_DESC,\n" +
//		"l.ABSENCE_TYPE_CODE, l.ABSENCE_CATEGORY_CODE, l.APPROVAL_STATUS, l.DAYS_DURATION,\n" +
//		"l.EMPLOYEE_ID, l.ASSIGNMENT_INTEGRATION_ID, l.INTEGRATION_ID, l.NOTIFIED_DT\n" +
//		"from leave l left outer join physical p on(l.EMPLOYEE_ID=p.emp_no) where p.emp_no is null;\n" +
//		"\n" +
//		"\n" +
//		"--checking for TURNSTILE logs is null while on LEAVE\n" +
//		"\n" +
//		"insert into table leave_ft_2\n" +
//		"select l.* from leave_ft_1 l left outer join turnstile p on(l.EMPLOYEE_ID=p.emp_no) where p.emp_no is null;\n" +
//		"\n" +
//		"--flaging employee having LAN logs while on Leave after all filter\n" +
//		"\n" +
//		"insert into table leave_net\n" +
//		"select distinct ABSENCE_BEGIN_DT,\n" +
//		"ABSENCE_END_DT ,\n" +
//		"ABSENCE_BEGIN_TM ,\n" +
//		"ABSENCE_END_TM ,\n" +
//		"ORIGINAL_BEGIN_DT ,\n" +
//		"ABSENCE_REASON_CODE ,\n" +
//		"ABSENCE_REASON_NAME ,\n" +
//		"ABSENCE_REASON_DESC ,\n" +
//		"ABSENCE_TYPE_CODE ,\n" +
//		"ABSENCE_CATEGORY_CODE ,\n" +
//		"APPROVAL_STATUS ,\n" +
//		"DAYS_DURATION ,\n" +
//		"o.EMPLOYEE_ID ,\n" +
//		"ASSIGNMENT_INTEGRATION_ID ,\n" +
//		"INTEGRATION_ID  ,NOTIFIED_DT ,i.ipaddress,i.date1\n" +
//		"from nxlogs_ex3 i join leave_ft_2 o on(i.employee_id=o.employee_id) where i.date1 between o.ABSENCE_BEGIN_DT and o.ABSENCE_END_DT;\n" +
//		"\n" +
//		"INSERT OVERWRITE TABLE leave_net1\n" +
//		"select distinct ABSENCE_BEGIN_DT,\n" +
//		"ABSENCE_END_DT ,\n" +
//		"ABSENCE_BEGIN_TM ,\n" +
//		"ABSENCE_END_TM ,\n" +
//		"ORIGINAL_BEGIN_DT ,\n" +
//		"ABSENCE_REASON_CODE ,\n" +
//		"ABSENCE_REASON_NAME ,\n" +
//		"ABSENCE_REASON_DESC ,\n" +
//		"ABSENCE_TYPE_CODE ,\n" +
//		"ABSENCE_CATEGORY_CODE ,\n" +
//		"APPROVAL_STATUS ,\n" +
//		"DAYS_DURATION ,\n" +
//		"EMPLOYEE_ID ,\n" +
//		"ASSIGNMENT_INTEGRATION_ID ,\n" +
//		"INTEGRATION_ID  ,NOTIFIED_DT ,ipaddress,date1,date1 FROM LEAVE_NET;";

	public static final String query = "use clu_poc;\n" +
			"\n" +
			"insert overwrite table ALL_CUST_ACCT partition (ACCT='iss')\n" +
			"select \n" +
			"\"\\N\" FLAG_JOIN,\n" +
			"cust.COUNTRY_CODE,\n" +
			"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
			"cust.CUSTOMER_BRANCH_NUMBER,\n" +
			"cust.CUSTOMER_SERIAL_NUMBER,\n" +
			"cust.MODE_FLAG,\n" +
			"concat(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(concat(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(concat(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
			" NULL CUSTOMER_REFERENCE_2,\n" +
			"cust.CUSTOMER_FULL_NAME,\n" +
			"cust.CUSTOMER_NAME,\n" +
			"cust.CUSTOMER_SHORT_NAME,\n" +
			"cust.INDUSTRY_CODE,\n" +
			"cust.MARKET_SECTOR_1,\n" +
			"cust.CUSTOMER_LEGAL_TYPE,\n" +
			"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
			"cust.ACCOUNT_OFFICER,\n" +
			"cust.CUSTOMER_STATUS,\n" +
			"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
			"cust.NATIONALITY_CODE,\n" +
			"cust.CORRESPONDENT_BANK_ID,\n" +
			"cust.CORRESPONDENT_BRANCH_ID,\n" +
			"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
			"cust_class.BANK_INDICATOR,\n" +
			"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
			"accnt.ACCOUNT_COUNTRY_CODE,\n" +
			"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
			"accnt.ACCOUNT_BRANCH,\n" +
			"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
			"accnt.ACCOUNT_SUFFIX,\n" +
			"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(concat(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(concat(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(concat(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
			" NULL CARD_NUMBER,\n" +
			"accnt.ACCOUNT_SHORT_NAME,\n" +
			"accnt.CURRENCY,\n" +
			"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
			"accnt.ACCOUNT_STATUS,\n" +
			"accnt.ACCOUNT_TYPE ACCOUNT_TYPE,\n" +
			"accnt.ACCOUNT_FILE ACCOUNT_FILE,\n" +
			" NULL TRANSACTION_REF_NO,\n" +
			" NULL SYSTEM_CODE_EXT,\n" +
			" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
			" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
			" NULL EXTERNAL_ACCOUNT_GPS,\n" +
			"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
			" from \n" +
			"CUSTOMER_INDVL cust\n" +
			"right outer join\n" +
			"ss_account accnt\n" +
			"on cust.COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
			"and cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
			"and cust.CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
			"and cust.CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
			"--and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
			"left outer join CUST_GROUP cust_group\n" +
			"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
			"left outer join CUST_CLASSIFICATION cust_class\n" +
			"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
			"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
			"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;";
/*		public static final String query = "use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='iss')\n" +
				"select \n" +
				"\"N\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"accnt.ACCOUNT_TYPE ACCOUNT_TYPE,\n" +
				"accnt.ACCOUNT_FILE ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"CUSTOMER_INDVL cust\n" +
				"right outer join\n" +
				"ss_account accnt\n" +
				"on cust.COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"--and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table clu_poc.CLU_ALL_COUNTRY_FLAT_FILE partition (COUNTRY='IN')\n" +
				"select \n" +
				"FLAG_JOIN  ,\n" +
				"COUNTRY_CODE ,\n" +
				"DOMESTIC_CUSTOMER_GROUP_MEMBER  ,\n" +
				"CUSTOMER_BRANCH_NUMBER  ,\n" +
				"CUSTOMER_SERIAL_NUMBER  ,\n" +
				"MODE_FLAG ,\n" +
				"CUSTOMER_REFERENCE  ,\n" +
				"CUSTOMER_REFERENCE_2  ,\n" +
				"CUSTOMER_FULL_NAME  ,\n" +
				"CUSTOMER_NAME  ,\n" +
				"CUSTOMER_SHORT_NAME  ,\n" +
				"INDUSTRY_CODE  ,\n" +
				"MARKET_SECTOR_1  ,\n" +
				"CUSTOMER_LEGAL_TYPE  ,\n" +
				"GHO_CUSTOMER_CLASSIFICATION  ,\n" +
				"ACCOUNT_OFFICER  ,\n" +
				"CUSTOMER_STATUS  ,\n" +
				"COUNTRY_OF_REGISTRATION_RESIDENCE  ,\n" +
				"NATIONALITY_CODE  ,\n" +
				"CORRESPONDENT_BANK_ID  ,\n" +
				"CORRESPONDENT_BRANCH_ID  ,\n" +
				"CBID  ,\n" +
				"BANK_INDICATOR  ,\n" +
				"INDIVIDUAL_NON_INDIVIDUAL  ,\n" +
				"ACCOUNT_COUNTRY_CODE  ,\n" +
				"GROUP_MEMBER_ABBREVIATION  ,\n" +
				"ACCOUNT_BRANCH  ,\n" +
				"ACCOUNT_NUMBER_SERIAL  ,\n" +
				"ACCOUNT_SUFFIX  ,\n" +
				"ACCOUNT_REFERENCE  ,\n" +
				"CARD_NUMBER  ,\n" +
				"ACCOUNT_SHORT_NAME  ,\n" +
				"CURRENCY  ,\n" +
				"ACCOUNT_PRODUCT_TYPE  ,\n" +
				"ACCOUNT_STATUS  ,\n" +
				"ACCOUNT_TYPE  ,\n" +
				"ACCOUNT_FILE  ,\n" +
				"TRANSACTION_REF_NO  ,\n" +
				"SYSTEM_CODE_EXT  ,\n" +
				"EXTERNAL_ACCOUNT_NUMBER  ,\n" +
				"INTERNATIONAL_BANK_ACCOUNT_NUMBER  ,\n" +
				"EXTERNAL_ACCOUNT_GPS  ,\n" +
				"LOB_HUB_CUSTOMER_GROUP  ,\n" +
				"'\\N' LOB_HUB_FACTA_CORP  ,\n" +
				"'\\N' LOB_HUB_FACTA_NON_CORP  ,\n" +
				"'\\N' LOB_HUB_GLEAM  ,\n" +
				"'\\N' LOB_DR_GBM  ,\n" +
				"'\\N' LOB_DR_CMB  ,\n" +
				"'\\N' LOB_DR_GPB  ,\n" +
				"'\\N' LOB_DR_RBWM  ,\n" +
				"'\\N' LOB_LOCAL    ,\n" +
				"'\\N' LOB_USE    ,\n" +
				"'\\N' GUCI  ,\n" +
				"'\\N' GRID      ,\n" +
				"'\\N' GID      ,\n" +
				"'HUB' DATA_SYSTEM\n" +
				"from ALL_CUST_ACCT\n" +
				"where (ACCOUNT_REFERENCE is not null OR ACCOUNT_FILE='CP')\n" +
				"AND CUSTOMER_REFERENCE is not null;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table  CUSTOMER_INDVL_JOINT\n" +
				"select\n" +
				"indvl.COUNTRY_CODE\t\t,\n" +
				"indvl.DOMESTIC_CUSTOMER_GROUP_MEMBER\t\t,\n" +
				"indvl.CUSTOMER_BRANCH_NUMBER\t\t,\n" +
				"indvl.CUSTOMER_SERIAL_NUMBER\t\t,\n" +
				"indvl.MODE_FLAG\t\t,\n" +
				"indvl.CUSTOMER_FULL_NAME\t\t,\n" +
				"indvl.CUSTOMER_NAME\t\t,\n" +
				"indvl.CUSTOMER_SHORT_NAME\t\t,\n" +
				"indvl.INDUSTRY_CODE\t\t,\n" +
				"indvl.MARKET_SECTOR_1\t\t,\n" +
				"indvl.CUSTOMER_LEGAL_TYPE\t\t,\n" +
				"indvl.GHO_CUSTOMER_CLASSIFICATION\t\t,\n" +
				"indvl.ACCOUNT_OFFICER\t\t,\n" +
				"indvl.CUSTOMER_STATUS\t\t,\n" +
				"indvl.COUNTRY_OF_REGISTRATION_RESIDENCE\t\t,\n" +
				"indvl.NATIONALITY_CODE\t\t,\n" +
				"indvl.CORRESPONDENT_BANK_ID\t\t,\n" +
				"indvl.CORRESPONDENT_BRANCH_ID\t\t,\n" +
				"joint.MODE_FLAG JOINT_MODE_FLAG ,\n" +
				"joint.COUNTRY_CODE JOINT_COUNTRY_CODE ,\n" +
				"joint.DOMESTIC_CUSTOMER_GROUP_MEMBER JOINT_DOMESTIC_GROUP_MEMBER ,\n" +
				"joint.CUSTOMER_BRANCH_NUMBER JOINT_CUSTOMER_BRANCH_NUMBER ,\n" +
				"joint.CUSTOMER_SERIAL_NUMBER JOINT_CUSTOMER_SERIAL_NUMBER \n" +
				"from\n" +
				"CUSTOMER_INDVL indvl inner join CUSTOMER_JOINT joint\n" +
				"on indvl.COUNTRY_CODE=joint.DOMESTIC_CUSTOMER_COUNTRY_HOLDERS\n" +
				"and indvl.DOMESTIC_CUSTOMER_GROUP_MEMBER=joint.DOMESTIC_CUSTOMER_GROUP_MEMBER_HOLDER\n" +
				"and indvl.CUSTOMER_BRANCH_NUMBER=joint.DOMESTIC_CUSTOMER_GROUP_BRANCH_HOLDER\n" +
				"and indvl.CUSTOMER_SERIAL_NUMBER=joint.DOMESTIC_CUSTOMER_SERIAL_HOLDER\n" +
				"and indvl.MODE_FLAG=joint.MODE_FLAG;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='jss')\n" +
				"select \n" +
				"\"Y\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"accnt.ACCOUNT_TYPE ACCOUNT_TYPE,\n" +
				"accnt.ACCOUNT_FILE ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"customer_indvl_joint cust \n" +
				"inner join \n" +
				"ss_account accnt \n" +
				"on cust.JOINT_COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.JOINT_DOMESTIC_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.JOINT_CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.JOINT_CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"--and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='idd')\n" +
				"select \n" +
				"\"N\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"NULL ACCOUNT_TYPE,\n" +
				"'DD' ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"CUSTOMER_INDVL cust\n" +
				"right outer join\n" +
				"dd_account accnt\n" +
				"on cust.COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='jdd')\n" +
				"select \n" +
				"\"Y\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"NULL ACCOUNT_TYPE,\n" +
				"'DD' ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"customer_indvl_joint cust  \n" +
				"inner join \n" +
				"dd_account accnt \n" +
				"on cust.JOINT_COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.JOINT_DOMESTIC_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.JOINT_CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.JOINT_CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"--and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='icp')\n" +
				"select \n" +
				"\"N\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				" NULL domestic_customer_country,\n" +
				" NULL GROUP_MEMBER_ABBREVIATION,\n" +
				" NULL ACCOUNT_BRANCH,\n" +
				" NULL ACCOUNT_NUMBER_SERIAL,\n" +
				" NULL ACCOUNT_SUFFIX,\n" +
				" NULL  ACCOUNT_REFERENCE,\n" +
				"accnt.account_number CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				" NULL ACCOUNT_TYPE,\n" +
				" 'CP' ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"CUSTOMER_INDVL cust\n" +
				"right outer join\n" +
				"CP_ACCOUNT accnt\n" +
				"on cust.COUNTRY_CODE=accnt.domestic_customer_country\n" +
				"and cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.CUSTOMER_BRANCH_NUMBER=accnt.tpc_branch_number\n" +
				"and cust.CUSTOMER_SERIAL_NUMBER=accnt.tpc_CUSTOMER_SERIAL_NUMBER\n" +
				"--and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='jcp')\n" +
				"select \n" +
				"\"Y\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				" NULL DOMESTIC_CUSTOMER_COUNTRY,\n" +
				" NULL GROUP_MEMBER_ABBREVIATION,\n" +
				" NULL ACCOUNT_BRANCH,\n" +
				" NULL ACCOUNT_NUMBER_SERIAL,\n" +
				" NULL ACCOUNT_SUFFIX,\n" +
				" NULL  ACCOUNT_REFERENCE,\n" +
				"accnt.ACCOUNT_NUMBER CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				" NULL ACCOUNT_TYPE,\n" +
				" 'CP' ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"CUSTOMER_INDVL_JOINT cust \n" +
				"inner join \n" +
				"CP_ACCOUNT accnt \n" +
				"on cust.JOINT_COUNTRY_CODE=accnt.DOMESTIC_CUSTOMER_COUNTRY\n" +
				"and cust.JOINT_DOMESTIC_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.JOINT_CUSTOMER_BRANCH_NUMBER=accnt.TPC_BRANCH_NUMBER\n" +
				"and cust.JOINT_CUSTOMER_SERIAL_NUMBER=accnt.TPC_CUSTOMER_SERIAL_NUMBER\n" +
				"--and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='ils')\n" +
				"select \n" +
				"\"N\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"NULL ACCOUNT_TYPE,\n" +
				"'LS' ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"CUSTOMER_INDVL cust\n" +
				"right outer join\n" +
				"ls_account accnt\n" +
				"on cust.COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='jls')\n" +
				"select \n" +
				"\"Y\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"NULL ACCOUNT_TYPE,\n" +
				"'LS' ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"customer_indvl_joint cust \n" +
				"inner join \n" +
				"ls_account accnt \n" +
				"on cust.JOINT_COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.JOINT_DOMESTIC_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.JOINT_CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.JOINT_CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"--and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='ipl')\n" +
				"select \n" +
				"\"N\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"NULL ACCOUNT_TYPE,\n" +
				"'PL' ACCOUNT_FILE,\n" +
				"'transaction_reference_number' TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"CUSTOMER_INDVL cust\n" +
				"right outer join\n" +
				"pl_account accnt\n" +
				"on cust.COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='jpl')\n" +
				"select \n" +
				"\"Y\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"NULL ACCOUNT_TYPE,\n" +
				"'PL' ACCOUNT_FILE,\n" +
				"'transaction_reference_number' TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"customer_indvl_joint cust \n" +
				"inner join \n" +
				"pl_account accnt \n" +
				"on cust.JOINT_COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.JOINT_DOMESTIC_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.JOINT_CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.JOINT_CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"--and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='itc')\n" +
				"select \n" +
				"\"N\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"NULL ACCOUNT_TYPE,\n" +
				"'TC' ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"CUSTOMER_INDVL cust\n" +
				"right outer join\n" +
				"tc_account accnt\n" +
				"on cust.COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='jtc')\n" +
				"select \n" +
				"\"Y\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"NULL ACCOUNT_TYPE,\n" +
				"'TC' ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"customer_indvl_joint cust \n" +
				"inner join \n" +
				"tc_account accnt \n" +
				"on cust.JOINT_COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.JOINT_DOMESTIC_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.JOINT_CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.JOINT_CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"--and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='itd')\n" +
				"select \n" +
				"\"N\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"NULL ACCOUNT_TYPE,\n" +
				"'TD' ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"CUSTOMER_INDVL cust\n" +
				"right outer join\n" +
				"td_account accnt\n" +
				"on cust.COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table ALL_CUST_ACCT partition (ACCT='jtd')\n" +
				"select \n" +
				"\"Y\" FLAG_JOIN,\n" +
				"cust.COUNTRY_CODE,\n" +
				"cust.DOMESTIC_CUSTOMER_GROUP_MEMBER,\n" +
				"cust.CUSTOMER_BRANCH_NUMBER,\n" +
				"cust.CUSTOMER_SERIAL_NUMBER,\n" +
				"cust.MODE_FLAG,\n" +
				"CONCAT(CUST.COUNTRY_CODE, CUST.DOMESTIC_CUSTOMER_GROUP_MEMBER, lpad(CONCAT(floor(CUST.CUSTOMER_BRANCH_NUMBER)),3,'0'), lpad(CONCAT(floor(CUST.CUSTOMER_SERIAL_NUMBER)),6,'0')) as CUSTOMER_REFERENCE, \n" +
				" NULL CUSTOMER_REFERENCE_2,\n" +
				"cust.CUSTOMER_FULL_NAME,\n" +
				"cust.CUSTOMER_NAME,\n" +
				"cust.CUSTOMER_SHORT_NAME,\n" +
				"cust.INDUSTRY_CODE,\n" +
				"cust.MARKET_SECTOR_1,\n" +
				"cust.CUSTOMER_LEGAL_TYPE,\n" +
				"cust.GHO_CUSTOMER_CLASSIFICATION,\n" +
				"cust.ACCOUNT_OFFICER,\n" +
				"cust.CUSTOMER_STATUS,\n" +
				"cust.COUNTRY_OF_REGISTRATION_RESIDENCE,\n" +
				"cust.NATIONALITY_CODE,\n" +
				"cust.CORRESPONDENT_BANK_ID,\n" +
				"cust.CORRESPONDENT_BRANCH_ID,\n" +
				"CONCAT(CUST.CORRESPONDENT_BANK_ID ,CUST.CORRESPONDENT_BRANCH_ID) cbid,\n" +
				"cust_class.BANK_INDICATOR,\n" +
				"cust_class.INDIVIDUAL_NON_INDIVIDUAL,\n" +
				"accnt.ACCOUNT_COUNTRY_CODE,\n" +
				"accnt.GROUP_MEMBER_ABBREVIATION,\n" +
				"accnt.ACCOUNT_BRANCH,\n" +
				"accnt.ACCOUNT_NUMBER_SERIAL,\n" +
				"accnt.ACCOUNT_SUFFIX,\n" +
				"CONCAT(accnt.ACCOUNT_COUNTRY_CODE ,accnt.GROUP_MEMBER_ABBREVIATION, lpad(CONCAT(floor(accnt.ACCOUNT_BRANCH)),3,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_NUMBER_SERIAL)),6,'0') ,lpad(CONCAT(floor(accnt.ACCOUNT_SUFFIX)),3,'0')) ACCOUNT_REFERENCE,\n" +
				" NULL CARD_NUMBER,\n" +
				"accnt.ACCOUNT_SHORT_NAME,\n" +
				"accnt.CURRENCY,\n" +
				"accnt.ACCOUNT_PRODUCT_TYPE,\n" +
				"accnt.ACCOUNT_STATUS,\n" +
				"NULL ACCOUNT_TYPE,\n" +
				"'TD' ACCOUNT_FILE,\n" +
				" NULL TRANSACTION_REF_NO,\n" +
				" NULL SYSTEM_CODE_EXT,\n" +
				" NULL EXTERNAL_ACCOUNT_NUMBER,\n" +
				" NULL INTERNATIONAL_BANK_ACCOUNT_NUMBER,\n" +
				" NULL EXTERNAL_ACCOUNT_GPS,\n" +
				"cust_group.CUSTOMER_GROUP  LOB_HUB_CUSTOMER_GROUP\n" +
				" from \n" +
				"customer_indvl_joint cust \n" +
				"inner join \n" +
				"td_account accnt \n" +
				"on cust.JOINT_COUNTRY_CODE=accnt.ACCOUNT_COUNTRY_CODE\n" +
				"and cust.JOINT_DOMESTIC_GROUP_MEMBER=accnt.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"and cust.JOINT_CUSTOMER_BRANCH_NUMBER=accnt.CUSTOMER_BRANCH_NUMBER\n" +
				"and cust.JOINT_CUSTOMER_SERIAL_NUMBER=accnt.CUSTOMER_SERIAL_NUMBER\n" +
				"--and cust.MODE_FLAG=accnt.MODE_FLAG\n" +
				"left outer join CUST_GROUP cust_group\n" +
				"on cust.GHO_CUSTOMER_CLASSIFICATION=cust_group.GHO_CUSTOMER_CLASSIFICATION\n" +
				"left outer join CUST_CLASSIFICATION cust_class\n" +
				"ON cust.COUNTRY_CODE=cust_class.COUNTRY_CODE\n" +
				"AND cust.DOMESTIC_CUSTOMER_GROUP_MEMBER=cust_class.GROUP_MEMBER_ABBREVIATION\n" +
				"AND cust.GHO_CUSTOMER_CLASSIFICATION=cust_class.GHO_CUSTOMER_CLASSIFICATION;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table clu_poc.CLU_ALL_COUNTRY_FLAT_FILE partition (COUNTRY ='IN')\n" +
				"select \n" +
				"FF.FLAG_JOIN  ,\n" +
				"FF.COUNTRY_CODE,\n" +
				"FF.DOMESTIC_CUSTOMER_GROUP_MEMBER  ,\n" +
				"FF.CUSTOMER_BRANCH_NUMBER  ,\n" +
				"FF.CUSTOMER_SERIAL_NUMBER  ,\n" +
				"FF.MODE_FLAG  ,\n" +
				"FF.CUSTOMER_REFERENCE  ,\n" +
				"FF.CUSTOMER_REFERENCE_2  ,\n" +
				"FF.CUSTOMER_FULL_NAME  ,\n" +
				"FF.CUSTOMER_NAME  ,\n" +
				"FF.CUSTOMER_SHORT_NAME  ,\n" +
				"FF.INDUSTRY_CODE  ,\n" +
				"FF.MARKET_SECTOR_1  ,\n" +
				"FF.CUSTOMER_LEGAL_TYPE  ,\n" +
				"FF.GHO_CUSTOMER_CLASSIFICATION  ,\n" +
				"FF.ACCOUNT_OFFICER  ,\n" +
				"FF.CUSTOMER_STATUS  ,\n" +
				"FF.COUNTRY_OF_REGISTRATION_RESIDENCE  ,\n" +
				"FF.NATIONALITY_CODE  ,\n" +
				"FF.CORRESPONDENT_BANK_ID  ,\n" +
				"FF.CORRESPONDENT_BRANCH_ID  ,\n" +
				"FF.CBID  ,\n" +
				"FF.BANK_INDICATOR  ,\n" +
				"FF.INDIVIDUAL_NON_INDIVIDUAL  ,\n" +
				"FF.ACCOUNT_COUNTRY_CODE  ,\n" +
				"FF.GROUP_MEMBER_ABBREVIATION  ,\n" +
				"FF.ACCOUNT_BRANCH  ,\n" +
				"FF.ACCOUNT_NUMBER_SERIAL  ,\n" +
				"FF.ACCOUNT_SUFFIX  ,\n" +
				"FF.ACCOUNT_REFERENCE  ,\n" +
				"FF.CARD_NUMBER  ,\n" +
				"FF.ACCOUNT_SHORT_NAME  ,\n" +
				"FF.CURRENCY  ,\n" +
				"FF.ACCOUNT_PRODUCT_TYPE  ,\n" +
				"FF.ACCOUNT_STATUS  ,\n" +
				"FF.ACCOUNT_TYPE  ,\n" +
				"FF.ACCOUNT_FILE  ,\n" +
				"FF.TRANSACTION_REF_NO  ,\n" +
				"FF.SYSTEM_CODE_EXT  ,\n" +
				"FF.EXTERNAL_ACCOUNT_NUMBER  ,\n" +
				"FF.INTERNATIONAL_BANK_ACCOUNT_NUMBER  ,\n" +
				"FF.EXTERNAL_ACCOUNT_GPS  ,\n" +
				"FF.LOB_HUB_CUSTOMER_GROUP  ,\n" +
				"FF.LOB_HUB_FACTA_CORP  ,\n" +
				"FACTA.HSBC_BUSINESS_GROUP LOB_HUB_FACTA_NON_CORP  ,\n" +
				"FF.LOB_HUB_GLEAM  ,\n" +
				"FF.LOB_DR_GBM  ,\n" +
				"FF.LOB_DR_CMB  ,\n" +
				"FF.LOB_DR_GPB  ,\n" +
				"FF.LOB_DR_RBWM  ,\n" +
				"FF.LOB_LOCAL  ,\n" +
				"FF.LOB_USE    ,\n" +
				"FF.GUCI      ,\n" +
				"FF.GRID      ,\n" +
				"FF.GID      ,\n" +
				"FF.DATA_SYSTEM\t\n" +
				"from CLU_ALL_COUNTRY_FLAT_FILE FF\n" +
				"LEFT OUTER JOIN NON_CORPORATE_FACTA_LOB FACTA\n" +
				"ON FF.COUNTRY_CODE=FACTA.COUNTRY_CODE\n" +
				"AND FF.DOMESTIC_CUSTOMER_GROUP_MEMBER=FACTA.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"AND FF.CUSTOMER_BRANCH_NUMBER=FACTA.CUSTOMER_BRANCH_NUMBER\n" +
				"AND FF.CUSTOMER_SERIAL_NUMBER=FACTA.CUSTOMER_SERIAL_NUMBER\n" +
				"--AND FF.MODE_FLAG=FACTA.MODE_FLAG\n" +
				"AND FF.COUNTRY='IN'\n" +
				"AND ACCOUNT_REFERENCE is not null\n" +
				"AND CUSTOMER_REFERENCE is not null;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table clu_poc.CLU_ALL_COUNTRY_FLAT_FILE partition (COUNTRY ='IN')\n" +
				"select \n" +
				"FF.FLAG_JOIN  ,\n" +
				"FF.COUNTRY_CODE,\n" +
				"FF.DOMESTIC_CUSTOMER_GROUP_MEMBER  ,\n" +
				"FF.CUSTOMER_BRANCH_NUMBER  ,\n" +
				"FF.CUSTOMER_SERIAL_NUMBER  ,\n" +
				"FF.MODE_FLAG  ,\n" +
				"FF.CUSTOMER_REFERENCE  ,\n" +
				"FF.CUSTOMER_REFERENCE_2  ,\n" +
				"FF.CUSTOMER_FULL_NAME  ,\n" +
				"FF.CUSTOMER_NAME  ,\n" +
				"FF.CUSTOMER_SHORT_NAME  ,\n" +
				"FF.INDUSTRY_CODE  ,\n" +
				"FF.MARKET_SECTOR_1  ,\n" +
				"FF.CUSTOMER_LEGAL_TYPE  ,\n" +
				"FF.GHO_CUSTOMER_CLASSIFICATION  ,\n" +
				"FF.ACCOUNT_OFFICER  ,\n" +
				"FF.CUSTOMER_STATUS  ,\n" +
				"FF.COUNTRY_OF_REGISTRATION_RESIDENCE  ,\n" +
				"FF.NATIONALITY_CODE  ,\n" +
				"FF.CORRESPONDENT_BANK_ID  ,\n" +
				"FF.CORRESPONDENT_BRANCH_ID  ,\n" +
				"FF.CBID  ,\n" +
				"FF.BANK_INDICATOR  ,\n" +
				"FF.INDIVIDUAL_NON_INDIVIDUAL  ,\n" +
				"FF.ACCOUNT_COUNTRY_CODE  ,\n" +
				"FF.GROUP_MEMBER_ABBREVIATION  ,\n" +
				"FF.ACCOUNT_BRANCH  ,\n" +
				"FF.ACCOUNT_NUMBER_SERIAL  ,\n" +
				"FF.ACCOUNT_SUFFIX  ,\n" +
				"FF.ACCOUNT_REFERENCE  ,\n" +
				"FF.CARD_NUMBER  ,\n" +
				"FF.ACCOUNT_SHORT_NAME  ,\n" +
				"FF.CURRENCY  ,\n" +
				"FF.ACCOUNT_PRODUCT_TYPE  ,\n" +
				"FF.ACCOUNT_STATUS  ,\n" +
				"FF.ACCOUNT_TYPE  ,\n" +
				"FF.ACCOUNT_FILE  ,\n" +
				"FF.TRANSACTION_REF_NO  ,\n" +
				"FF.SYSTEM_CODE_EXT  ,\n" +
				"FF.EXTERNAL_ACCOUNT_NUMBER  ,\n" +
				"FF.INTERNATIONAL_BANK_ACCOUNT_NUMBER  ,\n" +
				"FF.EXTERNAL_ACCOUNT_GPS  ,\n" +
				"FF.LOB_HUB_CUSTOMER_GROUP  ,\n" +
				"FACTA.HSBC_BUSINESS_GROUP LOB_HUB_FACTA_CORP  ,\n" +
				"FF.LOB_HUB_FACTA_NON_CORP  ,\n" +
				"FF.LOB_HUB_GLEAM  ,\n" +
				"FF.LOB_DR_GBM  ,\n" +
				"FF.LOB_DR_CMB  ,\n" +
				"FF.LOB_DR_GPB  ,\n" +
				"FF.LOB_DR_RBWM  ,\n" +
				"FF.LOB_LOCAL  ,\n" +
				"FF.LOB_USE    ,\n" +
				"FF.GUCI      ,\n" +
				"FF.GRID      ,\n" +
				"FF.GID      ,\n" +
				"FF.DATA_SYSTEM\t\n" +
				"from CLU_ALL_COUNTRY_FLAT_FILE FF\n" +
				"LEFT OUTER JOIN CORPORATE_FACTA_LOB FACTA\n" +
				"ON FF.COUNTRY_CODE=FACTA.COUNTRY_CODE\n" +
				"AND FF.DOMESTIC_CUSTOMER_GROUP_MEMBER=FACTA.DOMESTIC_CUSTOMER_GROUP_MEMBER\n" +
				"AND FF.CUSTOMER_BRANCH_NUMBER=FACTA.CUSTOMER_BRANCH_NUMBER\n" +
				"AND FF.CUSTOMER_SERIAL_NUMBER=FACTA.CUSTOMER_SERIAL_NUMBER\n" +
				"AND FF.COUNTRY='IN'\n" +
				"AND FF.ACCOUNT_REFERENCE is not null\n" +
				"AND FF.CUSTOMER_REFERENCE is not null;\n" +
				"\n" +
				"use clu_poc;\n" +
				"\n" +
				"insert overwrite table clu_poc.CLU_ALL_COUNTRY_FLAT_FILE partition (COUNTRY ='IN')\n" +
				"select \n" +
				"FF.FLAG_JOIN  ,\n" +
				"FF.COUNTRY_CODE,\n" +
				"FF.DOMESTIC_CUSTOMER_GROUP_MEMBER  ,\n" +
				"FF.CUSTOMER_BRANCH_NUMBER  ,\n" +
				"FF.CUSTOMER_SERIAL_NUMBER  ,\n" +
				"FF.MODE_FLAG  ,\n" +
				"FF.CUSTOMER_REFERENCE  ,\n" +
				"FF.CUSTOMER_REFERENCE_2  ,\n" +
				"FF.CUSTOMER_FULL_NAME  ,\n" +
				"FF.CUSTOMER_NAME  ,\n" +
				"FF.CUSTOMER_SHORT_NAME  ,\n" +
				"FF.INDUSTRY_CODE  ,\n" +
				"FF.MARKET_SECTOR_1  ,\n" +
				"FF.CUSTOMER_LEGAL_TYPE  ,\n" +
				"FF.GHO_CUSTOMER_CLASSIFICATION  ,\n" +
				"FF.ACCOUNT_OFFICER  ,\n" +
				"FF.CUSTOMER_STATUS  ,\n" +
				"FF.COUNTRY_OF_REGISTRATION_RESIDENCE  ,\n" +
				"FF.NATIONALITY_CODE  ,\n" +
				"FF.CORRESPONDENT_BANK_ID  ,\n" +
				"FF.CORRESPONDENT_BRANCH_ID  ,\n" +
				"FF.CBID  ,\n" +
				"FF.BANK_INDICATOR  ,\n" +
				"FF.INDIVIDUAL_NON_INDIVIDUAL  ,\n" +
				"FF.ACCOUNT_COUNTRY_CODE  ,\n" +
				"FF.GROUP_MEMBER_ABBREVIATION  ,\n" +
				"FF.ACCOUNT_BRANCH  ,\n" +
				"FF.ACCOUNT_NUMBER_SERIAL  ,\n" +
				"FF.ACCOUNT_SUFFIX  ,\n" +
				"FF.ACCOUNT_REFERENCE  ,\n" +
				"FF.CARD_NUMBER  ,\n" +
				"FF.ACCOUNT_SHORT_NAME  ,\n" +
				"FF.CURRENCY  ,\n" +
				"FF.ACCOUNT_PRODUCT_TYPE  ,\n" +
				"FF.ACCOUNT_STATUS  ,\n" +
				"FF.ACCOUNT_TYPE  ,\n" +
				"FF.ACCOUNT_FILE  ,\n" +
				"FF.TRANSACTION_REF_NO  ,\n" +
				"EXT.SYSTEM_CODE SYSTEM_CODE_EXT  ,\n" +
				"EXT.EXTERNAL_ACCOUNT_NUMBER  EXTERNAL_ACCOUNT_NUMBER,\n" +
				"FF.INTERNATIONAL_BANK_ACCOUNT_NUMBER  ,\n" +
				"FF.EXTERNAL_ACCOUNT_GPS  ,\n" +
				"FF.LOB_HUB_CUSTOMER_GROUP  ,\n" +
				"FF.LOB_HUB_FACTA_CORP  ,\n" +
				"FF.LOB_HUB_FACTA_NON_CORP  ,\n" +
				"FF.LOB_HUB_GLEAM  ,\n" +
				"FF.LOB_DR_GBM  ,\n" +
				"FF.LOB_DR_CMB  ,\n" +
				"FF.LOB_DR_GPB  ,\n" +
				"FF.LOB_DR_RBWM  ,\n" +
				"FF.LOB_LOCAL  ,\n" +
				"FF.LOB_USE    ,\n" +
				"FF.GUCI      ,\n" +
				"FF.GRID      ,\n" +
				"FF.GID      ,\n" +
				"FF.DATA_SYSTEM\n" +
				"from CLU_ALL_COUNTRY_FLAT_FILE FF\n" +
				"LEFT OUTER JOIN EXT_ACCT_FIELDS EXT\n" +
				"ON FF.COUNTRY_CODE=EXT.COUNTRY_CODE\n" +
				"AND FF.GROUP_MEMBER_ABBREVIATION=EXT.GROUP_MEMBER_ABBREVIATION\n" +
				"AND FF.CUSTOMER_BRANCH_NUMBER=EXT.ACCOUNT_BRANCH\n" +
				"AND FF.CUSTOMER_SERIAL_NUMBER=EXT.ACCOUNT_NUMBER_SERIAL\n" +
				"AND FF.ACCOUNT_SUFFIX=EXT.ACCOUNT_SUFFIX\n" +
				"AND FF.COUNTRY='IN'\n" +
				"AND FF.ACCOUNT_REFERENCE is not null\n" +
				"AND FF.CUSTOMER_REFERENCE is not null;";
*/
//		public static final String query = "insert into table leave_ft_2\n" +
//		"select l.* from leave_ft_1 l left outer join turnstile p on(l.EMPLOYEE_ID=p.emp_no) where p.emp_no is null;";

//		public static final String query = "insert into table leave_net\n" +
//				"select distinct o.ABSENCE_BEGIN_DT,\n" +
//				"i.ABSENCE_END_DT ,\n" +
//				"i.ABSENCE_BEGIN_TM ,\n" +
//				"i.ABSENCE_END_TM ,\n" +
//				"i.ORIGINAL_BEGIN_DT ,\n" +
//				"i.ABSENCE_REASON_CODE ,\n" +
//				"i.ABSENCE_REASON_NAME ,\n" +
//				"i.ABSENCE_REASON_DESC ,\n" +
//				"i.ABSENCE_TYPE_CODE ,\n" +
//				"i.ABSENCE_CATEGORY_CODE ,\n" +
//				"i.APPROVAL_STATUS ,\n" +
//				"i.DAYS_DURATION ,\n" +
//				"o.EMPLOYEE_ID ,\n" +
//				"i.ASSIGNMENT_INTEGRATION_ID ,\n" +
//				"i.INTEGRATION_ID  ,i.NOTIFIED_DT ,i.ipaddress,i.date1\n" +
//				"from nxlogs_ex3 i join leave_ft_2 o on(i.employee_id=o.employee_id) where i.date1 between o.ABSENCE_BEGIN_DT and o.ABSENCE_END_DT;";

//		public static final String query = "INSERT OVERWRITE TABLE leave_net1\n" +
//				"select distinct ABSENCE_BEGIN_DT,\n" +
//				"ABSENCE_END_DT ,\n" +
//				"ABSENCE_BEGIN_TM ,\n" +
//				"ABSENCE_END_TM ,\n" +
//				"ORIGINAL_BEGIN_DT ,\n" +
//				"ABSENCE_REASON_CODE ,\n" +
//				"ABSENCE_REASON_NAME ,\n" +
//				"ABSENCE_REASON_DESC ,\n" +
//				"ABSENCE_TYPE_CODE ,\n" +
//				"ABSENCE_CATEGORY_CODE ,\n" +
//				"APPROVAL_STATUS ,\n" +
//				"DAYS_DURATION ,\n" +
//				"EMPLOYEE_ID ,\n" +
//				"ASSIGNMENT_INTEGRATION_ID ,\n" +
//				"INTEGRATION_ID  ,NOTIFIED_DT ,ipaddress,date1,date1 FROM LEAVE_NET;";


//	public static final String query = "set hive.exec.dynamic.partition.mode=nonstrict;\n" +
//			"set hive.optimize.sort.dynamic.partition=false;\n" +
//			"\n" +
//			"use bank;\n" +
//			"\n" +
//			"drop table if exists bank.final;\n" +
//			"create table if not exists bank.final (account_id bigint, city varchar(20),creation_timestamp timestamp) partitioned by (instanceexecid bigint) stored as orc;\n" +
//			"\n" +
//			"alter table final add if not exists partition(instanceexecid=1);\n" +
//			"alter table final add if not exists partition(instanceexecid=2);\n" +
//			"alter table final add if not exists partition(instanceexecid=10);\n" +
//			"\n" +
//			"drop table if exists bank.account_base;\n" +
//			"CREATE TABLE if not exists bank.account_base\n" +
//			"(account_id bigint, city varchar(20),creation_timestamp timestamp, instanceexecid bigint, otherinfo varchar(100)) stored as orc;\n" +
//			"\n" +
//			"insert overwrite table bank.final partition (instanceexecid) select account_id,city,creation_timestamp,instanceexecid from bank.account_base;\n" +
//			"\n" +
//			"drop table if exists bank.tmp_table;\n" +
//			"CREATE TABLE if not exists bank.tmp_table\n" +
//			"(account_id bigint, city varchar(20),creation_timestamp timestamp,rownum int) stored as orc;\n" +
//			"\n" +
//			"insert overwrite table bank.tmp_table select * from (select account_id,city,cts,(row_number() over (partition by account_id order by cts desc )) as rn from (\n" +
//			"select account_id,city,min(creation_timestamp) as cts from  bank.final where instanceexecid in (1,10) group by account_id,city) t1)t2 where t2.rn <=2;\n" +
//			"\n" +
//			"insert overwrite table bank.final select account_id,city,creation_timestamp from tmp_table where rownum=1;\n" +
//			"insert overwrite table bank.final select account_id,city,creation_timestamp from tmp_table where rownum=2;\n" +
//			"\n" +
//			"alter table final drop partition (instanceexecid=10);";
//	public static final String query = "use bank;\n" +
//		"insert overwrite table bank.tmp_table select account_id, city, cts, rn from (select account_id,city,cts,(row_number() over (partition by account_id order by cts desc )) as rn from (select account_id,city,min(creation_timestamp) as cts from final where instanceexecid in (1,10) group by account_id,city) t1)t2 where t2.rn <=2;";

}