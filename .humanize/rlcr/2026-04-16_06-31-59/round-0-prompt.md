Read and execute below with ultrathink

## Goal Tracker Setup (REQUIRED FIRST STEP)

Before starting implementation, you MUST initialize the Goal Tracker:

1. Read @/app/workspaces/d2ec6a65-6577-4333-93c3-5190dc25b4f3/.humanize/rlcr/2026-04-16_06-31-59/goal-tracker.md
2. If the "Ultimate Goal" section says "[To be extracted...]", extract a clear goal statement from the plan
3. If the "Acceptance Criteria" section says "[To be defined...]", define 3-7 specific, testable criteria
4. Populate the "Active Tasks" table with tasks from the plan, mapping each to an AC and filling Tag/Owner
5. Write the updated goal-tracker.md

**IMPORTANT**: The IMMUTABLE SECTION can only be modified in Round 0. After this round, it becomes read-only.

---

## Implementation Plan

For all tasks that need to be completed, please use the Task system (TaskCreate, TaskUpdate, TaskList) to track each item in order of importance.
You are strictly prohibited from only addressing the most important issues - you MUST create Tasks for ALL discovered issues and attempt to resolve each one.

## Task Tag Routing (MUST FOLLOW)

Each task must have one routing tag from the plan: `coding` or `analyze`.

- Tag `coding`: Claude executes the task directly.
- Tag `analyze`: Claude must execute via `/humanize:ask-codex`, then integrate Codex output.
- Keep Goal Tracker "Active Tasks" columns **Tag** and **Owner** aligned with execution (`coding -> claude`, `analyze -> codex`).
- If a task has no explicit tag, default to `coding` (Claude executes directly).

# 现在我想写一个java基于springboot的后端应用，需要有swagger文档；带有前端页面，启动后都从统一端口暴露

## Goal Description

# Requirement

现在我想写一个java基于springboot的后端应用，需要有swagger文档；带有前端页面，启动后都从统一端口暴露服务；项目的主要需求是：1.用于企业员工管理 2.支持员工邮箱注册账号，邮箱登录，邮箱服务于留在配置文件配置后运行代码就可以发送验证码 3.用户分角色，如 ceo，cto，程序员等有组织结构；管理员可以剔除用户 4.任务看板，高一级的用户可以给下级安排任务，并且有看板，自己的任务，自己给别人分配的任务 4.数据库使用mysql，前端使用react

---

## Standard Deliverables (mandatory for every project)

- **README.md** — must be included at the project root with: project title & description, prerequisites, installation steps, usage examples with code snippets, configuration options, and project structure overview.
- **Git commits** — use conventional commit prefix `feat:` for all commits.

## Acceptance Criteria

- AC-1: All features described in the requirements are implemented and functional
  - Positive Tests: All described features work as expected
  - Negative Tests: Invalid inputs are rejected with clear errors
- AC-2: Code passes all tests (pytest or equivalent)
  - Positive Tests: All test suites pass with no failures
  - Negative Tests: Intentionally broken implementations cause test failures
- AC-3: README.md is included with setup and usage instructions

## Path Boundaries

### Upper Bound
Fully implement all features from requirements with comprehensive test coverage.

### Lower Bound
Core features working with basic tests and README.

## Dependencies and Sequence

### Milestones
1. Core implementation: Implement all main features
2. Tests: Write comprehensive test suite
3. Documentation: Create README.md

## Task Breakdown

| Task ID | Description | Target AC | Tag | Depends On |
|---------|-------------|-----------|-----|------------|
| task1 | Implement core features from requirements | AC-1 | coding | - |
| task2 | Write test suite | AC-2 | coding | task1 |
| task3 | Create README.md | AC-3 | coding | task1 |

## Claude-Codex Deliberation

### Agreements
- All requirements should be implemented as specified

### Convergence Status
- Final Status: `converged`

## Original Requirements

# Requirement

现在我想写一个java基于springboot的后端应用，需要有swagger文档；带有前端页面，启动后都从统一端口暴露服务；项目的主要需求是：1.用于企业员工管理 2.支持员工邮箱注册账号，邮箱登录，邮箱服务于留在配置文件配置后运行代码就可以发送验证码 3.用户分角色，如 ceo，cto，程序员等有组织结构；管理员可以剔除用户 4.任务看板，高一级的用户可以给下级安排任务，并且有看板，自己的任务，自己给别人分配的任务 4.数据库使用mysql，前端使用react

---

## Standard Deliverables (mandatory for every project)

- **README.md** — must be included at the project root with: project title & description, prerequisites, installation steps, usage examples with code snippets, configuration options, and project structure overview.
- **Git commits** — use conventional commit prefix `feat:` for all commits.

---

## BitLesson Selection (REQUIRED FOR EACH TASK)

Before executing each task or sub-task, you MUST:

1. Read @/app/workspaces/d2ec6a65-6577-4333-93c3-5190dc25b4f3/.humanize/bitlesson.md
2. Run `bitlesson-selector` for each task/sub-task to select relevant lesson IDs
3. Follow the selected lesson IDs (or `NONE`) during implementation

Include a `## BitLesson Delta` section in your summary with:
- Action: none|add|update
- Lesson ID(s): NONE or comma-separated IDs
- Notes: what changed and why (required if action is add or update)

Reference: @/app/workspaces/d2ec6a65-6577-4333-93c3-5190dc25b4f3/.humanize/bitlesson.md

## Agent Teams Mode

You are operating in **Agent Teams mode** as the **Team Leader** within an RLCR (Review-Loop-Correct-Repeat) development cycle.

This is the initial round. Read the implementation plan thoroughly before creating your team. Key RLCR files to be aware of:
- **Plan file** (provided above): The full scope of work and requirements your team must implement
- **Goal tracker** (`goal-tracker.md`): Tracks acceptance criteria, task status, and plan evolution - read it before splitting tasks
- **Work summary**: After all teammates finish, you must write a summary of what was accomplished into the designated summary file

### Your Role

You are the team leader. Your ONLY job is coordination and delegation. You must NEVER write code, edit files, or implement anything yourself.

Your primary responsibilities are:
- **Split tasks** into independent, parallelizable units of work
- **Create agent teams** to execute these tasks using the Task tool with `team_name` parameter
- **Coordinate** team members to prevent overlapping or conflicting changes
- **Monitor progress** and resolve blocking issues between team members
- **Wait for teammates** to finish their work before proceeding - do not implement tasks yourself while waiting

If you feel the urge to implement something directly, STOP and delegate it to a team member instead.

### Guidelines

1. **Task Splitting**: Break work into independent tasks that can be worked on in parallel without file conflicts. Each task should have clear scope and acceptance criteria. Aim for 5-6 tasks per teammate to keep everyone productive and allow reassignment if someone gets stuck.
2. **Cold Start**: Every team member starts with zero prior context (they do NOT inherit your conversation history). However, they DO automatically load project-level CLAUDE.md files and MCP servers. When spawning members, focus on providing: the implementation plan or relevant goals, specific file paths they need to work on, what has been done so far, and what exactly needs to be accomplished. Do not repeat what CLAUDE.md already covers.
3. **File Conflict Prevention**: Two teammates editing the same file causes silent overwrites, not merge conflicts - one teammate's work will be completely lost. Assign strict file ownership boundaries. If two tasks must touch the same file, sequence them with task dependencies (blockedBy) so they never run in parallel.
4. **Coordination**: Track team member progress via TaskList and resolve any discovered dependencies. If a member is blocked or stuck, help unblock them or reassign the work to another member.
5. **Quality**: Review team member output before considering tasks complete. Verify that changes are correct, do not conflict with other members' work, and meet the acceptance criteria.
6. **Commits**: Each team member should commit their own changes. You coordinate the overall commit strategy and ensure all commits are properly sequenced.
7. **Plan Approval**: For high-risk or architecturally significant tasks, consider requiring teammates to plan before implementing (using plan mode). Review and approve their plans before they proceed.
8. **BitLesson Discipline**: Require running `bitlesson-selector` before each sub-task and record selected lesson IDs (or `NONE`) in the work notes.

### Important

- Use the Task tool to spawn agents as team members
- Monitor team members and reassign work if they get stuck
- Merge team work and resolve any conflicts before writing your summary
- Do NOT write code yourself - if you catch yourself about to edit a file or run implementation commands, delegate it instead
- When teammates go idle after sending you a message, this is NORMAL - they are waiting for your response, not done forever

---

## Goal Tracker Rules

Throughout your work, you MUST maintain the Goal Tracker:

1. **Before starting a task**: Mark it as "in_progress" in Active Tasks
   - Confirm Tag/Owner routing is correct before execution
2. **After completing a task**: Move it to "Completed and Verified" with evidence (but mark as "pending verification")
3. **If you discover the plan has errors**:
   - Do NOT silently change direction
   - Add entry to "Plan Evolution Log" with justification
   - Explain how the change still serves the Ultimate Goal
4. **If you need to defer a task**:
   - Move it to "Explicitly Deferred" section
   - Provide strong justification
   - Explain impact on Acceptance Criteria
5. **If you discover new issues**: Add to "Open Issues" table

---

Note: You MUST NOT try to exit `start-rlcr-loop` loop by lying or edit loop state file or try to execute `cancel-rlcr-loop`

After completing the work, please:
0. If you have access to the `code-simplifier` agent, use it to review and optimize the code you just wrote
1. Finalize @/app/workspaces/d2ec6a65-6577-4333-93c3-5190dc25b4f3/.humanize/rlcr/2026-04-16_06-31-59/goal-tracker.md (this is Round 0, so you are initializing it - see "Goal Tracker Setup" above)
2. Commit your changes with a descriptive commit message
3. Write your work summary into @/app/workspaces/d2ec6a65-6577-4333-93c3-5190dc25b4f3/.humanize/rlcr/2026-04-16_06-31-59/round-0-summary.md

Note: Since `--push-every-round` is enabled, you must push your commits to remote after each round.
