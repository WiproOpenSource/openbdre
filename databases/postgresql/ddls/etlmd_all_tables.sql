/*etlmd_bus_domain.sql  */
DROP TABLE IF EXISTS bus_domain;

CREATE TABLE bus_domain (
  bus_domain_id SERIAL NOT NULL,
  description varchar(256) NOT NULL,
  bus_domain_name varchar(45) NOT NULL,
  bus_domain_owner varchar(45) NOT NULL,
  PRIMARY KEY (bus_domain_id)
);

/* etlmd_batch_status.sql */

DROP TABLE IF EXISTS batch_status;

CREATE TABLE batch_status (
  batch_state_id INT NOT NULL,
  description varchar(45) NOT NULL,
  PRIMARY KEY (batch_state_id)
);

/* etlmd_process_type.sql */


DROP TABLE IF EXISTS process_type;

CREATE TABLE process_type (
  process_type_id int NOT NULL,
  process_type_name varchar(45) NOT NULL,
  parent_process_type_id int,
  PRIMARY KEY (process_type_id)
);



/* etlmd_exec_status.sql */


DROP TABLE IF EXISTS exec_status;

CREATE TABLE exec_status (
  exec_state_id int NOT NULL,
  description varchar(45) NOT NULL,
  PRIMARY KEY (exec_state_id)
);


/*etlmd_workflow_type.sql*/
DROP TABLE IF EXISTS workflow_type;

CREATE TABLE workflow_type (
  workflow_id INT NOT NULL,
    workflow_type_name VARCHAR(45) NOT NULL,
    PRIMARY KEY (workflow_id)
);

/* etlmd_servers.sql */


DROP TABLE IF EXISTS servers;


CREATE TABLE servers (
  server_id SERIAL NOT NULL,
  server_type varchar(45) NOT NULL,
  server_name varchar(45) NOT NULL,
  server_metainfo varchar(45) DEFAULT NULL,
  login_user varchar(45) DEFAULT NULL,
  login_password varchar(45) DEFAULT NULL,
  ssh_private_key varchar(512) DEFAULT NULL,
  server_ip varchar(45) DEFAULT NULL,
  PRIMARY KEY (server_id)
);


/* etlmd_process_template.sql */

DROP TABLE IF EXISTS process_template;

CREATE TABLE process_template (
  process_template_id serial NOT NULL,
  description varchar(256) NOT NULL,
  add_ts timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  process_name varchar(45) NOT NULL,
  bus_domain_id int NOT NULL references bus_domain(bus_domain_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  process_type_id int NOT NULL references process_type(process_type_id) ON DELETE NO ACTION
ON UPDATE NO ACTION,
  parent_process_id int DEFAULT NULL references process_template(process_template_id) ON
DELETE NO ACTION ON UPDATE NO ACTION,
  can_recover boolean  DEFAULT 'true',
  batch_cut_pattern varchar(45) DEFAULT NULL,
  next_process_template_id VARCHAR(256) DEFAULT '' NOT NULL,
  delete_flag boolean  DEFAULT false,
  workflow_id int DEFAULT '1' references workflow_type(workflow_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  PRIMARY KEY (process_template_id)


);


/* etlmd_properties_template.sql */


DROP TABLE if exists properties_template;

CREATE TABLE properties_template (
  process_template_id int NOT NULL references process_template(process_template_id) ON
DELETE NO ACTION ON UPDATE NO ACTION,
  config_group varchar(10) NOT NULL,
  prop_temp_key varchar(128) NOT NULL,
  prop_temp_value varchar(2048) NOT NULL,
  description varchar(1028) NOT NULL,
  PRIMARY KEY (process_template_id,prop_temp_key)

);


/* etlmd_process.sql */


DROP TABLE IF EXISTS process;

CREATE TABLE process (
  process_id serial NOT NULL,
  description varchar(256) NOT NULL,
  add_ts timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  process_name varchar(45) NOT NULL,
  bus_domain_id int NOT NULL references bus_domain(bus_domain_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  process_type_id int NOT NULL references process_type(process_type_id) ON DELETE NO ACTION
ON UPDATE NO ACTION,
  parent_process_id int DEFAULT NULL references process(process_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  can_recover boolean  DEFAULT 'true',
  enqueuing_process_id int NOT NULL DEFAULT '0',
  batch_cut_pattern varchar(45) DEFAULT NULL,
  next_process_id varchar(256) NOT NULL DEFAULT '',
  delete_flag boolean  DEFAULT 'false',
  workflow_id int DEFAULT '1' references workflow_type(workflow_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  process_template_id int DEFAULT '0' references process_template(process_template_id) ON
DELETE NO ACTION ON UPDATE NO ACTION,
  edit_ts timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (process_id)

);


/* etlmd_properties.sql */

DROP TABLE IF EXISTS properties;

CREATE TABLE properties (
  process_id int NOT NULL references process(process_id) ON DELETE NO ACTION ON UPDATE NO
ACTION,
  config_group varchar(10) NOT NULL,
  prop_key varchar(128) NOT NULL,
  prop_value varchar(2048) NOT NULL,
  description varchar(1028) NOT NULL,
  PRIMARY KEY (process_id,prop_key)

);


/* etlmd_hive_tables.sql */

DROP TABLE IF EXISTS hive_tables;


CREATE TABLE hive_tables (
  table_id SERIAL NOT NULL,
  comments varchar(256) NOT NULL,
  location_type varchar(45) NOT NULL,
  dbname varchar(45) DEFAULT NULL,
  batch_id_partition_col varchar(45) DEFAULT NULL,
  table_name varchar(45) NOT NULL,
  type varchar(45) NOT NULL,
  ddl varchar(2048) NOT NULL,
  PRIMARY KEY (table_id)
);


/* etlmd_etl_driver.sql */

DROP TABLE IF EXISTS etl_driver;


CREATE TABLE IF NOT EXISTS etl_driver (
  etl_process_id INT NOT NULL references process(process_id) ON DELETE NO ACTION ON UPDATE
NO ACTION,
  raw_table_id INT NOT NULL references hive_tables(table_id) ON DELETE NO ACTION ON UPDATE
NO ACTION,
  base_table_id INT NULL references hive_tables(table_id) ON DELETE NO ACTION ON UPDATE NO
ACTION ,
  insert_type SMALLINT NULL,
  drop_raw boolean  DEFAULT false,
  raw_view_id INT NOT NULL references hive_tables(table_id) ON DELETE NO ACTION ON UPDATE NO
ACTION,
  PRIMARY KEY (etl_process_id)
);


/* etlmd_instance_exec.sql */


DROP TABLE IF EXISTS instance_exec;


CREATE TABLE instance_exec (
  instance_exec_id bigserial NOT NULL,
  process_id int NOT NULL references process(process_id) ON DELETE NO ACTION ON UPDATE NO
ACTION,
  start_ts timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  end_ts timestamp NULL DEFAULT NULL,
  exec_state int NOT NULL REFERENCES exec_status(exec_state_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  PRIMARY KEY (instance_exec_id)
);

/* etlmd_batch.sql */

DROP TABLE IF EXISTS batch;

CREATE TABLE batch (
  batch_id BIGSERIAL NOT NULL,
  source_instance_exec_id bigint DEFAULT NULL REFERENCES instance_exec(instance_exec_id) ON
DELETE NO ACTION ON UPDATE NO ACTION,
  batch_type varchar(45) NOT NULL,
  PRIMARY KEY (batch_id)
  );




/* etlmd_file.sql */

DROP TABLE IF EXISTS file;

CREATE TABLE file (
  batch_id bigint NOT NULL REFERENCES batch(batch_id) ON DELETE NO ACTION ON UPDATE NO
ACTION,
  server_id int NOT NULL references servers(server_id) ON DELETE NO ACTION ON UPDATE NO
ACTION,
  path varchar(256) NOT NULL,
  file_size bigint NOT NULL,
  file_hash varchar(100) DEFAULT NULL,
  creation_ts timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP

);


/* etlmd_batch_consump_queue.sql */

DROP TABLE IF EXISTS batch_consump_queue;


CREATE TABLE batch_consump_queue (
  source_batch_id bigint NOT NULL references batch(batch_id) ON DELETE NO ACTION ON UPDATE
NO ACTION,
  target_batch_id bigint DEFAULT NULL references batch (batch_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  queue_id bigserial NOT NULL,
  insert_ts timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  source_process_id int DEFAULT NULL,
  start_ts timestamp NULL DEFAULT NULL,
  end_ts timestamp NULL DEFAULT NULL,
  batch_state int NOT NULL references batch_status(batch_state_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  batch_marking varchar(45) DEFAULT NULL,
  process_id int NOT NULL REFERENCES process(process_id) ON DELETE NO ACTION ON UPDATE NO
ACTION,
  PRIMARY KEY (queue_id)

);

/* etlmd_archive_consump_queue.sql */

DROP TABLE IF EXISTS archive_consump_queue;

CREATE TABLE archive_consump_queue (
  source_batch_id bigint NOT NULL references batch(batch_id) ON DELETE NO ACTION ON UPDATE
NO ACTION,
  target_batch_id bigint DEFAULT NULL references batch(batch_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  queue_id bigserial NOT NULL,
  insert_ts timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  source_process_id int DEFAULT NULL,
  start_ts timestamp NULL DEFAULT NULL,
  end_ts timestamp NULL DEFAULT NULL,
  batch_state int NOT NULL references batch_status(batch_state_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  batch_marking varchar(45) DEFAULT NULL,
  process_id int NOT NULL references process(process_id) ON DELETE NO ACTION ON UPDATE NO
ACTION,
  PRIMARY KEY (queue_id)
 );


/* etlmd_etljob.sql */

DROP TABLE IF EXISTS etlstep;


CREATE TABLE etlstep (
  uuid varchar(128) NOT NULL,
  serial_number bigint NOT NULL,
  bus_domain_id int NOT NULL,
  process_name varchar(256) NOT NULL,
  description varchar(2048) NOT NULL,
  base_table_name varchar(45) DEFAULT NULL,
  raw_table_name varchar(45) DEFAULT NULL,
  raw_view_name varchar(45) DEFAULT NULL,
  base_db_name varchar(45) DEFAULT NULL,
  raw_db_name varchar(45) DEFAULT NULL,
  base_table_ddl varchar(2048) DEFAULT NULL,
  raw_table_ddl varchar(2048) DEFAULT NULL,
  raw_view_ddl varchar(2048) DEFAULT NULL,
  raw_partition_col varchar(45) DEFAULT NULL,
  drop_raw boolean DEFAULT false,
  enq_id int DEFAULT NULL,
  column_info varchar(2048) DEFAULT NULL,
  serde_properties varchar(2048) DEFAULT NULL,
  table_properties varchar(2048) DEFAULT NULL,
  input_format varchar(2048) DEFAULT NULL,
  PRIMARY KEY (serial_number,uuid)
);


/*etlmd_user.sql*/

DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;

CREATE  TABLE users (
  username VARCHAR(45) NOT NULL ,
  password VARCHAR(45) NOT NULL ,
  enabled boolean DEFAULT true ,
  PRIMARY KEY (username));


CREATE TABLE user_roles (
  user_role_id SERIAL NOT NULL,
  username VARCHAR(45) NOT NULL REFERENCES users(username),
  ROLE VARCHAR(45) NOT NULL,
  PRIMARY KEY (user_role_id),
  CONSTRAINT uni_username_role UNIQUE(ROLE,username));



/* etlmd_process_log.sql */

DROP TABLE IF EXISTS process_log;

CREATE TABLE process_log (
  log_id bigserial NOT NULL,
  add_ts timestamp,
  process_id int NOT NULL references process(process_id) ON DELETE NO ACTION ON UPDATE NO
ACTION,
  log_category varchar(10) NOT NULL,
  message_id varchar(128) NOT NULL,
  message varchar(1024) NOT NULL,
  instance_ref bigint,
  PRIMARY KEY (log_id)

);


/* etlmd_intermediate.sql */

DROP TABLE IF EXISTS intermediate;

CREATE TABLE intermediate (
  uuid varchar(64) NOT NULL,
  inter_key varchar(128) NOT NULL,
  inter_value varchar(2048) NOT NULL,
  PRIMARY KEY (inter_key,uuid)
);


/* etlmd_data_lineage.sql */



DROP TABLE IF EXISTS lineage_relation;
DROP TABLE IF EXISTS lineage_node;
DROP TABLE IF EXISTS lineage_query;
DROP TABLE IF EXISTS lineage_query_type;
DROP TABLE IF EXISTS lineage_node_type;

-- lineage table ddls

CREATE TABLE lineage_node_type (
  node_type_id int NOT NULL,
  node_type_name varchar(45) NOT NULL,
  PRIMARY KEY (node_type_id)
);

CREATE TABLE lineage_query_type (
  query_type_id INT NOT NULL,
  query_type_name varchar(255) NOT NULL,
  PRIMARY KEY (query_type_id)
);

CREATE TABLE lineage_query (
  query_id varchar(100) NOT NULL,
  query_string text ,
  query_type_id int NOT NULL references lineage_query_type(query_type_id) ON DELETE NO
ACTION ON UPDATE NO ACTION,
  create_ts timestamp DEFAULT CURRENT_TIMESTAMP,
  process_id int ,
  instance_exec_id bigint DEFAULT NULL,
  PRIMARY KEY (query_id)

);

CREATE TABLE lineage_node (
  node_id varchar(100) NOT NULL,
  node_type_id int NOT NULL REFERENCES lineage_node_type(node_type_id) ON DELETE NO ACTION
ON UPDATE NO ACTION,
  container_node_id varchar(100) DEFAULT NULL REFERENCES lineage_node(node_id) ON DELETE NO
ACTION ON UPDATE NO ACTION,
  node_order int DEFAULT '0',
  insert_ts timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_ts timestamp NULL DEFAULT NULL,
  dot_string text,
  dot_label text,
  display_name varchar(256) DEFAULT NULL,
  PRIMARY KEY (node_id)
 );

CREATE TABLE lineage_relation (
  relation_id varchar(100) NOT NULL,
  src_node_id varchar(100) DEFAULT NULL REFERENCES lineage_node(node_id) ON DELETE NO ACTION
ON UPDATE NO ACTION,
  target_node_id varchar(100) DEFAULT NULL REFERENCES lineage_node(node_id) ON DELETE NO ACTION
  ON UPDATE NO ACTION,
  query_id varchar(100) NOT NULL REFERENCES lineage_query(query_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
  dot_string text,
  PRIMARY KEY (relation_id)

);

/* etlmd_deploy_status.sql */

DROP TABLE IF EXISTS deploy_status;

CREATE TABLE deploy_status (
  deploy_status_id smallint NOT NULL,
  description varchar(45) NOT NULL,
  PRIMARY KEY (deploy_status_id)
);



/* etlmd_general_config.sql */


DROP TABLE IF EXISTS general_config;

CREATE TABLE general_config(
  config_group varchar(128) NOT NULL,
  gc_key varchar(128) NOT NULL,
  gc_value varchar(2048)  NULL,
  description varchar(1028) NOT NULL,
  required boolean  DEFAULT false,
  default_val varchar(2048)  NULL,
  type varchar(20) NOT NULL DEFAULT 'text',
  enabled boolean DEFAULT true,
  PRIMARY KEY (config_group,gc_key)
);




/* etlmd_process_deployment_queue.sql */


DROP TABLE IF EXISTS process_deployment_queue;

CREATE TABLE process_deployment_queue (
   deployment_id bigserial NOT NULL ,
   process_id int NOT NULL REFERENCES process(process_id) ON DELETE NO ACTION ON UPDATE NO
ACTION,
   start_ts timestamp NULL DEFAULT NULL,
   insert_ts timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   end_ts timestamp NULL DEFAULT NULL,
   deploy_status_id smallint NOT NULL DEFAULT 1 REFERENCES deploy_status (deploy_status_id)
ON DELETE NO ACTION ON UPDATE NO ACTION,
   user_name varchar(45) NOT NULL,
   bus_domain_id int NOT NULL REFERENCES bus_domain(bus_domain_id) ON DELETE NO ACTION ON
UPDATE NO ACTION,
   process_type_id int NOT NULL REFERENCES process_type(process_type_id) ON DELETE NO ACTION
ON UPDATE NO ACTION,
   deploy_script_location varchar(1000) DEFAULT NULL,
  PRIMARY KEY (deployment_id)

);

/* etlmd_crawler.sql */

DROP TABLE IF EXISTS Docidsdb;

CREATE TABLE Docidsdb (
   docid bigserial NOT NULL ,
   url varchar(3000),
   PRIMARY KEY (docid)
);

DROP TABLE IF EXISTS Statisticsdb;

CREATE TABLE Statisticsdb (
   uniqid bigserial NOT NULL,
   value bigint,
   name varchar(255),
   PRIMARY KEY (uniqid)
);

DROP TABLE IF EXISTS Pendingurlsdb;

CREATE TABLE Pendingurlsdb (
   uniqid bigserial NOT NULL,
   pid bigint,
   instanceexecid bigint,
   url varchar(3000),
   docid int NOT NULL,
   parentdocid int NOT NULL,
   parenturl varchar(1000),
   depth smallint NOT NULL,
   domain varchar(255),
   subdomain varchar(255),
   path varchar(1000),
   anchor varchar(255),
   priority int NOT NULL,
   tag varchar(255),
   PRIMARY KEY (uniqid)
);

DROP TABLE IF EXISTS Weburlsdb;

CREATE TABLE Weburlsdb (
   uniqid bigserial NOT NULL,
   pid bigint,
   instanceexecid bigint,
   url varchar(3000),
   docid int NOT NULL,
   parentdocid int NOT NULL,
   parenturl varchar(1000),
   depth smallint NOT NULL,
   domain varchar(255),
   subdomain varchar(255),
   path varchar(1000),
   anchor varchar(255),
   priority int NOT NULL,
   tag varchar(255),
   PRIMARY KEY (uniqid)
);
