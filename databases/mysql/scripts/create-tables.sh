#!/bin/sh
mysqluser=$1
mysqlpwd=$2
dbname=$3
mysql_host=$4
mysql_port=$5

#echo "Please enter username: "
#read mysqluser
#echo "Please enter mysqlpwd: "
#read mysqlpwd
#echo "Please enter dbname: "
#read dbname
#echo "Please enter mysql_host: "
#read mysql_host
#echo "Please enter mysql_port: "
#read mysql_port

mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_workflow_type.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_user.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_bus_domain.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_batch_status.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_process_type.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_exec_status.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_hive_tables.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_servers.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_instance_exec.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_batch.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_file.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_batch_consump_queue.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_archive_consump_queue.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_intermediate.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_process.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_properties.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_process_log.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_etl_driver.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_etljob.sql

# data lineage
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_data_lineage.sql
# process template
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_process_template.sql
# properties template
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_properties_template.sql


# process deploy
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_process_deployment_queue.sql
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_deploy_status.sql

# crawler table
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_crawler.sql

# General Config Table and entries
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < mysql/ddls/etlmd_general_config.sql

# Setup data
mysql -h $mysql_host -P $mysql_port -u $mysqluser --password=$mysqlpwd $dbname < db_setup.sql

