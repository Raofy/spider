DROP TABLE
IF
	EXISTS `template`;
CREATE TABLE `template` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`page_site` VARCHAR ( 500 ) DEFAULT NULL COMMENT '页面起始网址',
	`real_site` text COMMENT '抓取真正网址',
	`title` VARCHAR ( 500 ) DEFAULT NULL COMMENT '页面标题，默认为网页标题',
	`filter_by` INT ( 11 ) DEFAULT '1' COMMENT '去重方式 ：0 不过滤 ｜1 根据url进行过滤 ｜ 2 根据网页源码进行过滤 ｜ 3 根据结果过滤',
	`status` TINYINT ( 4 ) DEFAULT '1' COMMENT '当前页面监控状态 0停止  1运行中  2出错',
	`interval_time` INT ( 10 ) DEFAULT '10' COMMENT '页面爬取间隔，单位为秒',
	`channel` VARCHAR ( 250 ) DEFAULT NULL COMMENT '同一个标题可能有多个不同的频道',
	`category` VARCHAR ( 50 ) DEFAULT NULL COMMENT '分类',
	`encoding` VARCHAR ( 20 ) DEFAULT 'UTF-8',
	`create_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加监控页面时间',
	`priority` INT ( 11 ) DEFAULT '50' COMMENT '页面优先级',
	`is_foreign` TINYINT ( 1 ) DEFAULT '0',
	`dept_id` BIGINT ( 20 ) DEFAULT NULL COMMENT '所属部门',
	`update_time` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
	`is_special` TINYINT ( 1 ) DEFAULT '0',
	`detail_encoding` VARCHAR ( 20 ) DEFAULT 'UTF-8',
	`spider_temp_id` BIGINT DEFAULT NULL COMMENT '特殊爬虫模版的id',
	`encrypt_type` VARCHAR ( 20 ) DEFAULT NULL COMMENT '解密类型',
	`remark` VARCHAR ( 500 ) DEFAULT NULL COMMENT '备注',
	`deleted` INT DEFAULT NULL COMMENT '是否删除',
	PRIMARY KEY ( `id` ),
	KEY `page_site_idx` ( `page_site` )
) COMMENT '爬虫模版';


DROP TABLE
IF
	EXISTS `template_rule`;
CREATE TABLE `template_rule` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`temp_id` BIGINT ( 20 ) NOT NULL COMMENT '模版id',
	`type` INT ( 11 ) DEFAULT '1' COMMENT '内容类型，1文本，2json，3jsonp',
	`headers` VARCHAR ( 500 ) DEFAULT NULL COMMENT '头信息',
	`method` VARCHAR ( 50 ) DEFAULT NULL COMMENT '方法，post、get',
	`params` VARCHAR ( 500 ) DEFAULT NULL COMMENT '附带参数',
	`rules_json` VARCHAR ( 500 ) DEFAULT NULL COMMENT '规则，json',
	`is_detail` TINYINT ( 1 ) DEFAULT '0' COMMENT '是否为详情页面的规则',
	PRIMARY KEY ( `id` ),
KEY `temp_id_idx` ( `temp_id` )
) COMMENT '爬虫模版规则';




