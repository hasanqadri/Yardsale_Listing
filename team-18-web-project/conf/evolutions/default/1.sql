# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table lineItems (
  id                        integer auto_increment not null,
  sale_item_id              integer,
  transaction_id            integer,
  quantity                  integer,
  unit_price                float,
  name                      varchar(255),
  user_created_id           integer,
  constraint pk_lineItems primary key (id))
;

create table pictures (
  id                        integer auto_increment not null,
  image                     longblob,
  constraint pk_pictures primary key (id))
;

create table roles (
  id                        integer auto_increment not null,
  name                      varchar(255) not null,
  user_id                   integer not null,
  sale_id                   integer not null,
  constraint pk_roles primary key (id))
;

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
  status                    integer default 0,
  constraint pk_sales primary key (id))
;

create table saleItems (
  id                        integer auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  price                     float,
  picture_id                integer,
  sale_id                   integer,
  user_created_id           integer,
  quantity                  integer,
  constraint pk_saleItems primary key (id))
;

create table transactions (
  id                        integer auto_increment not null,
  sale_id                   integer,
  cashier_id                integer,
  buyer_name                varchar(255),
  buyer_address             varchar(255),
  buyer_email               varchar(255),
  completed                 tinyint default 0,
  payment_method            varchar(255),
  random_nonce              integer,
  date                      datetime(6),
  constraint pk_transactions primary key (id))
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
  profile_picture_id        int default 0,
  constraint pk_users primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table lineItems;

drop table pictures;

drop table roles;

drop table sales;

drop table saleItems;

drop table transactions;

drop table users;

SET FOREIGN_KEY_CHECKS=1;

