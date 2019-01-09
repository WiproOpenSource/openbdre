create database if not exists raw;
use raw;
CREATE TABLE if not exists `employee`(
  `empno` int,
  `name` string,
  `deptno` int,
  `salary` string)
ROW FORMAT DELIMITED
  FIELDS TERMINATED BY '\u0001'
  COLLECTION ITEMS TERMINATED BY '\u0002'
  MAP KEYS TERMINATED BY '\u0003'
STORED AS INPUTFORMAT
  'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat';


CREATE TABLE  if not exists `department`(
  `deptno` int,
  `deptname` string)
ROW FORMAT DELIMITED
  FIELDS TERMINATED BY '\u0001'
  COLLECTION ITEMS TERMINATED BY '\u0002'
  MAP KEYS TERMINATED BY '\u0003'
STORED AS INPUTFORMAT
  'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat';


CREATE TABLE  if not exists `current_employee`(
  `empname` string,
  `deptname` string)
ROW FORMAT DELIMITED
  FIELDS TERMINATED BY '\u0001'
  COLLECTION ITEMS TERMINATED BY '\u0002'
  MAP KEYS TERMINATED BY '\u0003'
STORED AS INPUTFORMAT
  'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat';


CREATE TABLE  if not exists `current_employee_2`(
  `salary` string,
  `deptname` string,
  `empname` string)
ROW FORMAT DELIMITED
  FIELDS TERMINATED BY '\u0001'
  COLLECTION ITEMS TERMINATED BY '\u0002'
  MAP KEYS TERMINATED BY '\u0003'
STORED AS INPUTFORMAT
  'org.apache.hadoop.mapred.TextInputFormat'
OUTPUTFORMAT
  'org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat';

add jar /home/cloudera/hive-plugin-1.1-SNAPSHOT-jar-with-dependencies.jar;
set hive.exec.post.hooks=com.wipro.ats.bdre.hiveplugin.hook.LineageHook;
insert overwrite table current_employee_2 select e.salary, d.deptname, e.name from employee e, department d where e.deptno = d.deptno;
