# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table sales (
  id                        integer auto_increment not null,
  city                      varchar(255),
  state                     varchar(255),
  user_created_id           integer,
  is_open                   integer default 0,
  constraint pk_sales primary key (id))
;

create table saleItems (
  id                        integer auto_increment not null,
  sale_id                   integer,
  name                      varchar(255),
  description               integer default 0,
  constraint pk_saleItems primary key (id))
;

create table users (
  id                        integer auto_increment not null,
  email                     varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  username                  varchar(255),
  login_attempts            tinyint default 0,
  super_user                tinyint default 0,
  profile_picture_id        integer,
  constraint pk_users primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table sales;

drop table saleItems;

drop table users;

SET FOREIGN_KEY_CHECKS=1;

