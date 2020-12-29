
DROP TABLE
IF
	EXISTS `product_task_info`;
CREATE TABLE `product_task_info` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`product` INT ( 11 ) DEFAULT NULL COMMENT '生产任务总数',
	`un_put` INT ( 11 ) DEFAULT NULL COMMENT '重复的任务数',
	`times` INT ( 11 ) DEFAULT NULL COMMENT '生产任务次数',
	`puted` INT ( 11 ) DEFAULT NULL COMMENT '放入队列的数量',
	`geted` INT ( 11 ) DEFAULT NULL COMMENT '已经被领取的任务数',
	`cur_queue_size` INT ( 11 ) DEFAULT NULL COMMENT '当前队列的长度',
	`cur_foreign_queue_size` INT ( 11 ) DEFAULT NULL COMMENT '当前队列的长度',
	`create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY ( `id` ),
KEY `create_time_idx` ( `create_time` )
) COMMENT = '任务统计';

INSERT INTO `schedule_job` (`bean_name`, `params`, `cron_expression`, `status`, `remark`, `create_time`) VALUES ('urlTaskStatisticsTask', null, '0/10 * * * * ?', '0', '定时统计调度器产生的任务', now());

