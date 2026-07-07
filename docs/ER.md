# 东软颐养中心管理系统 ER 图

```mermaid
erDiagram
    SYS_USER ||--o{ SERVICE_RELATION : "作为健康管家"
    CUSTOMER ||--o{ SERVICE_RELATION : "作为客户"
    CUSTOMER ||--o| BED : "占用床位"
    CUSTOMER ||--o{ CHECK_IN : "入住记录"
    BED ||--o{ CHECK_IN : "被入住"
    CUSTOMER ||--o{ CHECK_OUT : "退住记录"
    CUSTOMER ||--o{ OUTING : "外出记录"
    CUSTOMER ||--o{ MEAL : "膳食记录"
    CUSTOMER ||--o{ SERVICE_PURCHASE : "购买服务"
    CARE_SERVICE ||--o{ SERVICE_PURCHASE : "被购买"
    NURSING_LEVEL ||--o{ NURSING_ITEM : "包含护理项目"
    CUSTOMER ||--o{ NURSING_RECORD : "接受护理"
    NURSING_ITEM ||--o{ NURSING_RECORD : "护理记录"

    SYS_USER {
        bigint id PK
        varchar phone UK "手机号"
        varchar password "BCrypt密码"
        varchar name "姓名"
        int age "年龄"
        varchar gender "性别"
        varchar role "ADMIN/MANAGER/NURSE/USER"
        int enabled "1正常/0禁用"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    CUSTOMER {
        bigint id PK
        varchar customer_no UK "客户编号"
        varchar name "姓名"
        varchar gender "性别"
        int age "年龄"
        varchar id_card "身份证号"
        varchar phone "联系电话"
        varchar emergency_contact "紧急联系人"
        date check_in_date "入住日期"
        varchar health_status "健康状态"
        varchar remark "备注"
        int checked_in "1已入住/0未入住"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    BED {
        bigint id PK
        varchar room_no "房间编号"
        varchar bed_no "床位编号"
        varchar status "空闲/已入住/维修中"
        bigint customer_id FK "当前客户"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    CHECK_IN {
        bigint id PK
        varchar register_no UK "登记编号"
        bigint customer_id FK
        bigint bed_id FK
        date check_in_date "入住日期"
        int contract_months "合同期限"
        decimal deposit "押金"
        varchar operator "经办人"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    CHECK_OUT {
        bigint id PK
        varchar checkout_no UK "退住编号"
        bigint customer_id FK
        date checkout_date "退住日期"
        varchar reason "退住原因"
        varchar operator "经办人"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    OUTING {
        bigint id PK
        varchar outing_no UK "外出编号"
        bigint customer_id FK
        datetime out_time "外出时间"
        datetime expected_return_time "预计返回"
        datetime actual_return_time "实际返回"
        varchar remark "备注"
        varchar status "外出中/已返回"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    MEAL {
        bigint id PK
        varchar meal_no UK "餐食编号"
        bigint customer_id FK
        varchar breakfast "早餐"
        varchar lunch "午餐"
        varchar dinner "晚餐"
        varchar special_need "特殊需求"
        date meal_date "日期"
        varchar meal_img "图片URL"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    CARE_SERVICE {
        bigint id PK
        varchar service_name "服务名称"
        decimal price "价格"
        varchar content "服务内容"
        varchar period "服务周期"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    SERVICE_RELATION {
        bigint id PK
        bigint customer_id FK
        bigint manager_id FK "健康管家"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    SERVICE_PURCHASE {
        bigint id PK
        bigint customer_id FK
        bigint service_id FK
        date purchase_date "购买日期"
        varchar status "有效/已结束"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    NURSING_LEVEL {
        bigint id PK
        varchar level_name UK "级别名称"
        varchar description "说明"
        decimal fee "收费标准"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    NURSING_ITEM {
        bigint id PK
        varchar item_name "项目名称"
        varchar description "说明"
        bigint level_id FK "护理级别"
        varchar frequency "执行频率"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    NURSING_RECORD {
        bigint id PK
        varchar record_no UK "记录编号"
        bigint customer_id FK
        bigint item_id FK "护理项目"
        varchar nurse_name "护理人员"
        datetime nursing_time "护理时间"
        varchar result "正常/异常/已完成"
        varchar remark "备注"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }

    NURSE_AREA {
        bigint id PK
        varchar area_name "区域名称"
        varchar remark "备注"
        datetime create_time
        datetime update_time
        int deleted "逻辑删除"
    }
```

## 实体关系说明

| 关系 | 说明 |
|------|------|
| CUSTOMER → BED | 一个客户占用一个床位（退住后释放） |
| CUSTOMER → CHECK_IN | 一个客户可有多次入住记录 |
| BED → CHECK_IN | 一个床位可被多次入住使用 |
| CUSTOMER → CHECK_OUT | 一个客户可有多次退住记录 |
| CUSTOMER → OUTING | 一个客户可有多次外出记录 |
| CUSTOMER → MEAL | 一个客户每天可有膳食记录 |
| CUSTOMER → SERVICE_PURCHASE | 一个客户可购买多个服务 |
| CARE_SERVICE → SERVICE_PURCHASE | 一个服务可被多人购买 |
| NURSING_LEVEL → NURSING_ITEM | 一个级别包含多个护理项目 |
| CUSTOMER → NURSING_RECORD | 一个客户可有多条护理记录 |
| NURSING_ITEM → NURSING_RECORD | 一个护理项目可有多条执行记录 |
| SYS_USER → SERVICE_RELATION | 健康管家与客户的服务关系 |
| CUSTOMER → SERVICE_RELATION | 客户与健康管家的服务关系 |
