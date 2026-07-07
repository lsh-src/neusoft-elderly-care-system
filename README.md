# 东软颐养中心管理系统

> 养老机构运营管理平台 — 前后端分离、模块化架构、配置驱动前端

---

## 目录

- [技术栈](#技术栈)
- [项目结构总览](#项目结构总览)
- [后端架构：为什么拆成 11 个模块](#后端架构为什么拆成-11-个模块)
- [前端架构：为什么只用一个 CrudPage](#前端架构为什么只用一个-crudpage)
- [数据流：一次完整的请求旅程](#数据流一次完整的请求旅程)
- [安全设计](#安全设计)
- [数据库设计](#数据库设计)
- [如何扩展新模块](#如何扩展新模块)
- [快速启动](#快速启动)
- [演示账号](#演示账号)

---

## 技术栈

| 层级 | 技术 | 版本 | 选型理由 |
|------|------|------|----------|
| 前端框架 | Vue 3 + Vite | 3.5 / 6.4 | 组合式 API + 极速 HMR |
| UI 组件库 | Element Plus | 2.9 | 企业级组件丰富，中文生态好 |
| 状态管理 | Pinia | 2.3 | Vue 3 官方推荐，轻量 |
| 路由 | Vue Router | 4.5 | 配合 Pinia 做权限守卫 |
| HTTP 客户端 | Axios | 1.7 | 拦截器统一处理 token 和错误 |
| 图表 | ECharts | 5.6 | 仪表盘数据可视化 |
| 后端框架 | Spring Boot | 3.3.5 | Java 17，主流企业级框架 |
| ORM | MyBatis-Plus | 3.5.7 | 单表零 SQL，XML 支持联表 |
| 安全 | Spring Security + JWT | 6.x / 0.12.6 | 无状态认证，角色权限 |
| 数据库 | MySQL | 8.0 | 关系型数据，事务支持 |
| 文件存储 | 阿里云 OSS | — | 图片上传 CDN 加速 |

---

## 项目结构总览

```
neusoftelderlycare1/
├── backend/                          # Spring Boot 多模块 Maven 项目
│   ├── pom.xml                       # 父 POM（依赖版本管理 + 模块声明）
│   ├── elderlycare-common/           # 🧱 公共基础层
│   ├── elderlycare-auth/             # 🔐 认证鉴权模块
│   ├── elderlycare-user/             # 👤 用户管理
│   ├── elderlycare-customer/         # 👥 客户管理
│   ├── elderlycare-bed/              # 🛏️ 床位管理
│   ├── elderlycare-checkin/          # 📋 入住/退住/外出登记
│   ├── elderlycare-meal/             # 🍽️ 膳食管理
│   ├── elderlycare-service/          # ⭐ 服务管理
│   ├── elderlycare-nursing/          # 💊 护理管理
│   ├── elderlycare-dashboard/        # 📊 统计仪表盘
│   └── elderlycare-app/              # 🚀 应用启动入口
│
├── frontend/                         # Vue 3 SPA
│   ├── src/
│   │   ├── views/
│   │   │   ├── CrudPage.vue          # ⭐ 核心：通用 CRUD 页面组件
│   │   │   ├── moduleConfig.js       # ⭐ 核心：模块字段配置
│   │   │   ├── Layout.vue            # 侧边栏 + 顶栏布局壳
│   │   │   ├── Dashboard.vue         # ECharts 仪表盘
│   │   │   ├── MealCalendar.vue      # 膳食日历
│   │   │   ├── Profile.vue           # 个人信息
│   │   │   ├── Login.vue / Register.vue
│   │   ├── router/index.js           # 路由 + 菜单配置 + 权限守卫
│   │   ├── api/crud.js               # 通用 CRUD API 封装
│   │   ├── stores/user.js            # Pinia 用户状态
│   │   ├── utils/request.js          # Axios 实例 + 拦截器
│   │   └── styles/theme.css          # 全局主题变量
│   ├── vite.config.js
│   └── package.json
│
├── database/                         # 建表 SQL
├── docs/                             # ER 图
└── postman/                          # API 测试集合
```

---

## 后端架构：为什么拆成 11 个模块

### 设计思想

传统单体 Spring Boot 项目把所有 Controller、Service、Mapper 放在一个模块里。随着业务增长，会出现：

- **编译慢**：改一行代码，整个项目重新编译
- **职责不清**：护理模块的开发者可能误改膳食模块的代码
- **依赖混乱**：不知道哪些类被哪些模块依赖

本项目采用 **Maven 多模块** 架构，每个业务域一个独立模块，解决上述问题。

### 模块依赖关系

```
                        ┌─────────────┐
                        │   common    │  ← 实体、BaseEntity、ApiResponse、
                        │  (基础层)    │    PageQuery、BaseCrudController、
                        └──────┬──────┘    OssUtil、NumberGenerator
                               │
                        ┌──────▼──────┐
                        │    auth     │  ← JWT 过滤器、Token 生成、
                        │  (认证层)    │    UserDetails、SysUser
                        └──────┬──────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
    ┌─────▼─────┐      ┌──────▼──────┐      ┌─────▼─────┐
    │   user    │      │  customer   │      │    bed    │
    │  用户管理  │      │  客户管理    │      │  床位管理  │
    └───────────┘      └──────┬──────┘      └─────┬─────┘
                              │                    │
                       ┌──────▼────────────────────▼──────┐
                       │           checkin                │
                       │  入住登记 / 退住登记 / 外出登记    │
                       │  (依赖 customer + bed 更新状态)   │
                       └──────────────────────────────────┘

    ┌───────────┐  ┌───────────┐  ┌───────────┐
    │   meal    │  │  service  │  │  nursing  │
    │  膳食管理  │  │  服务管理  │  │  护理管理  │
    └───────────┘  └───────────┘  └───────────┘
          │              │              │
          └──────────────┼──────────────┘
                         │
                  ┌──────▼──────┐
                  │  dashboard  │  ← 聚合所有模块的统计数据
                  │  统计仪表盘  │
                  └──────┬──────┘
                         │
                  ┌──────▼──────┐
                  │     app     │  ← Spring Boot 启动入口
                  │  (应用入口)  │    SecurityConfig、DataInitializer
                  └─────────────┘    依赖所有模块
```

### 每个模块内部结构（以 checkin 为例）

```
elderlycare-checkin/
└── src/main/java/com/neusoft/elderlycare/
    ├── controller/
    │   ├── CheckInController.java     # 入住登记 REST 接口
    │   ├── CheckOutController.java    # 退住登记 REST 接口
    │   └── OutingController.java      # 外出登记 REST 接口
    ├── service/
    │   ├── CheckInService.java        # 接口
    │   ├── CheckInServiceImpl.java    # 实现（含业务逻辑）
    │   ├── CheckOutService.java
    │   ├── CheckOutServiceImpl.java
    │   ├── OutingService.java
    │   └── OutingServiceImpl.java
    └── mapper/
        ├── CheckInMapper.java         # MyBatis 接口
        ├── CheckOutMapper.java
        └── OutingMapper.java
```

> **注意**：实体类（Entity）统一放在 `elderlycare-common` 模块，因为多个模块需要共享同一实体。Mapper XML 文件放在各自模块的 `src/main/resources/mapper/` 目录下。

### 为什么 checkin 依赖 customer 和 bed

入住登记（CheckIn）的业务逻辑不仅是"插入一条入住记录"，还需要：

1. 更新 `customer.checked_in = 1`（标记客户已入住）
2. 更新 `bed.status = '已入住'` 并关联 `bed.customer_id`
3. 退住时反向操作：释放床位、标记客户未入住

所以 `checkin` 模块必须依赖 `customer` 和 `bed` 模块的 Service。

### 为什么 dashboard 依赖几乎所有模块

仪表盘需要聚合统计：

- 总客户数、在住人数 → `customer` 模块
- 空闲床位数 → `bed` 模块
- 入住趋势（近 7 天） → `checkin` 模块
- 护理记录统计 → `nursing` 模块
- 服务订单数 → `service` 模块

这是唯一一个"读多写少、跨域聚合"的模块。

### BaseCrudController 的复用设计

`common` 模块提供了 `BaseCrudController<T>` 抽象类，封装了标准 CRUD 接口：

```java
public abstract class BaseCrudController<T extends BaseEntity, S extends IService<T>> {
    @GetMapping("/page")    // 分页查询
    @GetMapping("/{id}")    // 按 ID 查询
    @PostMapping            // 新增
    @PutMapping("/{id}")    // 修改
    @DeleteMapping("/{id}") // 删除
}
```

业务 Controller 只需继承它，`@Override` 需要定制的方法（如 `page()` 做联表查询、`create()` 做业务校验），其余接口自动复用。**这就是为什么大部分 Controller 代码很短。**

---

## 前端架构：为什么只用一个 CrudPage

### 问题：13 个模块，13 个页面？

系统有 13 个 CRUD 模块（客户、床位、入住、退住、外出、膳食、服务对象、服务关注、服务购买、护理级别、护理内容、护理记录、负责区域）。如果每个模块写一个独立的 `.vue` 文件，会有大量重复代码：

- 表格渲染（el-table + 分页 + 搜索）
- 新增/编辑弹窗（el-dialog + el-form + 表单校验）
- 详情抽屉（el-drawer + el-descriptions）
- 删除确认（ElMessageBox.confirm）

### 解决方案：配置驱动的通用 CRUD 组件

前端只用 **一个 `CrudPage.vue`** + **一个 `moduleConfig.js`**，通过配置区分所有模块。

#### moduleConfig.js — 字段配置表

```javascript
// 每个模块的表单字段定义：[字段名, 标签, 类型, 是否必填, 选项, 默认值]
export const fieldMap = {
  customers: [
    ['name', '姓名', 'text', true],
    ['gender', '性别', 'select', true, ['男', '女'], '男'],
    ['age', '年龄', 'number', true],
    ['phone', '联系电话', 'text'],
    ['checkInDate', '入住日期', 'date'],
    // ...
  ],
  beds: [
    ['roomNo', '房间编号', 'text', true],
    ['bedNo', '床位编号', 'text', true],
    ['status', '状态', 'select', true, ['空闲', '已入住', '维修中'], '空闲'],
  ],
  // ... 其他模块
}

// 下拉框数据源配置
export const dropdownSources = {
  'check-ins': [
    { prop: 'customerId', resource: 'customers', label: 'name', value: 'id' },
    { prop: 'bedId', resource: 'beds', label: 'bedNo', value: 'id' },
  ],
  // ...
}
```

#### CrudPage.vue — 渲染引擎

```
moduleConfig.js                    CrudPage.vue
┌─────────────────┐    props     ┌──────────────────────────┐
│ fieldMap        │ ──────────→  │ <el-table>               │
│ dropdownSources │              │   动态列 from fieldMap    │
│ searchHints     │              │ </el-table>              │
│ getExtraColumns │              │                          │
└─────────────────┘              │ <el-dialog>              │
                                 │   动态表单 from fieldMap  │
                                 │   下拉框 from dropdownSrc │
                                 │ </el-dialog>             │
                                 │                          │
                                 │ <el-drawer>              │
                                 │   动态详情 from fieldMap  │
                                 │ </el-drawer>             │
                                 └──────────────────────────┘
```

#### 路由如何驱动

```javascript
// router/index.js
const routes = menuGroups.flatMap(group => group.children).map(item => ({
  path: item.path,
  component: CrudPage,           // 所有 CRUD 模块共用同一个组件
  props: { module: item },       // 通过 props 传入模块配置
}))

// 侧边栏菜单也从 menuGroups 生成，支持按角色过滤
menuGroups.forEach(group => {
  group.children.forEach(item => {
    if (item.roles.includes(userStore.role)) { /* 显示菜单 */ }
  })
})
```

#### 新增一个模块只需 3 步

**第 1 步**：后端 — 继承 `BaseCrudController`

```java
@RestController
@RequestMapping("/nurse-areas")
public class NurseAreaController extends BaseCrudController<NurseArea, NurseAreaService> {
    // 自动获得 /page, /{id}, POST, PUT, DELETE 接口
}
```

**第 2 步**：前端 `moduleConfig.js` — 加字段配置

```javascript
'nurse-areas': [
  ['areaName', '区域名称', 'text', true],
  ['remark', '备注', 'textarea'],
]
```

**第 3 步**：前端 `router/index.js` — 加菜单项

```javascript
{ path: 'nurse-areas', title: '负责区域', resource: 'nurse-areas', icon: 'Location', roles: ['ROLE_ADMIN', 'ROLE_MANAGER'] }
```

**不需要写任何新的 `.vue` 文件。**

---

## 数据流：一次完整的请求旅程

以"查看入住登记列表"为例：

```
用户点击侧边栏「入住登记」
        │
        ▼
┌─── 前端 ────────────────────────────────────────────────────────────┐
│                                                                     │
│  1. Vue Router 匹配 /check-ins 路由                                 │
│  2. 加载 CrudPage.vue，传入 props.module = {                        │
│       path: 'check-ins', resource: 'check-ins', title: '入住登记'   │
│     }                                                               │
│  3. onMounted → load() → pageApi('check-ins', { current:1, size:10 })│
│  4. Axios GET /api/check-ins/page?current=1&size=10                 │
│  5. 请求头自动附带 Authorization: Bearer <JWT>                      │
│                                                                     │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
                          Vite 代理 (localhost:5173 → localhost:8080)
                                 │
                                 ▼
┌─── 后端 ────────────────────────────────────────────────────────────┐
│                                                                     │
│  6. JwtAuthenticationFilter 解析 token → 设置 SecurityContext       │
│  7. CheckInController.page() 被调用                                 │
│     ├── @PreAuthorize("hasAnyRole('ADMIN','MANAGER')") 权限校验     │
│     └── 调用 CheckInService.getCheckInPage(page, keyword)           │
│  8. CheckInServiceImpl 调用 CheckInMapper.selectCheckInPage()       │
│  9. MyBatis 执行 CheckInMapper.xml 中的 SQL：                       │
│     SELECT ci.*, c.name AS customer_name, b.bed_no AS bed_no        │
│     FROM check_in ci                                                │
│     LEFT JOIN customer c ON ci.customer_id = c.id                   │
│     LEFT JOIN bed b ON ci.bed_id = b.id                             │
│     WHERE ci.deleted = 0                                            │
│     ORDER BY ci.create_time DESC                                    │
│  10. 返回 ApiResponse<Page<CheckIn>>                                │
│      { code: 200, data: { records: [...], total: 42 } }             │
│                                                                     │
└────────────────────────────────┬────────────────────────────────────┘
                                 │
                                 ▼
┌─── 前端 ────────────────────────────────────────────────────────────┐
│                                                                     │
│  11. Axios 拦截器检查 code === 200 → 返回 result.data               │
│  12. CrudPage 更新 rows.value = res.records, total = res.total      │
│  13. el-table 渲染：                                                │
│      - 序号列（自动计算）                                            │
│      - 额外列：registerNo（来自 getExtraColumns 配置）              │
│      - 字段列：customerId → 显示 customerName（联查字段）           │
│               bedId → 显示 bedNo（联查字段）                        │
│               checkInDate、contractMonths、deposit 等               │
│      - 操作列：详情 / 编辑 / 删除                                   │
│  14. el-pagination 显示分页控件                                     │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 安全设计

### 认证流程

```
用户输入手机号 + 密码 → POST /api/auth/login
        │
        ▼
AuthController.login()
  ├── SysUserMapper 按手机号查用户
  ├── BCrypt.matches(明文密码, 数据库哈希) 校验密码
  ├── JwtTokenProvider.generateToken(手机号, 角色) 生成 JWT
  └── 返回 { token, user }

后续所有请求：
  请求头 Authorization: Bearer <JWT>
        │
        ▼
JwtAuthenticationFilter.doFilterInternal()
  ├── 从 Header 提取 Bearer token
  ├── JwtTokenProvider.validateToken(token) 验证签名和过期
  ├── 从 token 解析出手机号 + 角色
  ├── UserDetailsServiceImpl.loadUserByUsername(手机号) 加载用户
  └── 设置 SecurityContext.authentication
```

### 权限模型

| 角色 | 可访问模块 |
|------|-----------|
| `ROLE_ADMIN` | 全部模块 + 用户管理 |
| `ROLE_MANAGER` | 客户、床位、入住/退住/外出、膳食、服务、护理、仪表盘 |
| `ROLE_USER` | 客户（只读）、外出、膳食、服务、护理（只读）、仪表盘 |

权限通过两层控制：

1. **URL 级别**：`SecurityConfig` 中 `.requestMatchers("/users/**").hasRole("ADMIN")`
2. **方法级别**：Controller 上 `@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")`

### 前端权限

```javascript
// router/index.js — 路由守卫
router.beforeEach((to) => {
  if (!userStore.token) return '/login'                    // 未登录 → 登录页
  if (to.meta.roles && !to.meta.roles.includes(userStore.role))
    return '/dashboard'                                    // 无权限 → 首页
})

// Layout.vue — 菜单过滤
const filterGroupItems = (items) =>
  items.filter(item => item.roles.includes(userStore.role))
```

---

## 数据库设计

### 通用字段（BaseEntity）

所有业务表共享以下字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | BIGINT AUTO_INCREMENT | 主键 |
| `create_time` | DATETIME | 创建时间（自动填充） |
| `update_time` | DATETIME | 更新时间（自动填充） |
| `deleted` | INT DEFAULT 0 | 逻辑删除（0=正常, 1=已删除） |

MyBatis-Plus 配置：
```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true    # 下划线 → 驼峰自动映射
  global-config:
    db-config:
      id-type: auto                        # 自增主键
      logic-delete-field: deleted          # 全局逻辑删除字段
```

### 实体与表对应

| 实体 | 表名 | 业务说明 |
|------|------|----------|
| SysUser | sys_user | 系统用户（管理员、健康管家、护士、老人） |
| Customer | customer | 入住客户（老人） |
| Bed | bed | 床位（房间号 + 床位号 + 状态） |
| CheckIn | check_in | 入住登记（关联客户 + 床位） |
| CheckOut | check_out | 退住登记 |
| Outing | outing | 外出登记 |
| Meal | meal | 膳食记录（早/午/晚 + 图片） |
| CareService | care_service | 服务项目（价格、内容、周期） |
| ServiceRelation | service_relation | 服务关系（客户 ↔ 健康管家） |
| ServicePurchase | service_purchase | 服务购买记录 |
| NursingLevel | nursing_level | 护理级别（名称、费用） |
| NursingItem | nursing_item | 护理项目（名称、频率、所属级别） |
| NursingRecord | nursing_record | 护理记录（谁、什么时候、做了什么） |
| NurseArea | nurse_area | 负责区域 |

### 联表查询设计

部分实体有 `@TableField(exist = false)` 的非数据库字段，用于联表查询展示：

```java
// Bed.java
@TableField(exist = false)
private String customerName;  // 来自 customer 表的 name 字段
```

对应的 Mapper XML 通过 LEFT JOIN 赋值：

```xml
<select id="selectBedPage" resultType="Bed">
    SELECT b.*, c.name AS customer_name
    FROM bed b
    LEFT JOIN customer c ON b.customer_id = c.id AND c.deleted = 0
    WHERE b.deleted = 0
</select>
```

---

## 快速启动

### 环境要求

- Java 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0

### 1. 初始化数据库

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE neusoft_elderly_care CHARACTER SET utf8mb4;"

# 导入建表 SQL
mysql -u root -p neusoft_elderly_care < database/neusoft_elderly_care.sql
```

### 2. 启动后端

```bash
cd backend
mvn clean package -DskipTests
java -jar elderlycare-app/target/elderlycare-app-1.0.0.jar
```

或在 IDEA 中直接运行 `ElderlyCareApplication.main()`。

后端地址：`http://localhost:8080/api`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：`http://localhost:5173`

---

## 演示账号

| 角色 | 手机号 | 密码 | 权限范围 |
|------|--------|------|----------|
| 管理员 | 13800000000 | 123456 | 全部功能 + 用户管理 |
| 健康管家 | 13800000001 | 123456 | 业务管理（无用户管理） |
| 入住老人 | 13800000002 | 123456 | 查看个人信息、膳食、服务 |

> 后端启动时 `DataInitializer` 自动检查并创建以上账号，无需手动初始化。

---

## 项目亮点

| 特性 | 实现方式 |
|------|----------|
| **零重复页面** | 13 个 CRUD 模块共用一个 `CrudPage.vue`，配置驱动 |
| **零 SQL 开发** | 单表操作全靠 MyBatis-Plus BaseMapper，联表才写 XML |
| **自动编号** | `NumberGenerator` 工具类按前缀自动生成（如 RZ20260608-001） |
| **逻辑删除** | `@TableLogic` 注解全局生效，删除只是标记 `deleted=1` |
| **自动填充** | `createTime` / `updateTime` 由 MyBatis-Plus MetaObjectHandler 自动写入 |
| **角色权限** | 后端 `@PreAuthorize` + 前端路由守卫 + 菜单过滤，三层控制 |
| **文件上传** | 阿里云 OSS 直传，`UploadController` 返回 CDN URL |
| **过渡动画** | 交叉淡入（无 `mode="out-in"`），避免复杂组件销毁时白屏 |
