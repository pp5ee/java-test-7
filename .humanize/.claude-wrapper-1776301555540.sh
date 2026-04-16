#!/bin/sh
cd '/app/workspaces/d2ec6a65-6577-4333-93c3-5190dc25b4f3' || exit 1
exec 'claude' '--dangerously-skip-permissions' '--print' '/humanize:start-rlcr-loop docs/plan.md --max 6 --yolo --codex-model minimax-m2.5:high --full-review-round 2 --track-plan-file --agent-teams --push-every-round'
