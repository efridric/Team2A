# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table commitment (
  id                        bigint not null,
  title                     varchar(255),
  date                      date,
  repeating                 boolean,
  owner_id                  bigint,
  source                    varchar(255),
  duration                  time,
  constraint pk_commitment primary key (id))
;

create table task (
  id                        bigint not null,
  title                     varchar(255),
  is_complete               boolean,
  due_date                  date,
  owner_id                  bigint,
  source                    varchar(255),
  effort                    time,
  prority                   integer,
  constraint pk_task primary key (id))
;

create table user (
  id                        bigint not null,
  email                     varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  moodle_login              varchar(255),
  moodle_password           varchar(255),
  constraint pk_user primary key (id))
;

create sequence commitment_seq;

create sequence task_seq;

create sequence user_seq;

alter table commitment add constraint fk_commitment_owner_1 foreign key (owner_id) references user (id) on delete restrict on update restrict;
create index ix_commitment_owner_1 on commitment (owner_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists commitment;

drop table if exists task;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists commitment_seq;

drop sequence if exists task_seq;

drop sequence if exists user_seq;

