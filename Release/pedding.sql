ALTER TABLE promotion ADD code varchar(20) NOT NULL DEFAULT '';

ALTER TABLE member ADD vip_id INT NOT NULL DEFAULT -1;


CREATE TABLE `vip` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `client_id` int(11) NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `level_id` int(11) NOT NULL,
  `logo` varchar(256) NOT NULL DEFAULT '',
  `days` varchar(20) NOT NULL DEFAULT '',
  `deposit_amount` decimal(10,2) NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'Normal',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


