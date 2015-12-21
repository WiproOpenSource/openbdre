DROP TABLE IF EXISTS `Docidsdb`;
CREATE TABLE Docidsdb (docId int not null auto_increment, url varchar(3000), primary key (docId));
DROP TABLE IF EXISTS `Statisticsdb`;
CREATE TABLE Statisticsdb (uniqid bigint not null auto_increment, value bigint, name varchar(255), primary key (uniqid));
DROP TABLE IF EXISTS `Pendingurlsdb`;
CREATE TABLE Pendingurlsdb (uniqid bigint not null auto_increment, pid bigint, instanceExecid bigint, url varchar(3000), docid int not null, parentDocid int not null, parentUrl varchar(1000), depth smallint not null, domain varchar(255), subDomain varchar(255), path varchar(1000), anchor varchar(255),priority tinyint not null, tag varchar(255), primary key (uniqid));
DROP TABLE IF EXISTS `Weburlsdb`;
CREATE TABLE Weburlsdb (uniqid bigint not null auto_increment, pid bigint, instanceExecid bigint, url varchar(3000), docid int not null, parentDocid int not null, parentUrl varchar(1000), depth smallint not null, domain varchar(255), subDomain varchar(255), path varchar(1000), anchor varchar(255),priority tinyint not null, tag varchar(255), primary key (uniqid));
