create database if not exists firefly_whisper;
use firefly_whisper;

drop table if exists `message`;
drop table if exists `article_comment`;
drop table if exists `article_like`;
drop table if exists `article_image`;
drop table if exists `article`;
drop table if exists `user_follow`;
drop table if exists `user`;

create table `user` (
    id int unsigned primary key auto_increment,
    phone_number char(11) not null unique,
    `password` char(32),
    personal_signature varchar(32),
    nickname varchar(8) not null,
    email varchar(128),
    avatar_url varchar(128) not null,
    create_time datetime not null,
    update_time datetime not null
);

create table `user_follow` (
    id int unsigned primary key auto_increment,
    follower_user_id int unsigned not null,
    followed_user_id int unsigned not null,
    foreign key (follower_user_id) references `user` (id),
    foreign key (followed_user_id) references `user` (id),
    unique key (follower_user_id, followed_user_id),
    check (follower_user_id <> followed_user_id)
);

create table `article` (
    id int unsigned primary key auto_increment,
    content varchar(512),
    user_id int unsigned not null,
    create_time datetime not null,
    foreign key (user_id) references `user`(id)
);

create table `article_image` (
    id int unsigned primary key auto_increment,
    article_id int unsigned not null,
    image_url varchar(255) not null,
    foreign key (article_id) references `article` (id)
);

create table `article_like` (
    id int unsigned primary key auto_increment,
    user_id int unsigned not null,
    article_id int unsigned not null,
    foreign key (user_id) references `user` (id),
    foreign key (article_id) references `article` (id),
    unique key (article_id, user_id)
);

create table `article_comment` (
    id int unsigned primary key auto_increment,
    content varchar(64) not null,
    user_id int unsigned not null,
    article_id int unsigned not null,
    create_time datetime not null,
    foreign key (user_id) references `user` (id),
    foreign key (article_id) references `article` (id)
);

create table `message` (
    id int unsigned primary key auto_increment,
    image_url varchar(255),
    text_content varchar(64),
    sending_user_id int unsigned not null,
    receiving_user_id int unsigned not null,
    create_time datetime not null,
    foreign key (sending_user_id) references `user` (id),
    foreign key (receiving_user_id) references `user` (id),
    check (sending_user_id <> receiving_user_id)
);
