# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table users (
  id                        integer auto_increment not null,
  email                     varchar(255),
  name                      varchar(255),
  password                  varchar(255),
  username                  varchar(255),
  account_locked            integer,
  login_attempts            integer,
  constraint pk_users primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table users;

SET FOREIGN_KEY_CHECKS=1;

