drop table if exists authorities;
drop table if exists users;
drop table if exists acl_entry;
drop table if exists acl_object_identity;
drop table if exists acl_class;
drop table if exists acl_sid;
drop table if exists persistent_logins;
drop table if exists menus;
drop table if exists contacts;
drop table if exists group_authorities;
drop table if exists group_members;
drop table if exists groups;


create table users (
    username varchar(50) not null primary key,
    password varchar(60) not null,
    enabled boolean not null
) engine = InnoDb;

create table authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    foreign key (username) references users (username),
    unique index authorities_idx_1 (username, authority)
) engine = InnoDb;

create table groups (
    id bigint unsigned not null auto_increment primary key,
    group_name varchar(50) not null
) engine = InnoDb;

create table group_authorities (
    group_id bigint unsigned not null,
    authority varchar(50) not null,
    foreign key (group_id) references groups (id)
) engine = InnoDb;

create table group_members (
    id bigint unsigned not null auto_increment primary key,
    username varchar(50) not null,
    group_id bigint unsigned not null,
    foreign key (group_id) references groups (id)
) engine = InnoDb;

CREATE TABLE acl_sid (
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	principal BOOLEAN NOT NULL,
	sid VARCHAR(100) NOT NULL,
	UNIQUE KEY unique_acl_sid (sid, principal)
) ENGINE=InnoDB;

CREATE TABLE acl_class (
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	class VARCHAR(100) NOT NULL,
	UNIQUE KEY uk_acl_class (class)
) ENGINE=InnoDB;

CREATE TABLE acl_object_identity (
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	object_id_class BIGINT UNSIGNED NOT NULL,
	object_id_identity BIGINT NOT NULL,
	parent_object BIGINT UNSIGNED,
	owner_sid BIGINT UNSIGNED,
	entries_inheriting BOOLEAN NOT NULL,
	UNIQUE KEY uk_acl_object_identity (object_id_class, object_id_identity),
	CONSTRAINT fk_acl_object_identity_parent FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id),
	CONSTRAINT fk_acl_object_identity_class FOREIGN KEY (object_id_class) REFERENCES acl_class (id),
	CONSTRAINT fk_acl_object_identity_owner FOREIGN KEY (owner_sid) REFERENCES acl_sid (id)
) ENGINE=InnoDB;

CREATE TABLE acl_entry (
	id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	acl_object_identity BIGINT UNSIGNED NOT NULL,
	ace_order INTEGER NOT NULL,
	sid BIGINT UNSIGNED NOT NULL,
	mask INTEGER UNSIGNED NOT NULL,
	granting BOOLEAN NOT NULL,
	audit_success BOOLEAN NOT NULL,
	audit_failure BOOLEAN NOT NULL,
	UNIQUE KEY unique_acl_entry (acl_object_identity, ace_order),
	CONSTRAINT fk_acl_entry_object FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id),
	CONSTRAINT fk_acl_entry_acl FOREIGN KEY (sid) REFERENCES acl_sid (id)
) ENGINE=InnoDB;

create table persistent_logins (
	username varchar(64) not null,
	series varchar(64) primary key,
	token varchar(64) not null,
	last_used timestamp not null
);

create table contacts (
    id bigint unsigned not null auto_increment primary key,
    contact_name varchar(50) not null,
    email varchar(50) not null
) engine = InnoDb;

create table menus (
    id bigint unsigned not null auto_increment primary key,
    menu_name varchar(50) not null,
    menu_path varchar(50) not null
) engine = InnoDb;
