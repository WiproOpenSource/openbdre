#!/bin/bash

database=h2
hibernate_connection_driver_class=org_h2_Driver
hibernate_connection_url=jdbc:h2:~/bdre
hibernate_connection_username=root
hibernate_connection_password=root
hibernate_dialect=org_hibernate_dialect_H2Dialect
hibernate_default_schema=BDRE

function writeDBConf {

     echo "hibernate.current_session_context_class=thread" > $(dirname $0)/md-dao/src/main/resources/db.properties
     echo "hibernate.transaction.factory_class=org.hibernate.transaction.JDBCTransactionFactory" >> $(dirname $0)/md-dao/src/main/resources/db.properties
     echo "hibernate.show_sql=true" >> $(dirname $0)/md-dao/src/main/resources/db.properties
     echo database=$database >> $(dirname $0)/md-dao/src/main/resources/db.properties
     echo hibernate.connection.driver_class=$hibernate_connection_driver_class >> $(dirname $0)/md-dao/src/main/resources/db.properties
     echo hibernate.connection.url=$hibernate_connection_url >> $(dirname $0)/md-dao/src/main/resources/db.properties
     echo hibernate.connection.username=$hibernate_connection_username >> $(dirname $0)/md-dao/src/main/resources/db.properties
     echo hibernate.connection.password=$hibernate_connection_password >> $(dirname $0)/md-dao/src/main/resources/db.properties
     echo hibernate.dialect=$hibernate_dialect >> $(dirname $0)/md-dao/src/main/resources/db.properties
     echo hibernate.default_schema=$hibernate_default_schema >> $(dirname $0)/md-dao/src/main/resources/db.properties
}

echo Supported DB
echo "1) H2 (Default - Good for running BDRE user interface only. )"
echo "2) Oracle"
echo "3) MySQL"
echo "4) PostgreSQL"
echo

read -p "Select Database Type(Enter 1, 2, 3 , 4 or leave empty and press empty to select the default DB):" var_dbtype

if [ -n "$var_dbtype" ]; then
    echo
else
    var_dbtype=1
fi


read -p "Enter DB username (Type username or leave it blank for default 'root'): " var_username
read -p "Enter DB password (Type username or leave it blank for default 'root'): " var_password


if [ -n "$var_username" ]; then
    echo
else
    var_username='root'
fi
if [ -n "$var_password" ]; then
    echo
else
    var_password='root'
fi

if [ $var_dbtype -eq 2 ]; then
    read -p "Enter DB hostname (Type db hostname or leave it blank for default 'localhost'): " var_host
    read -p "Enter DB port (Type db port or leave it blank for default '1521'): " var_port
    read -p "Enter DB name (Type db name or leave it blank for default 'xe'): " var_dbname
    read -p "Enter DB schema (Type schema or leave it blank for default '$var_username'): " var_schema
    if [ -n "$var_host" ]; then
        echo
    else
        var_host='localhost'
    fi
    if [ -n "$var_port" ]; then
        echo
    else
        var_port='1521'
    fi
    if [ -n "$var_dbname" ]; then
        echo
    else
        var_dbname='xe'
    fi
    if [ -z "$var_schema" ]; then
        var_schema=$var_username
    fi


    database=oracle
    hibernate_connection_driver_class=oracle.jdbc.driver.OracleDriver
    hibernate_connection_url=jdbc:oracle:thin:@$var_host:$var_port/$var_dbname
    hibernate_connection_username=$var_username
    hibernate_connection_password=$var_password
    hibernate_dialect=org.hibernate.dialect.Oracle10gDialect
    hibernate_default_schema=$var_schema

elif [ $var_dbtype -eq 3 ]; then
    read -p "Enter DB hostname (Type db hostname or leave it blank for default 'localhost'): " var_host
    read -p "Enter DB port (Type db port or leave it blank for default '3306'): " var_port
    read -p "Enter DB name (Type db name or leave it blank for default 'platmd'): " var_dbname
    read -p "Enter DB schema (Type schema or leave it blank for default '$var_dbname'): " var_schema

    if [ -n "$var_host" ]; then
        echo
    else
        var_host='localhost'
    fi
    if [ -n "$var_port" ]; then
        echo
    else
        var_port='3306'
    fi
    if [ -n "$var_dbname" ]; then
        echo
    else
        var_dbname='platmd'
    fi
    if [ -z "$var_schema" ]; then
        var_schema=$var_dbname
    fi


    database=mysql
    hibernate_connection_driver_class="com.mysql.jdbc.Driver"
    hibernate_connection_url="jdbc:mysql://$var_host:$var_port/$var_dbname"
    hibernate_connection_username="$var_username"
    hibernate_connection_password="$var_password"
    hibernate_dialect="org.hibernate.dialect.MySQLDialect"
    hibernate_default_schema="$var_schema"

elif [ $var_dbtype -eq 4 ]; then
   read -p "Enter DB hostname (Type db hostname or leave it blank for default 'localhost'): " var_host
   read -p "Enter DB port (Type db port or leave it blank for default '5432'): " var_port
   read -p "Enter DB name (Type db name or leave it blank for default 'platmd'): " var_dbname
   read -p "Enter DB schema (Type schema or leave it blank for default '$var_username'): " var_schema

    if [ -n "$var_host" ]; then
        echo
    else
        var_host='localhost'
    fi
    if [ -n "$var_port" ]; then
        echo
    else
        var_port='5432'
    fi
    if [ -n "$var_dbname" ]; then
        echo
    else
        var_dbname='platmd'
    fi
     if [ -z "$var_schema" ]; then
        var_schema=$var_username
     fi
   database=postgresql
   hibernate_connection_driver_class="org.postgresql.Driver"
   hibernate_connection_url="jdbc:postgresql://$var_host:$var_port/$var_dbname"
   hibernate_connection_username="$var_username"
   hibernate_connection_password="$var_password"
   hibernate_dialect="org.hibernate.dialect.PostgreSQLDialect"
   hibernate_default_schema="$var_schema"
else
   read -p "Enter DB file name (Type db file location for embedded H2 DB or leave it blank for default '~/bdre'): " var_dbname
    read -p "Enter DB schema (Type schema or leave it blank for default 'BDRE'): " var_schema
    if [ -n "$var_dbname" ]; then
        echo
    else
        var_dbname='~/bdre'
    fi
    if [ -z "$var_schema" ]; then
        var_schema="BDRE"
    fi
    database=h2
    hibernate_connection_driver_class="org.h2.Driver"
    hibernate_connection_url="jdbc:h2:$var_dbname"
    hibernate_connection_username=$var_username
    hibernate_connection_password=$var_password
    hibernate_dialect="org.hibernate.dialect.H2Dialect"
    hibernate_default_schema="$var_schema"
fi

echo "Please confirm:"
echo
echo Database Type: $database
echo JDBC Driver Class: $hibernate_connection_driver_class
echo JDBC Connection URL: $hibernate_connection_url
echo Database Username: $hibernate_connection_username
echo Database Password: $hibernate_connection_password
echo Hibernate Dialect: $hibernate_dialect
echo Database Schema: $hibernate_default_schema

read -p "Are those correct? (type y or n - default y): " var_confirm

if [ -z "$var_confirm" ]; then
    writeDBConf
    echo "Database configuration written to $(dirname $0)/md-dao/src/main/resources/db.properties"
elif [ "$var_confirm" == "y" ]; then
    writeDBConf
    echo "Database configuration written to $(dirname $0)/md-dao/src/main/resources/db.properties"
elif [ "$var_confirm" == "n" ]; then
    echo "Exiting. Please run this script again"
else
    echo "Exiting. Please run this script again"
fi

if [ $var_dbtype -eq 3 ]; then
    mysql -u$var_password -p$var_username -e "create database if not exists $var_dbname"
    mysql -u$var_password -p$var_username $var_dbname < $(dirname $0)/databases/mysql/ddls/drop_tables.sql
    mysql -u$var_password -p$var_username $var_dbname < $(dirname $0)/databases/mysql/ddls/create_tables.sql
    if [ $? -eq 0 ]; then
        echo "Tables created successfully in MySQL $var_dbnam DB"
    fi
fi
