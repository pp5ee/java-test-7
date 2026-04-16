# Goal Tracker

<!--
This file tracks the ultimate goal, acceptance criteria, and plan evolution.
It prevents goal drift by maintaining a persistent anchor across all rounds.

RULES:
- IMMUTABLE SECTION: Do not modify after initialization
- MUTABLE SECTION: Update each round, but document all changes
- Every task must be in one of: Active, Completed, or Deferred
- Deferred items require explicit justification
-->

## IMMUTABLE SECTION
<!-- Do not modify after initialization -->

### Ultimate Goal

# Requirement

现在我想写一个java基于springboot的后端应用，需要有swagger文档；带有前端页面，启动后都从统一端口暴露服务；项目的主要需求是：1.用于企业员工管理 2.支持员工邮箱注册账号，邮箱登录，邮箱服务于留在配置文件配置后运行代码就可以发送验证码 3.用户分角色，如 ceo，cto，程序员等有组织结构；管理员可以剔除用户 4.任务看板，高一级的用户可以给下级安排任务，并且有看板，自己的任务，自己给别人分配的任务 4.数据库使用mysql，前端使用react

---

## Standard Deliverables (mandatory for every project)

### Acceptance Criteria
<!-- Each criterion must be independently verifiable -->
<!-- Claude must extract or define these in Round 0 -->


- AC-1: All features described in the requirements are implemented and functional
  - Positive Tests: All described features work as expected
  - Negative Tests: Invalid inputs are rejected with clear errors
- AC-2: Code passes all tests (pytest or equivalent)
  - Positive Tests: All test suites pass with no failures
  - Negative Tests: Intentionally broken implementations cause test failures
- AC-3: README.md is included with setup and usage instructions

## Path Boundaries

---

## MUTABLE SECTION
<!-- Update each round with justification for changes -->

### Plan Version: 1 (Updated: Round 0)

#### Plan Evolution Log
<!-- Document any changes to the plan with justification -->
| Round | Change | Reason | Impact on AC |
|-------|--------|--------|--------------|
| 0 | Initial plan | - | - |

#### Active Tasks
<!-- Map each task to its target Acceptance Criterion and routing tag -->
| Task | Target AC | Status | Tag | Owner | Notes |
|------|-----------|--------|-----|-------|-------|
| Project setup: create Spring Boot project structure with Maven, configure Swagger, set up MySQL, add Nginx for unified port | AC-1 | completed | coding | claude | - |
| User authentication: email registration, email login, verification code sending via SMTP | AC-1 | completed | coding | claude | - |
| Role management: user roles (CEO, CTO, Developer, etc.), org structure, admin can remove users | AC-1 | completed | coding | claude | - |
| Task board: hierarchical task assignment, kanban view, my tasks, assigned tasks | AC-1 | completed | coding | claude | - |
| React frontend: employee management UI, auth pages, task board UI, integrate with backend | AC-1 | completed | coding | claude | - |
| Write test suite | AC-2 | completed | coding | claude | Tests written (cannot run without Java/Maven in current env) |
| Create README.md | AC-3 | completed | coding | claude | - |

### Completed and Verified
<!-- Only move tasks here after Codex verification -->
| AC | Task | Completed Round | Verified Round | Evidence |
|----|------|-----------------|----------------|----------|

### Explicitly Deferred
<!-- Items here require strong justification -->
| Task | Original AC | Deferred Since | Justification | When to Reconsider |
|------|-------------|----------------|---------------|-------------------|

### Open Issues
<!-- Issues discovered during implementation -->
| Issue | Discovered Round | Blocking AC | Resolution Path |
|-------|-----------------|-------------|-----------------|
