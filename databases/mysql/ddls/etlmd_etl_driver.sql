
-- MySQL dump 10.13  Distrib 5.6.17, for Win32 (x86)
--
-- Host: 127.0.0.1    Database: etlmd
-- ------------------------------------------------------
-- Server version	5.6.21

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
-- Table structure for table `etl_driver`
--

DROP TABLE IF EXISTS `etl_driver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE IF NOT EXISTS `etl_driver` (
  `etl_process_id` INT NOT NULL,
  `raw_table_id` INT NOT NULL,
  `base_table_id` INT NULL COMMENT 'Core may be null if we don\'t want to do R2B',
  `insert_type` SMALLINT NULL,
  `drop_raw` TINYINT(1) DEFAULT '0',
  `raw_view_id` INT NOT NULL,
  PRIMARY KEY (`etl_process_id`),

  CONSTRAINT `table_id_etl_driver` FOREIGN KEY (`raw_table_id`) REFERENCES `hive_tables` (`table_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `table_id2_etl_driver` FOREIGN KEY (`base_table_id`) REFERENCES `hive_tables` (`table_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `batch_id_etl_driver` FOREIGN KEY (`etl_process_id`) REFERENCES `process` (`process_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `view_id_etl_driver` FOREIGN KEY (`raw_view_id`) REFERENCES `hive_tables` (`table_id`)  ON DELETE NO ACTION ON UPDATE NO ACTION)
  ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8;

