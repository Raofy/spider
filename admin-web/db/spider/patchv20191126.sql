ALTER TABLE template ADD COLUMN deleted INT DEFAULT 0 ;
ALTER TABLE template ADD COLUMN proxy_level INT DEFAULT 0 ;
ALTER TABLE template ADD COLUMN `expire_time` TIMESTAMP NULL DEFAULT NULL COMMENT '模版预计过期时间';

ALTER TABLE template ADD COLUMN create_user VARCHAR ( 100 ) DEFAULT null COMMENT '操作用户';
ALTER TABLE template ADD COLUMN update_user VARCHAR ( 100 ) DEFAULT null COMMENT '操作用户';
ALTER TABLE template ADD COLUMN delete_user VARCHAR ( 100 ) DEFAULT null COMMENT '操作用户';

ALTER TABLE template ADD COLUMN `delete_time` TIMESTAMP NULL DEFAULT NULL COMMENT '删除时间';


DROP TABLE
IF
	EXISTS `ip_info`;
CREATE TABLE `ip_info` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`ip` VARCHAR ( 100 ) COMMENT 'ip:端口、113.120.61.166:22989',
	`is_foreign` TINYINT ( 1 ) DEFAULT '0',
	`area` VARCHAR ( 50 ) DEFAULT '中国' COMMENT '地区',
	`platform` VARCHAR ( 50 ) DEFAULT NULL COMMENT '代理平台',
	`delay` INT DEFAULT NULL COMMENT 'ip 延迟 ms',
	`check_times` INT DEFAULT 0 COMMENT '检查次数',
	`is_valid`  DEFAULT 0 COMMENT '是否有效',
	`proxy_level` INT DEFAULT 2 COMMENT ' 2 底质量代理 ｜ 3 高质量代理',
	`expire_time` TIMESTAMP NULL DEFAULT NULL COMMENT '预计过期时间',
	`check_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '检验时间',
	`create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`deleted` INT DEFAULT 0 COMMENT '是否删除',
	PRIMARY KEY ( `id` ),
	KEY `ip_idx` ( ip ),
	KEY `check_time_idx` ( check_time )
) COMMENT '代理ip 信息';

CREATE UNIQUE INDEX idx_ip_del on ip_info(ip, deleted);