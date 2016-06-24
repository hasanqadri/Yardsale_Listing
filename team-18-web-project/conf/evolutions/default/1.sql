# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table sales (
  id                        integer auto_increment not null,
  city                      varchar(255),
  state                     varchar(255),
  user_created_id           integer,
  is_current                integer default 1,
  constraint pk_sales primary key (id))
;

create table saleItems (
  id                        integer auto_increment not null,
  sale_id                   integer,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_saleItems primary key (id))
;

create table usersNcheek3 (
  id                        integer auto_increment not null,
  email                     varchar(255),
  name                      varchar(255),
  password                  varchar(255),
  username                  varchar(255),
  login_attempts            integer,
  constraint pk_usersNcheek3 primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table sales;

drop table saleItems;

drop table usersNcheek3;

SET FOREIGN_KEY_CHECKS=1;

