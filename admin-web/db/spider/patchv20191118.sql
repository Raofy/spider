
DROP TABLE
IF
	EXISTS `spider_run_info`;
CREATE TABLE `spider_run_info` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`run` INT ( 11 ) DEFAULT 0 COMMENT '已经跑的任务数',
	`fail` INT ( 11 ) DEFAULT 0 COMMENT '出错任务数',
	`new_data` INT ( 11 ) DEFAULT 0 COMMENT '新增数据次数',
	`use_time` INT ( 11 ) DEFAULT 0 COMMENT '平均采集时间(ms)',
	`file_size` INT ( 11 ) DEFAULT 0 COMMENT '平均文件大小(kb)',
	`create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`ip` VARCHAR ( 50 ) NOT NULL COMMENT 'ip 地址',
	PRIMARY KEY ( `id` ),
KEY `create_time_idx` ( `create_time` )
) COMMENT = '爬虫服务统计';


DROP TABLE
IF
	EXISTS `spider_fail`;
CREATE TABLE `spider_fail` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`ip` VARCHAR ( 50 ) NOT NULL COMMENT 'ip 地址',
	`temp_id` INT ( 11 ) DEFAULT NULL COMMENT '模版 id',
	`err_msg` text DEFAULT NULL COMMENT '错误信息',
	`create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY ( `id` ),
	KEY `create_time_idx` ( `create_time` ),
	KEY `ip_temp_id_idx` ( `ip`, `temp_id` )
) COMMENT = '爬虫失败信息';