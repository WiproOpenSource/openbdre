

DROP TABLE IF EXISTS `lineage_relation`;
DROP TABLE IF EXISTS `lineage_node`;
DROP TABLE IF EXISTS `lineage_query`;
DROP TABLE IF EXISTS `lineage_query_type`;
DROP TABLE IF EXISTS `lineage_node_type`;

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

