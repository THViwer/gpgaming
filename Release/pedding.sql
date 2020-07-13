
-- 会员添加注册ip和风险等级
ALTER TABLE member ADD register_ip varchar(32) not null default 'None';
ALTER TABLE member ADD risk_level varchar(20) not null default 'None';
