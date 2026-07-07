# 东软颐养中心管理系统 — 数据库表结构

> 数据库名：`neusoft_elderly_care`，引擎：InnoDB，字符集：utf8mb4

---

## 1. sys_user（系统用户表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 用户ID |
| phone | VARCHAR(20) | UNIQUE, NOT NULL | 手机号（登录账号） |
| password | VARCHAR(100) | NOT NULL | BCrypt 加密密码 |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| age | INT | | 年龄 |
| gender | VARCHAR(10) | | 性别 |
| role | VARCHAR(30) | NOT NULL | 角色：ROLE_ADMIN / ROLE_MANAGER / ROLE_NURSE / ROLE_USER |
| enabled | TINYINT | DEFAULT 1 | 启用状态：1正常，0禁用 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除：0正常，1已删除 |

```sql
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `name` varchar(50) NOT NULL COMMENT '姓名',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `role` varchar(30) NOT NULL COMMENT '角色',
  `enabled` tinyint(4) DEFAULT 1 COMMENT '状态 1-正常 0-禁用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
```

---

## 2. customer（客户管理表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| customer_no | VARCHAR(32) | UNIQUE, NOT NULL | 客户编号（自动生成） |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| gender | VARCHAR(10) | | 性别 |
| age | INT | | 年龄 |
| id_card | VARCHAR(30) | UNIQUE | 身份证号 |
| phone | VARCHAR(20) | | 联系电话 |
| emergency_contact | VARCHAR(100) | | 紧急联系人 |
| check_in_date | DATE | | 入住日期 |
| health_status | VARCHAR(100) | | 健康状态 |
| remark | VARCHAR(500) | | 备注 |
| checked_in | TINYINT | DEFAULT 0 | 是否入住：1已入住，0未入住 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_no` varchar(32) NOT NULL COMMENT '客户编号',
  `name` varchar(50) NOT NULL COMMENT '客户姓名',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `id_card` varchar(30) DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `emergency_contact` varchar(100) DEFAULT NULL COMMENT '紧急联系人',
  `check_in_date` date DEFAULT NULL COMMENT '入住日期',
  `health_status` varchar(100) DEFAULT NULL COMMENT '健康状况',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `checked_in` tinyint(4) DEFAULT 0 COMMENT '是否入住 0-未入住 1-已入住',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_no` (`customer_no`, `deleted`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  KEY `idx_customer_name` (`name`),
  KEY `idx_customer_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户管理表';
```

---

## 3. bed（床位管理表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| room_no | VARCHAR(10) | NOT NULL | 房间号 |
| bed_no | VARCHAR(10) | NOT NULL | 床位号 |
| status | ENUM | NOT NULL, DEFAULT '空闲' | 状态：空闲 / 已入住 / 维修中 |
| customer_id | BIGINT | FK → customer.id | 当前入住客户ID |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `bed` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_no` varchar(10) NOT NULL COMMENT '房间号',
  `bed_no` varchar(10) NOT NULL COMMENT '床位号',
  `status` enum('空闲','已入住','维修中') NOT NULL DEFAULT '空闲' COMMENT '状态',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_bed` (`room_no`, `bed_no`, `deleted`),
  KEY `fk_bed_customer` (`customer_id`),
  CONSTRAINT `fk_bed_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位管理表';
```

---

## 4. check_in（入住登记表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| register_no | VARCHAR(32) | UNIQUE, NOT NULL | 登记编号（自动生成） |
| customer_id | BIGINT | FK → customer.id, NOT NULL | 客户ID |
| bed_id | BIGINT | FK → bed.id, NOT NULL | 床位ID |
| check_in_date | DATE | NOT NULL | 入住日期 |
| contract_months | INT | | 合同期限（月） |
| deposit | DECIMAL(10,2) | | 押金 |
| operator | VARCHAR(50) | | 经办人 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `check_in` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `register_no` varchar(32) NOT NULL COMMENT '登记编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `bed_id` bigint(20) NOT NULL COMMENT '床位ID',
  `check_in_date` date NOT NULL COMMENT '入住日期',
  `contract_months` int(11) DEFAULT NULL COMMENT '合同月数',
  `deposit` decimal(10,2) DEFAULT NULL COMMENT '押金',
  `operator` varchar(50) DEFAULT NULL COMMENT '经办人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_register_no` (`register_no`, `deleted`),
  KEY `fk_checkin_customer` (`customer_id`),
  KEY `fk_checkin_bed` (`bed_id`),
  CONSTRAINT `fk_checkin_bed` FOREIGN KEY (`bed_id`) REFERENCES `bed` (`id`),
  CONSTRAINT `fk_checkin_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住登记表';
```

---

## 5. check_out（退住登记表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| checkout_no | VARCHAR(32) | UNIQUE, NOT NULL | 退住编号（自动生成） |
| customer_id | BIGINT | FK → customer.id, NOT NULL | 客户ID |
| checkout_date | DATE | NOT NULL | 退住日期 |
| reason | VARCHAR(300) | | 退住原因 |
| operator | VARCHAR(50) | | 经办人 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `check_out` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `checkout_no` varchar(32) NOT NULL COMMENT '退住编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `checkout_date` date NOT NULL COMMENT '退住日期',
  `reason` varchar(300) DEFAULT NULL COMMENT '原因',
  `operator` varchar(50) DEFAULT NULL COMMENT '经办人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_checkout_no` (`checkout_no`, `deleted`),
  KEY `fk_checkout_customer` (`customer_id`),
  CONSTRAINT `fk_checkout_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退住登记表';
```

---

## 6. outing（外出登记表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| outing_no | VARCHAR(32) | UNIQUE, NOT NULL | 外出编号（自动生成） |
| customer_id | BIGINT | FK → customer.id, NOT NULL | 客户ID |
| out_time | DATETIME | NOT NULL | 外出时间 |
| expected_return_time | DATETIME | | 预计返回时间 |
| actual_return_time | DATETIME | | 实际返回时间 |
| remark | VARCHAR(500) | | 备注 |
| status | VARCHAR(20) | DEFAULT '外出中' | 状态：外出中 / 已返回 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `outing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `outing_no` varchar(32) NOT NULL COMMENT '外出编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `out_time` datetime NOT NULL COMMENT '外出时间',
  `expected_return_time` datetime DEFAULT NULL COMMENT '预计返回',
  `actual_return_time` datetime DEFAULT NULL COMMENT '实际返回',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` varchar(20) DEFAULT '外出中' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_outing_no` (`outing_no`, `deleted`),
  KEY `fk_outing_customer` (`customer_id`),
  CONSTRAINT `fk_outing_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外出登记表';
```

---

## 7. meal（膳食管理表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| meal_no | VARCHAR(32) | UNIQUE, NOT NULL | 餐食编号（自动生成） |
| customer_id | BIGINT | FK → customer.id, NOT NULL | 客户ID |
| breakfast | VARCHAR(200) | | 早餐 |
| lunch | VARCHAR(200) | | 午餐 |
| dinner | VARCHAR(200) | | 晚餐 |
| special_need | VARCHAR(300) | | 特殊需求 |
| meal_date | DATE | NOT NULL | 日期 |
| meal_img | VARCHAR(255) | | 膳食图片URL |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `meal` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `meal_no` varchar(32) NOT NULL COMMENT '餐食编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `breakfast` varchar(200) DEFAULT NULL COMMENT '早餐',
  `lunch` varchar(200) DEFAULT NULL COMMENT '午餐',
  `dinner` varchar(200) DEFAULT NULL COMMENT '晚餐',
  `special_need` varchar(300) DEFAULT NULL COMMENT '特殊需求',
  `meal_date` date NOT NULL COMMENT '日期',
  `meal_img` varchar(255) DEFAULT '' COMMENT '膳食图片地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_meal_no` (`meal_no`, `deleted`),
  KEY `fk_meal_customer` (`customer_id`),
  CONSTRAINT `fk_meal_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='膳食管理表';
```

---

## 8. care_service（服务关注表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| service_name | VARCHAR(80) | NOT NULL | 服务名称 |
| price | DECIMAL(10,2) | NOT NULL | 价格 |
| content | VARCHAR(500) | | 服务内容 |
| period | VARCHAR(50) | | 周期 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `care_service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `service_name` varchar(80) NOT NULL COMMENT '服务名称',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `content` varchar(500) DEFAULT NULL COMMENT '服务内容',
  `period` varchar(50) DEFAULT NULL COMMENT '周期',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_service_name` (`service_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务关注表';
```

---

## 9. service_relation（服务关系表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| customer_id | BIGINT | FK → customer.id, NOT NULL | 客户ID |
| manager_id | BIGINT | FK → sys_user.id, NOT NULL | 健康管家ID |
| remark | VARCHAR(70) | | 备注 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `service_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `manager_id` bigint(20) NOT NULL COMMENT '管家ID',
  `remark` varchar(70) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_manager` (`customer_id`, `manager_id`),
  KEY `fk_relation_manager` (`manager_id`),
  CONSTRAINT `fk_relation_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `fk_relation_manager` FOREIGN KEY (`manager_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务关系表';
```

---

## 10. service_purchase（服务购买表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| customer_id | BIGINT | FK → customer.id, NOT NULL | 客户ID |
| service_id | BIGINT | FK → care_service.id, NOT NULL | 服务ID |
| purchase_date | DATE | NOT NULL | 购买日期 |
| status | VARCHAR(20) | DEFAULT '有效' | 状态：有效 / 已结束 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `service_purchase` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `service_id` bigint(20) NOT NULL COMMENT '服务ID',
  `purchase_date` date NOT NULL COMMENT '购买日期',
  `status` varchar(20) DEFAULT '有效' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `fk_purchase_customer` (`customer_id`),
  KEY `fk_purchase_service` (`service_id`),
  CONSTRAINT `fk_purchase_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `fk_purchase_service` FOREIGN KEY (`service_id`) REFERENCES `care_service` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务购买表';
```

---

## 11. nursing_level（护理级别表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| level_name | VARCHAR(50) | UNIQUE, NOT NULL | 级别名称 |
| description | VARCHAR(500) | | 说明 |
| fee | DECIMAL(10,2) | NOT NULL | 收费标准 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `nursing_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `level_name` varchar(50) NOT NULL COMMENT '护理级别名称',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `fee` decimal(10,2) NOT NULL COMMENT '费用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level_name` (`level_name`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理级别表';
```

---

## 12. nursing_item（护理项目表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| item_name | VARCHAR(80) | NOT NULL | 项目名称 |
| description | VARCHAR(500) | | 说明 |
| level_id | BIGINT | FK → nursing_level.id, NOT NULL | 所属护理级别ID |
| frequency | VARCHAR(80) | | 执行频率 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `nursing_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `item_name` varchar(80) NOT NULL COMMENT '项目名称',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `level_id` bigint(20) NOT NULL COMMENT '级别ID',
  `frequency` varchar(80) DEFAULT NULL COMMENT '频率',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `fk_item_level` (`level_id`),
  CONSTRAINT `fk_item_level` FOREIGN KEY (`level_id`) REFERENCES `nursing_level` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理项目表';
```

---

## 13. nursing_record（护理记录表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| record_no | VARCHAR(32) | UNIQUE, NOT NULL | 记录编号（自动生成） |
| customer_id | BIGINT | FK → customer.id, NOT NULL | 客户ID |
| item_id | BIGINT | FK → nursing_item.id, NOT NULL | 护理项目ID |
| nurse_name | VARCHAR(50) | NOT NULL | 护理人员 |
| nursing_time | DATETIME | NOT NULL | 护理时间 |
| result | VARCHAR(80) | | 结果：正常 / 异常 / 已完成 |
| remark | VARCHAR(500) | | 备注 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `nursing_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `record_no` varchar(32) NOT NULL COMMENT '记录编号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `item_id` bigint(20) NOT NULL COMMENT '项目ID',
  `nurse_name` varchar(50) NOT NULL COMMENT '护理人',
  `nursing_time` datetime NOT NULL COMMENT '护理时间',
  `result` varchar(80) DEFAULT NULL COMMENT '结果',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_record_no` (`record_no`, `deleted`),
  KEY `fk_record_customer` (`customer_id`),
  KEY `fk_record_item` (`item_id`),
  CONSTRAINT `fk_record_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `fk_record_item` FOREIGN KEY (`item_id`) REFERENCES `nursing_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理记录表';
```

---

## 14. nurse_area（护理区域表）

| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键ID |
| area_name | VARCHAR(50) | UNIQUE, NOT NULL | 区域名称 |
| remark | VARCHAR(200) | | 备注 |
| create_time | DATETIME | DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| update_time | DATETIME | DEFAULT CURRENT_TIMESTAMP ON UPDATE | 更新时间 |
| deleted | TINYINT | DEFAULT 0 | 逻辑删除 |

```sql
CREATE TABLE `nurse_area` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `area_name` varchar(50) NOT NULL COMMENT '区域名称',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_area_name` (`area_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='护理区域表';
```

---

## 外键关系汇总

| 外键名 | 源表.字段 | 目标表.字段 | 说明 |
|--------|----------|-----------|------|
| fk_bed_customer | bed.customer_id | customer.id | 床位当前入住客户 |
| fk_checkin_customer | check_in.customer_id | customer.id | 入住客户 |
| fk_checkin_bed | check_in.bed_id | bed.id | 入住床位 |
| fk_checkout_customer | check_out.customer_id | customer.id | 退住客户 |
| fk_outing_customer | outing.customer_id | customer.id | 外出客户 |
| fk_meal_customer | meal.customer_id | customer.id | 膳食客户 |
| fk_relation_customer | service_relation.customer_id | customer.id | 服务关系客户 |
| fk_relation_manager | service_relation.manager_id | sys_user.id | 服务关系健康管家 |
| fk_purchase_customer | service_purchase.customer_id | customer.id | 购买服务客户 |
| fk_purchase_service | service_purchase.service_id | care_service.id | 购买的服务项目 |
| fk_item_level | nursing_item.level_id | nursing_level.id | 护理项目所属级别 |
| fk_record_customer | nursing_record.customer_id | customer.id | 护理记录客户 |
| fk_record_item | nursing_record.item_id | nursing_item.id | 护理记录项目 |
