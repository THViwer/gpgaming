
-- 会员添加注册ip和风险等级
ALTER TABLE member ADD register_ip varchar(32) not null default 'None';
ALTER TABLE member ADD risk_level varchar(20) not null default 'None';

-- 平台logo可配置
ALTER TABLE platform_bind ADD name varchar(64) not null default '';
ALTER TABLE platform_bind ADD icon varchar(256) not null default '';
ALTER TABLE platform_bind ADD mobile_icon varchar(256) not null default '';
ALTER TABLE platform_bind ADD disable_icon varchar(256) not null default '';
ALTER TABLE platform_bind ADD mobile_disable_icon varchar(256) not null default '';
ALTER TABLE platform_bind ADD origin_icon varchar(256) not null default '';
ALTER TABLE platform_bind ADD origin_icon_over varchar(256) not null default '';
ALTER TABLE platform_bind ADD platform_detail_icon varchar(256) not null default '';
ALTER TABLE platform_bind ADD platform_detail_icon_over varchar(256) not null default '';

