# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table items (
  sale_item_id              integer auto_increment not null,
  constraint pk_items primary key (sale_item_id))
;

create table users (
  id                        integer auto_increment not null,
  email                     varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  username                  varchar(255),
  account_locked            integer,
  login_attempts            integer,
  constraint pk_users primary key (id))
;

create table yardSales (
  yard_sale_id              integer auto_increment not null,
  city_name                 varchar(255),
  state_name                varchar(255),
  is_current                tinyint(1) default 0,
  constraint pk_yardSales primary key (yard_sale_id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table items;

drop table users;

drop table yardSales;

SET FOREIGN_KEY_CHECKS=1;

