ALTER TABLE pay_order ADD pay_id Int not null;
ALTER TABLE pay_order ADD bank varchar(20) default null;

-- 更新pay_order数据
select * from pay_bind;
update pay_order o set pay_id = 1 where pay_type = 'M3Pay';
update pay_order o set pay_id = 3 where pay_type = 'SurePay';
update pay_order o set pay_id = 4 where pay_type  = 'FPX';