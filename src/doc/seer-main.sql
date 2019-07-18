/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : 127.0.0.1:3306
 Source Schema         : seer-main

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 17/07/2019 23:06:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for account_auth
-- ----------------------------
DROP TABLE IF EXISTS `account_auth`;
CREATE TABLE `account_auth`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `owner_weight_threshold` bigint(11) NULL DEFAULT NULL COMMENT '账户权限阈值',
  `owner_account_auths` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '账户权限授权账户',
  `owner_key_auths` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '账户权限授权公钥',
  `owner_address_auths` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '账户权限授权地址',
  `active_weight_threshold` bigint(11) NULL DEFAULT NULL COMMENT '活跃权限阈值',
  `active_key_auths` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '资金权限授权公钥',
  `active_address_auths` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '资金权限授权地址',
  `memo_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注公钥',
  `voting_account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '投票代理账户',
  `num_committee` bigint(11) NULL DEFAULT NULL COMMENT '投票相关',
  `num_authenticator` bigint(11) NULL DEFAULT NULL COMMENT '认证相关',
  `num_supervisor` bigint(11) NULL DEFAULT NULL COMMENT '监管相关',
  `votes` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '投票ID列表',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for account_base
-- ----------------------------
DROP TABLE IF EXISTS `account_base`;
CREATE TABLE `account_base`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `registrar` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册人',
  `referrer` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '推荐人',
  `referrer_percent` bigint(11) NULL DEFAULT NULL COMMENT '推荐人手续费分成百分比',
  `reg_time` bigint(11) NULL DEFAULT NULL COMMENT '账号注册时间，区块时间+8h后的东八区时间戳',
  `recent_play_time` bigint(11) NULL DEFAULT NULL COMMENT '最近投注时间，区块时间+8h后的东八区时间戳',
  `registered` bigint(11) NULL DEFAULT NULL COMMENT '累计注册用户',
  `is_player` bigint(1) NULL DEFAULT NULL COMMENT '投注用户，0 非投注；1 投注。一次修改',
  `is_seerbot` bigint(1) NULL DEFAULT NULL COMMENT '是否机器人账号，0 不是；1 是。一次修改',
  `account_prtp_count` bigint(11) NULL DEFAULT NULL COMMENT '玩家投注次数',
  `account_prtp_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '玩家投注金额',
  `claimed_faucet_profit` decimal(40, 5) NULL DEFAULT NULL COMMENT '已领取水龙头收入',
  `claimed_fees_profit` decimal(40, 5) NULL DEFAULT NULL COMMENT '已领取手续费分成',
  `total_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '手续费总消耗',
  `total_transfer_count_fees` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '用户转账支出',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最新更新时的块高',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户基础信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for account_house_total
-- ----------------------------
DROP TABLE IF EXISTS `account_house_total`;
CREATE TABLE `account_house_total`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `house_advprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '房主高级手续费收入总额',
  `house_botadvprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '房主高级机器人手续费收入总额',
  `house_seerbot_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '房主机器人预测手续费',
  `total_prtp_profit` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主总收入',
  `total_play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主对应房间的预测总额',
  `total_advplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主高级投注总额',
  `total_botadvplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主高级机器人投注总额',
  `total_botplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主机器人预测总额',
  `house_rooms` bigint(11) NULL DEFAULT NULL COMMENT '房主总房间数',
  `prtp_rooms` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '参与过的房间列表',
  `active_rooms` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主有真人参与的房间列表',
  `house_prtp_count` bigint(11) NULL DEFAULT NULL COMMENT '房主房间总参与人数',
  `house_botprtp_count` bigint(11) NULL DEFAULT NULL COMMENT '房主房间总参与机器人数',
  `house_prtp_times` bigint(11) NULL DEFAULT NULL COMMENT '房主房间总参与人次',
  `house_botprtp_times` bigint(11) NULL DEFAULT NULL COMMENT '房主房间机器人总参与人次',
  `house_adv_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主高级总派奖',
  `house_botadv_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主高级机器人和房主总派奖',
  `total_adv_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '房主高级手续费支出总额',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最近更新时的块高',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户房主系列' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for account_house_type
-- ----------------------------
DROP TABLE IF EXISTS `account_house_type`;
CREATE TABLE `account_house_type`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `house_pvdprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '房主PVD手续费收入总额',
  `house_pvpprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '房主PVP手续费收入总额',
  `house_botpvdprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '房主PVD机器人手续费收入总额',
  `house_botpvpprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '房主PVP机器人手续费收入总额',
  `total_pvp_profit` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主PVP总抽成',
  `total_botpvp_profit` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主PVP机器人总抽成',
  `total_pvdplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主PVD投注总额',
  `total_pvpplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主PVP投注总额',
  `total_botpvdplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主PVD机器人投注总额',
  `total_botpvpplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主PVP机器人投注总额',
  `house_pvd_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主PVD总派奖',
  `house_pvp_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主PVP总派奖',
  `total_pvd_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '房主PVD手续费支出总额',
  `house_botpvd_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主PVD机器人和房主总派奖',
  `house_botpvp_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房主PVP机器人和房主总派奖',
  `total_pvp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '房主PVP手续费支出总额',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最近更新时的块高',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '房主PVDPVP系列' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for account_register
-- ----------------------------
DROP TABLE IF EXISTS `account_register`;
CREATE TABLE `account_register`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `registrar` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '注册人',
  `reg_time` bigint(11) NULL DEFAULT NULL COMMENT '注册时间',
  `players` bigint(11) NULL DEFAULT NULL COMMENT '注册人累计投注用户',
  `bot_players` bigint(11) NULL DEFAULT NULL COMMENT '注册人累计机器人投注用户',
  `true_players` bigint(11) NULL DEFAULT NULL COMMENT '注册人注册当天投注用户数',
  `bot_true_players` bigint(11) NULL DEFAULT NULL COMMENT '注册人注册当天机器人投注用户数',
  `deposit_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '注册人充值金额',
  `bot_deposit_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '注册人机器人充值金额',
  `deposit_count` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '注册人充值笔数',
  `bot_deposit_count` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '注册人机器人充值笔数',
  `transfer_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '注册人转账金额',
  `bot_transfer_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '注册人机器人转账金额',
  `transfer_count` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '注册人转账笔数',
  `bot_transfer_count` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '注册人机器人转账笔数',
  `seerbot_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '注册人机器人预测手续费',
  `total_registered_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '用户注册用户支出',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最近更新时的块高',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户注册系列' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for daily_account_details
-- ----------------------------
DROP TABLE IF EXISTS `daily_account_details`;
CREATE TABLE `daily_account_details`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `zero_timestamp` bigint(11) NULL DEFAULT NULL COMMENT '东八区0点时间戳',
  `time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '当天日期，如2019-05-24',
  `issuer` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `daily_prtp_count` bigint(11) NULL DEFAULT NULL COMMENT '投注次数',
  `daily_prtp_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '投注额，五位小数精度。JSONArray',
  `daily_fee` decimal(40, 5) NULL DEFAULT NULL COMMENT '手续费',
  `daily_profit` decimal(40, 5) NULL DEFAULT NULL COMMENT 'dapp收入',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最新块高',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '日期',
  `is_bot` int(1) NULL DEFAULT NULL COMMENT '0 非机器人；1 机器人',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name&today`(`zero_timestamp`, `issuer`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9551 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每日用户表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for daily_data_faucet
-- ----------------------------
DROP TABLE IF EXISTS `daily_data_faucet`;
CREATE TABLE `daily_data_faucet`  (
  `timestamp` bigint(11) NOT NULL COMMENT '每天0点的时间戳',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日期如：2019-06-12',
  `daily_registered` bigint(11) NULL DEFAULT NULL COMMENT '新增注册用户',
  `total_registered` bigint(11) NULL DEFAULT NULL COMMENT '累计注册用户',
  `daily_trueplayers` bigint(11) NULL DEFAULT NULL COMMENT '新增注册且投注用户数',
  `daily_players` bigint(11) NULL DEFAULT NULL COMMENT '新增投注用户',
  `total_players` bigint(11) NULL DEFAULT NULL COMMENT '累计投注用户',
  `total_active_player` bigint(11) NULL DEFAULT NULL COMMENT '活跃投注用户',
  `daily_deposit_count` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '充值笔数',
  `total_transfer_count_fees` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计DAPP新注册用户转账支出',
  `total_deposit_count` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计充值笔数',
  `daily_deposit_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '充值额',
  `total_deposit_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计充值额',
  `daily_transfer_count` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '转账笔数',
  `total_transfer_count` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计转账笔数',
  `daily_transfer_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '转账额',
  `total_transfer_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计转账额',
  `total_registered_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计DAPP注册用户支出',
  `registered_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT 'DAPP注册用户支出',
  `transfer_count_fees` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'DAPP新注册用户转账支出',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最新更新时的区块高度',
  `total_faucet_profit` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计水龙头收入',
  `faucet_profit` decimal(40, 5) NULL DEFAULT NULL COMMENT '水龙头收入',
  PRIMARY KEY (`timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每日总体指标之水龙头账户系列' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for daily_data_houses
-- ----------------------------
DROP TABLE IF EXISTS `daily_data_houses`;
CREATE TABLE `daily_data_houses`  (
  `timestamp` bigint(11) NOT NULL COMMENT '每天0点的时间戳',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日期如：2019-06-12',
  `total_pvp_profit` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计pvp抽成收入',
  `total_subsidy` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计dapp补贴',
  `daily_subsidy` decimal(40, 5) NULL DEFAULT NULL COMMENT 'dapp补贴',
  `daily_new_rooms` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '新增房间数',
  `daily_active_rooms` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '有真人参与的房间数',
  `daily_prtp_rate` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '新增房间参与率',
  `total_rooms` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计房间数',
  `total_prtp_rate` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计房间参与率',
  `total_advplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计高级投注总额',
  `total_advprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计高级房间手续费收入',
  `total_adv_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计高级派奖总额',
  `daily_opening_room` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '可投注总房间数',
  `prtp_times` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '投注人次',
  `total_prtp_times` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计投注人次',
  `total_pvpplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计PVP投注总额',
  `total_play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计投注额',
  `total_pvpprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计PVP房间手续费收入',
  `total_pvp_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计PVP派奖总额',
  `total_pvp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计PVP房间手续费',
  `total_pvdplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计PVD投注总额',
  `total_pvd_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计PVD房间手续费',
  `total_adv_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计高级房间手续费',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最近更新时的区块高度',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  `daily_active_rooms_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '每天真人参与房间列表',
  `total_active_rooms_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计真人参与房间列表',
  `total_active_rooms` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计有真人参与的房间数',
  PRIMARY KEY (`timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每日总体指标之房主账户系列' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for daily_data_total
-- ----------------------------
DROP TABLE IF EXISTS `daily_data_total`;
CREATE TABLE `daily_data_total`  (
  `timestamp` bigint(11) NOT NULL COMMENT '每天0点的时间戳',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日期如：2019-06-12',
  `pvpplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'PVP投注总额',
  `pvpprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT 'PVP房间手续费收入\n\n',
  `pvp_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'PVP派奖总额',
  `pvp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT 'PVP房间手续费',
  `pvdplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'PVD投注总额',
  `pvdprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT 'PVD房间手续费收入',
  `pvd_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'PVD派奖总额',
  `total_roomprtp_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计DAPP所有房间收入',
  `total_account_botprtp_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计机器人总体支出',
  `pvd_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT 'PVD房间手续费',
  `advplay_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '高级投注总额',
  `advprtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '高级房间手续费收入',
  `adv_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '高级派奖总额',
  `adv_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '高级房间手续费',
  `roomprtp_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'DAPP所有房间收入',
  `roomprtp_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'DAPP所有房间支出',
  `account_botprtp_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '机器人总体支出',
  `total_roomprtp_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计DAPP所有房间支出',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最近更新时的块高',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '总体指标每日累积系列' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for daily_house_room
-- ----------------------------
DROP TABLE IF EXISTS `daily_house_room`;
CREATE TABLE `daily_house_room`  (
  `timestamp` bigint(11) NOT NULL COMMENT '每天0点的时间戳',
  `time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日期',
  `daily_new_rooms` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '新增房间数',
  `daily_prtp_rate` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '新增房间参与率',
  `total_rooms` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计房间数',
  `total_prtp_rate` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计房间参与率',
  `daily_opening_room` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '可投注房间数',
  `prtp_times` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '投注人次',
  `total_prtp_times` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计投注人次',
  `play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '投注额',
  `total_play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计投注额',
  `new_rooms_active_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '每天新增房间中且真人参与的房间列表',
  `new_rooms_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '每天新增房间列表',
  `new_active_rooms` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '每天新增房间中且真人参与的房间数',
  `total_active_rooms_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计真人参与房间列表',
  `total_active_rooms` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计有真人参与的房间数',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最近更新时的区块高度',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分平台房间每日指标' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for daily_profit
-- ----------------------------
DROP TABLE IF EXISTS `daily_profit`;
CREATE TABLE `daily_profit`  (
  `timestamp` bigint(11) NOT NULL COMMENT '每天0点的时间戳',
  `time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日期',
  `dapp_profit` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'Dapp收入',
  `total_dapp_profit` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计dapp收入',
  `faucet_profit` decimal(40, 5) NULL DEFAULT NULL COMMENT 'DAPP水龙头收入',
  `total_faucet_profit` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计DAPP水龙头收入',
  `fees_profit` decimal(40, 5) NULL DEFAULT NULL COMMENT '手续费收入',
  `total_fees_profit` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计手续费收入',
  `daily_subsidy` decimal(40, 5) NULL DEFAULT NULL COMMENT 'dapp补贴',
  `total_subsidy` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计dapp补贴',
  `account_botprtp_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '机器人总体支出',
  `total_account_botprtp_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计机器人总体支出',
  `roomprtp_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'DAPP所有房间收入',
  `total_roomprtp_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计DAPP所有房间收入',
  `roomprtp_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'DAPP所有房间支出',
  `total_roomprtp_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计DAPP所有房间支出',
  `registered_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT 'DAPP注册用户支出',
  `total_registered_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计DAPP注册用户支出',
  `transfer_count_fees` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'DAPP新注册用户转账支出',
  `total_transfer_count_fees` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计DAPP新注册用户转账支出',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '块高',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  `collected_fees` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '待领取手续费分成',
  `allowed_withdraw` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '可领取数量',
  PRIMARY KEY (`timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '收益每日指标' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for daily_room_details
-- ----------------------------
DROP TABLE IF EXISTS `daily_room_details`;
CREATE TABLE `daily_room_details`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `zero_timestamp` bigint(11) NULL DEFAULT NULL COMMENT '0点时间戳',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日期',
  `house` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台号',
  `room` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房间号',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '房间描述',
  `daily_player_count` bigint(11) NULL DEFAULT NULL COMMENT '投注人次',
  `daily_shares` decimal(40, 5) NULL DEFAULT NULL COMMENT '投注额度',
  `dapp_profit` decimal(40, 5) NULL DEFAULT NULL COMMENT 'dapp收入',
  `dapp_total_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'dapp补贴',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最新块高',
  `create_time` bigint(11) NULL DEFAULT NULL,
  `update_time` bigint(11) NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL,
  `type` bigint(1) NULL DEFAULT NULL COMMENT '房间类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3604 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每日房间表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for daily_type_room
-- ----------------------------
DROP TABLE IF EXISTS `daily_type_room`;
CREATE TABLE `daily_type_room`  (
  `timestamp` bigint(11) NOT NULL COMMENT '每天0点的时间戳',
  `time` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日期',
  `pvp_profit` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'pvp抽成收入',
  `total_pvp_profit` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计pvp抽成收入',
  `pvp_play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'PVP投注总额',
  `total_pvp_play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计PVP投注总额',
  `pvp_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'PVP派奖总额',
  `total_pvp_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计PVP派奖总额',
  `pvp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT 'PVP房间手续费',
  `total_pvp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计PVP房间手续费',
  `pvp_prtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT 'PVP房间手续费收入',
  `total_pvp_prtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计PVP房间手续费收入',
  `pvd_play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'PVD投注总额',
  `pvd_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'PVD派奖总额',
  `total_pvd_play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计PVD投注总额',
  `total_pvd_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计PVD派奖总额',
  `pvd_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT 'PVD房间手续费',
  `total_pvd_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计PVD房间手续费',
  `pvd_prtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT 'PVD房间手续费收入',
  `total_pvd_prtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计PVD房间手续费收入',
  `adv_play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '高级投注总额',
  `total_adv_play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计高级投注总额',
  `adv_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '高级派奖总额',
  `total_adv_settle` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '累计高级派奖总额',
  `adv_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '高级房间手续费',
  `total_adv_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计高级房间手续费',
  `adv_prtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '高级房间手续费收入',
  `total_adv_prtp_fees` decimal(40, 5) NULL DEFAULT NULL COMMENT '累计高级房间手续费收入',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最近更新时的区块高度',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` bigint(11) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '分类型房间每日指标' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for seer_block
-- ----------------------------
DROP TABLE IF EXISTS `seer_block`;
CREATE TABLE `seer_block`  (
  `id` bigint(20) NOT NULL COMMENT '主键，即块高',
  `previous` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `timestamp` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `east_eight_timestamp` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `witness` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `merkle_root` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `extensions` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `witness_signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `transaction_ids` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `block_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `signing_key` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `txs_count` int(11) NULL DEFAULT NULL,
  `create_time` bigint(11) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '区块数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for seer_block_sync
-- ----------------------------
DROP TABLE IF EXISTS `seer_block_sync`;
CREATE TABLE `seer_block_sync`  (
  `id` bigint(11) NOT NULL,
  `block_sync` bigint(11) NOT NULL,
  `memo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int(1) NOT NULL COMMENT '状态，0 正常；1 暂停',
  `save_block` int(1) NOT NULL COMMENT '是否保存区块信息，0 保存；1 不保存',
  `save_tx` int(1) NOT NULL COMMENT '是否保存交易信息，0 保存；1 不保存',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务、区块扫描配置' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for seer_transaction
-- ----------------------------
DROP TABLE IF EXISTS `seer_transaction`;
CREATE TABLE `seer_transaction`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 自增',
  `block_height` bigint(11) NULL DEFAULT NULL COMMENT '块高',
  `tx_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '交易哈希',
  `ref_block_num` bigint(11) NULL DEFAULT NULL,
  `ref_block_prefix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `expiration` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `operations` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '操作',
  `extensions` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '过期时间',
  `signatures` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `operation_results` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '操作结果',
  `block_time` bigint(11) NULL DEFAULT NULL COMMENT '出块时间，东八区时间戳',
  `create_time` bigint(11) NULL DEFAULT NULL COMMENT '写入数据库的时间',
  `nonce` bigint(11) NULL DEFAULT NULL COMMENT '交易中的操作序号',
  `type` bigint(11) NULL DEFAULT NULL COMMENT '交易类型',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `block`(`tx_id`, `block_height`, `nonce`) USING BTREE,
  INDEX `height`(`block_height`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 263066 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '区块交易数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for total_room_details
-- ----------------------------
DROP TABLE IF EXISTS `total_room_details`;
CREATE TABLE `total_room_details`  (
  `id` bigint(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `room` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房间号',
  `total_bot_play_amount` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'dapp补贴',
  `room_status` int(1) NULL DEFAULT NULL COMMENT '房间状态，0 opening，1 closed',
  `room_start` bigint(11) NULL DEFAULT NULL COMMENT '房间开始时间',
  `room_stop` bigint(11) NULL DEFAULT NULL COMMENT '房间结束时间',
  `last_block` bigint(11) NULL DEFAULT NULL COMMENT '最新块高',
  `create_time` bigint(11) NULL DEFAULT NULL,
  `update_time` bigint(11) NULL DEFAULT NULL,
  `create_date` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1493 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '房间累积表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_config
-- ----------------------------
DROP TABLE IF EXISTS `user_config`;
CREATE TABLE `user_config`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `faucet_accounts` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '水龙头账户',
  `gateways` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '网关及承兑',
  `assets` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '统计资产ID',
  `houses` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '房主账户',
  `seer_bots` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '机器人账号列表',
  `default_asset` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认显示的资产',
  `default_owner` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认显示的房主账户',
  `white_accounts` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '排除账户，此列表中的账户不进行统计',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户配置表' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
