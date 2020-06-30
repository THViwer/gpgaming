ALTER TABLE member ADD sale_id Int not null default -1;
ALTER TABLE member ADD sale_scope varchar(20) not null default 'System';

ALTER TABLE member_daily_report ADD sale_id Int not null default -1;


ALTER TABLE waiter ADD role varchar(20) not null default 'Waiter';
ALTER TABLE waiter ADD own_customer_scale DECIMAL(10,2) not null default 0;
ALTER TABLE waiter ADD system_customer_scale DECIMAL(10,2) not null default 0;
ALTER TABLE member_daily_report ADD sale_scope VARCHAR(20) not null default 'System';


CREATE TABLE `member_info` (
  `member_id` int(11) unsigned NOT NULL,
  `boss_id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `agent_id` int(11) NOT NULL,
  `sale_id` int(11) NOT NULL,
  `username` varchar(64) NOT NULL DEFAULT '',
  `total_deposit` decimal(10,2) NOT NULL DEFAULT '0.00',
  `last_deposit_time` timestamp NULL DEFAULT NULL,
  `total_deposit_count` int(11) NOT NULL DEFAULT '0',
  `total_withdraw` decimal(10,2) NOT NULL DEFAULT '0.00',
  `last_withdraw_time` timestamp NULL DEFAULT NULL,
  `total_withdraw_count` int(11) NOT NULL DEFAULT '0',
  `register_time` timestamp NULL DEFAULT NULL,
  `last_login_time` timestamp NULL DEFAULT NULL,
  `login_count` int(11) NOT NULL DEFAULT '0',
  `last_sale_time` timestamp NULL DEFAULT NULL,
  `sale_count` int(11) NOT NULL DEFAULT '0',
  `status` varchar(20) NOT NULL DEFAULT 'Normal',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sale_log` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `boss_id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `sale_id` int(11) NOT NULL,
  `member_id` int(11) NOT NULL,
  `remark` varchar(256) NOT NULL DEFAULT '',
  `status` varchar(20) NOT NULL DEFAULT 'Normal',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sale_month_report` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `boss_id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `sale_id` int(11) NOT NULL,
  `sale_username` varchar(64) NOT NULL DEFAULT '',
  `day` varchar(20) NOT NULL DEFAULT '',
  `own_total_deposit` decimal(10,2) NOT NULL,
  `own_total_withdraw` decimal(10,2) NOT NULL,
  `own_total_promotion` decimal(10,2) NOT NULL,
  `own_total_rebate` decimal(10,2) NOT NULL,
  `own_customer_scale` decimal(10,2) NOT NULL,
  `own_customer_fee` decimal(10,2) NOT NULL,
  `system_total_deposit` decimal(10,2) NOT NULL,
  `system_total_withdraw` decimal(10,2) NOT NULL,
  `system_total_promotion` decimal(10,2) NOT NULL,
  `system_total_rebate` decimal(10,2) NOT NULL,
  `system_customer_scale` decimal(10,2) NOT NULL,
  `system_customer_fee` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Normal',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `sale_daily_report` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `boss_id` int(11) NOT NULL,
  `client_id` int(11) NOT NULL,
  `sale_id` int(11) NOT NULL,
  `sale_username` varchar(64) NOT NULL DEFAULT '',
  `day` varchar(20) NOT NULL DEFAULT '',
  `own_total_deposit` decimal(10,2) NOT NULL,
  `own_total_withdraw` decimal(10,2) NOT NULL,
  `own_total_promotion` decimal(10,2) NOT NULL,
  `own_total_rebate` decimal(10,2) NOT NULL,
  `own_customer_scale` decimal(10,2) NOT NULL,
  `own_customer_fee` decimal(10,2) NOT NULL,
  `system_total_deposit` decimal(10,2) NOT NULL,
  `system_total_withdraw` decimal(10,2) NOT NULL,
  `system_total_promotion` decimal(10,2) NOT NULL,
  `system_total_rebate` decimal(10,2) NOT NULL,
  `system_customer_scale` decimal(10,2) NOT NULL,
  `system_customer_fee` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Normal',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;