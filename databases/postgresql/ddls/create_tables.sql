/*etlmd_bus_domain.sql  */


CREATE TABLE bus_domain (
  bus_domain_id SERIAL NOT NULL,
  description varchar(256) NOT NULL,
  bus_domain_name varchar(45) NOT NULL,
  bus_domain_owner varchar(45) NOT NULL,
  PRIMARY KEY (bus_domain_id)
);

/* etlmd_batch_status.sql */



CREATE TABLE batch_status (
  batch_state_id INT NOT NULL,
  description varchar(45) NOT NULL,
  PRIMARY KEY (batch_state_id)
);

/* etlmd_process_type.sql */




CREATE TABLE process_type (
  process_type_id int NOT NULL,
  process_type_name varchar(45) NOT NULL,
  parent_process_type_id int,
  PRIMARY KEY (process_type_id)
);



/* etlmd_exec_status.sql */




CREATE TABLE exec_status (
  exec_state_id int NOT NULL,
  description varchar(45) NOT NULL,
  PRIMARY KEY (exec_state_id)
);


/*etlmd_workflow_type.sql*/


CREATE TABLE workflow_type (
  workflow_id INT NOT NULL,
    workflow_type_name VARCHAR(45) NOT NULL,
    PRIMARY KEY (workflow_id)
);
/* permission_type.sql */

CREATE TABLE permission_type (
  permission_type_id number(11) NOT NULL,
  permission_type_name varchar(45) NOT NULL,
  PRIMARY KEY (permission_type_id)
);
/*etlmd_user.sql*/




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




/* etlmd_servers.sql */

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




CREATE TABLE properties_template (
  process_template_id int NOT NULL references process_template(process_template_id) ON
DELETE NO ACTION ON UPDATE NO ACTION,
  config_group varchar(128) NOT NULL,
  prop_temp_key varchar(128) NOT NULL,
  prop_temp_value varchar(2048) NOT NULL,
  description varchar(1028) NOT NULL,
  PRIMARY KEY (process_template_id,prop_temp_key)

);


/* etlmd_process.sql */




CREATE TABLE process (
  process_id serial NOT NULL,
  description varchar(256) NOT NULL,
  add_ts timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  process_name varchar(45) NOT NULL,
  process_code VARCHAR(256),
   user_name VARCHAR(45),
      owner_role_id int(11),
      user_access_id int(1)  DEFAULT '7',
      group_access_id int(1)  DEFAULT '4',
      others_access_id int(1)  DEFAULT '0',
  bus_domain_id int NOT NULL references bus_domain(bus_domain_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  process_type_id int NOT NULL references process_type(process_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  parent_process_id int DEFAULT NULL references process(process_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  user_access_id REFERENCES permission_type (permission_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
     group_access_id REFERENCES permission_type (permission_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
     others_access_id REFERENCES permission_type (permission_type_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
     owner_role_id REFERENCES user_roles (user_role_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
      user_name REFERENCES users (username) ON DELETE NO ACTION ON UPDATE NO ACTION,
  can_recover boolean  DEFAULT 'true',
  enqueuing_process_id int NOT NULL DEFAULT '0',
  batch_cut_pattern varchar(45) DEFAULT NULL,
  next_process_id varchar(256) NOT NULL DEFAULT '',
  delete_flag boolean  DEFAULT 'false',
  workflow_id int DEFAULT '1' references workflow_type(workflow_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  process_template_id int DEFAULT '0' references process_template(process_template_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  edit_ts timestamp DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (process_id)

);


/* etlmd_properties.sql */



CREATE TABLE properties (
  process_id int NOT NULL references process(process_id) ON DELETE NO ACTION ON UPDATE NO
ACTION,
  config_group varchar(128) NOT NULL,
  prop_key varchar(128) NOT NULL,
  prop_value varchar(2048) NOT NULL,
  description varchar(1028) NOT NULL,
  PRIMARY KEY (process_id,prop_key)

);

/* etlmd_instance_exec.sql */


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



CREATE TABLE batch (
  batch_id BIGSERIAL NOT NULL,
  source_instance_exec_id bigint DEFAULT NULL REFERENCES instance_exec(instance_exec_id) ON
DELETE NO ACTION ON UPDATE NO ACTION,
  batch_type varchar(45) NOT NULL,
  PRIMARY KEY (batch_id)
  );




/* etlmd_file.sql */



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



/* etlmd_process_log.sql */



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



CREATE TABLE intermediate (
  uuid varchar(64) NOT NULL,
  inter_key varchar(128) NOT NULL,
  inter_value varchar(2048) NOT NULL,
  PRIMARY KEY (inter_key,uuid)
);


/* etlmd_data_lineage.sql */









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



CREATE TABLE deploy_status (
  deploy_status_id smallint NOT NULL,
  description varchar(45) NOT NULL,
  PRIMARY KEY (deploy_status_id)
);



/* etlmd_general_config.sql */




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

/* etlmd_crawler.sql  */



CREATE TABLE Docidsdb (
   docid bigserial NOT NULL ,
   url varchar(3000),
   PRIMARY KEY (docid)
);



CREATE TABLE Statisticsdb (
   uniqid bigserial NOT NULL,
   value bigint,
   name varchar(255),
   PRIMARY KEY (uniqid)
);



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

CREATE TABLE app_deployment_queue_status (
  app_deployment_status_id smallint not null,
  description varchar(45) not null,
  PRIMARY KEY (app_deployment_status_id)
);


CREATE TABLE app_deployment_queue (
  app_deployment_queue_id bigserial not null,
  process_id int not null REFERENCES process(process_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  username varchar(45)  not null  REFERENCES users(username) ON DELETE NO ACTION ON UPDATE NO ACTION,
  app_domain varchar(45) not null,
  app_name varchar(45) not null,
  app_deployment_status_id smallint not null REFERENCES app_deployment_queue_status(app_deployment_status_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  PRIMARY KEY (app_deployment_queue_id),
);

CREATE TABLE INSTALLED_PLUGINS (
  PLUGIN_UNIQUE_ID VARCHAR(128) NOT NULL,
  PLUGIN_ID VARCHAR(128) NOT NULL,
  NAME VARCHAR(128),
  DESCRIPTION VARCHAR(128),
  PLUGIN_VERSION VARCHAR(128) NOT NULL DEFAULT "1.0.0",
  AUTHOR VARCHAR(128),
  ADD_TS TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PLUGIN VARCHAR(128),
  UNINSTALLABLE BOOLEAN DEFAULT true,
  PRIMARY KEY (PLUGIN_UNIQUE_ID)
);


CREATE TABLE PLUGIN_DEPENDENCY (
  DEPENDENCY_ID INT(11) NOT NULL serial,
  PLUGIN_UNIQUE_ID VARCHAR(128) NOT NULL REFERENCES INSTALLED_PLUGINS (PLUGIN_UNIQUE_ID) ON DELETE NO ACTION ON UPDATE NO ACTION,
  DEPENDENT_PLUGIN_UNIQUE_ID VARCHAR(128)REFERENCES INSTALLED_PLUGINS (PLUGIN_UNIQUE_ID) ON DELETE NO ACTION ON UPDATE NO ACTION,
  PRIMARY KEY (DEPENDENCY_ID),
) ;

CREATE TABLE PLUGIN_CONFIG (
  PLUGIN_UNIQUE_ID VARCHAR(128) NOT NULL REFERENCES INSTALLED_PLUGINS (PLUGIN_UNIQUE_ID) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONFIG_GROUP VARCHAR(128),
  PLUGIN_KEY VARCHAR(128),
  PLUGIN_VALUE VARCHAR(128),
  PRIMARY KEY (PLUGIN_KEY,PLUGIN_UNIQUE_ID),
) ;

CREATE TABLE analytics_apps (
  analytic_apps_id bigserial not null,
  process_id int not null REFERENCES process(process_id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  industry_name varchar(45) not null,
  category_name varchar(45) not null,
  app_description varchar(45) not null,
  app_name varchar(45) not null,
  questions_json varchar(45) not null,
  dashboard_url varchar(45) not null,
  ddp_url varchar(45) not null,
  app_image varchar(45) not null,
  PRIMARY KEY (ANALYTIC_APPS_ID),
 );


