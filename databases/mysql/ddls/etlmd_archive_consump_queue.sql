
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
-- Table structure for table `archive_consump_queue`
--

DROP TABLE IF EXISTS `archive_consump_queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `archive_consump_queue` (
  `source_batch_id` bigint(20) NOT NULL,
  `target_batch_id` bigint(20) DEFAULT NULL,
  `queue_id` bigint(20) NOT NULL auto_increment,
  `insert_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `source_process_id` int(11) DEFAULT NULL,
  `start_ts` timestamp NULL DEFAULT NULL,
  `end_ts` timestamp NULL DEFAULT NULL,
  `batch_state` int(11) NOT NULL,
  `batch_marking` varchar(45) DEFAULT NULL,
  `process_id` int(11) NOT NULL,
  PRIMARY KEY (`queue_id`),
  KEY `source_batch_archive_consump_queue` (`source_batch_id`),
  KEY `target_batch_archive_consump_queue` (`target_batch_id`),
  KEY `batch_state_archive_consump_queue` (`batch_state`),
  CONSTRAINT `process_id_archive_consump_queue` FOREIGN KEY (`process_id`) REFERENCES `process` (`process_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `batch_state_archive_consump_queue` FOREIGN KEY (`batch_state`) REFERENCES `batch_status` (`batch_state_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `source_batch_archive_consump_queue` FOREIGN KEY (`source_batch_id`) REFERENCES `batch` (`batch_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `target_batch_archive_consump_queue` FOREIGN KEY (`target_batch_id`) REFERENCES `batch` (`batch_id`) ON DELETE NO ACTION ON UPDATE NO ACTION

) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-12-18 12:42:29
