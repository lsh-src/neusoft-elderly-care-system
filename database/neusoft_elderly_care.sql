/*
 Navicat Premium Dump SQL

 Source Server         : MySql
 Source Server Type    : MySQL
 Source Server Version : 80011 (8.0.11)
 Source Host           : localhost:3306
 Source Schema         : neusoft_elderly_care

 Target Server Type    : MySQL
 Target Server Version : 80011 (8.0.11)
 File Encoding         : 65001

 Date: 08/07/2026 09:49:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bed
-- ----------------------------
DROP TABLE IF EXISTS `bed`;
CREATE TABLE `bed`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房间号',
  `bed_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '床位号',
  `status` enum('空闲','已入住','维修中') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '空闲' COMMENT '状态',
  `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '客户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_room_bed`(`room_no` ASC, `bed_no` ASC, `deleted` ASC) USING BTREE,
  INDEX `fk_bed_customer`(`customer_id` ASC) USING BTREE,
  CONSTRAINT `fk_bed_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '床位管理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of bed
-- ----------------------------
INSERT INTO `bed` VALUES (1, '101', '101-1', '已入住', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (2, '101', '101-2', '已入住', 2, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (3, '102', '102-1', '已入住', 3, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (4, '102', '102-2', '已入住', 4, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (5, '103', '103-1', '已入住', 5, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (6, '103', '103-2', '已入住', 6, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (7, '201', '201-1', '已入住', 7, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (8, '201', '201-2', '已入住', 8, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (9, '202', '202-1', '已入住', 9, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (10, '202', '202-2', '已入住', 10, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (11, '203', '203-1', '已入住', 11, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (12, '203', '203-2', '已入住', 12, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (13, '301', '301-1', '已入住', 13, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (14, '301', '301-2', '已入住', 14, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (15, '302', '302-1', '已入住', 15, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (16, '302', '302-2', '已入住', 16, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (17, '303', '303-1', '已入住', 17, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (18, '303', '303-2', '已入住', 18, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (19, '401', '401-1', '已入住', 20, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (20, '401', '401-2', '维修中', NULL, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `bed` VALUES (30, '1', '1', '已入住', 20, '2026-06-05 15:11:49', '2026-06-08 10:52:53', 1);
INSERT INTO `bed` VALUES (31, '501', '1', '已入住', 3, '2026-06-08 15:37:57', '2026-06-08 15:37:57', 0);
INSERT INTO `bed` VALUES (32, '501', '2', '空闲', NULL, '2026-06-08 15:38:06', '2026-07-07 20:20:06', 0);
INSERT INTO `bed` VALUES (34, '001', '001', '已入住', 33, '2026-07-07 21:26:13', '2026-07-07 21:38:07', 0);

-- ----------------------------
-- Table structure for care_service
-- ----------------------------
DROP TABLE IF EXISTS `care_service`;
CREATE TABLE `care_service`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `service_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务名称',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务内容',
  `period` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '周期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_service_name`(`service_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务关注表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of care_service
-- ----------------------------
INSERT INTO `care_service` VALUES (1, '每日送餐服务', 300.00, '三餐营养配送', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (2, '专业助浴服务', 200.00, '每周2次安全助浴', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (3, '康复训练服务', 500.00, '每日1次肢体康复', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (4, '慢病监测服务', 400.00, '每日血压血糖监测', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (5, '心理疏导服务', 350.00, '一对一心理疏导', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (6, '理发修剪服务', 100.00, '每月1次专业理发', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (7, '衣物清洗服务', 150.00, '每日衣物清洗晾晒', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (8, '生活用品代购', 200.00, '按需代买生活用品', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (9, '月度体检服务', 800.00, '每月基础体检', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (10, '静脉输液护理', 600.00, '专业护士输液操作', '次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (11, '气道吸痰护理', 400.00, '重症老人吸痰护理', '次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (12, '留置导尿护理', 300.00, '每日导尿护理', '天', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (13, '鼻饲喂食护理', 350.00, '营养鼻饲喂食', '天', '2026-04-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (14, '压疮预防护理', 450.00, '定时翻身防压疮', '天', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (15, '口腔清洁护理', 150.00, '每日口腔清洁', '天', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (16, '营养配餐服务', 500.00, '个性化定制膳食', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (17, '24小时专人陪护', 1000.00, '全天候贴身看护', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (18, '外出就医陪护', 300.00, '陪同就诊检查', '次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (19, '中医理疗服务', 400.00, '按摩针灸理疗', '月', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `care_service` VALUES (20, '健康档案管理', 200.00, '建立电子健康档案', '月', '2026-06-04 14:50:39', '2026-07-07 22:58:54', 0);
INSERT INTO `care_service` VALUES (24, '1', 10.00, '1', '1', '2026-06-17 14:27:37', '2026-06-17 14:27:40', 1);

-- ----------------------------
-- Table structure for check_in
-- ----------------------------
DROP TABLE IF EXISTS `check_in`;
CREATE TABLE `check_in`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `register_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登记编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `bed_id` bigint(20) NOT NULL COMMENT '床位ID',
  `check_in_date` date NOT NULL COMMENT '入住日期',
  `contract_months` int(11) NULL DEFAULT NULL COMMENT '合同月数',
  `deposit` decimal(10, 2) NULL DEFAULT NULL COMMENT '押金',
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经办人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_register_no`(`register_no` ASC, `deleted` ASC) USING BTREE,
  INDEX `fk_checkin_customer`(`customer_id` ASC) USING BTREE,
  INDEX `fk_checkin_bed`(`bed_id` ASC) USING BTREE,
  CONSTRAINT `fk_checkin_bed` FOREIGN KEY (`bed_id`) REFERENCES `bed` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_checkin_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '入住登记表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of check_in
-- ----------------------------
INSERT INTO `check_in` VALUES (1, 'IN2025001', 1, 1, '2025-01-01', 12, 6000.00, '张管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (2, 'IN2025002', 2, 2, '2025-01-05', 6, 3000.00, '张管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (3, 'IN2025003', 3, 3, '2025-01-10', 12, 6000.00, '王管家', '2026-06-04 15:00:12', '2026-06-08 15:38:46', 1);
INSERT INTO `check_in` VALUES (4, 'IN2025004', 4, 4, '2025-01-15', 24, 12000.00, '王管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (5, 'IN2025005', 5, 5, '2025-01-20', 6, 3000.00, '刘管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (6, 'IN2025006', 6, 6, '2025-02-01', 12, 6000.00, '刘管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (7, 'IN2025007', 7, 7, '2025-02-05', 12, 6000.00, '陈管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (8, 'IN2025008', 8, 8, '2025-02-10', 6, 3000.00, '陈管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (9, 'IN2025009', 9, 9, '2025-02-15', 12, 6000.00, '赵管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (10, 'IN2025010', 10, 10, '2025-02-20', 6, 3000.00, '赵管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (11, 'IN2025011', 11, 11, '2025-03-01', 12, 6000.00, '黄管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (12, 'IN2025012', 12, 12, '2025-03-05', 24, 12000.00, '黄管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (13, 'IN2025013', 13, 13, '2025-03-10', 12, 6000.00, '张管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (14, 'IN2025014', 14, 14, '2025-03-15', 6, 3000.00, '王管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (15, 'IN2025015', 15, 15, '2025-03-20', 12, 6000.00, '刘管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (16, 'IN2025016', 16, 16, '2025-03-25', 6, 3000.00, '陈管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (17, 'IN2025017', 17, 17, '2025-04-01', 12, 6000.00, '赵管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (18, 'IN2025018', 18, 18, '2025-04-05', 12, 6000.00, '黄管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (19, 'IN2025019', 19, 19, '2025-04-10', 3, 1500.00, '张管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (20, 'IN2025020', 20, 20, '2025-04-15', 12, 6000.00, '王管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_in` VALUES (30, 'CI20260605-001', 20, 30, '2026-06-15', 10, 10.00, '傅护士', '2026-06-05 15:12:02', '2026-06-05 15:12:04', 1);
INSERT INTO `check_in` VALUES (31, 'CI20260608-001', 3, 31, '2026-06-10', 10, 10.00, '健康管家', '2026-06-08 16:01:36', '2026-07-07 20:20:13', 0);
INSERT INTO `check_in` VALUES (32, 'CI20260707-001', 33, 34, '2026-07-07', 6, 12000.00, '周建宏', '2026-07-07 21:26:45', '2026-07-07 21:39:17', 1);
INSERT INTO `check_in` VALUES (34, 'CI20260707-002', 33, 34, '2026-07-07', 6, 12000.00, '周建宏', '2026-07-07 21:39:37', '2026-07-07 21:39:37', 0);

-- ----------------------------
-- Table structure for check_out
-- ----------------------------
DROP TABLE IF EXISTS `check_out`;
CREATE TABLE `check_out`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `checkout_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '退住编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `checkout_date` date NOT NULL COMMENT '退住日期',
  `reason` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原因',
  `operator` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经办人',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_checkout_no`(`checkout_no` ASC, `deleted` ASC) USING BTREE,
  INDEX `fk_checkout_customer`(`customer_id` ASC) USING BTREE,
  CONSTRAINT `fk_checkout_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '退住登记表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of check_out
-- ----------------------------
INSERT INTO `check_out` VALUES (1, 'CO2025001', 19, '2025-07-10', '家属接回家专人照料', '张管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (2, 'CO2025002', 18, '2025-08-05', '身体康复回家养老', '黄管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (3, 'CO2025003', 17, '2025-09-01', '转专科医院治疗', '赵管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (4, 'CO2025004', 16, '2025-09-15', '养老合同到期', '陈管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (5, 'CO2025005', 15, '2025-10-01', '随子女生活退住', '刘管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (6, 'CO2025006', 14, '2025-10-10', '身体痊愈回家', '王管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (7, 'CO2025007', 13, '2025-11-01', '家属返乡一同生活', '张管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (8, 'CO2025008', 12, '2025-11-15', '透析治疗结束', '黄管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (9, 'CO2025009', 11, '2025-12-01', '痛风症状康复', '黄管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (10, 'CO2025010', 10, '2025-12-10', '眼部治疗完成', '赵管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (11, 'CO2025011', 9, '2026-01-01', '血脂指标正常', '赵管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (12, 'CO2025012', 8, '2026-01-15', '骨质疏松好转', '陈管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (13, 'CO2025013', 7, '2026-02-01', '肺部病情稳定', '陈管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (14, 'CO2025014', 6, '2026-02-10', '帕金森护理结束', '刘管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (15, 'CO2025015', 5, '2026-03-01', '身体硬朗自主生活', '刘管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (16, 'CO2025016', 4, '2026-03-10', '护理方案调整', '王管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (17, 'CO2025017', 3, '2026-04-01', '脑梗康复完成', '王管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (18, 'CO2025018', 2, '2026-04-15', '糖尿病控制稳定', '张管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (19, 'CO2025019', 1, '2026-05-01', '高血压病情稳定', '张管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (20, 'CO2025020', 20, '2026-05-10', '合同到期续费', '王管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (21, 'CO2025021', 11, '2026-06-01', '短期照料结束', '黄管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (22, 'CO2025022', 12, '2026-06-10', '转院继续治疗', '黄管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (23, 'CO2025023', 13, '2026-07-01', '回老家养老', '张管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (24, 'CO2025024', 14, '2026-07-15', '身体康复无需照料', '王管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (25, 'CO2025025', 15, '2026-08-01', '自愿申请退住', '刘管家', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `check_out` VALUES (31, 'CO20260605-001', 19, '2026-06-20', '1', '袁护士', '2026-06-05 15:12:11', '2026-06-05 15:12:16', 1);
INSERT INTO `check_out` VALUES (32, 'CO20260608-001', 19, '2026-06-11', '1', '普通客户', '2026-06-08 10:58:04', '2026-06-08 10:58:06', 1);
INSERT INTO `check_out` VALUES (33, 'CO20260608-002', 19, '2026-06-09', '1', '普通客户', '2026-06-08 10:58:14', '2026-06-08 10:58:17', 1);
INSERT INTO `check_out` VALUES (34, 'CO20260608-003', 19, '2026-05-31', '1', '劳动法', '2026-06-08 10:58:26', '2026-06-08 10:58:28', 1);
INSERT INTO `check_out` VALUES (35, 'CO20260707-001', 33, '2026-09-07', '康复了', '周建宏', '2026-07-07 21:40:18', '2026-07-07 21:45:53', 0);
INSERT INTO `check_out` VALUES (36, 'CO20260707-002', 33, '2026-07-07', '1221', '周六', '2026-07-07 21:46:01', '2026-07-07 21:46:01', 0);

-- ----------------------------
-- Table structure for customer
-- ----------------------------
DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户编号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '客户姓名',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `id_card` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `emergency_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `check_in_date` date NULL DEFAULT NULL COMMENT '入住日期',
  `health_status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '健康状况',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `checked_in` tinyint(4) NULL DEFAULT 0 COMMENT '是否入住 0-未入住 1-已入住',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_id_card`(`id_card` ASC) USING BTREE,
  UNIQUE INDEX `uk_customer_no`(`customer_no` ASC, `deleted` ASC) USING BTREE,
  INDEX `idx_customer_name`(`name` ASC) USING BTREE,
  INDEX `idx_customer_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客户管理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of customer
-- ----------------------------
INSERT INTO `customer` VALUES (1, 'CUST001', '张福顺', '男', 78, '110105194601121234', '13800010001', '张子豪', '2025-01-01', '高血压', '病情稳定', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (2, 'CUST002', '李桂兰', '女', 82, '110105194202221235', '13800010002', '李丽', '2025-01-05', '糖尿病', '下肢行动不便', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (3, 'CUST003', '王建国', '男', 75, '110105194903131236', '13800010003', '王强', '2025-01-10', '脑梗', '术后康复中', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (4, 'CUST004', '赵秀英', '女', 80, '110105194404241237', '13800010004', '赵敏', '2025-01-15', '老年痴呆', '24小时看护', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (5, 'CUST005', '孙永福', '男', 72, '110105195205151238', '13800010005', '孙浩', '2025-01-20', '身体健康', '完全自理', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (6, 'CUST006', '周玉珍', '女', 85, '110105193906261239', '13800010006', '周倩', '2025-02-01', '帕金森', '特级护理', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (7, 'CUST007', '吴明远', '男', 76, '110105194707171240', '13800010007', '吴杰', '2025-02-05', '慢阻肺', '需持续吸氧', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (8, 'CUST008', '郑淑琴', '女', 79, '110105194508281241', '13800010008', '郑燕', '2025-02-10', '骨质疏松', '防跌倒护理', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (9, 'CUST009', '马长海', '男', 73, '110105195009191242', '13800010009', '马涛', '2025-02-15', '高血脂', '低盐饮食', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (10, 'CUST010', '朱秀莲', '女', 81, '110105194310301243', '13800010010', '朱琳', '2025-02-20', '白内障', '视力障碍', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (11, 'CUST011', '林德才', '男', 77, '110105194611111244', '13800010011', '林宇', '2025-03-01', '痛风', '定期理疗', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (12, 'CUST012', '高凤兰', '女', 83, '110105194012221245', '13800010012', '高静', '2025-03-05', '肾衰竭', '每周透析', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (13, 'CUST013', '罗振江', '男', 74, '110105194901131246', '13800010013', '罗凯', '2025-03-10', '慢性胃炎', '易消化饮食', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (14, 'CUST014', '韩桂英', '女', 84, '110105193802241247', '13800010014', '韩雪', '2025-03-15', '冠心病', '常备急救药', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (15, 'CUST015', '谢洪斌', '男', 71, '110105195303151248', '13800010015', '谢明', '2025-03-20', '身体健康', '自主活动', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (16, 'CUST016', '宋桂兰', '女', 78, '110105194604261249', '13800010016', '宋佳', '2025-03-25', '类风湿', '关节活动受限', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (17, 'CUST017', '唐守义', '男', 80, '110105194405171250', '13800010017', '唐鑫', '2025-04-01', '脑梗恢复期', '康复训练', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (18, 'CUST018', '曹玉梅', '女', 76, '110105194806281251', '13800010018', '曹颖', '2025-04-05', '高血压', '血压波动大', 1, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (19, 'CUST019', '曾庆山', '男', 82, '110105194107191252', '13800010019', '曾辉', '2026-06-11', '2型糖尿病', '胰岛素治疗', 0, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `customer` VALUES (20, 'CUST020', '袁秀琴', '女', 75, '110105194908301253', '13800010020', '袁梦', '2026-06-07', '身体健康', '生活自理', 1, '2026-06-04 14:50:39', '2026-07-07 21:24:24', 0);
INSERT INTO `customer` VALUES (28, '1', '1', '男', 10, NULL, '1', '1', '2026-06-23', '1', '1', 0, '2026-06-05 14:50:38', '2026-06-05 14:50:43', 1);
INSERT INTO `customer` VALUES (30, 'C20260605-001', '1', '男', 10, NULL, '1', '1', '2026-06-08', '1', '1', 0, '2026-06-05 15:11:22', '2026-06-05 15:11:24', 1);
INSERT INTO `customer` VALUES (31, 'C20260605-002', '1', '男', 10, NULL, '1', '1', '2026-06-22', '1', '1', 0, '2026-06-05 15:11:32', '2026-06-05 15:11:44', 1);
INSERT INTO `customer` VALUES (32, 'C20260707-001', '122', '男', 12, NULL, '21', '12', '2026-07-27', '1221', '12', 0, '2026-07-07 20:19:52', '2026-07-07 20:19:56', 1);
INSERT INTO `customer` VALUES (33, 'C20260707-002', '李小二', '男', 78, NULL, '132139900223', '123333344432', '2026-07-01', '健康', '有心脏病隐患', 0, '2026-07-07 21:25:37', '2026-07-07 21:39:00', 0);

-- ----------------------------
-- Table structure for meal
-- ----------------------------
DROP TABLE IF EXISTS `meal`;
CREATE TABLE `meal`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `meal_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '餐食编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `breakfast` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '早餐',
  `lunch` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '午餐',
  `dinner` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '晚餐',
  `special_need` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '特殊需求',
  `meal_date` date NOT NULL COMMENT '日期',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  `meal_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '膳食图片地址',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_meal_no`(`meal_no` ASC, `deleted` ASC) USING BTREE,
  INDEX `fk_meal_customer`(`customer_id` ASC) USING BTREE,
  CONSTRAINT `fk_meal_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 83 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '膳食管理表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of meal
-- ----------------------------
INSERT INTO `meal` VALUES (1, 'MEAL2025001', 1, '小米粥+水煮蛋', '杂粮饭+清蒸鱼+青菜', '南瓜粥+馒头', '低盐高血压餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (2, 'MEAL2025002', 2, '燕麦粥+包子', '荞麦饭+瘦肉+菠菜', '山药粥', '低糖糖尿病餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (3, 'MEAL2025003', 3, '豆浆+花卷', '软米饭+炖鸡+白菜', '小米粥', '脑梗术后软食', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (4, 'MEAL2025004', 4, '牛奶+面包', '软烂面条+鸡蛋羹', '米糊', '痴呆老人流质餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (5, 'MEAL2025005', 5, '玉米粥+茶叶蛋', '米饭+炒时蔬+豆腐', '紫薯粥', '常规老年健康餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (6, 'MEAL2025006', 6, '杂粮粥+包子', '米饭+清蒸蛋+西兰花', '百合粥', '帕金森高蛋白餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (7, 'MEAL2025007', 7, '粥品+蒸蛋', '软饭+鱼汤+青菜', '萝卜粥', '肺病清淡饮食', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (8, 'MEAL2025008', 8, '小米粥+馒头', '米饭+炖排骨+青菜', '南瓜粥', '骨质疏松高钙餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (9, 'MEAL2025009', 9, '燕麦粥+鸡蛋', '杂粮饭+凉拌菜+瘦肉', '绿豆粥', '高血脂低脂餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (10, 'MEAL2025010', 10, '粥+面包', '软饭+蒸蛋+青菜', '山药粥', '易消化护眼餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (11, 'MEAL2025011', 11, '红豆粥+包子', '米饭+炖肉+菠菜', '小米粥', '痛风低嘌呤餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (12, 'MEAL2025012', 12, '豆浆+花卷', '软饭+鸡蛋+青菜', '南瓜粥', '肾病优质蛋白餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (13, 'MEAL2025013', 13, '玉米粥+鸡蛋', '米饭+炒青菜+豆腐', '紫薯粥', '胃炎温和饮食', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (14, 'MEAL2025014', 14, '牛奶+馒头', '软饭+清蒸鱼+白菜', '小米粥', '心脏病低盐餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (15, 'MEAL2025015', 15, '杂粮粥+包子', '米饭+时蔬+瘦肉', '百合粥', '老年常规餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (16, 'MEAL2025016', 16, '小米粥+蒸蛋', '软饭+炖鸡+西兰花', '萝卜粥', '关节炎养护餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (17, 'MEAL2025017', 17, '燕麦粥+花卷', '杂粮饭+青菜+鸡蛋', '山药粥', '康复期营养餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (18, 'MEAL2025018', 18, '粥+鸡蛋', '软饭+清蒸鱼+菠菜', '南瓜粥', '高血压控压餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (19, 'MEAL2025019', 19, '豆浆+包子', '米饭+豆腐+青菜', '小米粥', '糖尿病低糖餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (20, 'MEAL2025020', 20, '玉米粥+面包', '米饭+炒时蔬+瘦肉', '紫薯粥', '通用老年健康餐', '2025-05-01', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (21, 'MEAL2025021', 1, '小米粥+鸡蛋', '软饭+青菜+鱼', '山药粥', '高血压低盐餐', '2025-05-02', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (22, 'MEAL2025022', 2, '燕麦粥+馒头', '杂粮饭+瘦肉+菠菜', '南瓜粥', '糖尿病低糖餐', '2025-05-02', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (23, 'MEAL2025023', 3, '豆浆+花卷', '软烂面条+蛋羹', '米糊', '术后软食餐', '2025-05-02', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (24, 'MEAL2025024', 4, '牛奶+面包', '软饭+蒸蛋+白菜', '小米粥', '痴呆流质餐', '2025-05-02', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (25, 'MEAL2025025', 5, '红豆粥+鸡蛋', '米饭+时蔬+豆腐', '紫薯粥', '常规健康餐', '2025-05-02', '2026-06-04 15:00:13', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (26, 'MEAL2026052001', 1, '小米粥+水煮蛋+小咸菜', '软米饭+清蒸鲈鱼+油麦菜', '南瓜粥+白面馒头', '高血压低盐餐', '2026-05-20', '2026-06-04 17:52:59', '2026-06-08 11:05:56', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1810d8c2-252d-4ce5-afa8-be1ed0619c58.png');
INSERT INTO `meal` VALUES (27, 'MEAL2026052002', 2, '无糖燕麦粥+菜包', '杂粮饭+瘦猪肉+娃娃菜', '山药小米粥', '糖尿病低糖餐', '2026-05-20', '2026-06-04 17:52:59', '2026-06-08 11:06:04', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/ff0298d6-c940-4b08-a906-91defad0ad7a.png');
INSERT INTO `meal` VALUES (28, 'MEAL2026052003', 3, '豆浆+小花卷', '软烂面片+鸡蛋羹', '米糊', '术后流食餐', '2026-05-20', '2026-06-04 17:52:59', '2026-07-05 18:35:31', 1, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/5f1615c1-a627-4337-9e07-9765d4fec0af.png');
INSERT INTO `meal` VALUES (29, 'MEAL2026052101', 4, '牛奶+蒸馒头', '软饭+豆腐+生菜', '紫薯粥', '常规老年餐', '2026-05-21', '2026-06-04 17:52:59', '2026-06-08 11:06:25', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/db392b59-e295-4d45-9bcc-6db3f41973cb.png');
INSERT INTO `meal` VALUES (30, 'MEAL2026052102', 5, '红豆粥+鸡蛋', '米饭+冬瓜丸子+青菜', '百合粥', '痛风低嘌呤餐', '2026-05-21', '2026-06-04 17:52:59', '2026-06-08 11:06:46', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/0fc7b2a7-89f2-45c3-9de2-803941ed2a61.png');
INSERT INTO `meal` VALUES (31, 'MEAL2026052103', 6, '玉米粥+豆沙包', '软饭+炖鸡胸+西兰花', '萝卜粥', '养胃易消化餐', '2026-05-21', '2026-06-04 17:52:59', '2026-06-08 11:06:52', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/aa12f056-61e1-422c-abda-23ce149108bd.png');
INSERT INTO `meal` VALUES (32, 'MEAL2026052201', 7, '杂粮粥+鸡蛋', '软烂米饭+清蒸鳕鱼+菠菜', '小米粥', '高蛋白帕金森餐', '2026-05-22', '2026-06-04 17:52:59', '2026-07-05 18:35:34', 1, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/deefd1a1-339a-49ef-a37b-3faa75172d5c.png');
INSERT INTO `meal` VALUES (33, 'MEAL2026052202', 8, '粥品+蒸蛋', '软饭+鱼汤+青菜', '萝卜粥', '肺病清淡饮食', '2026-05-22', '2026-06-04 17:52:59', '2026-06-08 11:07:04', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/ac5fb471-f580-4720-848f-5a6653831c33.png');
INSERT INTO `meal` VALUES (34, 'MEAL2026052203', 9, '小米粥+馒头', '米饭+炖排骨+青菜', '南瓜粥', '骨质疏松高钙餐', '2026-05-22', '2026-06-04 17:52:59', '2026-06-08 11:07:10', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (35, 'MEAL2026052301', 10, '燕麦粥+鸡蛋', '杂粮饭+凉拌菜+瘦肉', '绿豆粥', '高血脂低脂餐', '2026-05-23', '2026-06-04 17:52:59', '2026-06-08 11:07:15', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/3473a309-f465-4882-9b44-de20322ac86c.png');
INSERT INTO `meal` VALUES (36, 'MEAL2026052302', 11, '粥+面包', '软饭+蒸蛋+青菜', '山药粥', '易消化护眼餐', '2026-05-23', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (37, 'MEAL2026052303', 12, '红豆粥+包子', '米饭+炖肉+菠菜', '小米粥', '痛风低嘌呤餐', '2026-05-23', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (38, 'MEAL2026052401', 13, '豆浆+花卷', '软饭+鸡蛋+青菜', '南瓜粥', '肾病优质蛋白餐', '2026-05-24', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (39, 'MEAL2026052402', 14, '玉米粥+鸡蛋', '米饭+炒青菜+豆腐', '紫薯粥', '胃炎温和饮食', '2026-05-24', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (40, 'MEAL2026052403', 15, '牛奶+馒头', '软饭+清蒸鱼+白菜', '小米粥', '心脏病低盐餐', '2026-05-24', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (41, 'MEAL2026052501', 16, '杂粮粥+包子', '米饭+时蔬+瘦肉', '百合粥', '老年常规餐', '2026-05-25', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (42, 'MEAL2026052502', 17, '小米粥+蒸蛋', '软饭+炖鸡+西兰花', '萝卜粥', '关节炎养护餐', '2026-05-25', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (43, 'MEAL2026052503', 18, '燕麦粥+花卷', '杂粮饭+青菜+鸡蛋', '山药粥', '康复期营养餐', '2026-05-25', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (44, 'MEAL2026052601', 19, '粥+鸡蛋', '软饭+清蒸鱼+菠菜', '南瓜粥', '高血压控压餐', '2026-05-26', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (45, 'MEAL2026052602', 20, '豆浆+包子', '米饭+豆腐+青菜', '小米粥', '糖尿病低糖餐', '2026-05-26', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (46, 'MEAL2026052603', 1, '小米粥+水煮蛋', '软米饭+青菜+鱼', '山药粥', '高血压低盐餐', '2026-05-26', '2026-06-04 17:52:59', '2026-07-05 16:28:56', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (47, 'MEAL2026052701', 2, '燕麦粥+馒头', '杂粮饭+瘦肉+菠菜', '南瓜粥', '糖尿病低糖餐', '2026-05-27', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (48, 'MEAL2026052702', 3, '豆浆+花卷', '软烂面条+蛋羹', '米糊', '术后软食餐', '2026-05-27', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (49, 'MEAL2026052703', 4, '牛奶+面包', '软饭+蒸蛋+白菜', '小米粥', '痴呆流质餐', '2026-05-27', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (50, 'MEAL2026052801', 5, '红豆粥+鸡蛋', '米饭+时蔬+豆腐', '紫薯粥', '常规健康餐', '2026-05-28', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (51, 'MEAL2026052802', 6, '杂粮粥+包子', '米饭+清蒸蛋+西兰花', '百合粥', '帕金森高蛋白餐', '2026-05-28', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (52, 'MEAL2026052803', 7, '粥品+蒸蛋', '软饭+鱼汤+青菜', '萝卜粥', '肺病清淡饮食', '2026-05-28', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (53, 'MEAL2026052901', 8, '小米粥+馒头', '米饭+炖排骨+青菜', '南瓜粥', '骨质疏松高钙餐', '2026-05-29', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (54, 'MEAL2026052902', 9, '燕麦粥+鸡蛋', '杂粮饭+凉拌菜+瘦肉', '绿豆粥', '高血脂低脂餐', '2026-05-29', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (55, 'MEAL2026052903', 10, '粥+面包', '软饭+蒸蛋+青菜', '山药粥', '易消化护眼餐', '2026-05-29', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (56, 'MEAL2026053001', 11, '红豆粥+包子', '米饭+炖肉+菠菜', '小米粥', '痛风低嘌呤餐', '2026-05-30', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (57, 'MEAL2026053002', 12, '豆浆+花卷', '软饭+鸡蛋+青菜', '南瓜粥', '肾病优质蛋白餐', '2026-05-30', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (58, 'MEAL2026053003', 13, '玉米粥+鸡蛋', '米饭+炒青菜+豆腐', '紫薯粥', '胃炎温和饮食', '2026-05-30', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (59, 'MEAL2026053101', 14, '牛奶+馒头', '软饭+清蒸鱼+白菜', '小米粥', '心脏病低盐餐', '2026-05-31', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (60, 'MEAL2026053102', 15, '杂粮粥+包子', '米饭+时蔬+瘦肉', '百合粥', '老年常规餐', '2026-05-31', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (61, 'MEAL2026053103', 16, '小米粥+蒸蛋', '软饭+炖鸡+西兰花', '萝卜粥', '关节炎养护餐', '2026-05-31', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (62, 'MEAL2026060101', 17, '燕麦粥+花卷', '杂粮饭+青菜+鸡蛋', '山药粥', '康复期营养餐', '2026-06-01', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (63, 'MEAL2026060102', 18, '粥+鸡蛋', '软饭+清蒸鱼+菠菜', '南瓜粥', '高血压控压餐', '2026-06-01', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (64, 'MEAL2026060103', 19, '豆浆+包子', '米饭+豆腐+青菜', '小米粥', '糖尿病低糖餐', '2026-06-01', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (65, 'MEAL2026060201', 20, '小米粥+水煮蛋', '软米饭+青菜+鱼', '山药粥', '高血压低盐餐', '2026-06-02', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (66, 'MEAL2026060202', 1, '燕麦粥+馒头', '杂粮饭+瘦肉+菠菜', '南瓜粥', '糖尿病低糖餐', '2026-06-02', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (67, 'MEAL2026060203', 2, '豆浆+花卷', '软烂面条+蛋羹', '米糊', '术后软食餐', '2026-06-02', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (68, 'MEAL2026060301', 3, '牛奶+面包', '软饭+蒸蛋+白菜', '小米粥', '痴呆流质餐', '2026-06-03', '2026-06-04 17:52:59', '2026-06-08 11:09:51', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/1fcfe218-2537-476e-8d52-597eea51f8c2.png');
INSERT INTO `meal` VALUES (69, 'MEAL2026060302', 4, '红豆粥+鸡蛋', '米饭+时蔬+豆腐', '紫薯粥', '常规健康餐', '2026-06-03', '2026-06-04 17:52:59', '2026-06-05 10:08:06', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/d60dcc11-e526-4594-87f8-bb2d56b89806.png');
INSERT INTO `meal` VALUES (70, 'MEAL2026060303', 5, '杂粮粥+包子', '米饭+清蒸蛋+西兰花', '百合粥', '帕金森高蛋白餐', '2026-06-03', '2026-06-04 17:52:59', '2026-06-05 10:07:52', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/51e73f83-287a-4017-ae67-b735d412d30c.png');
INSERT INTO `meal` VALUES (71, 'MEAL2026060401', 6, '粥品+蒸蛋', '软饭+鱼汤+青菜', '萝卜粥', '肺病清淡饮食', '2026-06-04', '2026-06-04 17:52:59', '2026-06-05 10:07:25', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/7bcbd36a-7ec5-4d0a-8210-58ce0b5a1ddc.png');
INSERT INTO `meal` VALUES (72, 'MEAL2026060402', 7, '小米粥+馒头', '米饭+炖排骨+青菜', '南瓜粥', '骨质疏松高钙餐', '2026-06-04', '2026-06-04 17:52:59', '2026-06-05 10:07:31', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/83811286-d125-42d2-8c7b-e35aeaa5aacd.png');
INSERT INTO `meal` VALUES (73, 'MEAL2026060403', 8, '燕麦粥+鸡蛋', '杂粮饭+凉拌菜+瘦肉', '绿豆粥', '高血脂低脂餐', '2026-06-04', '2026-06-04 17:52:59', '2026-06-05 10:07:37', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/2925e32c-4377-4739-9983-9e99c8682b85.png');
INSERT INTO `meal` VALUES (74, 'MEAL2026060501', 9, '粥+面包', '软饭+蒸蛋+青菜', '山药粥', '易消化护眼餐', '2026-06-05', '2026-06-04 17:52:59', '2026-06-05 10:12:17', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/08134499-7b67-44f7-8966-8b38f291c5a5.png');
INSERT INTO `meal` VALUES (75, 'MEAL2026060502', 10, '红豆粥+包子', '米饭+炖肉+菠菜', '小米粥', '痛风低嘌呤餐', '2026-06-05', '2026-06-04 17:52:59', '2026-06-05 10:07:13', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/54880734-c7b9-49c6-a520-7877a35423a3.png');
INSERT INTO `meal` VALUES (76, 'MEAL2026060503', 11, '豆浆+花卷', '软饭+鸡蛋+青菜', '南瓜粥', '肾病优质蛋白餐', '2026-06-05', '2026-06-04 17:52:59', '2026-06-05 10:07:18', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/a143fda8-971a-4579-868a-64ab252b3c9b.png');
INSERT INTO `meal` VALUES (80, 'M20260618-001', 18, '1', '1', '1', '11', '2026-06-17', '2026-06-18 11:09:06', '2026-06-18 11:09:09', 1, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/76137be9-6376-4ded-974c-0c04dc636da9.jpg');
INSERT INTO `meal` VALUES (81, 'M20260705-001', 19, '12', '321', '231', '321', '2026-07-09', '2026-07-05 17:06:26', '2026-07-05 18:35:20', 1, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/fdd146b0-0d3d-47a2-b4a0-59990620dd5c.jpg');
INSERT INTO `meal` VALUES (82, 'M20260707-001', 33, '馒头加咸菜', '馒头加咸菜', '馒头加咸菜', '多来点水', '2026-07-07', '2026-07-07 23:01:07', '2026-07-07 23:01:07', 0, 'https://lishaohan-oss.oss-cn-beijing.aliyuncs.com/0659c9fe-6bc1-4338-b770-f6682301eb37.jpg');

-- ----------------------------
-- Table structure for nurse_area
-- ----------------------------
DROP TABLE IF EXISTS `nurse_area`;
CREATE TABLE `nurse_area`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `area_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区域名称',
  `remark` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_area_name`(`area_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理区域表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of nurse_area
-- ----------------------------
INSERT INTO `nurse_area` VALUES (1, '一楼普通区', '自理老人护理区', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nurse_area` VALUES (2, '二楼特护区', '失能老人专护区', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nurse_area` VALUES (3, '三楼康复区', '术后康复护理区', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nurse_area` VALUES (4, '四楼认知区', '阿尔茨海默症专区', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nurse_area` VALUES (5, '五楼安宁区', '临终关怀护理区', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nurse_area` VALUES (6, '六楼日间区', '日间托养护理区', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nurse_area` VALUES (11, '河北工程大学22号G楼', '护理服务', '2026-07-05 16:32:21', '2026-07-05 16:32:25', 1);
INSERT INTO `nurse_area` VALUES (12, '河北工程大学2c107', '生活愉快', '2026-07-05 17:10:00', '2026-07-05 17:10:17', 0);

-- ----------------------------
-- Table structure for nursing_item
-- ----------------------------
DROP TABLE IF EXISTS `nursing_item`;
CREATE TABLE `nursing_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '项目名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `level_id` bigint(20) NOT NULL COMMENT '级别ID',
  `frequency` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '频率',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_item_level`(`level_id` ASC) USING BTREE,
  CONSTRAINT `fk_item_level` FOREIGN KEY (`level_id`) REFERENCES `nursing_level` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理项目表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of nursing_item
-- ----------------------------
INSERT INTO `nursing_item` VALUES (1, '晨间基础护理', '洗脸、刷牙、整理仪容', 1, '每日1次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (2, '晚间舒适护理', '洗脚、擦身、睡前照料', 1, '每日1次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (3, '饮食协助照料', '喂食、喂水、餐具清洁', 1, '每日3次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (4, '排泄护理服务', '大小便清洁、更换纸尿裤', 1, '按需服务', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (5, '皮肤压疮护理', '翻身、按摩、皮肤清洁', 1, '每日2次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (6, '肢体康复训练', '关节活动、肌力训练', 2, '每日1次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (7, '生命体征监测', '体温、脉搏、呼吸测量', 2, '每日2次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (8, '血糖监测护理', '空腹血糖、餐后血糖测量', 2, '每日1次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (9, '心理情绪疏导', '聊天、安抚、精神慰藉', 3, '每周2次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (10, '仪容修剪服务', '理发、剪指甲、剃须', 3, '每月1次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (11, '安全助浴服务', '洗澡、搓背、防跌倒护理', 3, '每周2次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (12, '静脉输液操作', '专业输液、穿刺护理', 4, '按需服务', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (13, '重症吸痰护理', '气道清洁、吸痰操作', 4, '按需服务', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (14, '导尿更换护理', '尿管更换、会阴清洁', 4, '每日1次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (15, '鼻饲营养喂养', '胃管喂食、营养支持', 4, '每日3次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (16, '个性化配餐', '根据病情定制饮食', 5, '每日3次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (17, '中医推拿理疗', '按摩、放松肌肉', 5, '每日1次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (18, '口腔清洁护理', '漱口、口腔护理防感染', 6, '每日2次', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (19, '临终舒缓护理', '疼痛缓解、临终关怀', 6, '24小时', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (20, '外出陪同护理', '就医、活动全程陪同', 7, '按需服务', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_item` VALUES (21, '晨间护理', '洗脸刷牙、整理床铺', 5, '每2小时1次', '2026-06-04 14:50:40', '2026-07-05 16:31:08', 0);
INSERT INTO `nursing_item` VALUES (22, '晚间护理', '洗脚擦身、更换衣物', 1, '每日1次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (23, '饮食照料', '喂食助餐、营养配餐', 1, '每日3次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (24, '排泄护理', '更换尿布、协助排便', 1, '按需', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (25, '皮肤护理', '翻身按摩、防压疮', 1, '每2小时1次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (26, '口腔护理', '清洁口腔、预防感染', 1, '每日2次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (27, '头发护理', '洗头理发、保持整洁', 2, '每周2次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (28, '指甲护理', '修剪指甲、清洁甲沟', 2, '每周1次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (29, '助浴服务', '温水沐浴、身体清洁', 2, '每周2次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (30, '康复训练', '肢体活动、功能恢复', 3, '每日2次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (31, '血压监测', '测量血压、记录数据', 3, '每日2次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (32, '血糖监测', '测量血糖、调整饮食', 3, '每日3次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (33, '输液护理', '静脉滴注、观察反应', 4, '按需', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (34, '吸痰护理', '呼吸道清理、保持通畅', 4, '按需', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (35, '导尿护理', '尿路通畅、预防感染', 4, '每日1次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (36, '鼻饲护理', '营养供给、管路护理', 4, '每日4次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (37, '心理疏导', '情绪安抚、精神慰藉', 5, '每周3次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (38, '理疗服务', '红外线照射、超声波', 5, '每日1次', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (39, '临终护理', '舒适关怀、减轻痛苦', 6, '24小时', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (40, '外出陪护', '就医陪同、安全接送', 2, '按需', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_item` VALUES (42, '1', '1231', 16, '每日1次', '2026-07-05 17:08:38', '2026-07-05 17:08:41', 1);

-- ----------------------------
-- Table structure for nursing_level
-- ----------------------------
DROP TABLE IF EXISTS `nursing_level`;
CREATE TABLE `nursing_level`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `level_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '护理级别名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `fee` decimal(10, 2) NOT NULL COMMENT '费用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_level_name`(`level_name` ASC, `deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理级别表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of nursing_level
-- ----------------------------
INSERT INTO `nursing_level` VALUES (1, '一级全护', '24小时全方位照料', 3200.00, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_level` VALUES (2, '二级半护', '半自理老人辅助照料', 2200.00, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_level` VALUES (3, '三级自护', '自理老人基础服务', 1000.00, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_level` VALUES (4, '特级特护', '重症老人专项护理', 5500.00, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_level` VALUES (5, '康复护理', '术后/慢病康复训练', 2600.00, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_level` VALUES (6, '安宁疗护', '临终老人舒缓护理', 3600.00, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_level` VALUES (7, '认知照护', '痴呆老人专属护理', 2300.00, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_level` VALUES (8, '慢病管理', '三高老人健康管理', 1800.00, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_level` VALUES (9, '高端护理', 'VIP老人尊享服务', 4200.00, '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `nursing_level` VALUES (16, '温柔服务', '温柔体贴的关心', 1000.00, '2026-07-05 17:08:08', '2026-07-07 23:01:53', 0);

-- ----------------------------
-- Table structure for nursing_record
-- ----------------------------
DROP TABLE IF EXISTS `nursing_record`;
CREATE TABLE `nursing_record`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `item_id` bigint(20) NOT NULL COMMENT '项目ID',
  `nurse_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '护理人',
  `nursing_time` datetime NOT NULL COMMENT '护理时间',
  `result` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '结果',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_record_no`(`record_no` ASC, `deleted` ASC) USING BTREE,
  INDEX `fk_record_customer`(`customer_id` ASC) USING BTREE,
  INDEX `fk_record_item`(`item_id` ASC) USING BTREE,
  CONSTRAINT `fk_record_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_record_item` FOREIGN KEY (`item_id`) REFERENCES `nursing_item` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '护理记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of nursing_record
-- ----------------------------
INSERT INTO `nursing_record` VALUES (1, '1', 1, 1, '林护士', '2025-05-01 07:00:00', '已完成', '晨间护理：洗脸刷牙', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (2, '2', 2, 2, '何护士', '2025-05-01 19:00:00', '已完成', '晚间护理：洗脚擦身', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (3, '3', 3, 1, '高护士', '2025-05-01 10:00:00', '已完成', '皮肤护理：翻身按摩', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (4, '4', 4, 2, '罗护士', '2025-05-01 15:00:00', '已完成', '心理疏导：情绪安抚', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (5, '5', 5, 1, '郑护士', '2025-05-01 12:00:00', '已完成', '饮食照料：喂食助餐', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (6, '6', 6, 2, '韩护士', '2025-05-01 08:00:00', '已完成', '排泄护理：更换尿布', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (7, '7', 7, 1, '曹护士', '2025-05-01 09:00:00', '已完成', '康复训练：肢体活动', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (8, '8', 8, 2, '曾护士', '2025-05-01 08:00:00', '已完成', '血压监测：135/85mmHg', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (9, '9', 9, 1, '袁护士', '2025-05-01 07:30:00', '已完成', '血糖监测：6.2mmol/L', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (10, '10', 10, 2, '邓护士', '2025-05-01 14:00:00', '已完成', '理发服务：修剪整齐', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (11, '11', 11, 1, '许护士', '2025-05-01 16:00:00', '已完成', '助浴服务：温水清洁', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (12, '12', 12, 2, '傅护士', '2025-05-01 11:00:00', '已完成', '输液护理：静脉滴注', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (13, '13', 13, 1, '林护士', '2025-05-01 13:00:00', '已完成', '吸痰护理：呼吸道清理', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (14, '14', 14, 2, '何护士', '2025-05-01 10:00:00', '已完成', '导尿护理：尿路通畅', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (15, '15', 15, 1, '高护士', '2025-05-01 12:30:00', '已完成', '鼻饲护理：营养供给', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (16, '16', 16, 2, '罗护士', '2025-05-01 18:00:00', '已完成', '营养配餐：糖尿病餐', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (17, '17', 17, 1, '郑护士', '2025-05-01 15:30:00', '已完成', '理疗服务：红外线照射', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (18, '18', 18, 2, '韩护士', '2025-05-01 07:30:00', '已完成', '口腔护理：清洁杀菌', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (19, '19', 19, 1, '曹护士', '2025-05-01 20:00:00', '已完成', '临终护理：舒适关怀', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (20, '20', 20, 2, '曾护士', '2025-05-01 09:30:00', '已完成', '外出陪护：安全接送', '2026-06-04 14:50:40', '2026-06-04 14:50:40', 0);
INSERT INTO `nursing_record` VALUES (23, 'R20260705-001', 19, 35, '许护士', '2026-07-21 00:00:00', '正常', '很好', '2026-07-05 16:31:47', '2026-07-05 17:09:16', 1);
INSERT INTO `nursing_record` VALUES (24, 'R20260705-002', 20, 21, '傅护士', '2026-06-29 00:00:00', '已完成', '好好生活', '2026-07-05 17:09:32', '2026-07-05 17:09:35', 1);
INSERT INTO `nursing_record` VALUES (25, 'R20260707-001', 20, 21, '傅护士', '2026-07-07 00:00:00', '已完成', '恨', '2026-07-07 23:02:46', '2026-07-07 23:02:46', 0);

-- ----------------------------
-- Table structure for outing
-- ----------------------------
DROP TABLE IF EXISTS `outing`;
CREATE TABLE `outing`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `outing_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '外出编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `out_time` datetime NOT NULL COMMENT '外出时间',
  `expected_return_time` datetime NULL DEFAULT NULL COMMENT '预计返回',
  `actual_return_time` datetime NULL DEFAULT NULL COMMENT '实际返回',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '外出中' COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_outing_no`(`outing_no` ASC, `deleted` ASC) USING BTREE,
  INDEX `fk_outing_customer`(`customer_id` ASC) USING BTREE,
  CONSTRAINT `fk_outing_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '外出登记表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of outing
-- ----------------------------
INSERT INTO `outing` VALUES (1, 'OUT2025001', 1, '2025-05-02 08:30:00', '2025-05-02 12:00:00', '2025-05-02 11:30:00', '社区医院体检', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (2, 'OUT2025002', 2, '2025-05-03 09:00:00', '2025-05-03 17:00:00', '2025-05-03 16:30:00', '探望子女', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (3, 'OUT2025003', 3, '2025-05-04 08:00:00', '2025-05-04 12:30:00', '2025-05-04 12:00:00', '医院复查', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (4, 'OUT2025004', 4, '2025-05-05 10:00:00', '2025-05-05 16:00:00', NULL, '家属陪同散步', '外出中', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (5, 'OUT2025005', 5, '2025-05-06 08:30:00', '2025-05-06 11:00:00', '2025-05-06 10:30:00', '药店购药', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (6, 'OUT2025006', 6, '2025-05-07 09:30:00', '2025-05-07 15:00:00', '2025-05-07 14:30:00', '康复理疗', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (7, 'OUT2025007', 7, '2025-05-08 08:00:00', '2025-05-08 13:00:00', '2025-05-08 12:30:00', '吸氧治疗', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (8, 'OUT2025008', 8, '2025-05-09 14:00:00', '2025-05-09 17:00:00', '2025-05-09 16:30:00', '公园散步', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (9, 'OUT2025009', 9, '2025-05-10 08:30:00', '2025-05-10 12:00:00', '2025-05-10 11:30:00', '血脂检查', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (10, 'OUT2025010', 10, '2025-05-11 09:00:00', '2025-05-11 11:30:00', '2025-05-11 11:00:00', '眼科检查', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (11, 'OUT2025011', 11, '2025-05-12 08:00:00', '2025-05-12 14:00:00', '2025-05-12 13:30:00', '痛风理疗', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (12, 'OUT2025012', 12, '2025-05-13 07:30:00', '2025-05-13 12:00:00', '2025-05-13 11:30:00', '医院透析', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (13, 'OUT2025013', 13, '2025-05-14 10:00:00', '2025-05-14 16:00:00', NULL, '回老家探亲', '外出中', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (14, 'OUT2025014', 14, '2025-05-15 08:30:00', '2025-05-15 12:30:00', '2025-05-15 12:00:00', '心脏检查', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (15, 'OUT2025015', 15, '2025-05-16 09:30:00', '2025-05-16 11:00:00', '2025-05-16 10:30:00', '社区活动', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (16, 'OUT2025016', 16, '2025-05-17 08:00:00', '2025-05-17 13:00:00', '2025-05-17 12:30:00', '关节理疗', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (17, 'OUT2025017', 17, '2025-05-18 09:00:00', '2025-05-18 15:00:00', '2025-05-18 14:30:00', '康复训练', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (18, 'OUT2025018', 18, '2025-05-19 08:30:00', '2025-05-19 12:00:00', '2025-05-19 11:30:00', '血压检查', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (19, 'OUT2025019', 19, '2025-05-20 10:00:00', '2025-05-20 16:00:00', '2025-05-20 15:30:00', '家属陪同外出', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (20, 'OUT2025020', 20, '2025-05-21 09:30:00', '2025-05-21 11:30:00', '2025-05-21 11:00:00', '超市购物', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (21, 'OUT2025021', 1, '2025-06-01 08:30:00', '2025-06-01 12:00:00', NULL, '医院复诊', '外出中', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (22, 'OUT2025022', 2, '2025-06-02 09:00:00', '2025-06-02 17:00:00', '2025-06-02 16:30:00', '家庭聚会', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (23, 'OUT2025023', 3, '2025-06-03 08:00:00', '2025-06-03 12:30:00', '2025-06-03 12:00:00', '康复检查', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (24, 'OUT2025024', 4, '2025-06-04 10:00:00', '2025-06-04 16:00:00', NULL, '户外散心', '外出中', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (25, 'OUT2025025', 5, '2025-06-05 08:30:00', '2025-06-05 11:00:00', '2025-06-05 10:30:00', '疫苗接种', '已返回', '2026-06-04 15:00:12', '2026-06-04 15:00:12', 0);
INSERT INTO `outing` VALUES (31, 'O20260605-001', 20, '2026-06-11 00:00:00', '2026-06-16 00:00:00', '2026-07-07 22:28:13', '1', '已返回', '2026-06-05 15:12:26', '2026-07-07 22:28:41', 1);
INSERT INTO `outing` VALUES (32, 'O20260707-001', 33, '2026-07-07 00:00:00', '2026-07-07 00:00:00', '2026-07-07 22:16:23', '12', '已返回', '2026-07-07 22:04:31', '2026-07-07 22:18:09', 1);
INSERT INTO `outing` VALUES (33, 'O20260707-002', 20, '2026-07-07 00:00:00', '2026-07-09 00:00:00', '2026-07-07 22:28:37', '回家', '已返回', '2026-07-07 22:28:34', '2026-07-07 22:28:34', 0);

-- ----------------------------
-- Table structure for service_purchase
-- ----------------------------
DROP TABLE IF EXISTS `service_purchase`;
CREATE TABLE `service_purchase`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `service_id` bigint(20) NOT NULL COMMENT '服务ID',
  `purchase_date` date NOT NULL COMMENT '购买日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '有效' COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_purchase_customer`(`customer_id` ASC) USING BTREE,
  INDEX `fk_purchase_service`(`service_id` ASC) USING BTREE,
  CONSTRAINT `fk_purchase_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_purchase_service` FOREIGN KEY (`service_id`) REFERENCES `care_service` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务购买表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of service_purchase
-- ----------------------------
INSERT INTO `service_purchase` VALUES (1, 1, 1, '2025-01-01', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (2, 2, 2, '2025-01-05', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (3, 3, 3, '2025-01-10', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (4, 4, 4, '2025-01-15', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (5, 5, 5, '2025-01-20', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (6, 6, 6, '2025-02-01', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (7, 7, 7, '2025-02-05', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (8, 8, 8, '2025-02-10', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (9, 9, 9, '2025-02-15', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (10, 10, 10, '2025-02-20', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (11, 11, 11, '2025-03-01', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (12, 12, 12, '2025-03-05', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (13, 13, 13, '2025-03-10', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (14, 14, 14, '2025-03-15', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (15, 15, 15, '2025-03-20', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (16, 16, 16, '2025-03-25', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (17, 17, 17, '2025-04-01', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (18, 18, 18, '2025-04-05', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (19, 19, 19, '2025-04-10', '已失效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (20, 20, 20, '2025-04-15', '有效', '2026-06-04 14:50:39', '2026-06-04 14:50:39', 0);
INSERT INTO `service_purchase` VALUES (21, 20, 19, '2026-06-10', '有效', '2026-06-05 12:00:13', '2026-06-05 12:00:16', 1);
INSERT INTO `service_purchase` VALUES (22, 19, 19, '2026-06-16', '有效', '2026-06-05 15:12:52', '2026-06-05 15:12:55', 1);
INSERT INTO `service_purchase` VALUES (23, 33, 9, '2026-07-08', '已结束', '2026-07-07 22:59:05', '2026-07-07 22:59:13', 0);

-- ----------------------------
-- Table structure for service_relation
-- ----------------------------
DROP TABLE IF EXISTS `service_relation`;
CREATE TABLE `service_relation`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `manager_id` bigint(20) NOT NULL COMMENT '管家ID',
  `remark` varchar(70) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_customer_manager`(`customer_id` ASC, `manager_id` ASC) USING BTREE,
  INDEX `fk_relation_manager`(`manager_id` ASC) USING BTREE,
  CONSTRAINT `fk_relation_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_relation_manager` FOREIGN KEY (`manager_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '服务关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of service_relation
-- ----------------------------
INSERT INTO `service_relation` VALUES (1, 1, 3, '张管家负责张福顺日常服务', '2025-01-01 08:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (2, 2, 3, '张管家负责李桂兰日常服务', '2025-01-05 09:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (3, 3, 4, '王管家负责王建国康复服务', '2025-01-10 10:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (4, 4, 4, '王管家负责赵秀英看护服务', '2025-01-15 11:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (5, 5, 5, '刘管家负责孙永福基础服务', '2025-01-20 14:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (6, 6, 5, '刘管家负责周玉珍特级服务', '2025-02-01 15:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (7, 7, 6, '陈管家负责吴明远吸氧护理', '2025-02-05 16:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (8, 8, 6, '陈管家负责郑淑琴防跌倒服务', '2025-02-10 17:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (9, 9, 7, '赵管家负责马长海饮食管理', '2025-02-15 08:30:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (10, 10, 7, '赵管家负责朱秀莲视力辅助', '2025-02-20 09:30:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (11, 11, 8, '黄管家负责林德才理疗服务', '2025-03-01 10:30:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (12, 12, 8, '黄管家负责高凤兰透析陪同', '2025-03-05 11:30:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (13, 13, 3, '张管家负责罗振江饮食护理', '2025-03-10 14:30:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (14, 14, 4, '王管家负责韩桂英急救管理', '2025-03-15 15:30:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (15, 15, 5, '刘管家负责谢洪斌日常服务', '2025-03-20 16:30:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (16, 16, 6, '陈管家负责宋桂兰关节护理', '2025-03-25 17:30:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (17, 17, 7, '赵管家负责唐守义康复训练', '2025-04-01 08:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (18, 18, 8, '黄管家负责曹玉梅血压监测', '2025-04-05 09:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (19, 19, 3, '张管家负责曾庆山胰岛素护理', '2025-04-10 10:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (20, 20, 4, '王管家负责袁秀琴日常服务', '2025-04-15 11:00:00', '2026-06-04 14:50:40', 0);
INSERT INTO `service_relation` VALUES (27, 19, 8, NULL, '2026-06-05 15:18:07', '2026-06-05 15:18:17', 1);
INSERT INTO `service_relation` VALUES (28, 19, 22, NULL, '2026-06-08 10:57:50', '2026-06-08 10:57:53', 1);
INSERT INTO `service_relation` VALUES (29, 33, 22, NULL, '2026-07-07 22:58:34', '2026-07-07 22:58:34', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `age` int(11) NULL DEFAULT NULL COMMENT '年龄',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '性别',
  `role` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色',
  `enabled` tinyint(4) NULL DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NULL DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 41 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, '13900000001', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '系统管理员', 38, '男', 'ROLE_ADMIN', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (2, '13900000002', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '李主管', 36, '女', 'ROLE_ADMIN', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (3, '13900000010', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '张管家', 32, '女', 'ROLE_MANAGER', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (4, '13900000011', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '王管家', 30, '男', 'ROLE_MANAGER', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (5, '13900000012', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '刘管家', 29, '女', 'ROLE_MANAGER', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (6, '13900000013', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '陈管家', 33, '男', 'ROLE_MANAGER', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (7, '13900000014', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '赵管家', 28, '女', 'ROLE_MANAGER', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (8, '13900000015', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '黄管家', 31, '男', 'ROLE_MANAGER', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (9, '13900000100', '$2a$10$3aG97FhYi6ebvaJoI4Uc9uY.xouCli2z276pVdIFNOhaoz/2nk74i', '林护士', 24, '女', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-07-07 20:34:27', 0);
INSERT INTO `sys_user` VALUES (10, '13900000101', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '何护士', 25, '女', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (11, '13900000102', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '高护士', 26, '男', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (12, '13900000103', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '罗护士', 23, '女', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (13, '13900000104', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '郑护士', 27, '女', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (14, '13900000105', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '韩护士', 25, '男', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (15, '13900000106', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '曹护士', 24, '女', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (16, '13900000107', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '曾护士', 26, '女', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (17, '13900000108', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '袁护士', 28, '男', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (18, '13900000109', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '邓护士', 22, '女', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (19, '13900000110', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '许护士', 25, '女', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (20, '13900000111', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '傅护士', 27, '男', 'ROLE_NURSE', 1, '2026-06-04 14:50:39', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (21, '13800000000', '$2a$10$3aG97FhYi6ebvaJoI4Uc9uY.xouCli2z276pVdIFNOhaoz/2nk74i', '系统管理员', 42, '男', 'ROLE_ADMIN', 1, '2026-06-04 14:51:40', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (22, '13800000001', '$2a$10$LE0ZeR1zJuQ4iqum2ljFhurkwcKDw8GWH.z5vJH6fQJe8R93y8/ku', '健康管家', 35, '女', 'ROLE_MANAGER', 1, '2026-06-04 14:51:40', '2026-06-08 14:35:00', 0);
INSERT INTO `sys_user` VALUES (23, '13800000002', '$2a$10$atbXthsSqf3YvOMzaRYFk.XED35oSixl6EI5W2Z75Nozx78J1QMAG', '普通客户', 68, '男', 'ROLE_USER', 1, '2026-06-04 14:51:40', '2026-06-04 21:00:42', 0);
INSERT INTO `sys_user` VALUES (29, '13460772822', '$2a$10$lD9W1DlgGtRo9PHI5QLoNOXKNxbu3YLHpYLuu2I7qZdT9ctpfToQ', '劳动法', 60, '男', 'ROLE_USER', 1, '2026-06-08 10:43:43', '2026-07-05 17:15:22', 1);
INSERT INTO `sys_user` VALUES (30, '132132132', '', '2131', 21, '男', 'ROLE_USER', 1, '2026-07-05 17:15:34', '2026-07-05 17:15:36', 1);
INSERT INTO `sys_user` VALUES (31, '138787979822', '1234546', '刘伍', 67, '男', 'ROLE_USER', 1, '2026-07-06 22:04:14', '2026-07-06 22:04:14', 0);
INSERT INTO `sys_user` VALUES (32, '13000000004', '$2a$10$og964kNvBbPUz9b4zpSJOOaMud6Ivr3bVhZzVmP7fRr5rm9k60Mw2', '李八', 60, '男', 'ROLE_USER', 1, '2026-07-06 22:17:20', '2026-07-06 22:17:20', 0);
INSERT INTO `sys_user` VALUES (33, '13530002379', '$2a$10$Fs5QJ/iyXikKnOlVNV8bFeCY.13FiryMy6PiH2ef6BbC3/9R9cNoS', '周建宏', 60, '女', 'ROLE_USER', 1, '2026-07-06 22:30:18', '2026-07-06 22:30:18', 0);
INSERT INTO `sys_user` VALUES (34, '13020002908', '$2a$10$qqmJBUXzAxykCQHQCXneFuWl3WSiWAOkSLmCK2mLv9Qqo4u7ddfwK', '王五', 60, '男', 'ROLE_USER', 1, '2026-07-06 22:39:17', '2026-07-06 22:39:17', 0);
INSERT INTO `sys_user` VALUES (35, '13413454554', '$2a$10$nnZmt/1dJL8L8cFOhUNQqujg.IOCROVfAcNQPtfNU.EjtaDIurINa', '周六', 60, '男', 'ROLE_USER', 1, '2026-07-06 22:40:18', '2026-07-06 22:40:18', 0);
INSERT INTO `sys_user` VALUES (36, '18903420403', '$2a$10$CGsPsjUMCojYKl61IwDoxOC6s77lQx.T2DYv8DPGRhZ7lolLHueLe', '刘久', 63, '男', 'ROLE_NURSE', 1, '2026-07-06 22:40:48', '2026-07-07 20:34:25', 0);
INSERT INTO `sys_user` VALUES (40, '13460089092', '123456', '张海涛', 22, '女', 'ROLE_NURSE', 1, '2026-07-07 23:07:30', '2026-07-08 01:47:55', 0);

SET FOREIGN_KEY_CHECKS = 1;
