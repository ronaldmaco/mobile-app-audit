# Install

The skill is a folder, `skills/mobile-app-audit/`, with a `SKILL.md` file that follows the open [Agent Skills](https://agentskills.io) format. Any agent that reads that format can use it. Install **one** copy per agent: some agents read several folders, and a second, older copy can load instead of the latest one.

## Quickest: one folder for most agents

```bash
git clone https://github.com/ronaldmaco/mobile-app-audit.git
cd mobile-app-audit
scripts/install.sh            # copies to ~/.agents/skills/mobile-app-audit
```

`~/.agents/skills` is read by Codex, Cursor and OpenCode, so one install covers all three. Add `--link` to symlink the folder instead of copying it, so `git pull` updates the skill. The script warns you if it finds another copy.

## Per agent

| Agent | User-wide folder | Project folder | Install | Tested |
|---|---|---|---|---|
| **Claude Code** | plugin, or `~/.claude/skills` | `.claude/skills` | `/plugin marketplace add ronaldmaco/mobile-app-audit` then `/plugin install mobile-app-audit@mobile-app-audit` | ✅ real audits + evals |
| **Claude.ai / desktop** | Settings → Capabilities → Skills | n/a | Upload a zip of `skills/mobile-app-audit/` | ✅ |
| **OpenAI Codex** | `~/.agents/skills` | `.agents/skills` | `scripts/install.sh` | not yet |
| **Cursor** | `~/.agents/skills` or `~/.cursor/skills` | `.agents/skills` or `.cursor/skills` | `scripts/install.sh` | not yet |
| **OpenCode** | `~/.agents/skills` or `~/.config/opencode/skills` | `.agents/skills` or `.opencode/skills` | `scripts/install.sh` | see [compatibility](#compatibility-results) |
| **Gemini CLI** | `~/.gemini/skills` | `.gemini/skills` or `.agents/skills` | `scripts/install.sh --agent gemini` | see [compatibility](#compatibility-results) |
| **GitHub Copilot** (CLI, VS Code agent mode, cloud agent) | `~/.copilot/skills` | `.github/skills` or `.agents/skills` | `scripts/install.sh --agent copilot` | not yet |

For one repository only, shared with your team through git: `scripts/install.sh --project /path/to/app` puts it in `.agents/skills`, which Codex, Cursor, Copilot, Gemini CLI and OpenCode all read.

**Cursor also reads `~/.claude/skills` and `~/.codex/skills`.** If you use Claude Code and Cursor on the same machine, one copy in `~/.claude/skills` (or the plugin) can be enough; do not add a second one in `~/.agents/skills`.

Folder names and paths come from each agent's official docs as of November 2026. Agents change quickly; if a skill does not show up, check the agent's docs and restart it.

## What the skill needs from the agent

| Capability | Needed? | Used for |
|---|---|---|
| Read files, search code | Required | Every mode |
| Run shell commands | Required for device, backend and build checks | `xcrun simctl`, `adb`, `curl`, build tools |
| Device tool (e.g. mobile-mcp, or the agent's own simulator tool) | Optional | Element lists and taps on iOS. Without it, use Maestro or ask the user to tap |
| Browser | Optional | Store pages, web views, CMS |
| Sub-agents / background tasks | Optional | Wide code searches |

## Compatibility results

How the skill behaved in each agent, on the eval set in [`evals/`](../evals/). An agent is marked tested only after a real run.

| Agent | Version | Result |
|---|---|---|
| Claude Code (Opus 5.5) | 2.2.0 | 8/8 evals with the skill, 5/8 without. [Details](../evals/results/2.2.0/claude-code/summary.md) |
| Claude Code (Opus 5.5) | 2.4.0 | Android evals e009-e013: 5/5 with and without the skill. [Details](../evals/results/2.4.0/claude-code/summary.md) |
| Claude Code (Opus 5.5) | 2.5.0 | Trigger eval: the skill loads on 8/8 requests it should and 0/6 it should not (1 run per prompt; prompts written with the description) (2.4.1: 6/8). [Details](../evals/results/2.5.0/claude-code/triggers.md) |
| OpenCode 1.18 | 2.3.0 | `opencode debug skill` lists the skill from a project's `.agents/skills`. Eval runs did not finish: the default model was unavailable and two free models timed out, so behaviour is not measured yet. |
| Gemini CLI 0.58 | 2.3.0 | Ran eval e003 headless (`--skip-trust`, plan mode) with the skill in the project's `.agents/skills`. The answer found the cause but showed no sign of the skill (no impact map, no Verified/Suspected, no Home fallback), so the skill was most likely not loaded or not triggered. Checking discovery was blocked by the account's daily quota. To retry: install with `scripts/install.sh --agent gemini`, check `gemini skills list`, and ask for the skill by name. |
