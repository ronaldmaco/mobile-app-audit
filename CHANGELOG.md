# Changelog

Format: [Keep a Changelog](https://keepachangelog.com/). Versioning: [SemVer](https://semver.org/).

## 2.5.0 - 2026-11-12
- Skill description now also covers reports from users, testers or reviewers that something in a mobile app is wrong, wrong only for some users, intermittent or still failing after a fix. 2.4.1 missed 2 of 8 trigger prompts (the e014 prompt and a request to review payment code before a release) and never loaded in the 10 with-skill runs of the e014 pilots.
- New trigger eval `evals/triggers.json` (8 should, 6 should not), written in the same session as the new description. Claude Code, Opus 5.5, 1 run per prompt and description: 2.4.1 activated on 6/8 and 0/6; 2.5.0 on 8/8 and 0/6.
- e014 hardened to v2 and piloted twice (ceiling both times; kept as a regression check). The pilots ran before this fix, so the skill was never invoked in them.

## 2.4.1 - 2026-11-11
Docs and version metadata only; the skill's behaviour is unchanged.
- README intro: findings are described as Verified (reproduced, shown by a request, or traced to a concrete code trigger) or Suspected, instead of "every finding is proven".
- README evals badge: the single "13/13 with skill" badge added two eval sets that are not comparable. It is replaced by one badge per set with and without the skill and the number of runs: e001-e008 (2.2.0) 8/8 vs 5/8, e009-e013 (2.4.0) 5/5 vs 5/5, 1 run per condition.
- README opening line and agent badge: the skill follows the Agent Skills format and is validated on Claude Code; other agents point to the Compatibility table instead of being listed as supported.

## 2.4.0 - 2026-11-06
- Cases 008-012 from a sibling check of an Android app (Kotlin and legacy Java) against fixes made on iOS: fixes that never reached the other platform, loading flag never reset, errors without a body and cache returned as success, random getter, mutable shared enum constants.
- Evals e009-e013 with Kotlin fixtures. Claude Code (Opus 5.5) passes 5/5 with and without the skill: the fixtures are too small to show a difference, so they serve as a regression check. Details in `evals/results/2.4.0/`.
- README: Android marked as used in real audits; roadmap renumbered (2.5 more stacks, 2.6 flows).
- Sibling check method in SKILL.md 2.8; six new patterns; networking, analytics and Android stack checks.

## 2.3.0 - 2026-11-06
- Agent-neutral: device tools described by capability (shell first, mobile-mcp, agent-specific simulator tools); "work folder" instead of agent-specific terms; new section 1a on what the skill needs from the agent.
- `scripts/install.sh`: installs into `~/.agents/skills` (Codex, Cursor, OpenCode), `~/.claude/skills`, `~/.copilot/skills`, `~/.gemini/skills` or a project's `.agents/skills`, with `--link` and a warning about duplicate copies.
- `docs/INSTALL.md` with folders per agent and compatibility results; `AGENTS.md` with rules for agents that change this repo; `agents/openai.yaml` display metadata for Codex.
- Device testing: Android shell equivalents for elements, dark mode and rotation; iOS taps without a device tool go through Maestro.
- First eval results: Claude Code (Opus 5.5) passes 8/8 with the skill and 5/8 without; answers, blind judge verdicts and limits in `evals/results/2.2.0/`.

## 2.2.0 - 2026-11-06
- Added cases 002-007 from real audits (anonymised): write used as a read, valid preferences with an empty Home, launch crash with a new SDK, consent and logout leaks, the same crash in sibling apps, silent failures and late images.
- Added `evals/`: 8 evals with fixtures and must / must_not criteria, including one where the right answer is "not reproduced".
- Added `examples/bug-verification-report.md`, a full mode A report.
- New patterns: write used as a read, empty result shown as an error, unpaired events, late async result on a reused view, consent not reaching the SDK.
- New checks: read methods for reads, non-2xx is an error even if the body decodes, image reuse and disk cache, unpaired system notifications, URL entry points after a lifecycle migration, consent wiring, cache location, combination sweep, launch with the submission toolchain.
- Impact map: the expected result is per consumer (explicit filter vs curated feed).
- All cases, fixtures and examples anonymised (domain, names, numbers, vendors). New anonymisation rule in the skill and CONTRIBUTING, `scripts/check-anonymised.sh` with git hooks and a CI workflow.
- New README with banner, badges, comparison table, flow diagram, case index and compatibility table.
- Repository history restarted at 2.2.0.

## 2.1.0 - 2026-11-05
- Split into a short core `SKILL.md` plus `references/` loaded on demand, so each run spends fewer tokens.
- Added the impact map (inputs x consumers x states x data): a bug is fixed only when the whole map passes, not only the reported case.
- Added the ground-truth oracle (compare the screen with the API for the same parameters) and a backend catalog sweep (ids, orphans, empty options, 404 links).
- Added the fix review gate and the principle "never hide the user's input"; history check of earlier fixes.
- Added patterns: empty filter means "any", sentinel ids colliding, silent drop, orphan backend data, dead test setup.
- Added environment preflight (disk space, one build folder, test target health) and device pitfalls (taps during scroll, verify stored state, slow first launch).
- Added `cases/` with the first real case; README note to keep a single installed copy.

## 2.0.0 - 2026-11-04
- Added work modes (bug verification, fix verification, targeted, full, store readiness).
- Added bug verification loop: intake, confirm installed build, reproduce, root cause, fix, regression test, verify, delivery check.
- Added "still happens after the fix" delivery checklist (builds, backend deploy, CDN cache, OTA channels, stale local data).
- Added key-frame extraction from user videos, token economy rules and an audit log file to resume work.
- Added security (OWASP MASVS), performance, accessibility, localization, push notifications, purchases and production telemetry.
- Merged reference files into a single SKILL.md.

## 1.0.0
- First version based on mobile-mcp practice: static checklist, backend/CDN checks, device testing, store review.
