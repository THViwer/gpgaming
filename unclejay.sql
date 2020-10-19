alter table  promotion add show_latest_promotion TINYINT default 0;

alter table platform_bind add unclejay_moble_ocon VARCHAR(256) default null;

alter table banner add platform_category VARCHAR (20) default null;