# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table sales (
  id                        integer auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  street                    varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  zip                       integer,
  start_date                datetime(6),
  end_date                  datetime(6),
  user_created_id           integer,
  is_active                 integer default 0,
  constraint pk_sales primary key (id))
;

create table saleItems (
  id                        integer auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  price                     float,
  picture_id                integer,
  sale_id                   integer,
  constraint pk_saleItems primary key (id))
;

create table users (
  id                        integer auto_increment not null,
  email                     varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  username                  varchar(255),
  password                  varchar(255),
  login_attempts            tinyint default 0,
  super_admin               tinyint default 0,
  profile_picture_id        integer,
  constraint pk_users primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table sales;

drop table saleItems;

drop table users;

SET FOREIGN_KEY_CHECKS=1;

