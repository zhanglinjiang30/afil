alter table t_user add otc_name varchar(20) default '' comment 'otc名称';

alter table t_sms_config add email_region_id varchar(20) default '' comment '区域';
alter table t_sms_config add email_access_key_id varchar(50) default '' comment 'key';
alter table t_sms_config add email_secret varchar(50) default '' comment '私钥';
alter table t_sms_config add email_account_name varchar(50) default '' comment '账户';
alter table t_sms_config add email_tag varchar(20) default '' comment '标签';

---------------助记词和创建此助记词的设备号记录--------------
CREATE TABLE `t_passphrase_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `passphrase` varchar(200) DEFAULT '' COMMENT '助记词',
  `device_id` varchar(100) DEFAULT '' COMMENT '设备号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

---------------地址和创建此地址的设备号记录--------------
CREATE TABLE `t_address_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `passphrase_id` bigint(20) DEFAULT 0 COMMENT '私钥表id',
  `index` int(10) DEFAULT 0 COMMENT '地址序号',
  `address` varchar(200) DEFAULT '' COMMENT '地址',
  `private_key` varchar(100) DEFAULT '' COMMENT '私钥',
  `device_id` varchar(100) DEFAULT '' COMMENT '首次创建的设备号',
  `active` tinyint(2) DEFAULT 0 COMMENT '地址是否被激活0=未激活，1=激活',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

------------设备在线持有的地址记录-------
CREATE TABLE `t_address_holding_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `device_id` varchar(20) DEFAULT '' COMMENT '设备号',
  `address_id` bigint(20) DEFAULT 0 COMMENT '地址表id',
  `name` varchar(100) DEFAULT '' COMMENT '钱包名称',
  `main` tinyint(2) DEFAULT 0 COMMENT '是否为主地址0=否，1=是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

---------区块表-----
CREATE TABLE `t_block` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `height` bigint(20) DEFAULT 0 COMMENT '高度',
  `transaction_count` int(10) DEFAULT 0 COMMENT '交易数量',
  `reward` decimal(20, 8) DEFAULT 0.00000000 COMMENT '区块奖励',
  `hash` varchar(200) DEFAULT '' COMMENT '区块哈希',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

---------区块交易表-----
CREATE TABLE `t_block_transaction` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `tx_hash` varchar(200) DEFAULT '' COMMENT '交易哈希',
  `height` bigint(20) DEFAULT 0 COMMENT '交易高度',
  `fee` decimal(20, 8) DEFAULT 0.00000000 COMMENT '矿工费(SUC)',
  `from_address` varchar(200) DEFAULT '' COMMENT '转出地址',
  `to_address` varchar(200) DEFAULT '' COMMENT '转入地址',
  `currency_id` bigint(20) DEFAULT '' COMMENT '交易币种',
  `amount` decimal(20, 8) DEFAULT 0.00000000 COMMENT '交易数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--------待确认交易--------
CREATE TABLE `t_block_transaction_unconfirm` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `fee` decimal(20, 8) DEFAULT 0.00000000 COMMENT '矿工费(SUC)',
  `from_address` varchar(200) DEFAULT '' COMMENT '转出地址',
  `to_address` varchar(200) DEFAULT '' COMMENT '转入地址',
  `currency_id` bigint(20) DEFAULT '' COMMENT '交易币种',
  `amount` decimal(20, 8) DEFAULT 0.00000000 COMMENT '交易数量',
  `confirm` tinyint(0) DEFAULT 0 COMMENT '0=未打包，1=已打包',
  `ex_id` bigint(0) DEFAULT 0 COMMENT '外部id',
  `tx_hash` varchar(100) DEFAULT '' COMMENT '交易哈希',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

--------转账记录--------
CREATE TABLE `t_transfer_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `from_address` varchar(200) DEFAULT '' COMMENT '转出地址',
  `to_address` varchar(200) DEFAULT '' COMMENT '转入地址',
  `currency_id` bigint(20) DEFAULT 0 COMMENT '交易币种',
  `amount` decimal(20, 8) DEFAULT 0.00000000 COMMENT '交易数量',
  `fee_currency_id` bigint(20) DEFAULT 0 COMMENT '手续费币种',
  `fee` decimal(20, 8) DEFAULT 0.00000000 COMMENT '手续费数量',
  `status` tinyint(2) DEFAULT 0 COMMENT '状态 0=未确认，1=已确认',
  `tx_hash` varchar(200) DEFAULT '' COMMENT '交易哈希',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


--------空投记录--------
CREATE TABLE `t_airdrop_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `address_id` bigint(20) DEFAULT 0 COMMENT '地址id',
  `suc_amount` decimal(20, 8) DEFAULT 0.00000000 COMMENT '空投数量SUC',
  `usdt_amount` decimal(20, 8) DEFAULT 0.00000000 COMMENT '消耗的USDT数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


--------激活记录--------
CREATE TABLE `t_active_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `from_p_id` bigint(20) DEFAULT 0 COMMENT '激活方助记词id',
  `from_address_id` bigint(20) DEFAULT 0 COMMENT '激活方地址id',
  `name` varchar(50) DEFAULT '' COMMENT '被激活方标记名称',
  `to_address_id` bigint(20) DEFAULT 0 COMMENT '被激活地址id',
  `active_device` varchar(100) DEFAULT '' COMMENT '被激活时地址所处设备',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


-------币种最低持币和最佳持币-------
CREATE TABLE `t_currency_holding` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `currency_id` bigint(20) DEFAULT 0 COMMENT '币种id',
  `min_holding` decimal(20, 8) DEFAULT 0.00000000 COMMENT '最低持币数量',
  `nice_holding` decimal(20, 8) DEFAULT 0.00000000 COMMENT '最佳持币数量',
  `nice_profit` decimal(20, 8) DEFAULT 0.000000000 COMMENT '最佳持币的收益',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

------币种-矿池-用户单条记录统计---------
CREATE TABLE `t_pool_statistic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `currency_id` bigint(20) DEFAULT 0 COMMENT '币种id',
  `user_id` bigint(20) DEFAULT 0 COMMENT '用户id',
  `daily_profit` decimal(20, 8) DEFAULT 0.00000000 COMMENT '日收益',
  `total_profit` decimal(20, 8) DEFAULT 0.00000000 COMMENT '累计收益',
  `compute_power` decimal(20, 8) DEFAULT 0.000000000 COMMENT '自己的算力',
  `invite_compute_power` decimal(20, 8) DEFAULT 0.000000000 COMMENT '推广的算力',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-----------矿池收益记录------------
CREATE TABLE `t_pool_profit_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `currency_id` bigint(20) DEFAULT 0 COMMENT '币种id',
  `user_id` bigint(20) DEFAULT 0 COMMENT '用户id',
  `statistic_date` date DEFAULT NULL COMMENT '统计日期',
  `static_profit` decimal(20, 8) DEFAULT 0.00000000 COMMENT '挖矿收益【静态收益】',
  `dynamic_profit` decimal(20, 8) DEFAULT 0.000000000 COMMENT '推广收益【动态收益】',
  `profit_rate` decimal(20, 8) DEFAULT 0.000000000 COMMENT '持币收益率',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-----------地址算力记录------------
CREATE TABLE `t_compute_power_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT '' COMMENT '备注',
  `version` int(11) DEFAULT '0' COMMENT '版本号',
  `currency_id` bigint(20) DEFAULT 0 COMMENT '币种id',
  `user_id` bigint(20) DEFAULT 0 COMMENT '用户id',
  `p_user_id` bigint(20) DEFAULT 0 COMMENT '隶属于哪个地址id',
  `power` decimal(20, 8) DEFAULT 0.00000000 COMMENT '连带自己的算力',
  `large_zone` tinyint(2) DEFAULT 0 COMMENT '是否大区0=否 1=是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

alter table t_currency_config add `mine` decimal(20, 8) default 0.00000000 comment '转账矿工费';