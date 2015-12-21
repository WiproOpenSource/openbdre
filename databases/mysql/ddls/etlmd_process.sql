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

DROP TABLE IF EXISTS `process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `process` (
  `process_id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(256) NOT NULL,
  `add_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `process_name` varchar(45) NOT NULL,
  `bus_domain_id` int(11) NOT NULL,
  `process_type_id` int(11) NOT NULL,
  `parent_process_id` int(11) DEFAULT NULL,
  `can_recover` tinyint(1) DEFAULT '1',
  `enqueuing_process_id` int(11) NOT NULL DEFAULT '0',
  `batch_cut_pattern` varchar(45) DEFAULT NULL,
  `next_process_id` varchar(256) NOT NULL DEFAULT '',
  `delete_flag` tinyint(1) DEFAULT '0',
  `workflow_id` int(11) DEFAULT '1',
  `process_template_id` int(11) DEFAULT '0',
  `edit_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`process_id`),
  KEY `bus_domain_id` (`bus_domain_id`),
  KEY `process_type_id1` (`process_type_id`),
  KEY `original_process_id1` (`parent_process_id`),
  KEY `workflow_id` (`workflow_id`),
  KEY `process_template_id` (`process_template_id`),
  CONSTRAINT `bus_domain_id` FOREIGN KEY (`bus_domain_id`) REFERENCES `bus_domain` (`bus_domain_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `original_process_id1` FOREIGN KEY (`parent_process_id`) REFERENCES `process` (`process_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `process_ibfk_1` FOREIGN KEY (`process_template_id`) REFERENCES `process_template` (`process_template_id`),
  CONSTRAINT `process_type_id1` FOREIGN KEY (`process_type_id`) REFERENCES `process_type` (`process_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `workflow_id` FOREIGN KEY (`workflow_id`) REFERENCES `workflow_type` (`workflow_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-12-18 12:42:30

drop trigger if exists process_type_check_insert;
DELIMITER $$

CREATE TRIGGER process_type_check_insert
     BEFORE INSERT ON `process` FOR EACH ROW
     BEGIN
          IF NEW.process_type_id IN (1,2,3,4,5) AND NEW.parent_process_id IS NOT NULL
          THEN
               SIGNAL SQLSTATE '45000'
  SET MESSAGE_TEXT = '1,2,3,4,5 process types are applicable for parent processes only' ;
          END IF;

 IF NEW.process_type_id IN (6,7,8,9,10,11,12) AND NEW.parent_process_id IS NULL
          THEN
               SIGNAL SQLSTATE '45001'
  SET MESSAGE_TEXT = '6,7,8,9,10,11,12 process types are applicable for sub processes only' ;
          END IF;
-- if new rows parent does not belong to etl group where the new row type are 6,7,8 throw error
 IF NEW.process_type_id IN (6,7,8) AND (SELECT process_type_id from process where process_id=NEW.parent_process_id) != 5
          THEN
               SIGNAL SQLSTATE '45002'
  SET MESSAGE_TEXT = '6,7,8 process types should have etl process type(5) parent' ;
          END IF;
-- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error
 IF NEW.process_type_id IN (9,10,11) AND (SELECT process_type_id from process where process_id=NEW.parent_process_id) != 2
          THEN
               SIGNAL SQLSTATE '45003'
  SET MESSAGE_TEXT = '9,10,11 process types should have semantic process type(2) parent' ;
          END IF;
-- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error
 IF NEW.process_type_id IN (12) AND (SELECT process_type_id from process where process_id=NEW.parent_process_id) != 1
          THEN
               SIGNAL SQLSTATE '45003'
  SET MESSAGE_TEXT = '12 process types should have semantic process type(2) parent' ;
          END IF;
     END $$
DELIMITER ;


drop trigger if exists process_type_check_update;
DELIMITER $$

CREATE TRIGGER process_type_check_update
     BEFORE UPDATE ON `process` FOR EACH ROW
     BEGIN

      SET NEW.edit_ts = current_timestamp;

          IF NEW.process_type_id IN (1,2,3,4,5) AND NEW.parent_process_id IS NOT NULL
          THEN
               SIGNAL SQLSTATE '45000'

			   SET MESSAGE_TEXT = '1,2,3,4,5 process types are applicable for parent processes only' ;
          END IF;

		  IF NEW.process_type_id IN (6,7,8,9,10,11,12) AND NEW.parent_process_id IS NULL
          THEN
               SIGNAL SQLSTATE '45001'
			   SET MESSAGE_TEXT = '6,7,8,9,10,11,12 process types are applicable for sub processes only' ;
          END IF;
-- if new rows parent does not belong to etl group where the new row type are 6,7,8 throw error
		  IF NEW.process_type_id IN (6,7,8) AND (SELECT process_type_id from process where process_id=NEW.parent_process_id) != 5
          THEN
               SIGNAL SQLSTATE '45002'
			   SET MESSAGE_TEXT = '6,7,8 process types should have etl process type(5) parent' ;
          END IF;
-- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error
		  IF NEW.process_type_id IN (9,10,11) AND (SELECT process_type_id from process where process_id=NEW.parent_process_id) != 2
          THEN
               SIGNAL SQLSTATE '45003'
			   SET MESSAGE_TEXT = '9,10,11 process types should have semantic process type(2) parent' ;
          END IF;
-- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error
		  IF NEW.process_type_id IN (12) AND (SELECT process_type_id from process where process_id=NEW.parent_process_id) != 1
          THEN
               SIGNAL SQLSTATE '45003'
			   SET MESSAGE_TEXT = '12 process types should have semantic process type(2) parent' ;
          END IF;

     END;
$$
DELIMITER ;

