# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table commitment (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  date                      date,
  repeating                 tinyint(1) default 0,
  owner_id                  bigint,
  source                    varchar(255),
  duration                  time,
  constraint pk_commitment primary key (id))
;

create table task (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  description               varchar(255),
  category                  varchar(255),
  is_complete               integer,
  end                       datetime,
  start                     datetime,
  owner_id                  bigint,
  source                    varchar(255),
  effort                    time,
  priority                  integer,
  constraint pk_task primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(60),
  moodle_login              varchar(255),
  moodle_password           varchar(255),
  constraint pk_user primary key (id))
;

alter table commitment add constraint fk_commitment_owner_1 foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_commitment_owner_1 on commitment (owner_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table commitment;

drop table task;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

