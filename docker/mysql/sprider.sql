/*
Navicat MySQL Data Transfer

Source Server         : 爬虫docker数据库
Source Server Version : 80018
Source Host           : 47.111.13.97:3306
Source Database       : sprider

Target Server Type    : MYSQL
Target Server Version : 80018
File Encoding         : 65001

Date: 2020-01-06 11:25:58
*/

drop database IF EXISTS `sprider`  ;


CREATE DATABASE `sprider` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

use `sprider`;


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for analysis_his
-- ----------------------------
DROP TABLE IF EXISTS `analysis_his`;
CREATE TABLE `analysis_his` (
  `id` bigint(40) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `platform` varchar(40) DEFAULT NULL COMMENT '平台',
  `referer` varchar(500) DEFAULT NULL COMMENT '路径',
  `data` text COMMENT '数据',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `device_id` varchar(300) DEFAULT NULL COMMENT '设备id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8601 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户操作历史记录';

-- ----------------------------
-- Records of analysis_his
-- ----------------------------
INSERT INTO `analysis_his` VALUES ('612', 'andorid', '/参考/幻灯片', '数据', '2019-12-27 01:03:37', null);
INSERT INTO `analysis_his` VALUES ('8599', 'android', '快讯', '{\"type\":\"下拉刷新\",\"referer\":\"快讯\",\"sessionType\":3}', '2020-01-02 22:54:25', '607b5761f9194566b572cbba4263f125');
INSERT INTO `analysis_his` VALUES ('8600', 'ios', '快讯/综合', '{\"type\":\"下拉刷新\",\"referer\":\"快讯\\/综合\",\"sessionType\":3}', '2020-01-02 22:54:29', '59e509e4face3482e6c98844d9564525b0bafbfp');

-- ----------------------------
-- Table structure for ip_info
-- ----------------------------
DROP TABLE IF EXISTS `ip_info`;
CREATE TABLE `ip_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(100) DEFAULT NULL COMMENT 'ip:端口、113.120.61.166:22989',
  `is_foreign` tinyint(1) DEFAULT '0',
  `area` varchar(50) DEFAULT NULL COMMENT '地区',
  `platform` varchar(50) DEFAULT NULL COMMENT '代理平台',
  `delay` int(11) DEFAULT NULL COMMENT 'ip 延迟 ms',
  `check_times` int(11) DEFAULT NULL COMMENT '检查次数',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT '过期时间',
  `check_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '检验时间',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `proxy_level` int(11) DEFAULT '0',
  `deleted` int(11) DEFAULT '0',
  `is_valid` tinyint(2) DEFAULT '0' COMMENT '是否有效',
  `type` varchar(50) DEFAULT NULL COMMENT '类型 http/ https/ socket',
  `extra` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ip_idx` (`ip`),
  KEY `check_time_idx` (`check_time`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8 COMMENT='代理ip 信息';

-- ----------------------------
-- Records of ip_info
-- ----------------------------

-- ----------------------------
-- Table structure for page_new
-- ----------------------------
DROP TABLE IF EXISTS `page_new`;
CREATE TABLE `page_new` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `pagesite` varchar(500) DEFAULT NULL COMMENT '页面起始网址',
  `realsite` mediumtext COMMENT '抓取真正网址',
  `sitemd5` varchar(50) DEFAULT NULL COMMENT '根据网址生成的唯一md5',
  `pagetitle` varchar(500) DEFAULT NULL COMMENT '页面标题，默认为网页标题',
  `status` tinyint(4) DEFAULT '5' COMMENT '当前页面监控状态 0 没有监控1  正在监控  2 监控出问题了，创建页面默认就开始监控',
  `deltatime` int(10) DEFAULT '30000' COMMENT '页面爬取间隔，单位为毫秒，默认30秒钟爬取一次',
  `channel` varchar(50) DEFAULT NULL COMMENT '同一个标题可能有多个不同的频道',
  `headers` text,
  `cates` varchar(50) DEFAULT '文章' COMMENT '分类',
  `followlink` int(11) DEFAULT '1' COMMENT '是否抓取链接内容，1表示抓取默认值，0表示不抓取，只抓取链接就入库',
  `ctype` int(11) DEFAULT '1' COMMENT '内容类型，1文本，2json，3jsonp',
  `encoding` varchar(20) DEFAULT 'UTF-8',
  `link_unique_by` int(11) DEFAULT '1' COMMENT '1 通过链接，2通过文字',
  `linkrule` varchar(255) DEFAULT NULL COMMENT '链接规则，内容为text时为css规则，内容为json时【数组名#内容规则#链接规则】',
  `titlerule` varchar(50) DEFAULT NULL COMMENT '标题抓取规则',
  `contentrule` varchar(50) DEFAULT NULL COMMENT '内容抓取规则',
  `createtime` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加监控页面时间',
  `priority` int(11) DEFAULT '50' COMMENT '页面优先级',
  `foreign` int(11) DEFAULT '0',
  `group` varchar(20) DEFAULT '1' COMMENT '1 ',
  `updateat` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `skip_error` int(11) DEFAULT '0',
  `as_special` int(11) DEFAULT '0',
  `detail_encoding` varchar(20) DEFAULT 'UTF-8',
  `extra` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `cate` (`cates`)
) ENGINE=InnoDB AUTO_INCREMENT=1533 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of page_new
-- ----------------------------

-- ----------------------------
-- Table structure for product_task_info
-- ----------------------------
DROP TABLE IF EXISTS `product_task_info`;
CREATE TABLE `product_task_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product` int(11) DEFAULT NULL COMMENT '生产任务总数',
  `un_put` int(11) DEFAULT NULL COMMENT '重复的任务数',
  `times` int(11) DEFAULT NULL COMMENT '生产任务次数',
  `puted` int(11) DEFAULT NULL COMMENT '放入队列的数量',
  `geted` int(11) DEFAULT NULL COMMENT '已经被领取的任务数',
  `cur_queue_size` int(11) DEFAULT NULL COMMENT '当前队列的长度',
  `cur_foreign_queue_size` int(11) DEFAULT NULL COMMENT '当前队列的长度',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `create_time_idx` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=38689 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务统计';

-- ----------------------------
-- Records of product_task_info
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_BLOB_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_BLOB_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_CALENDARS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
CREATE TABLE `QRTZ_CALENDARS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_CALENDARS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_CRON_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_CRON_TRIGGERS
-- ----------------------------
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('RScheduler', 'TASK_1', 'DEFAULT', '0 0/30 * * * ?', 'GMT');
INSERT INTO `QRTZ_CRON_TRIGGERS` VALUES ('RScheduler', 'TASK_2', 'DEFAULT', '0/20 * * * * ?', 'GMT');

-- ----------------------------
-- Table structure for QRTZ_FIRED_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_FIRED_TRIGGERS
-- ----------------------------
INSERT INTO `QRTZ_FIRED_TRIGGERS` VALUES ('RScheduler', 'localhost15779349444521577934944452', 'TASK_2', 'DEFAULT', 'localhost1577934944452', '1577935420024', '1577935440000', '5', 'ACQUIRED', null, null, '0', '0');

-- ----------------------------
-- Table structure for QRTZ_JOB_DETAILS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_JOB_DETAILS
-- ----------------------------
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('RScheduler', 'TASK_1', 'DEFAULT', null, 'com.jin10.spider.modules.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720035636F6D2E6A696E31302E7370696465722E6D6F64756C65732E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200074C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B7870740008746573745461736B7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000016E7C5BBBE87874000E3020302F3330202A202A202A203F7372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000000000000017400066A696E73686974000CE58F82E695B0E6B58BE8AF95737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('RScheduler', 'TASK_2', 'DEFAULT', null, 'com.jin10.spider.modules.job.utils.ScheduleJob', '0', '0', '0', '0', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720035636F6D2E6A696E31302E7370696465722E6D6F64756C65732E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200074C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074001575726C5461736B537461746973746963735461736B7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000016E7C5C02387874000E302F3230202A202A202A202A203F7372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000000270740024E5AE9AE697B6E7BB9FE8AEA1E8B083E5BAA6E599A8E4BAA7E7949FE79A84E4BBBBE58AA1737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);

-- ----------------------------
-- Table structure for QRTZ_LOCKS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_LOCKS`;
CREATE TABLE `QRTZ_LOCKS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_LOCKS
-- ----------------------------
INSERT INTO `QRTZ_LOCKS` VALUES ('RScheduler', 'STATE_ACCESS');
INSERT INTO `QRTZ_LOCKS` VALUES ('RScheduler', 'TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_PAUSED_TRIGGER_GRPS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_SCHEDULER_STATE
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_SCHEDULER_STATE
-- ----------------------------
INSERT INTO `QRTZ_SCHEDULER_STATE` VALUES ('RScheduler', 'localhost1577934944452', '1577935427300', '15000');

-- ----------------------------
-- Table structure for QRTZ_SIMPLE_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_SIMPLE_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_SIMPROP_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_SIMPROP_TRIGGERS`;
CREATE TABLE `QRTZ_SIMPROP_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_SIMPROP_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_SIMPROP_TRIGGERS
-- ----------------------------

-- ----------------------------
-- Table structure for QRTZ_TRIGGERS
-- ----------------------------
DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
CREATE TABLE `QRTZ_TRIGGERS` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of QRTZ_TRIGGERS
-- ----------------------------
INSERT INTO `QRTZ_TRIGGERS` VALUES ('RScheduler', 'TASK_1', 'DEFAULT', 'TASK_1', 'DEFAULT', null, '1574046000000', '-1', '5', 'WAITING', 'CRON', '1574044460000', '0', null, '2', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720035636F6D2E6A696E31302E7370696465722E6D6F64756C65732E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200074C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B7870740008746573745461736B7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000016E7C5BBBE87874000E3020302F3330202A202A202A203F7372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B020000787000000000000000017400066A696E73686974000CE58F82E695B0E6B58BE8AF95737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);
INSERT INTO `QRTZ_TRIGGERS` VALUES ('RScheduler', 'TASK_2', 'DEFAULT', 'TASK_2', 'DEFAULT', null, '1574044460000', '-1', '5', 'WAITING', 'CRON', '1574044460000', '0', null, '2', 0xACED0005737200156F72672E71756172747A2E4A6F62446174614D61709FB083E8BFA9B0CB020000787200266F72672E71756172747A2E7574696C732E537472696E674B65794469727479466C61674D61708208E8C3FBC55D280200015A0013616C6C6F77735472616E7369656E74446174617872001D6F72672E71756172747A2E7574696C732E4469727479466C61674D617013E62EAD28760ACE0200025A000564697274794C00036D617074000F4C6A6176612F7574696C2F4D61703B787001737200116A6176612E7574696C2E486173684D61700507DAC1C31660D103000246000A6C6F6164466163746F724900097468726573686F6C6478703F4000000000000C7708000000100000000174000D4A4F425F504152414D5F4B455973720035636F6D2E6A696E31302E7370696465722E6D6F64756C65732E6A6F622E656E746974792E5363686564756C654A6F62456E7469747900000000000000010200074C00086265616E4E616D657400124C6A6176612F6C616E672F537472696E673B4C000A63726561746554696D657400104C6A6176612F7574696C2F446174653B4C000E63726F6E45787072657373696F6E71007E00094C00056A6F6249647400104C6A6176612F6C616E672F4C6F6E673B4C0006706172616D7371007E00094C000672656D61726B71007E00094C00067374617475737400134C6A6176612F6C616E672F496E74656765723B787074001575726C5461736B537461746973746963735461736B7372000E6A6176612E7574696C2E44617465686A81014B597419030000787077080000016E7C5C02387874000E302F3230202A202A202A202A203F7372000E6A6176612E6C616E672E4C6F6E673B8BE490CC8F23DF0200014A000576616C7565787200106A6176612E6C616E672E4E756D62657286AC951D0B94E08B0200007870000000000000000270740024E5AE9AE697B6E7BB9FE8AEA1E8B083E5BAA6E599A8E4BAA7E7949FE79A84E4BBBBE58AA1737200116A6176612E6C616E672E496E746567657212E2A0A4F781873802000149000576616C75657871007E0013000000007800);

-- ----------------------------
-- Table structure for schedule_job
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job`;
CREATE TABLE `schedule_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `cron_expression` varchar(100) DEFAULT NULL COMMENT 'cron表达式',
  `status` tinyint(4) DEFAULT NULL COMMENT '任务状态  0：正常  1：暂停',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='定时任务';

-- ----------------------------
-- Records of schedule_job
-- ----------------------------
INSERT INTO `schedule_job` VALUES ('1', 'testTask', 'jinshi', '0 0/30 * * * ?', '0', '参数测试', '2019-11-18 10:33:37');
INSERT INTO `schedule_job` VALUES ('2', 'urlTaskStatisticsTask', null, '0/20 * * * * ?', '0', '定时统计调度器产生的任务', '2019-11-18 10:33:55');

-- ----------------------------
-- Table structure for schedule_job_log
-- ----------------------------
DROP TABLE IF EXISTS `schedule_job_log`;
CREATE TABLE `schedule_job_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志id',
  `job_id` bigint(20) NOT NULL COMMENT '任务id',
  `bean_name` varchar(200) DEFAULT NULL COMMENT 'spring bean名称',
  `params` varchar(2000) DEFAULT NULL COMMENT '参数',
  `status` tinyint(4) NOT NULL COMMENT '任务状态    0：成功    1：失败',
  `error` varchar(2000) DEFAULT NULL COMMENT '失败信息',
  `times` int(11) NOT NULL COMMENT '耗时(单位：毫秒)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `job_id` (`job_id`)
) ENGINE=InnoDB AUTO_INCREMENT=95489 DEFAULT CHARSET=utf8 COMMENT='定时任务日志';

-- ----------------------------
-- Records of schedule_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for spider_category
-- ----------------------------
DROP TABLE IF EXISTS `spider_category`;
CREATE TABLE `spider_category` (
  `cate_id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '消息类别管理',
  `category_name` varchar(40) DEFAULT NULL COMMENT '类别名称',
  `category_color` varchar(20) DEFAULT NULL COMMENT '类别颜色',
  PRIMARY KEY (`cate_id`),
  UNIQUE KEY `spider_category_category_name_uindex` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息类别管理';

-- ----------------------------
-- Records of spider_category
-- ----------------------------
INSERT INTO `spider_category` VALUES ('1', '文章', '#FF00FF');
INSERT INTO `spider_category` VALUES ('2', '楼市', '#FFC125');
INSERT INTO `spider_category` VALUES ('3', '快讯', '#FF0000');
INSERT INTO `spider_category` VALUES ('4', '股市', '#21ff06');
INSERT INTO `spider_category` VALUES ('16', '公告', '#fc0107');
INSERT INTO `spider_category` VALUES ('17', '港股', '#996633');
INSERT INTO `spider_category` VALUES ('18', '期货', '#7FFFAA');
INSERT INTO `spider_category` VALUES ('20', 'A股分析', '	#FF8C00');
INSERT INTO `spider_category` VALUES ('21', '报告', '#1E90FF');
INSERT INTO `spider_category` VALUES ('22', '科技', '	#8A2BE2');
INSERT INTO `spider_category` VALUES ('23', '美股', '	#00FFFF');
INSERT INTO `spider_category` VALUES ('24', '货币', '	#8A2BE2');
INSERT INTO `spider_category` VALUES ('25', 'A股', '	#AFEEEE');
INSERT INTO `spider_category` VALUES ('26', '公司', '	#FFD700');
INSERT INTO `spider_category` VALUES ('27', '汽车', '	#F4A460');
INSERT INTO `spider_category` VALUES ('28', 'app', '#FFA07A');
INSERT INTO `spider_category` VALUES ('29', '新闻', '	#F08080');
INSERT INTO `spider_category` VALUES ('30', '外媒文章', '	#B22222');

-- ----------------------------
-- Table structure for spider_fail
-- ----------------------------
DROP TABLE IF EXISTS `spider_fail`;
CREATE TABLE `spider_fail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(50) NOT NULL COMMENT 'ip 地址',
  `temp_id` int(11) DEFAULT NULL COMMENT '模版 id',
  `err_msg` text COMMENT '错误信息',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `create_time_idx` (`create_time`),
  KEY `ip_temp_id_idx` (`ip`,`temp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=50542 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='爬虫失败信息';

-- ----------------------------
-- Records of spider_fail
-- ----------------------------

-- ----------------------------
-- Table structure for spider_label
-- ----------------------------
DROP TABLE IF EXISTS `spider_label`;
CREATE TABLE `spider_label` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `label_name` varchar(20) DEFAULT NULL COMMENT '标签名称',
  `label_color` varchar(20) DEFAULT NULL COMMENT '标签颜色配置',
  PRIMARY KEY (`id`),
  UNIQUE KEY `spider_label_label_name_uindex` (`label_name`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='爬虫消息标签库';

-- ----------------------------
-- Records of spider_label
-- ----------------------------
INSERT INTO `spider_label` VALUES ('13', '圆满', '#bdff60');

-- ----------------------------
-- Table structure for spider_message_2019_1
-- ----------------------------
DROP TABLE IF EXISTS `spider_message_2019_1`;
CREATE TABLE `spider_message_2019_1` (
  `id` bigint(70) NOT NULL COMMENT '主键id',
  `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `task_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分发任务id',
  `temp_id` int(10) DEFAULT NULL COMMENT '模板id',
  `dept_id` int(10) DEFAULT NULL COMMENT '部门id',
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '爬虫内容标题',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '爬虫内容对应的url',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '未知来源' COMMENT '网站来源',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '文章' COMMENT '消息类型  1.文章 2.快讯 3.楼市 等',
  `time` datetime DEFAULT NULL COMMENT '爬虫爬取的日期 ',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `channel` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '同一个标题可能有多个不同的频道',
  `insert_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `spider_message_msg_id_uindex` (`msg_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of spider_message_2019_1
-- ----------------------------

-- ----------------------------
-- Table structure for spider_message_2020_0
-- ----------------------------
DROP TABLE IF EXISTS `spider_message_2020_0`;
CREATE TABLE `spider_message_2020_0` (
  `id` bigint(70) NOT NULL COMMENT '主键id',
  `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `task_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分发任务id',
  `temp_id` int(10) DEFAULT NULL COMMENT '模板id',
  `dept_id` int(10) DEFAULT NULL COMMENT '部门id',
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '爬虫内容标题',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '爬虫内容对应的url',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '未知来源' COMMENT '网站来源',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '文章' COMMENT '消息类型  1.文章 2.快讯 3.楼市 等',
  `time` datetime DEFAULT NULL COMMENT '爬虫爬取的日期 ',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `channel` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '同一个标题可能有多个不同的频道',
  `insert_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `spider_message_msg_id_uindex` (`msg_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of spider_message_2020_0
-- ----------------------------

-- ----------------------------
-- Table structure for spider_message_2020_1
-- ----------------------------
DROP TABLE IF EXISTS `spider_message_2020_1`;
CREATE TABLE `spider_message_2020_1` (
  `id` bigint(70) NOT NULL COMMENT '主键id',
  `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `task_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分发任务id',
  `temp_id` int(10) DEFAULT NULL COMMENT '模板id',
  `dept_id` int(10) DEFAULT NULL COMMENT '部门id',
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '爬虫内容标题',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '爬虫内容对应的url',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '未知来源' COMMENT '网站来源',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '文章' COMMENT '消息类型  1.文章 2.快讯 3.楼市 等',
  `time` datetime DEFAULT NULL COMMENT '爬虫爬取的日期 ',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `channel` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '同一个标题可能有多个不同的频道',
  `insert_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `spider_message_msg_id_uindex` (`msg_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of spider_message_2020_1
-- ----------------------------

-- ----------------------------
-- Table structure for spider_message_2021_0
-- ----------------------------
DROP TABLE IF EXISTS `spider_message_2021_0`;
CREATE TABLE `spider_message_2021_0` (
  `id` bigint(70) NOT NULL COMMENT '主键id',
  `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `task_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分发任务id',
  `temp_id` int(10) DEFAULT NULL COMMENT '模板id',
  `dept_id` int(10) DEFAULT NULL COMMENT '部门id',
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '爬虫内容标题',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '爬虫内容对应的url',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '未知来源' COMMENT '网站来源',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '文章' COMMENT '消息类型  1.文章 2.快讯 3.楼市 等',
  `time` datetime DEFAULT NULL COMMENT '爬虫爬取的日期 ',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `channel` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '同一个标题可能有多个不同的频道',
  `insert_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `spider_message_msg_id_uindex` (`msg_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of spider_message_2021_0
-- ----------------------------

-- ----------------------------
-- Table structure for spider_message_2021_1
-- ----------------------------
DROP TABLE IF EXISTS `spider_message_2021_1`;
CREATE TABLE `spider_message_2021_1` (
  `id` bigint(70) NOT NULL COMMENT '主键id',
  `msg_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `task_id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分发任务id',
  `temp_id` int(10) DEFAULT NULL COMMENT '模板id',
  `dept_id` int(10) DEFAULT NULL COMMENT '部门id',
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '爬虫内容标题',
  `url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '爬虫内容对应的url',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '未知来源' COMMENT '网站来源',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '文章' COMMENT '消息类型  1.文章 2.快讯 3.楼市 等',
  `time` datetime DEFAULT NULL COMMENT '爬虫爬取的日期 ',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `channel` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '同一个标题可能有多个不同的频道',
  `insert_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `spider_message_msg_id_uindex` (`msg_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of spider_message_2021_1
-- ----------------------------

-- ----------------------------
-- Table structure for spider_message_abnormal
-- ----------------------------
DROP TABLE IF EXISTS `spider_message_abnormal`;
CREATE TABLE `spider_message_abnormal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `ex_type` int(1) DEFAULT NULL COMMENT '消息异常类型  1.category为空  2.category非法类型',
  `ex_remark` varchar(50) DEFAULT NULL COMMENT '消息异常描述',
  `spider_message` text COMMENT '不正常消息',
  `insert_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5266 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='异常消息记录';

-- ----------------------------
-- Records of spider_message_abnormal
-- ----------------------------

-- ----------------------------
-- Table structure for spider_message_monitor
-- ----------------------------
DROP TABLE IF EXISTS `spider_message_monitor`;
CREATE TABLE `spider_message_monitor` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `source` varchar(100) DEFAULT NULL COMMENT '消息来源',
  `category` varchar(100) DEFAULT NULL COMMENT '消息类型',
  `channel` varchar(100) DEFAULT NULL COMMENT '消息频道',
  `dispatch` int(1) DEFAULT NULL COMMENT '是否调度 0.是  1.否',
  `update_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=773 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息来源监控';

-- ----------------------------
-- Records of spider_message_monitor
-- ----------------------------

-- ----------------------------
-- Table structure for spider_message_screen
-- ----------------------------
DROP TABLE IF EXISTS `spider_message_screen`;
CREATE TABLE `spider_message_screen` (
  `id` bigint(70) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `msg_id` varchar(255) NOT NULL,
  `task_id` varchar(60) NOT NULL COMMENT '分发任务id',
  `temp_id` int(10) DEFAULT NULL COMMENT '模板id',
  `dept_id` int(10) DEFAULT NULL COMMENT '部门id',
  `title` text COMMENT '爬虫内容标题',
  `url` text COMMENT '爬虫内容对应的url',
  `source` varchar(50) DEFAULT '未知来源' COMMENT '网站来源',
  `category` varchar(20) DEFAULT '文章' COMMENT '消息类型  1.文章 2.快讯 3.楼市 等',
  `time` datetime DEFAULT NULL COMMENT '爬虫爬取的日期',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `channel` varchar(30) DEFAULT NULL COMMENT '同一个标题可能有多个不同的频道',
  `insert_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `spider_message_msg_id_uindex` (`msg_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3375 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='筛选爬虫消息列表';

-- ----------------------------
-- Records of spider_message_screen
-- ----------------------------

-- ----------------------------
-- Table structure for spider_run_info
-- ----------------------------
DROP TABLE IF EXISTS `spider_run_info`;
CREATE TABLE `spider_run_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `run` int(11) DEFAULT '0' COMMENT '已经跑的任务数',
  `fail` int(11) DEFAULT '0' COMMENT '出错任务数',
  `new_data` int(11) DEFAULT '0' COMMENT '新增数据次数',
  `use_time` int(11) DEFAULT '0' COMMENT '平均采集时间(ms)',
  `file_size` int(11) DEFAULT '0' COMMENT '平均文件大小(kb)',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `ip` varchar(50) NOT NULL COMMENT 'ip 地址',
  PRIMARY KEY (`id`),
  KEY `create_time_idx` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=39794 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='爬虫服务统计';

-- ----------------------------
-- Records of spider_run_info
-- ----------------------------

-- ----------------------------
-- Table structure for sys_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_data`;
CREATE TABLE `sys_data` (
  `data_id` bigint(10) NOT NULL AUTO_INCREMENT,
  `data_name` varchar(30) DEFAULT NULL COMMENT '数据权限名称',
  `data_perm` varchar(30) DEFAULT NULL COMMENT '数据权限标识',
  PRIMARY KEY (`data_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统数据权限';

-- ----------------------------
-- Records of sys_data
-- ----------------------------
INSERT INTO `sys_data` VALUES ('1', '爬虫监控', 'mointer');
INSERT INTO `sys_data` VALUES ('2', '服务器实时监控', 'server');
INSERT INTO `sys_data` VALUES ('4', '爬虫节点监控', 'node1');

-- ----------------------------
-- Table structure for sys_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_log`;
CREATE TABLE `sys_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(50) DEFAULT NULL COMMENT '用户操作',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `time` bigint(20) NOT NULL COMMENT '执行时长(毫秒)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP地址',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4949 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统日志';

-- ----------------------------
-- Records of sys_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
  `name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(200) DEFAULT NULL COMMENT '菜单URL',
  `perms` varchar(500) DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
  `type` int(11) DEFAULT NULL COMMENT '类型   0：目录   1：菜单   2：按钮',
  `icon` varchar(50) DEFAULT NULL COMMENT '菜单图标',
  `order_num` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单管理';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '0', '用户管理', '', 'sys:user', '0', '', '1');
INSERT INTO `sys_menu` VALUES ('2', '0', '角色管理', null, 'sys:role', '0', null, '2');
INSERT INTO `sys_menu` VALUES ('3', '0', '个人信息管理', '', '', '0', '', '3');
INSERT INTO `sys_menu` VALUES ('4', '0', '爬虫后台管理', '', '', '0', '', '4');
INSERT INTO `sys_menu` VALUES ('7', '0', '爬虫消息类别管理', '', 'sys:type', '0', null, '5');
INSERT INTO `sys_menu` VALUES ('8', '0', '爬虫消息历史记录', '', '', '0', null, '6');
INSERT INTO `sys_menu` VALUES ('9', '0', '爬虫标签管理', null, 'sys:label', '0', null, '7');
INSERT INTO `sys_menu` VALUES ('10', '0', '爬虫监控管理', null, '', '0', null, '8');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '超级管理员', '超级管理员', null, '2019-11-20 11:09:02');
INSERT INTO `sys_role` VALUES ('2', '开发人员', '开发人员', null, '2019-11-20 11:28:55');
INSERT INTO `sys_role` VALUES ('3', '可添加源用户', '可添加源用户', null, '2019-12-13 15:30:37');
INSERT INTO `sys_role` VALUES ('4', '普通用户', '普通用户', null, '2019-12-13 15:31:00');

-- ----------------------------
-- Table structure for sys_role_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_data`;
CREATE TABLE `sys_role_data` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` bigint(10) DEFAULT NULL COMMENT '角色id',
  `data_id` bigint(10) DEFAULT NULL COMMENT '数据id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色数据权限';

-- ----------------------------
-- Records of sys_role_data
-- ----------------------------
INSERT INTO `sys_role_data` VALUES ('5', '1', '1');
INSERT INTO `sys_role_data` VALUES ('6', '1', '2');
INSERT INTO `sys_role_data` VALUES ('8', '2', '4');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色与菜单对应关系';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1', '1', '1');
INSERT INTO `sys_role_menu` VALUES ('2', '1', '2');
INSERT INTO `sys_role_menu` VALUES ('3', '1', '3');
INSERT INTO `sys_role_menu` VALUES ('4', '1', '4');
INSERT INTO `sys_role_menu` VALUES ('21', '1', '9');
INSERT INTO `sys_role_menu` VALUES ('22', '1', '10');
INSERT INTO `sys_role_menu` VALUES ('23', '2', '3');
INSERT INTO `sys_role_menu` VALUES ('24', '2', '4');
INSERT INTO `sys_role_menu` VALUES ('25', '2', '10');
INSERT INTO `sys_role_menu` VALUES ('26', '1', '7');
INSERT INTO `sys_role_menu` VALUES ('27', '1', '8');
INSERT INTO `sys_role_menu` VALUES ('28', '3', '3');
INSERT INTO `sys_role_menu` VALUES ('29', '3', '4');
INSERT INTO `sys_role_menu` VALUES ('31', '4', '3');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) DEFAULT NULL COMMENT '密码',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(100) DEFAULT NULL COMMENT '手机号',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态  0：禁用   1：正常',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=1004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'admin', '$2a$10$IqetfiQLlcj7fmx2KvSpmeYyrNKMrqVqrjcDa3hRLEcLJovm.6CDO', '1', 'root@jinshi.io', '13612345678', '1', '2016-11-11 11:11:11', null);
INSERT INTO `sys_user` VALUES ('2', 'root', '$2a$10$wE2l4kdFWP/UDfTrB52NpODV84.TW1uKJEyCd8UARJsHP3NyrkRJa', null, '188866659959@163.com', '18883179959', '1', '2019-11-20 11:26:37', '2019-11-20 11:26:37');
INSERT INTO `sys_user` VALUES ('3', 'sam', '$2a$10$x20TtPemVj013wnpU8ccOOrCmilC8mD5OFZwWHZFwPcQEkJM3Le9W', null, '188866659959@163.com', '18883179959', '1', '2019-12-13 10:35:05', '2019-12-13 16:43:28');
INSERT INTO `sys_user` VALUES ('1000', '程明', '$2a$10$JW1Sz5HtlEiz.F74qpz8Rebg8cfKJjQUFtFcwgCfKDsGetldGOGdW', null, '', '', '1', '2019-12-13 15:44:49', '2019-12-13 15:44:49');
INSERT INTO `sys_user` VALUES ('1001', '方鸿达', '$2a$10$SJ/SL2VzXPiv4abL2PQfR.LMjLL26zpkpoGNXoedfpVPFltjTBsdm', null, '', '', '1', '2019-12-13 15:48:15', '2019-12-13 15:48:15');
INSERT INTO `sys_user` VALUES ('1002', '陈杜然', '$2a$10$wleNi.I4I6I78dD82h4H1.1n8A.s1Iyn0gDyJW33gHHVLMnzPuRRy', null, '', '', '1', '2019-12-13 15:49:23', '2019-12-13 15:49:23');
INSERT INTO `sys_user` VALUES ('1003', 'gam', '$2a$10$ZJdGogOtZJBs.ejNZnrMiuWOjusp41M71WG7tLdEd02qqYu7yUz3C', null, '188866659959@163.com', '18883179959', '1', '2019-12-19 14:14:13', '2019-12-19 14:14:13');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户与角色对应关系';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '1', '1');
INSERT INTO `sys_user_role` VALUES ('8', '1000', '2');
INSERT INTO `sys_user_role` VALUES ('9', '1001', '3');
INSERT INTO `sys_user_role` VALUES ('10', '1002', '4');
INSERT INTO `sys_user_role` VALUES ('11', '3', '1');
INSERT INTO `sys_user_role` VALUES ('12', '3', '4');
INSERT INTO `sys_user_role` VALUES ('13', '1003', '4');

-- ----------------------------
-- Table structure for task_info
-- ----------------------------
DROP TABLE IF EXISTS `task_info`;
CREATE TABLE `task_info` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `temp_id` int(11) NOT NULL COMMENT '模版 ID',
  `url` varchar(500) DEFAULT NULL COMMENT '爬虫地址',
  `task_uuid` varchar(50) DEFAULT NULL COMMENT '任务唯一ID',
  `err_msg` text COMMENT '错误信息',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `task_uuid_idx` (`task_uuid`,`temp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务信息';

-- ----------------------------
-- Records of task_info
-- ----------------------------

-- ----------------------------
-- Table structure for template
-- ----------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE `template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `page_site` varchar(500) DEFAULT NULL COMMENT '页面起始网址',
  `real_site` text COMMENT '抓取真正网址',
  `title` varchar(500) DEFAULT NULL COMMENT '页面标题，默认为网页标题',
  `filter_by` int(11) DEFAULT '1' COMMENT '去重方式 ：1 通过链接，2通过文字',
  `status` tinyint(4) DEFAULT '1' COMMENT '当前页面监控状态 0停止  1运行中  2出错',
  `interval_time` int(10) DEFAULT '20000' COMMENT '页面爬取间隔，单位为毫秒，默认20秒钟爬取一次',
  `channel` varchar(250) DEFAULT NULL COMMENT '同一个标题可能有多个不同的频道',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `encoding` varchar(20) DEFAULT 'UTF-8',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加监控页面时间',
  `priority` int(11) DEFAULT '50' COMMENT '页面优先级',
  `weforeign` tinyint(1) DEFAULT '0',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '所属部门',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `special` tinyint(1) DEFAULT '0',
  `detail_encoding` varchar(20) DEFAULT 'UTF-8',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` int(11) DEFAULT '0',
  `encrypt_type` varchar(20) DEFAULT NULL COMMENT '解密类型',
  `spider_temp_id` bigint(20) DEFAULT NULL COMMENT '特殊爬虫模版的id',
  `proxy_level` int(11) DEFAULT '0',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT '模版预计过期时间',
  `create_user` varchar(50) DEFAULT NULL COMMENT '操作用户',
  `update_user` varchar(50) DEFAULT NULL COMMENT '操作用户',
  `delete_user` varchar(50) DEFAULT NULL COMMENT '操作用户',
  `delete_time` timestamp NULL DEFAULT NULL COMMENT '删除时间',
  `stop_time` datetime DEFAULT NULL COMMENT '停止时间',
  `stop_reason` varchar(255) DEFAULT NULL COMMENT '停止原因',
  PRIMARY KEY (`id`),
  KEY `page_site_idx` (`page_site`)
) ENGINE=InnoDB AUTO_INCREMENT=809 DEFAULT CHARSET=utf8 COMMENT='爬虫模版';

-- ----------------------------
-- Records of template
-- ----------------------------

-- ----------------------------
-- Table structure for template_rule
-- ----------------------------
DROP TABLE IF EXISTS `template_rule`;
CREATE TABLE `template_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `temp_id` bigint(20) NOT NULL COMMENT '模版id',
  `type` int(11) DEFAULT '1' COMMENT '内容类型，1文本，2json，3jsonp',
  `headers` text COMMENT '头信息',
  `method` varchar(50) DEFAULT NULL COMMENT '方法，post、get',
  `params` text COMMENT '附带参数',
  `rules_json` varchar(500) DEFAULT NULL COMMENT '规则，json',
  `is_detail` tinyint(1) DEFAULT '0' COMMENT '是否为详情页面的规则',
  `spider_temp_id` bigint(20) DEFAULT NULL COMMENT '特殊爬虫模版的id',
  `encrypt_type` varchar(20) DEFAULT NULL COMMENT '解密类型',
  PRIMARY KEY (`id`),
  KEY `temp_id_idx` (`temp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=853 DEFAULT CHARSET=utf8 COMMENT='爬虫模版规则';

-- ----------------------------
-- Records of template_rule
-- ----------------------------

-- ----------------------------
-- Table structure for user_analysis
-- ----------------------------
DROP TABLE IF EXISTS `user_analysis`;
CREATE TABLE `user_analysis` (
  `id` bigint(40) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `event` varchar(250) DEFAULT NULL COMMENT '事件名称',
  `referer` varchar(250) DEFAULT NULL COMMENT '路径名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户行为分析测试表';

-- ----------------------------
-- Records of user_analysis
-- ----------------------------
