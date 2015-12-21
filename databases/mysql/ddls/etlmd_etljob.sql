-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: etlmd
-- ------------------------------------------------------
-- Server version 5.6.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `hive_tables`
--

DROP TABLE IF EXISTS `etlstep`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `etlstep` (
  `uuid` varchar(128) NOT NULL,
  `serial_number` bigint(20) NOT NULL,
  `bus_domain_id` int(11) NOT NULL,
  `process_name` varchar(256) NOT NULL,
  `description` varchar(2048) NOT NULL,
  `base_table_name` varchar(45) DEFAULT NULL,
  `raw_table_name` varchar(45) DEFAULT NULL,
  `raw_view_name` varchar(45) DEFAULT NULL,
  `base_db_name` varchar(45) DEFAULT NULL,
  `raw_db_name` varchar(45) DEFAULT NULL,
  `base_table_ddl` varchar(2048) DEFAULT NULL,
  `raw_table_ddl` varchar(2048) DEFAULT NULL,
  `raw_view_ddl` varchar(2048) DEFAULT NULL,
  `raw_partition_col` varchar(45) DEFAULT NULL,
  `drop_raw` tinyint(1) DEFAULT NULL,
  `enq_id` int(11) DEFAULT NULL,
  `column_info` varchar(2048) DEFAULT NULL,
  `serde_properties` varchar(2048) DEFAULT NULL,
  `table_properties` varchar(2048) DEFAULT NULL,
  `input_format` varchar(2048) DEFAULT NULL,
  PRIMARY KEY (`serial_number`,`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client=@saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;



-- Dump completed on 2014-12-18 12:42:30