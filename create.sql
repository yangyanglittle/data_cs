CREATE TABLE `table_db_info` (
  `auto_id` int(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '唯一键ID（配置ID）',
  `config_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '外键config表的id',
  `db_type` tinyint(4) DEFAULT NULL COMMENT '数据库类型 1源数据库 2目标数据库',
  `db_host` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '主机IP端口',
  `db_username` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '主机用户名',
  `db_password` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '主机密码',
  `data_base` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '数据库名',
  `mysql_type` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '数据库类型',
  `char_set` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '字符集 utf8',
  PRIMARY KEY (`auto_id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='数据库信息';

CREATE TABLE `table_config` (
  `auto_id` int(64) AUTO_INCREMENT COLLATE utf8_bin NOT NULL COMMENT '自增ID',
  `id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '唯一键ID（配置ID）',
  `name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '配置名称',
  `remark` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `update_by` varchar(64) NOT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL COMMENT '修改时间',
  `create_by` varchar(64) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `is_delete` varchar(4) COLLATE utf8_bin DEFAULT 0 COMMENT '是否废弃 0否 1是',
  `config_type` varchar(4) COLLATE utf8_bin DEFAULT 1 COMMENT '配置类型 1结构同步 2数据同步',
  PRIMARY KEY (`auto_id`), UNIQUE (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='表同步配置';

CREATE TABLE `data_retry` (
  `auto_id` int(64) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '唯一键ID（配置ID）',
  `exception_type` varchar(64) NOT NULL COMMENT '异常类型',
  `exception_message` varchar(512) NOT NULL COMMENT '异常描述信息',
  `retry_param` text NOT NULL COMMENT '重试需要参数',
  `exception_service_id` varchar(64) NOT NULL DEFAULT '' COMMENT '异常业务的Id',
  `retry_num` tinyint(4) NOT NULL COMMENT '重试处理次数，初始默认为0',
  `max_retry_num` tinyint(4) NOT NULL DEFAULT '6' COMMENT '最大重试次数，初始默认为6',
  `deal` tinyint(1) NOT NULL COMMENT '是否已处理成功: 是-1 否-0, 初始默认为0',
  `deal_ok_time` datetime DEFAULT NULL COMMENT '处理成功时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `exception_reason` text NOT NULL COMMENT '失败原因',
  PRIMARY KEY (`auto_id`),
  UNIQUE KEY `id` (`id`),
  KEY `idx_exception_service_id` (`exception_service_id`) USING BTREE COMMENT 'exception_service_id'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC COMMENT='数据同步异常重试表';