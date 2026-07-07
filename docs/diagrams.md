# 东软颐养中心管理系统 — 架构图集

> 所有图表使用 Mermaid 语法，可在 VSCode（安装 Markdown Preview Mermaid 插件）、Typora、GitHub 等工具中渲染。

---

## 目录

1. [ER 图（实体关系图）](#1-er-图实体关系图)
2. [系统架构图](#2-系统架构图)
3. [后端模块依赖图](#3-后端模块依赖图)
4. [前端组件关系图](#4-前端组件关系图)
5. [登录时序图](#5-登录时序图)
6. [CRUD 操作时序图](#6-crud-操作时序图)
7. [入住登记时序图](#7-入住登记时序图)
8. [退住登记时序图](#8-退住登记时序图)
9. [入住流程图](#9-入住流程图)
10. [退住流程图](#10-退住流程图)
11. [外出登记流程图](#11-外出登记流程图)
12. [前端路由守卫流程图](#12-前端路由守卫流程图)
13. [JWT 认证流程图](#13-jwt-认证流程图)
14. [文件上传流程图](#14-文件上传流程图)
15. [前端通用 CRUD 渲染流程图](#15-前端通用-crud-渲染流程图)
16. [角色权限矩阵图](#16-角色权限矩阵图)

---

## 1. ER 图（实体关系图）

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
        bigint id PK "自增主键"
        varchar phone UK "手机号（登录账号）"
        varchar password "BCrypt加密密码"
        varchar name "姓名"
        int age "年龄"
        varchar gender "性别"
        varchar role "角色: ADMIN/MANAGER/NURSE/USER"
        int enabled "启用状态: 1正常/0禁用"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除: 0正常/1删除"
    }

    CUSTOMER {
        bigint id PK "自增主键"
        varchar customer_no UK "客户编号（自动生成）"
        varchar name "姓名"
        varchar gender "性别"
        int age "年龄"
        varchar id_card "身份证号"
        varchar phone "联系电话"
        varchar emergency_contact "紧急联系人"
        date check_in_date "入住日期"
        varchar health_status "健康状态"
        varchar remark "备注"
        int checked_in "是否入住: 1已入住/0未入住"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    BED {
        bigint id PK "自增主键"
        varchar room_no "房间编号"
        varchar bed_no "床位编号"
        varchar status "状态: 空闲/已入住/维修中"
        bigint customer_id FK "当前入住客户ID"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    CHECK_IN {
        bigint id PK "自增主键"
        varchar register_no UK "登记编号（自动生成）"
        bigint customer_id FK "客户ID"
        bigint bed_id FK "床位ID"
        date check_in_date "入住日期"
        int contract_months "合同期限（月）"
        decimal deposit "押金"
        varchar operator "经办人"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    CHECK_OUT {
        bigint id PK "自增主键"
        varchar checkout_no UK "退住编号（自动生成）"
        bigint customer_id FK "客户ID"
        date checkout_date "退住日期"
        varchar reason "退住原因"
        varchar operator "经办人"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    OUTING {
        bigint id PK "自增主键"
        varchar outing_no UK "外出编号（自动生成）"
        bigint customer_id FK "客户ID"
        datetime out_time "外出时间"
        datetime expected_return_time "预计返回时间"
        datetime actual_return_time "实际返回时间"
        varchar remark "备注"
        varchar status "状态: 外出中/已返回"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    MEAL {
        bigint id PK "自增主键"
        varchar meal_no UK "餐食编号（自动生成）"
        bigint customer_id FK "客户ID"
        varchar breakfast "早餐"
        varchar lunch "午餐"
        varchar dinner "晚餐"
        varchar special_need "特殊需求"
        date meal_date "日期"
        varchar meal_img "膳食图片URL"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    CARE_SERVICE {
        bigint id PK "自增主键"
        varchar service_name "服务名称"
        decimal price "价格"
        varchar content "服务内容"
        varchar period "服务周期"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    SERVICE_RELATION {
        bigint id PK "自增主键"
        bigint customer_id FK "客户ID"
        bigint manager_id FK "健康管家ID（关联sys_user）"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    SERVICE_PURCHASE {
        bigint id PK "自增主键"
        bigint customer_id FK "客户ID"
        bigint service_id FK "服务项目ID"
        date purchase_date "购买日期"
        varchar status "状态: 有效/已结束"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    NURSING_LEVEL {
        bigint id PK "自增主键"
        varchar level_name UK "级别名称"
        varchar description "说明"
        decimal fee "收费标准"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    NURSING_ITEM {
        bigint id PK "自增主键"
        varchar item_name "护理项目名称"
        varchar description "说明"
        bigint level_id FK "所属护理级别ID"
        varchar frequency "执行频率"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    NURSING_RECORD {
        bigint id PK "自增主键"
        varchar record_no UK "记录编号（自动生成）"
        bigint customer_id FK "客户ID"
        bigint item_id FK "护理项目ID"
        varchar nurse_name "护理人员"
        datetime nursing_time "护理时间"
        varchar result "结果: 正常/异常/已完成"
        varchar remark "备注"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }

    NURSE_AREA {
        bigint id PK "自增主键"
        varchar area_name "区域名称"
        varchar remark "备注"
        datetime create_time "创建时间"
        datetime update_time "更新时间"
        int deleted "逻辑删除"
    }
```

---

## 2. 系统架构图

```mermaid
graph TB
    subgraph "用户浏览器"
        A[Vue 3 SPA]
    end

    subgraph "前端技术栈"
        B[Vite 6 开发服务器]
        C[Element Plus UI]
        D[Pinia 状态管理]
        E[Vue Router 路由]
        F[Axios HTTP 客户端]
        G[ECharts 图表]
    end

    subgraph "后端 Spring Boot 3.3"
        H[Spring Security + JWT]
        I[Spring MVC Controller]
        J[MyBatis-Plus ORM]
        K[Service 业务层]
        L[Mapper 数据访问层]
    end

    subgraph "数据层"
        M[(MySQL 8.0)]
        N[阿里云 OSS]
    end

    A --> B
    B -->|"/api 代理"| I
    A --> C
    A --> D
    A --> E
    A --> F
    A --> G
    F -->|HTTP + JWT| H
    H --> I
    I --> K
    K --> L
    L --> J
    J --> M
    I -->|上传图片| N
```

---

## 3. 后端模块依赖图

```mermaid
graph TD
    COMMON[elderlycare-common<br/>实体 / BaseEntity / ApiResponse<br/>BaseCrudController / OssUtil]
    AUTH[elderlycare-auth<br/>JWT / Security / SysUser]
    USER[elderlycare-user<br/>用户管理]
    CUSTOMER[elderlycare-customer<br/>客户管理]
    BED[elderlycare-bed<br/>床位管理]
    CHECKIN[elderlycare-checkin<br/>入住/退住/外出]
    MEAL[elderlycare-meal<br/>膳食管理]
    SERVICE[elderlycare-service<br/>服务管理]
    NURSING[elderlycare-nursing<br/>护理管理]
    DASHBOARD[elderlycare-dashboard<br/>统计仪表盘]
    APP[elderlycare-app<br/>启动入口 / 配置]

    COMMON --> AUTH
    AUTH --> USER
    AUTH --> CUSTOMER
    AUTH --> BED
    AUTH --> MEAL
    AUTH --> SERVICE
    AUTH --> NURSING

    CUSTOMER --> CHECKIN
    BED --> CHECKIN
    AUTH --> CHECKIN

    CUSTOMER --> DASHBOARD
    BED --> DASHBOARD
    CHECKIN --> DASHBOARD
    NURSING --> DASHBOARD
    SERVICE --> DASHBOARD

    USER --> APP
    CUSTOMER --> APP
    BED --> APP
    CHECKIN --> APP
    MEAL --> APP
    SERVICE --> APP
    NURSING --> APP
    DASHBOARD --> APP

    style COMMON fill:#e8f5e9,stroke:#2e7d32
    style AUTH fill:#fff3e0,stroke:#e65100
    style APP fill:#e3f2fd,stroke:#1565c0
    style DASHBOARD fill:#fce4ec,stroke:#c62828
```

---

## 4. 前端组件关系图

```mermaid
graph TD
    APP[App.vue] --> ROUTER[Vue Router]
    ROUTER --> LOGIN[Login.vue]
    ROUTER --> REGISTER[Register.vue]
    ROUTER --> LAYOUT[Layout.vue]

    LAYOUT --> SIDEBAR[侧边栏菜单<br/>el-menu + menuGroups]
    LAYOUT --> HEADER[顶栏<br/>用户信息 + 退出]
    LAYOUT --> ROUTERVIEW[router-view<br/>transition 交叉淡入]

    ROUTERVIEW --> DASHBOARD[Dashboard.vue<br/>ECharts 仪表盘]
    ROUTERVIEW --> CRUDPAGE[CrudPage.vue<br/>通用 CRUD 组件]
    ROUTERVIEW --> MEALCAL[MealCalendar.vue<br/>膳食日历]
    ROUTERVIEW --> PROFILE[Profile.vue<br/>个人信息]

    CRUDPAGE --> MODULECONFIG[moduleConfig.js<br/>字段配置表]
    CRUDPAGE --> CRUDAPI[api/crud.js<br/>通用 API]
    CRUDAPI --> REQUEST[utils/request.js<br/>Axios + JWT 拦截器]
    REQUEST --> BACKEND[后端 /api/*]

    LAYOUT --> STORE[stores/user.js<br/>Pinia 用户状态]
    STORE --> REQUEST

    style CRUDPAGE fill:#e8f5e9,stroke:#2e7d32
    style MODULECONFIG fill:#fff3e0,stroke:#e65100
    style LAYOUT fill:#e3f2fd,stroke:#1565c0
```

---

## 5. 登录时序图

```mermaid
sequenceDiagram
    actor User as 用户
    participant Login as Login.vue
    participant Store as Pinia Store
    participant API as api/auth.js
    participant Axios as Axios 拦截器
    participant Auth as AuthController
    participant DB as MySQL

    User->>Login: 输入手机号 + 密码
    Login->>API: login(phone, password)
    API->>Axios: POST /api/auth/login
    Axios->>Auth: HTTP 请求
    Auth->>DB: SELECT * FROM sys_user WHERE phone=?
    DB-->>Auth: SysUser 记录
    Auth->>Auth: BCrypt.matches(密码, 哈希)
    Auth->>Auth: JwtTokenProvider.generateToken(phone, role)
    Auth-->>Axios: { code:200, data:{ token, user } }
    Axios-->>API: result.data
    API-->>Store: { token, user }
    Store->>Store: 保存 token 到 localStorage
    Store->>Store: 保存 user 到 state
    Store-->>Login: 登录成功
    Login->>Login: router.push('/dashboard')
```

---

## 6. CRUD 操作时序图

```mermaid
sequenceDiagram
    actor User as 用户
    participant CP as CrudPage.vue
    participant API as api/crud.js
    participant Axios as Axios 拦截器
    participant Ctrl as Controller
    participant Svc as Service
    participant Mpr as Mapper
    participant DB as MySQL

    Note over User,DB: 查询分页
    User->>CP: 进入页面
    CP->>API: pageApi('customers', {current:1, size:10})
    API->>Axios: GET /api/customers/page
    Axios->>Ctrl: HTTP + JWT
    Ctrl->>Svc: getCustomerPage(page, keyword)
    Svc->>Mpr: selectCustomerPage(page, keyword)
    Mpr->>DB: SELECT * FROM customer WHERE deleted=0
    DB-->>Mpr: 结果集
    Mpr-->>Svc: IPage
    Svc-->>Ctrl: Page
    Ctrl-->>Axios: ApiResponse.success(page)
    Axios-->>API: result.data
    API-->>CP: { records, total }
    CP->>CP: el-table 渲染数据

    Note over User,DB: 新增
    User->>CP: 点击「新增」→ 填写表单 → 保存
    CP->>API: createApi('customers', formData)
    API->>Axios: POST /api/customers
    Axios->>Ctrl: HTTP + JWT + JSON Body
    Ctrl->>Svc: save(customer)
    Svc->>Mpr: insert
    Mpr->>DB: INSERT INTO customer (...)
    DB-->>Mpr: OK
    Svc-->>Ctrl: true
    Ctrl-->>Axios: ApiResponse.success(true)
    CP->>CP: ElMessage.success + 刷新列表

    Note over User,DB: 删除
    User->>CP: 点击「删除」→ 确认
    CP->>API: deleteApi('customers', id)
    API->>Axios: DELETE /api/customers/{id}
    Axios->>Ctrl: HTTP + JWT
    Ctrl->>Svc: removeById(id)
    Svc->>Mpr: update deleted=1
    Mpr->>DB: UPDATE customer SET deleted=1 WHERE id=?
    DB-->>Mpr: OK
    CP->>CP: ElMessage.success + 刷新列表
```

---

## 7. 入住登记时序图

```mermaid
sequenceDiagram
    actor User as 管理员/管家
    participant CP as CrudPage.vue
    participant Ctrl as CheckInController
    participant CISvc as CheckInService
    participant CSvc as CustomerService
    participant BSvc as BedService
    participant DB as MySQL

    User->>CP: 选择客户 + 床位 → 提交入住
    CP->>Ctrl: POST /api/check-ins
    Ctrl->>CISvc: create(checkIn)

    CISvc->>CISvc: 生成登记编号 RZ20260608-001
    CISvc->>DB: INSERT INTO check_in (...)
    CISvc->>BSvc: update bed set status='已入住', customer_id=?
    BSvc->>DB: UPDATE bed SET status='已入住', customer_id=? WHERE id=?
    CISvc->>CSvc: update customer set checked_in=1
    CSvc->>DB: UPDATE customer SET checked_in=1 WHERE id=?

    CISvc-->>Ctrl: true
    Ctrl-->>CP: ApiResponse.success
    CP->>CP: 刷新列表
```

---

## 8. 退住登记时序图

```mermaid
sequenceDiagram
    actor User as 管理员/管家
    participant CP as CrudPage.vue
    participant Ctrl as CheckOutController
    participant COSvc as CheckOutService
    participant CSvc as CustomerService
    participant BSvc as BedService
    participant DB as MySQL

    User->>CP: 选择客户 → 提交退住
    CP->>Ctrl: POST /api/check-outs
    Ctrl->>COSvc: create(checkOut)

    COSvc->>COSvc: 生成退住编号 TZ20260608-001
    COSvc->>DB: INSERT INTO check_out (...)

    COSvc->>DB: SELECT bed_id FROM check_in WHERE customer_id=? AND deleted=0
    DB-->>COSvc: bed_id

    COSvc->>BSvc: release bed (status='空闲', customer_id=null)
    BSvc->>DB: UPDATE bed SET status='空闲', customer_id=NULL WHERE id=?

    COSvc->>CSvc: update customer set checked_in=0
    CSvc->>DB: UPDATE customer SET checked_in=0 WHERE id=?

    COSvc-->>Ctrl: true
    Ctrl-->>CP: ApiResponse.success
    CP->>CP: 刷新列表
```

---

## 9. 入住流程图

```mermaid
flowchart TD
    A([开始]) --> B[选择客户]
    B --> C[选择空闲床位]
    C --> D[填写入住日期、合同期限、押金]
    D --> E[提交表单]
    E --> F{校验通过?}
    F -- 否 --> G[提示错误信息] --> D
    F -- 是 --> H[生成登记编号]
    H --> I[插入 check_in 记录]
    I --> J[更新 bed: status=已入住, customer_id=客户ID]
    J --> K[更新 customer: checked_in=1]
    K --> L[提示「入住成功」]
    L --> M[刷新列表]
    M --> N([结束])
```

---

## 10. 退住流程图

```mermaid
flowchart TD
    A([开始]) --> B[选择已入住客户]
    B --> C[填写退住日期、原因]
    C --> D[提交表单]
    D --> E{校验通过?}
    E -- 否 --> F[提示错误信息] --> C
    E -- 是 --> G[生成退住编号]
    G --> H[插入 check_out 记录]
    H --> I[查询该客户的入住记录获取 bed_id]
    I --> J[释放床位: status=空闲, customer_id=null]
    J --> K[更新 customer: checked_in=0]
    K --> L[提示「退住成功」]
    L --> M[刷新列表]
    M --> N([结束])
```

---

## 11. 外出登记流程图

```mermaid
flowchart TD
    A([开始]) --> B[选择已入住客户]
    B --> C[填写外出时间、预计返回时间、备注]
    C --> D[提交表单]
    D --> E{校验通过?}
    E -- 否 --> F[提示错误信息] --> C
    E -- 是 --> G[生成外出编号]
    G --> H[插入 outing 记录, status=外出中]
    H --> I[提示「外出登记成功」]
    I --> J[刷新列表]
    J --> K{客户返回?}
    K -- 点击「返回登记」--> L[更新 actual_return_time]
    L --> M[更新 status=已返回]
    M --> N[提示「返回登记成功」]
    N --> O([结束])
    K -- 未返回 --> O
```

---

## 12. 前端路由守卫流程图

```mermaid
flowchart TD
    A([用户导航到新路由]) --> B{路径是 /login 或 /register?}
    B -- 是 --> C[允许访问]
    B -- 否 --> D{localStorage 有 token?}
    D -- 否 --> E[重定向到 /login]
    D -- 是 --> F{路由 meta.roles 存在?}
    F -- 否 --> C
    F -- 是 --> G{用户角色在 roles 中?}
    G -- 是 --> C
    G -- 否 --> H[重定向到 /dashboard]
```

---

## 13. JWT 认证流程图

```mermaid
flowchart TD
    A([请求到达后端]) --> B{URL 是公开接口?}
    B -- 是 --> C[直接放行]
    B -- 否 --> D[JwtAuthenticationFilter 拦截]
    D --> E{Header 有 Authorization?}
    E -- 否 --> F[返回 401]
    E -- 是 --> G[提取 Bearer token]
    G --> H{token 有效?}
    H -- 否 --> F
    H -- 是 --> I[解析出手机号 + 角色]
    I --> J[UserDetailsServiceImpl 加载用户]
    J --> K{用户存在且 enabled=1?}
    K -- 否 --> F
    K -- 是 --> L[设置 SecurityContext]
    L --> M{Controller 方法有 @PreAuthorize?}
    M -- 否 --> N[执行 Controller]
    M -- 是 --> O{用户角色满足要求?}
    O -- 是 --> N
    O -- 否 --> P[返回 403]
```

---

## 14. 文件上传流程图

```mermaid
flowchart TD
    A([用户选择图片]) --> B[el-upload 组件]
    B --> C[POST /api/upload/file<br/>multipart/form-data]
    C --> D[Vite 代理转发到后端]
    D --> E{SecurityConfig 校验}
    E --> F["/upload/** permitAll<br/>无需认证"]
    F --> G[UploadController]
    G --> H{文件是否为空?}
    H -- 是 --> I[返回 400 错误]
    H -- 否 --> J[OssUtil.upload]
    J --> K[生成 UUID 文件名]
    K --> L[上传到阿里云 OSS]
    L --> M[返回 CDN URL]
    M --> N["ApiResponse.success(url)"]
    N --> O[el-upload on-success 回调]
    O --> P[URL 存入 form.mealImg]
    P --> Q([保存膳食时一并存入数据库])
```

---

## 15. 前端通用 CRUD 渲染流程图

```mermaid
flowchart TD
    A([路由匹配 CrudPage]) --> B[传入 props.module]
    B --> C{module.resource 是什么?}
    C --> D[moduleConfig.js 查询]
    D --> E["fieldMap[resource] → 表单字段定义"]
    D --> F["dropdownSources[resource] → 下拉数据源"]
    D --> G["getExtraColumns[resource] → 额外展示列"]

    E --> H[fields computed → 完整字段列表]
    H --> I["filterFields → 过滤掉 password/image"]

    subgraph "页面渲染"
        J[el-table 表格]
        K[el-dialog 新增/编辑弹窗]
        L[el-drawer 详情抽屉]
        M[el-pagination 分页]
    end

    I --> J
    I --> K
    I --> L
    H --> M

    subgraph "数据加载"
        N[onMounted → load]
        N --> O["pageApi(resource, query)"]
        O --> P["GET /api/{resource}/page"]
        P --> Q[rows.value = res.records]
        Q --> J
    end

    subgraph "下拉数据（按需加载）"
        R[用户点击「新增」或「编辑」]
        R --> S[loadAllDropdowns]
        S --> T["pageApi(config.resource)"]
        T --> U[dropdownData 注入 field.options]
        U --> K
    end

    J --> V[el-table-column 动态列]
    V --> W["外键字段: customerId → customerName"]
    V --> X["格式化字段: role → 管理员"]
    V --> Y["普通字段: 直接显示"]
```

---

## 16. 角色权限矩阵图

```mermaid
graph LR
    subgraph "ROLE_ADMIN 管理员"
        A1[用户管理 ✅]
        A2[客户管理 ✅]
        A3[床位管理 ✅]
        A4[入住/退住/外出 ✅]
        A5[膳食管理 ✅]
        A6[服务管理 ✅]
        A7[护理管理 ✅]
        A8[仪表盘 ✅]
    end

    subgraph "ROLE_MANAGER 健康管家"
        B1[用户管理 ❌]
        B2[客户管理 ✅]
        B3[床位管理 ✅]
        B4[入住/退住/外出 ✅]
        B5[膳食管理 ✅]
        B6[服务管理 ✅]
        B7[护理管理 ✅]
        B8[仪表盘 ✅]
    end

    subgraph "ROLE_USER 入住老人"
        C1[用户管理 ❌]
        C2[客户管理 ✅只读]
        C3[床位管理 ❌]
        C4[外出登记 ✅]
        C5[膳食管理 ✅]
        C6[服务关注 ✅]
        C7[护理记录 ✅只读]
        C8[仪表盘 ✅]
    end
```

---

## 附录：Mermaid 渲染工具

| 工具 | 说明 |
|------|------|
| **VSCode** | 安装 `Markdown Preview Mermaid Support` 插件 |
| **Typora** | 原生支持 Mermaid |
| **GitHub** | `.md` 文件中的 mermaid 代码块自动渲染 |
| **在线编辑器** | [mermaid.live](https://mermaid.live) |

---
