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
-- Table structure for table `process`
--
DROP TABLE IF EXISTS `process_deployment_queue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process_deployment_queue` (
   `deployment_id` bigint(20) NOT NULL AUTO_INCREMENT,
   `process_id` int(11) NOT NULL ,
   `start_ts` timestamp NULL DEFAULT NULL,
   `insert_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `end_ts` timestamp NULL DEFAULT NULL,
   `deploy_status_id` smallint NOT NULL DEFAULT 1,
   `user_name` varchar(45) NOT NULL,
   `bus_domain_id` int(11) NOT NULL,
   `process_type_id` int(11) NOT NULL,
   `deploy_script_location` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`deployment_id`),
  KEY `deploy_status_id` (`deploy_status_id`),
  KEY `deploy_process_id` (`process_id`),
  KEY `deploy_process_type_id` (`process_type_id`),
  KEY `deploy_bus_domain_id` (`bus_domain_id`),
  CONSTRAINT `deploy_status_id` FOREIGN KEY (`deploy_status_id`) REFERENCES `deploy_status` (`deploy_status_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `deploy_process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`process_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `deploy_process_type_id` FOREIGN KEY (`process_type_id`) REFERENCES `process_type` (`process_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `deploy_bus_domain_id` FOREIGN KEY (`bus_domain_id`) REFERENCES `bus_domain` (`bus_domain_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
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

 -- Dump completed on 2015-08-31 12:04:30
