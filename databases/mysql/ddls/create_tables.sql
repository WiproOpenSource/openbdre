CREATE TABLE `bus_domain` (
  `bus_domain_id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(256) NOT NULL,
  `bus_domain_name` varchar(45) NOT NULL,
  `bus_domain_owner` varchar(45) NOT NULL,
  PRIMARY KEY (`bus_domain_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

CREATE TABLE `batch_status` (
  `batch_state_id` int(11) NOT NULL,
  `description` varchar(45) NOT NULL,
  PRIMARY KEY (`batch_state_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `process_type` (
  `process_type_id` int(11) NOT NULL,
  `process_type_name` varchar(45) NOT NULL,
  `parent_process_type_id` int(11),
  PRIMARY KEY (`process_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `exec_status` (
  `exec_state_id` int(11) NOT NULL,
  `description` varchar(45) NOT NULL,
  PRIMARY KEY (`exec_state_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `workflow_type` (
  `workflow_id` int(11) NOT NULL,
    `workflow_type_name` varchar(45) NOT NULL,
    PRIMARY KEY (`workflow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE  TABLE users (
  username VARCHAR(45) NOT NULL ,
  password VARCHAR(45) NOT NULL ,
  enabled TINYINT(1) DEFAULT '1' ,
 PRIMARY KEY (username));


CREATE TABLE user_roles (
  user_role_id INT(11) NOT NULL AUTO_INCREMENT,
  username VARCHAR(45) NOT NULL,
  ROLE VARCHAR(45) NOT NULL,
  PRIMARY KEY (user_role_id),
  UNIQUE KEY uni_username_role (ROLE,username),
  KEY fk_username_idx (username),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username));



CREATE TABLE `hive_tables` (
  `table_id` int(11) NOT NULL auto_increment,
  `comments` varchar(256) NOT NULL,
  `location_type` varchar(45) NOT NULL,
  `dbname` varchar(45) DEFAULT NULL,
  `batch_id_partition_col` varchar(45) DEFAULT NULL,
  `table_name` varchar(45) NOT NULL,
  `type` varchar(45) NOT NULL,
  `ddl` varchar(2048) NOT NULL,
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `servers` (
  `server_id` int(11) NOT NULL AUTO_INCREMENT,
  `server_type` varchar(45) NOT NULL,
  `server_name` varchar(45) NOT NULL,
  `server_metainfo` varchar(45) DEFAULT NULL,
  `login_user` varchar(45) DEFAULT NULL,
  `login_password` varchar(45) DEFAULT NULL,
  `ssh_private_key` varchar(512) DEFAULT NULL,
  `server_ip` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`server_id`)
) ENGINE=InnoDB AUTO_INCREMENT=123459 DEFAULT CHARSET=utf8;

CREATE TABLE `process_template` (
  `process_template_id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(256) NOT NULL,
  `add_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `process_name` varchar(45) NOT NULL,
  `bus_domain_id` int(11) NOT NULL,
  `process_type_id` int(11) NOT NULL,
  `parent_process_id` int(11) DEFAULT NULL,
  `can_recover` tinyint(1) DEFAULT '1',
  `batch_cut_pattern` varchar(45) DEFAULT NULL,
  `next_process_template_id` VARCHAR(256) DEFAULT '' NOT NULL,
  `delete_flag` tinyint(1) DEFAULT '0',
  `workflow_id` int(11) DEFAULT '1',
  PRIMARY KEY (`process_template_id`),
  KEY `bus_domain_id_template` (`bus_domain_id`),
  KEY `process_type_id1_template` (`process_type_id`),
  KEY `original_process_id1_template` (`parent_process_id`),
  CONSTRAINT `bus_domain_id_template` FOREIGN KEY (`bus_domain_id`) REFERENCES `bus_domain` (`bus_domain_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `original_process_id1_template` FOREIGN KEY (`parent_process_id`) REFERENCES `process_template` (`process_template_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `workflow_id_template` FOREIGN KEY (`workflow_id`) REFERENCES `workflow_type` (`workflow_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `process_type_id1_template` FOREIGN KEY (`process_type_id`) REFERENCES `process_type` (`process_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION

) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8;

DELIMITER $$

CREATE TRIGGER process_template_type_check_insert
     BEFORE INSERT ON `process_template` FOR EACH ROW
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
 IF NEW.process_type_id IN (6,7,8) AND (SELECT process_type_id from process_template where process_template_id=NEW.parent_process_id) != 5
          THEN
               SIGNAL SQLSTATE '45002'
  SET MESSAGE_TEXT = '6,7,8 process types should have etl process type(5) parent' ;
          END IF;
-- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error
 IF NEW.process_type_id IN (9,10,11) AND (SELECT process_type_id from process_template where process_template_id=NEW.parent_process_id) != 2
          THEN
               SIGNAL SQLSTATE '45003'
  SET MESSAGE_TEXT = '9,10,11 process types should have semantic process type(2) parent' ;
          END IF;
-- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error
 IF NEW.process_type_id IN (12) AND (SELECT process_type_id from process_template where process_template_id=NEW.parent_process_id) != 1
          THEN
               SIGNAL SQLSTATE '45003'
  SET MESSAGE_TEXT = '12 process types should have semantic process type(2) parent' ;
          END IF;
     END $$
DELIMITER ;



DELIMITER $$

CREATE TRIGGER process_template_type_check_update
     BEFORE UPDATE ON `process_template` FOR EACH ROW
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
		  IF NEW.process_type_id IN (6,7,8) AND (SELECT process_type_id from process_template where process_template_id=NEW.parent_process_id) != 5
          THEN
               SIGNAL SQLSTATE '45002'
			   SET MESSAGE_TEXT = '6,7,8 process types should have etl process type(5) parent' ;
          END IF;
-- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error
		  IF NEW.process_type_id IN (9,10,11) AND (SELECT process_type_id from process_template where process_template_id=NEW.parent_process_id) != 2
          THEN
               SIGNAL SQLSTATE '45003'
			   SET MESSAGE_TEXT = '9,10,11 process types should have semantic process type(2) parent' ;
          END IF;
-- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error
		  IF NEW.process_type_id IN (12) AND (SELECT process_type_id from process_template where process_template_id=NEW.parent_process_id) != 1
          THEN
               SIGNAL SQLSTATE '45003'
			   SET MESSAGE_TEXT = '12 process types should have semantic process type(2) parent' ;
          END IF;
     END;
$$
DELIMITER ;

SET GLOBAL sql_mode='NO_AUTO_VALUE_ON_ZERO';


CREATE TABLE `properties_template` (
  `process_template_id` int(11) NOT NULL,
  `config_group` varchar(10) NOT NULL,
  `prop_temp_key` varchar(128) NOT NULL,
  `prop_temp_value` varchar(2048) NOT NULL,
  `description` varchar(1028) NOT NULL,
  PRIMARY KEY (`process_template_id`,`prop_temp_key`),
  CONSTRAINT `process_template_id5` FOREIGN KEY (`process_template_id`) REFERENCES `process_template` (`process_template_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



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


CREATE TABLE `properties` (
  `process_id` int(11) NOT NULL,
  `config_group` varchar(10) NOT NULL,
  `prop_key` varchar(128) NOT NULL,
  `prop_value` varchar(2048) NOT NULL,
  `description` varchar(1028) NOT NULL,
  PRIMARY KEY (`process_id`,`prop_key`),
  CONSTRAINT `process_id4` FOREIGN KEY (`process_id`) REFERENCES `process` (`process_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


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


CREATE TABLE `instance_exec` (
  `instance_exec_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `process_id` int(11) NOT NULL,
  `start_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `end_ts` timestamp NULL DEFAULT NULL,
  `exec_state` int(11) NOT NULL,
  PRIMARY KEY (`instance_exec_id`),
  KEY `process_id_instance_exec` (`process_id`),
  KEY `exec_state_instance_exec` (`exec_state`),
  CONSTRAINT `process_id_instance_exec` FOREIGN KEY (`process_id`) REFERENCES `process` (`process_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `exec_state_instance_exec` FOREIGN KEY (`exec_state`) REFERENCES `exec_status` (`exec_state_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=174 DEFAULT CHARSET=utf8;


CREATE TABLE `batch` (
  `batch_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `source_instance_exec_id` bigint(20) DEFAULT NULL,
  `batch_type` varchar(45) NOT NULL,
  PRIMARY KEY (`batch_id`),
  KEY `instance_exec_id` (`source_instance_exec_id`),
  CONSTRAINT `instance_exec_id` FOREIGN KEY (`source_instance_exec_id`) REFERENCES `instance_exec` (`instance_exec_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=212 DEFAULT CHARSET=utf8;


CREATE TABLE `file` (
  `batch_id` bigint(20) NOT NULL,
  `server_id` int(11) NOT NULL,
  `path` varchar(256) NOT NULL,
  `file_size` bigint(20) NOT NULL,
  `file_hash` varchar(100) DEFAULT NULL,
  `creation_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY `server_id` (`server_id`),
  KEY `unique_batch` (`batch_id`),
  CONSTRAINT `server_id` FOREIGN KEY (`server_id`) REFERENCES `servers` (`server_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `unique_batch` FOREIGN KEY (`batch_id`) REFERENCES `batch` (`batch_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `batch_consump_queue` (
  `source_batch_id` bigint(20) NOT NULL,
  `target_batch_id` bigint(20) DEFAULT NULL,
  `queue_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `insert_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `source_process_id` int(11) DEFAULT NULL,
  `start_ts` timestamp NULL DEFAULT NULL,
  `end_ts` timestamp NULL DEFAULT NULL,
  `batch_state` int(11) NOT NULL,
  `batch_marking` varchar(45) DEFAULT NULL,
  `process_id` int(11) NOT NULL,
  PRIMARY KEY (`queue_id`),
  KEY `source_batch_bcq` (`source_batch_id`),
  KEY `target_batch_bcq` (`target_batch_id`),
  KEY `batch_state_bcq` (`batch_state`),
  KEY `process_id_bcq` (`process_id`),
  CONSTRAINT `batch_state_bcq` FOREIGN KEY (`batch_state`) REFERENCES `batch_status` (`batch_state_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `process_id_bcq` FOREIGN KEY (`process_id`) REFERENCES `process` (`process_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `source_batch_bcq` FOREIGN KEY (`source_batch_id`) REFERENCES `batch` (`batch_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `target_batch_bcq` FOREIGN KEY (`target_batch_id`) REFERENCES `batch` (`batch_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1330 DEFAULT CHARSET=utf8;


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


CREATE TABLE `intermediate` (
  `uuid` varchar(64) NOT NULL,
  `inter_key` varchar(128) NOT NULL,
  `inter_value` varchar(2048) NOT NULL,
  PRIMARY KEY (`inter_key`,`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;





CREATE TABLE `process_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `add_ts` timestamp,
  `process_id` int(11) NOT NULL,
  `log_category` varchar(10) NOT NULL,
  `message_id` varchar(128) NOT NULL,
  `message` varchar(1024) NOT NULL,
  `instance_ref` bigint(20),
  PRIMARY KEY (`log_id`),
  CONSTRAINT `process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`process_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;





-- lineage table ddls

CREATE TABLE `lineage_node_type` (
  `node_type_id` int(11) NOT NULL,
  `node_type_name` varchar(45) NOT NULL,
  PRIMARY KEY (`node_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `lineage_query_type` (
  `query_type_id` INT(11) NOT NULL,
  `query_type_name` varchar(255) NOT NULL,
  PRIMARY KEY (`query_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `lineage_query` (
  `query_id` varchar(100) NOT NULL,
  `query_string` longtext ,
  `query_type_id` int(11) NOT NULL,
  `create_ts` timestamp DEFAULT CURRENT_TIMESTAMP,
  `process_id` int(11),
  `instance_exec_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`query_id`),
  KEY `process_id` (`process_id`),
  KEY `query_type_id` (`query_type_id`),
  -- CONSTRAINT `process_id` FOREIGN KEY (`process_id`) REFERENCES `process` (`process_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `query_type_id` FOREIGN KEY (`query_type_id`) REFERENCES `lineage_query_type` (`query_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `lineage_node` (
  `node_id` varchar(100) NOT NULL,
  `node_type_id` int(11) NOT NULL,
  `container_node_id` varchar(100) DEFAULT NULL,
  `node_order` int(11) DEFAULT '0',
  `insert_ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_ts` timestamp NULL DEFAULT NULL,
  `dot_string` longtext,
  `dot_label` longtext,
  `display_name` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`node_id`),
  KEY `node_type` (`node_type_id`),
  KEY `conatiner_node_id` (`container_node_id`),
  CONSTRAINT `conatiner_node_id` FOREIGN KEY (`container_node_id`) REFERENCES `lineage_node` (`node_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `node_type` FOREIGN KEY (`node_type_id`) REFERENCES `lineage_node_type` (`node_type_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `lineage_relation` (
  `relation_id` varchar(100) NOT NULL,
  `src_node_id` varchar(100) DEFAULT NULL,
  `target_node_id` varchar(100) DEFAULT NULL,
  `query_id` varchar(100) NOT NULL,
  `dot_string` longtext,
  PRIMARY KEY (`relation_id`),
  KEY `src_node_id` (`src_node_id`),
  KEY `target_node_id` (`target_node_id`),
  KEY `query_id` (`query_id`),
  CONSTRAINT `src_node_id` FOREIGN KEY (`src_node_id`) REFERENCES `lineage_node` (`node_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `target_node_id` FOREIGN KEY (`target_node_id`) REFERENCES `lineage_node` (`node_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `query_id` FOREIGN KEY (`query_id`) REFERENCES `lineage_query` (`query_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `deploy_status` (
  `deploy_status_id` smallint NOT NULL,
  `description` varchar(45) NOT NULL,
  PRIMARY KEY (`deploy_status_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `general_config` (
  `config_group` varchar(128) NOT NULL,
  `gc_key` varchar(128) NOT NULL,
  `gc_value` varchar(2048)  NULL,
  `description` varchar(1028) NOT NULL,
  `required` tinyint(1)  DEFAULT '0',
  `default_val` varchar(2048)  NULL,
  `type` varchar(20) NOT NULL DEFAULT 'text',
  `enabled` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`config_group`,`gc_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


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




CREATE TABLE Docidsdb (docid int not null auto_increment, url varchar(3000), primary key (docid));

CREATE TABLE Statisticsdb (uniqid bigint not null auto_increment, value bigint, name varchar(255), primary key (uniqid));

CREATE TABLE Pendingurlsdb (uniqid bigint not null auto_increment, pid bigint, instanceexecid bigint, url varchar(3000), docid int not null, parentdocid int not null, parenturl varchar(1000), depth smallint not null, domain varchar(255), subdomain varchar(255), path varchar(1000), anchor varchar(255),priority tinyint not null, tag varchar(255), primary key (uniqid));

CREATE TABLE Weburlsdb (uniqid bigint not null auto_increment, pid bigint, instanceexecid bigint, url varchar(3000), docid int not null, parentdocid int not null, parenturl varchar(1000), depth smallint not null, domain varchar(255), subdomain varchar(255), path varchar(1000), anchor varchar(255),priority tinyint not null, tag varchar(255), primary key (uniqid));

