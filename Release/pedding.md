ALTER TABLE member ADD sale_id Int not null default -1;
ALTER TABLE member ADD sale_scope varchar(20) not null default 'System';

ALTER TABLE member_daily_report ADD sale_id Int not null default -1;


ALTER TABLE waiter ADD role varchar(20) not null default 'Waiter';
ALTER TABLE waiter ADD own_customer_scale DECIMAL(10,2) not null default 0;
ALTER TABLE waiter ADD system_customer_scale DECIMAL(10,2) not null default 0;


