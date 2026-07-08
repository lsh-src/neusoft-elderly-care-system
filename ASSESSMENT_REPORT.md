# 东软颐养中心管理系统 — 现状评估报告

> 生成时间：2026-07-08

---

## 一、项目结构概览

| 维度 | 数据 |
|------|------|
| 后端 Java 源文件 | 130 个 |
| 前端 Vue 源文件 | 13 个 |
| 后端 Maven 模块 | 12 个 |
| 前端技术栈 | Vue 3.5 + Vite 6 + Element Plus 2.9 + Pinia 2.3 + ECharts 5.6 |
| 后端技术栈 | Spring Boot 3.3.5 + Spring Cloud 4.1.5 + MyBatis-Plus 3.5.7 + JWT 0.12.6 |
| 微服务基础设施 | Nacos + Spring Cloud Gateway + OpenFeign + RabbitMQ |

---

## 二、测试覆盖率现状

| 层级 | 测试文件数 | 覆盖率 |
|------|-----------|--------|
| 后端单元测试 | **0** | 0% |
| 后端集成测试 | **0** | 0% |
| 前端单元测试 | **0** | 0% |
| 前端 E2E 测试 | **0** | 0% |

**结论：项目完全没有自动化测试，所有功能依赖手动验证。**

---

## 三、Lint / Type Check 现状

| 工具 | 状态 |
|------|------|
| ESLint | ❌ 未配置 |
| TypeScript | ❌ 项目使用纯 JS，无类型检查 |
| Checkstyle / SpotBugs | ❌ 未配置 |
| Maven Compiler | ✅ Java 17 编译正常 |

---

## 四、构建状态

| 构建目标 | 状态 | 备注 |
|----------|------|------|
| `npm run build` (前端) | ✅ 通过 | 2 个 chunk 超 500KB 警告 |
| `mvn clean package` (后端) | ⚠️ 需要 Nacos/MySQL | 本地基础设施依赖 |

---

## 五、安全风险清单

### P0 — 安全漏洞

| # | 风险 | 位置 | 说明 |
|---|------|------|------|
| 1 | JWT 密钥硬编码默认值 | `application-common.yml:50` | `neusoft-elderly-care-dev-secret-key-2026` 作为默认值，生产环境若未设置环境变量则密钥泄露 |
| 2 | 数据库密码硬编码默认值 | `application-common.yml:20` | `123456` 作为默认 DB 密码 |
| 3 | 前端默认账号密码硬编码 | `Login.vue:69` | `phone: '13800000000', password: '123456'` 写死在代码中 |

### P1 — 功能缺陷

| # | 问题 | 位置 | 说明 |
|---|------|------|------|
| 4 | 前端 403 处理过于激进 | `request.js:32` | 收到 403 就清除 token 跳转登录页，但 403 可能只是权限不足（应留在当前页提示无权限） |
| 5 | Dashboard 下游 Feign 权限不一致 | 多个 Controller | 部分 Controller 的 @PreAuthorize 未覆盖所有合法角色，导致仪表盘数据为 0 |

### P2 — 代码质量

| # | 问题 | 位置 | 说明 |
|---|------|------|------|
| 6 | 前端 chunk 过大 | `vite build` 输出 | `index.js` 1.2MB、`Dashboard.js` 527KB，需代码分割 |
| 7 | 无输入验证 | 部分 Controller | 缺少 `@Valid` 注解，DTO 校验可能未生效 |

### P3 — 可优化项

| # | 问题 | 说明 |
|---|------|------|
| 8 | 无 ESLint 配置 | 前端代码风格无统一约束 |
| 9 | 无 CI/CD 配置 | 缺少 GitHub Actions / GitLab CI |
| 10 | `dist/` 目录被提交 | 构建产物不应进入版本控制 |

---

## 六、依赖版本检查

| 依赖 | 当前版本 | 最新稳定版 | 风险 |
|------|----------|-----------|------|
| Spring Boot | 3.3.5 | 3.4.x | 低 |
| MyBatis-Plus | 3.5.7 | 3.5.9 | 低 |
| Vue | 3.5.13 | 3.5.x | 低 |
| Element Plus | 2.9.1 | 2.9.x | 低 |
| ECharts | 5.6.0 | 5.6.x | 低 |

**结论：依赖版本较新，无严重过时风险。**
