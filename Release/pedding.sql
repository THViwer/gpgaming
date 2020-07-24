ALTER TABLE sale_daily_report ADD own_member_count INT DEFAULT 0;
ALTER TABLE sale_daily_report ADD own_system_count INT DEFAULT 0;
ALTER TABLE sale_month_report ADD system_member_count INT DEFAULT 0;
ALTER TABLE sale_month_report ADD own_system_count INT DEFAULT 0;
