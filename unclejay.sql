alter table  promotion add show_latest_promotion TINYINT default 0;

alter table platform_bind add unclejay_moble_ocon VARCHAR(256) default null;

alter table banner add platform_category VARCHAR (20) default null;



-- 首充

alter table member add first_deposit TINYINT default 0;
alter table deposit add first_deposit TINYINT default 0;
alter table pay_order add first_deposit TINYINT default 0;
alter table pay_order add first_deposit TINYINT default 0;
alter table client_daily_report add first_deposit_frequency int default 0;
alter table client_daily_report add total_first_deposit int default 0;
