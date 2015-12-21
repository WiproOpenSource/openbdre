CREATE TABLE bus_domain (
  bus_domain_id number(10,0) NOT NULL ,
  description varchar(256) NOT NULL,
  bus_domain_name varchar(45) NOT NULL,
  bus_domain_owner varchar(45) NOT NULL,
  PRIMARY KEY (bus_domain_id)
);
  CREATE SEQUENCE bus_domain_seq
  MINVALUE 1
  MAXVALUE 9999999999
  START WITH 1
  INCREMENT BY 1
  CACHE 2;



CREATE TABLE batch_status (
  batch_state_id number(10,0) NOT NULL,
  description varchar(45) NOT NULL,
  PRIMARY KEY (batch_state_id)
);




CREATE TABLE process_type (
  process_type_id number(10,0) NOT NULL,
  process_type_name varchar(45) NOT NULL,
  parent_process_type_id number(10,0),
  PRIMARY KEY (process_type_id)
);



CREATE TABLE exec_status (
  exec_state_id number(10,0) NOT NULL,
  description varchar(45) NOT NULL,
  PRIMARY KEY (exec_state_id)
);




CREATE TABLE workflow_type (
  workflow_id number(10,0) NOT NULL,
  workflow_type_name varchar(45) NOT NULL,
  PRIMARY KEY (workflow_id)) ;



CREATE TABLE servers (
	  server_id number(10,0) NOT NULL,
	  server_type varchar2(45) NOT NULL,
	  server_name varchar2(45) NOT NULL,
	  server_metainfo varchar2(45) DEFAULT NULL,
	  login_user varchar2(45) DEFAULT NULL,
	  login_password varchar2(45) DEFAULT NULL,
	  ssh_private_key varchar2(512) DEFAULT NULL,
	  server_ip varchar2(45) DEFAULT NULL,
	  CONSTRAINT SERVERS_PK PRIMARY KEY(server_id)
	);
 CREATE SEQUENCE servers_seq
  MINVALUE 1
  MAXVALUE 9999999999
  START WITH 1
  INCREMENT BY 1
  CACHE 2;



CREATE TABLE process_template
   (	 PROCESS_TEMPLATE_ID  number(10,0) NOT NULL ENABLE,
	 DESCRIPTION  VARCHAR2(256 BYTE) NOT NULL ENABLE,
	 ADD_TS  TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP NOT NULL ENABLE,
	 PROCESS_NAME  VARCHAR2(45 BYTE) NOT NULL ENABLE,
	 BUS_DOMAIN_ID  number(10,0) NOT NULL ENABLE,
	 PROCESS_TYPE_ID  number(10,0) NOT NULL ENABLE,
	 PARENT_PROCESS_ID  number(10,0),
	 CAN_RECOVER  number(1,0) DEFAULT 1 NOT NULL ENABLE,
	 BATCH_CUT_PATTERN  VARCHAR2(45 BYTE),
	 NEXT_PROCESS_TEMPLATE_ID  VARCHAR2(256 BYTE) DEFAULT '' NOT NULL ENABLE,
	 DELETE_FLAG  number(1,0) DEFAULT 0 NOT NULL ENABLE,
	 WORKFLOW_ID  number(10,0) DEFAULT 1,
	 PRIMARY KEY (PROCESS_TEMPLATE_ID),
   CONSTRAINT  BUS_DOMAIN_ID_TEMPLATE  FOREIGN KEY (BUS_DOMAIN_ID)
	  REFERENCES  BUS_DOMAIN  (BUS_DOMAIN_ID) ENABLE,
	 CONSTRAINT  ORIGINAL_PROCESS_ID1_TEMPLATE  FOREIGN KEY (PARENT_PROCESS_ID)
	  REFERENCES PROCESS_TEMPLATE  (PROCESS_TEMPLATE_ID) ENABLE,
	 CONSTRAINT  WORKFLOW_ID_TEMPLATE  FOREIGN KEY (WORKFLOW_ID)
	  REFERENCES WORKFLOW_TYPE  (WORKFLOW_ID) ENABLE,
	 CONSTRAINT  PROCESS_TYPE_ID1_TEMPLATE  FOREIGN KEY (PROCESS_TYPE_ID)
	  REFERENCES PROCESS_TYPE  (PROCESS_TYPE_ID) ENABLE
   ) ;

    CREATE SEQUENCE process_template_seq
     MINVALUE 1
     MAXVALUE 9999999999
     START WITH 1
     INCREMENT BY 1
     CACHE 2;




CREATE TABLE properties_template (
  process_template_id number(10,0) NOT NULL,
  config_group varchar2(10) NOT NULL,
  prop_temp_key varchar2(128) NOT NULL,
  prop_temp_value varchar2(2048) NOT NULL,
  description varchar2(1028) NOT NULL,
  CONSTRAINT PROPERTIES_TEMPLATE_PK PRIMARY KEY (process_template_id,prop_temp_key),
  CONSTRAINT PROCESS_TEMPLATE_ID5 FOREIGN KEY (process_template_id) REFERENCES process_template(process_template_id) enable
);


 CREATE TABLE  process
   (	PROCESS_ID  number(10,0) NOT NULL ENABLE,
	 DESCRIPTION  VARCHAR2(256 BYTE) NOT NULL ENABLE,
	 ADD_TS  TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP NOT NULL ENABLE,
	 EDIT_TS  TIMESTAMP (6) DEFAULT CURRENT_TIMESTAMP NOT NULL ENABLE,
	 PROCESS_NAME  VARCHAR2(45 BYTE) NOT NULL ENABLE,
	 BUS_DOMAIN_ID  number(10,0) NOT NULL ENABLE,
	 PROCESS_TYPE_ID  number(10,0) NOT NULL ENABLE,
	 PARENT_PROCESS_ID  number(10,0) DEFAULT NULL,
	 CAN_RECOVER  number(1,0) DEFAULT 1 NOT NULL ENABLE,
	 ENQUEUING_PROCESS_ID  number(10,0) DEFAULT 0 NOT NULL ENABLE,
	 BATCH_CUT_PATTERN  VARCHAR2(45 BYTE),
	 NEXT_PROCESS_ID  VARCHAR2(256 BYTE) DEFAULT '' NOT NULL ENABLE,
   DELETE_FLAG  number(1,0) DEFAULT 0 NOT NULL ENABLE,
	 WORKFLOW_ID  number(10,0) DEFAULT 1,
   PROCESS_TEMPLATE_ID number(10,0) DEFAULT 0,
	 PRIMARY KEY (PROCESS_ID),
  	 CONSTRAINT  BUS_DOMAIN_ID  FOREIGN KEY (BUS_DOMAIN_ID)
	  REFERENCES  BUS_DOMAIN  (BUS_DOMAIN_ID) ENABLE,
	 CONSTRAINT  ORIGINAL_PROCESS_ID1  FOREIGN KEY (PARENT_PROCESS_ID)
	  REFERENCES   PROCESS  (PROCESS_ID) ENABLE,
	 CONSTRAINT  WORKFLOW_ID  FOREIGN KEY (WORKFLOW_ID)
	  REFERENCES   WORKFLOW_TYPE  (WORKFLOW_ID) ENABLE,
	 CONSTRAINT  PROCESS_TYPE_ID1  FOREIGN KEY (PROCESS_TYPE_ID)
	  REFERENCES   PROCESS_TYPE  (PROCESS_TYPE_ID) ENABLE,
    CONSTRAINT PROCESS_IBFK_1 FOREIGN KEY (PROCESS_TEMPLATE_ID)
    REFERENCES PROCESS_TEMPLATE (PROCESS_TEMPLATE_ID) ENABLE
   ) ;

    CREATE SEQUENCE process_seq
     MINVALUE 1
     MAXVALUE 9999999999
     START WITH 1
     INCREMENT BY 1
     CACHE 2;

create or replace TRIGGER process_type_check_insert

BEFORE INSERT ON process FOR EACH ROW

DECLARE

l_process_type_id NUMBER(10,0);
BEGIN
IF :NEW.parent_process_id IS NOT NULL THEN
SELECT process_type_id into l_process_type_id
from process
where process_id=:NEW.parent_process_id;
END IF;

IF :NEW.process_type_id IN (1,2,3,4,5) AND :NEW.parent_process_id IS NOT NULL

THEN

 raise_application_error(-20001, '1,2,3,4,5 process types are applicable for parent processes only');

END IF;

IF :NEW.process_type_id IN (6,7,8,9,10,11,12) AND :NEW.parent_process_id IS NULL
 THEN
 raise_application_error(-20001,'6,7,8,9,10,11,12 process types are applicable for sub processes only');
 END IF;

-- if new rows parent does not belong to etl group where the new row type are 6,7,8 throw error

IF :NEW.process_type_id IN (6,7,8) AND l_process_type_id != 5
THEN
raise_application_error(-20001, '6,7,8 process types should have etl process type(5) parent') ;

 END IF;

-- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error

IF :NEW.process_type_id IN (9,10,11) AND l_process_type_id != 2
 THEN

raise_application_error(-20001,'9,10,11 process types should have semantic process type(2) parent' );

 END IF;

-- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error

IF :NEW.process_type_id IN (12) AND l_process_type_id != 1
 THEN

 raise_application_error(-20001, '12 process types should have semantic process type(2) parent' );

END IF;

END;

CREATE TRIGGER process_type_check_update
BEFORE UPDATE ON process FOR EACH ROW

DECLARE

l_process_type_id NUMBER(10,0);

BEGIN

select CURRENT_TIMESTAMP into :new.edit_ts from dual;
IF :NEW.parent_process_id IS NOT NULL THEN
SELECT process_type_id into l_process_type_id
from process
where process_id=:NEW.parent_process_id;
END IF;
IF :NEW.process_type_id IN (1,2,3,4,5) AND :NEW.parent_process_id IS NOT NULL
THEN
raise_application_error(-20001, '1,2,3,4,5 process types are applicable for parent processes only');
 END IF;
 IF :NEW.process_type_id IN (6,7,8,9,10,11,12) AND :NEW.parent_process_id IS NULL
THEN
raise_application_error(-20001,'6,7,8,9,10,11,12 process types are applicable for sub processes only');
END IF;

-- if new rows parent does not belong to etl group where the new row type are 6,7,8 throw error
IF :NEW.process_type_id IN (6,7,8) AND l_process_type_id != 5

THEN
raise_application_error(-20001, '6,7,8 process types should have etl process type(5) parent') ;
END IF;

 -- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error
IF :NEW.process_type_id IN (9,10,11) AND l_process_type_id != 2
THEN
raise_application_error(-20001,'9,10,11 process types should have semantic process type(2) parent' );
END IF;

 -- if new rows parent does not belong to semantic group where the new row type are 6,7,8 throw error

IF :NEW.process_type_id IN (12) AND l_process_type_id != 1

THEN

raise_application_error(-20001, '12 process types should have semantic process type(2) parent' );

END IF;
END;



CREATE TABLE properties (
  process_id number(10,0) NOT NULL,
  config_group varchar2(10) NOT NULL,
  prop_key varchar2(128) NOT NULL,
  prop_value varchar2(2048) NOT NULL,
  description varchar2(1028) NOT NULL,
  CONSTRAINT PROPERTIES_PK PRIMARY KEY (process_id,prop_key),
 CONSTRAINT PROPERTIES_PROCESS_FK FOREIGN KEY (process_id) REFERENCES process(process_id)
);



CREATE TABLE hive_tables (
  table_id number(10,0) NOT NULL,
  comments varchar2(256) NOT NULL,
  location_type varchar2(45) NOT NULL,
  dbname varchar2(45) DEFAULT NULL,
  batch_id_partition_col varchar2(45) DEFAULT NULL,
  table_name varchar2(45) NOT NULL,
  type varchar2(45) NOT NULL,
  ddl varchar2(2048) NOT NULL,
  PRIMARY KEY (table_id)
);

 CREATE SEQUENCE hive_tables_seq
  MINVALUE 1
  MAXVALUE 9999999999
  START WITH 1
  INCREMENT BY 1
  CACHE 2;





CREATE TABLE etl_driver (
  etl_process_id number(10,0) NOT NULL,
  raw_table_id number(10,0) NOT NULL,
  base_table_id number(10,0) NULL ,
  insert_type NUMBER(5,0) NULL,
  drop_raw NUMBER(1,0) DEFAULT 0 NOT NULL,
  raw_view_id number(10,0) NOT NULL,
  CONSTRAINT ETL_DRIVER_PK PRIMARY KEY (etl_process_id),
  CONSTRAINT table_id_etl_driver FOREIGN KEY (raw_table_id) REFERENCES hive_tables (table_id),
  CONSTRAINT table_id2_etl_driver FOREIGN KEY (base_table_id) REFERENCES hive_tables (table_id),
  CONSTRAINT batch_id_etl_driver FOREIGN KEY (etl_process_id) REFERENCES process (process_id),
  CONSTRAINT view_id_etl_driver FOREIGN KEY (raw_view_id) REFERENCES hive_tables (table_id)
  );




CREATE TABLE instance_exec (
  instance_exec_id number(19,0) NOT NULL,
  process_id  number(10,0) NOT NULL,
  start_ts timestamp  DEFAULT CURRENT_TIMESTAMP NOT NULL,
  end_ts timestamp  DEFAULT NULL,
  exec_state number(10,0) NOT NULL,
  PRIMARY KEY (instance_exec_id),
 CONSTRAINT process_id_instance_exec FOREIGN KEY (process_id) REFERENCES process (process_id),
 CONSTRAINT exec_state_instance_exec FOREIGN KEY (exec_state) REFERENCES exec_status (exec_state_id));

  CREATE SEQUENCE instance_exec_seq
  MINVALUE 1
  MAXVALUE 9999999999999999999
  START WITH 1
  INCREMENT BY 1
  CACHE 2;




CREATE TABLE batch(
    batch_id  number(19,0) NOT NULL ,
    source_instance_exec_id number(19,0) DEFAULT NULL,
    batch_type  VARCHAR(45) NOT NULL,
    PRIMARY KEY (batch_id),
    CONSTRAINT instance_exec_id FOREIGN KEY (source_instance_exec_id)
    REFERENCES instance_exec (instance_exec_id));

  CREATE SEQUENCE batch_seq
  MINVALUE 2
  MAXVALUE 9999999999999999999
  START WITH 2
  INCREMENT BY 1
  CACHE 3;




CREATE TABLE batch_file (
  batch_id number(19,0) NOT NULL,
  server_id number(10,0) NOT NULL,
  path varchar2(256) NOT NULL,
  file_size number(19,0) NOT NULL,
  file_hash varchar2(100) DEFAULT NULL,
  creation_ts timestamp(0) DEFAULT SYSTIMESTAMP NOT NULL,
  CONSTRAINT server_id_fk FOREIGN KEY (server_id) REFERENCES servers (server_id),
  CONSTRAINT unique_batch_fk FOREIGN KEY (batch_id) REFERENCES batch (batch_id)
);

CREATE INDEX server_id ON batch_file (server_id);
CREATE INDEX unique_batch ON batch_file (batch_id);

create trigger file_update_trigger
before update on batch_file for each row
begin
select CURRENT_TIMESTAMP into :new.creation_ts from dual;
end;
/



CREATE TABLE batch_consump_queue (
  source_batch_id number(19,0) NOT NULL,
  target_batch_id number(19,0) DEFAULT NULL,
  queue_id number(19,0) NOT NULL,
  insert_ts timestamp(0) DEFAULT SYSTIMESTAMP NOT NULL,
  source_process_id number(10,0) DEFAULT NULL,
  start_ts timestamp(0) DEFAULT NULL ,
  end_ts timestamp(0) DEFAULT NULL ,
  batch_state number(10,0) NOT NULL,
  batch_marking varchar2(45) DEFAULT NULL,
  process_id number(10,0) NOT NULL,
  CONSTRAINT BATCH_CONSUM_PK PRIMARY KEY (queue_id),
  CONSTRAINT batch_state_bcq_fk FOREIGN KEY (batch_state) REFERENCES batch_status (batch_state_id),
  CONSTRAINT process_id_bcq_fk FOREIGN KEY (process_id) REFERENCES process(process_id),
  CONSTRAINT source_batch_bcq_fk FOREIGN KEY (source_batch_id) REFERENCES batch (batch_id),
  CONSTRAINT target_batch_bcq_fk FOREIGN KEY (target_batch_id) REFERENCES batch (batch_id)
);

 CREATE SEQUENCE batch_consump_queue_seq
  MINVALUE 1
  MAXVALUE 9999999999999999999
  START WITH 1
  INCREMENT BY 1
  CACHE 2;

create trigger bcq_update_trigger
before update on batch_consump_queue for each row
begin
select CURRENT_TIMESTAMP into :new.insert_ts from dual;
end;
/

CREATE INDEX source_batch_bcq ON batch_consump_queue (source_batch_id);
CREATE INDEX target_batch_bcq ON batch_consump_queue (target_batch_id);
CREATE INDEX batch_state_bcq ON batch_consump_queue (batch_state);
CREATE INDEX process_id_bcq ON batch_consump_queue (process_id);




CREATE TABLE archive_consump_queue (
  source_batch_id number(19,0) NOT NULL,
  target_batch_id number(19,0) DEFAULT NULL,
  queue_id number(19,0) NOT NULL,
  insert_ts timestamp(0) DEFAULT SYSTIMESTAMP NOT NULL,
  source_process_id number(10,0) DEFAULT NULL,
  start_ts timestamp(0) DEFAULT NULL,
  end_ts timestamp(0) DEFAULT NULL,
  batch_state number(10,0) NOT NULL,
  batch_marking varchar2(45) DEFAULT NULL,
  process_id number(10,0) NOT NULL,
  CONSTRAINT ARCHIVE_CONSUM_PK PRIMARY KEY (queue_id),
  CONSTRAINT batch_state_archive_fk FOREIGN KEY (batch_state) REFERENCES batch_status (batch_state_id),
  CONSTRAINT source_batch_archive_fk FOREIGN KEY (source_batch_id) REFERENCES batch (batch_id),
  CONSTRAINT target_batch_archive_fk FOREIGN KEY (target_batch_id) REFERENCES batch (batch_id),
  CONSTRAINT PROCESS_ID_ACQ_FK FOREIGN KEY (process_id) REFERENCES process(process_id)
);

  CREATE SEQUENCE archive_consump_queue_seq
  MINVALUE 1
  MAXVALUE 9999999999999999999
  START WITH 1
  INCREMENT BY 1
  CACHE 2;



CREATE INDEX source_batch_archive ON archive_consump_queue (source_batch_id);
CREATE INDEX target_batch_archive ON archive_consump_queue (target_batch_id);
CREATE INDEX batch_state_archive ON archive_consump_queue (batch_state);

create trigger acq_update_trigger
before update on archive_consump_queue for each row
begin
select CURRENT_TIMESTAMP into :new.insert_ts from dual;
end;
/

CREATE TABLE etlstep (
  uuid varchar2(128) NOT NULL ,
  serial_number number(19,0) NOT NULL ,
  bus_domain_id number(10,0) NOT NULL,
  process_name varchar2(256) NOT NULL,
  description varchar2(2048) NOT NULL,
  base_table_name varchar2(45) ,
  raw_table_name varchar2(45) ,
  raw_view_name varchar2(45) ,
  base_db_name varchar2(45) DEFAULT NULL,
  raw_db_name varchar2(45) DEFAULT NULL,
  base_table_ddl varchar2(2048),
  raw_table_ddl varchar2(2048),
  raw_view_ddl varchar2(2048),
  raw_partition_col varchar2(45) ,
  drop_raw number(1,0) ,
  enq_id number(10,0),
  column_info varchar2(45) ,
  serde_properties varchar2(45) ,
  table_properties varchar2(45) ,
  input_format varchar2(45) ,
  CONSTRAINT pk_ID primary key (serial_number,uuid)
   );

 CREATE SEQUENCE etlstep_seq
  MINVALUE 1
  MAXVALUE 9999999999
  START WITH 1
  INCREMENT BY 1
  CACHE 2;




CREATE  TABLE users (
  username varchar2(45) NOT NULL ,
  password varchar2(45) NOT NULL ,
  enabled number(1,0)  DEFAULT 1 NOT NULL,
  CONSTRAINT USERS_PK PRIMARY KEY (username)
);


CREATE TABLE user_roles (
  user_role_id number(10,0) NOT NULL,
  username varchar2(45) NOT NULL,
  ROLE varchar2(45) NOT NULL,
  CONSTRAINT user_roles_PK PRIMARY KEY (user_role_id),
  CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users (username)
);

CREATE SEQUENCE user_roles_seq
  MINVALUE 3
  MAXVALUE 9999999999
  START WITH 3
  INCREMENT BY 1
  CACHE 2;




CREATE TABLE process_log (
  log_id number(19,0) NOT NULL,
  add_ts timestamp,
  process_id number(10,0) NOT NULL,
  log_category varchar2(10) NOT NULL,
  message_id varchar2(128) NOT NULL,
  message varchar2(1024) NOT NULL,
  instance_ref number(19,0),
  CONSTRAINT PROCESS_LOG_PK PRIMARY KEY (log_id),
  CONSTRAINT PROCESS_LOG_PROCESS_FK FOREIGN KEY (process_id) REFERENCES process(process_id)
);
 CREATE SEQUENCE process_log_seq
  MINVALUE 1
  MAXVALUE 9999999999999999999
  START WITH 1
  INCREMENT BY 1
  CACHE 2;





CREATE TABLE intermediate (
  uuid varchar2(64) NOT NULL,
  inter_key varchar2(128) NOT NULL,
  inter_value varchar2(2048) NOT NULL,
  CONSTRAINT INTERMEDIATE_PK PRIMARY KEY (inter_key,uuid)
);



CREATE TABLE lineage_node_type (
   node_type_id number(10,0) NOT NULL,
   node_type_name varchar2(45) NOT NULL,
   constraint lineage_node_tpk PRIMARY KEY (node_type_id)
   ) ;

CREATE TABLE lineage_query_type (
  query_type_id number(10,0) NOT NULL,
  query_type_name varchar2(255) NOT NULL,
  constraint lineage_query_tpk PRIMARY KEY (query_type_id)
  );
   CREATE SEQUENCE lineage_query_type_seq
    MINVALUE 3
    MAXVALUE 9999999999
    START WITH 3
    INCREMENT BY 1
    CACHE 2;



CREATE TABLE lineage_query (
  query_id varchar2(100) NOT NULL,
  query_string varchar(4000) ,
  query_type_id number(10,0) NOT NULL,
  create_ts timestamp(0) DEFAULT SYSTIMESTAMP,
  process_id number(10,0),
  instance_exec_id number(19,0) DEFAULT NULL,
  constraint lineage_query_pk PRIMARY KEY (query_id),
  CONSTRAINT query_type_id FOREIGN KEY (query_type_id) REFERENCES lineage_query_type (query_type_id)
 );

CREATE INDEX process_id ON lineage_query (process_id);
CREATE INDEX query_type_id ON lineage_query (query_type_id);

CREATE TABLE lineage_node (
  node_id varchar2(100) NOT NULL,
  node_type_id number(10,0) NOT NULL,
  container_node_id varchar2(100) DEFAULT NULL,
  node_order number(10,0) DEFAULT 0,
  insert_ts timestamp(0) DEFAULT SYSTIMESTAMP NOT NULL,
  update_ts timestamp(0) DEFAULT NULL NULL,
  dot_string varchar(4000),
  dot_label varchar(4000),
  display_name varchar2(256) DEFAULT NULL,
  constraint lineage_node_pk PRIMARY KEY (node_id)
 ,
  CONSTRAINT conatiner_node_id FOREIGN KEY (container_node_id) REFERENCES lineage_node (node_id),
  CONSTRAINT node_type FOREIGN KEY (node_type_id) REFERENCES lineage_node_type (node_type_id)
 );

CREATE INDEX node_type ON lineage_node (node_type_id);
CREATE INDEX conatiner_node_id ON lineage_node (container_node_id);

CREATE TABLE lineage_relation (
  relation_id varchar2(100) NOT NULL,
  src_node_id varchar2(100) DEFAULT NULL,
  target_node_id varchar2(100) DEFAULT NULL,
  query_id varchar2(100) NOT NULL,
  dot_string varchar(4000),
 constraint lineage_relation_pk  PRIMARY KEY (relation_id),
  CONSTRAINT src_node_id FOREIGN KEY (src_node_id) REFERENCES lineage_node (node_id),
  CONSTRAINT target_node_id FOREIGN KEY (target_node_id) REFERENCES lineage_node (node_id),
  CONSTRAINT query_id FOREIGN KEY (query_id) REFERENCES lineage_query (query_id)
 );

CREATE INDEX src_node_id ON lineage_relation (src_node_id);
CREATE INDEX target_node_id ON lineage_relation (target_node_id);
CREATE INDEX query_id ON lineage_relation (query_id);



CREATE TABLE deploy_status (
  deploy_status_id number(3,0) NOT NULL,
  description varchar(45) NOT NULL,
  PRIMARY KEY (deploy_status_id)
);


CREATE TABLE general_config (
   config_group varchar(128) NOT NULL,
  gc_key varchar(128) NOT NULL,
  gc_value varchar(2048)  NULL,
  description varchar(1028) NOT NULL,
  required number(1,0) DEFAULT 0 NOT NULL,
  default_val varchar(2048)  NULL,
  type varchar(20)  DEFAULT 'text' NOT NULL,
  enabled number(1,0) DEFAULT 1,
  PRIMARY KEY (config_group,gc_key)
);



CREATE TABLE process_deployment_queue(
   deployment_id number(19,0) NOT NULL,
   process_id number(10,0) NOT NULL ,
   start_ts timestamp  DEFAULT NULL NULL,
   insert_ts timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
   end_ts timestamp DEFAULT NULL NULL ,
   deploy_status_id number(3,0) DEFAULT 1 NOT NULL ,
   user_name varchar(45) NOT NULL,
   bus_domain_id number(10,0) NOT NULL,
   process_type_id number(10,0) NOT NULL,
   deploy_script_location varchar(1000) DEFAULT NULL,
   PRIMARY KEY (deployment_id),
  CONSTRAINT deploy_status_id FOREIGN KEY (deploy_status_id)REFERENCES deploy_status (deploy_status_id) enable,
  CONSTRAINT deploy_process_id FOREIGN KEY (process_id) REFERENCES process (process_id) enable,
  CONSTRAINT deploy_process_type_id FOREIGN KEY (process_type_id) REFERENCES process_type (process_type_id) enable,
  CONSTRAINT deploy_bus_domain_id FOREIGN KEY (bus_domain_id) REFERENCES bus_domain (bus_domain_id) enable
 );

 CREATE SEQUENCE process_deployment_queue_seq
     MINVALUE 1
     MAXVALUE 9999999999999999999
     START WITH 1
     INCREMENT BY 1
     CACHE 2;



CREATE TABLE Docidsdb (
   docId number(10,0) not null ,
   url varchar(3000),
   primary key (docId)
);

 CREATE SEQUENCE Docidsdb_SEQ
     MINVALUE 1
     MAXVALUE 9999999999
     START WITH 1
     INCREMENT BY 1
     CACHE 2;



CREATE TABLE Statisticsdb (
   uniqid number(19,0) not null,
   value number(19,0),
   name varchar(255),
   primary key (uniqid)
);
 CREATE SEQUENCE Statisticsdb_SEQ
      MINVALUE 1
      MAXVALUE 9999999999999999999
      START WITH 1
      INCREMENT BY 1
      CACHE 2;


CREATE TABLE Pendingurlsdb (
   uniqid number(19,0) not null,
   pid number(19,0),
   instanceExecid number(19,0),
   url varchar(3000),
   docid number(10,0) not null,
   parentDocid number(10,0) not null,
   parentUrl varchar(1000),
   depth number(5,0) not null,
   domain varchar(255),
   subDomain varchar(255),
   path varchar(1000),
   anchor varchar(255),
   priority number(3,0) not null,
   tag varchar(255),
   primary key (uniqid)
);

 CREATE SEQUENCE Pendingurlsdb_SEQ
       MINVALUE 1
       MAXVALUE 9999999999999999999
       START WITH 1
       INCREMENT BY 1
       CACHE 2;



CREATE TABLE Weburlsdb (
   uniqid number(19,0) not null ,
   pid number(19,0),
   instanceExecid number(19,0),
   url varchar(3000),
   docid number(10,0) not null,
   parentDocid number(10,0) not null,
   parentUrl varchar(1000),
   depth number(5,0) not null,
   domain varchar(255),
   subDomain varchar(255),
   path varchar(1000),
   anchor varchar(255),
   priority number(3,0) not null,
   tag varchar(255),
   primary key (uniqid)
);

CREATE SEQUENCE Weburlsdb_SEQ
       MINVALUE 1
       MAXVALUE 9999999999999999999
       START WITH 1
       INCREMENT BY 1
       CACHE 2;

commit;



