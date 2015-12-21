DROP TABLE if exists `properties_template`;

CREATE TABLE `properties_template` (
  `process_template_id` int(11) NOT NULL,
  `config_group` varchar(10) NOT NULL,
  `prop_temp_key` varchar(128) NOT NULL,
  `prop_temp_value` varchar(2048) NOT NULL,
  `description` varchar(1028) NOT NULL,
  PRIMARY KEY (`process_template_id`,`prop_temp_key`),
  CONSTRAINT `process_template_id5` FOREIGN KEY (`process_template_id`) REFERENCES `process_template` (`process_template_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8