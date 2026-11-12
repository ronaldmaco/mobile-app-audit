# Working on this repository

Instructions for coding agents (Claude Code, Codex, Cursor, Copilot, Gemini CLI, OpenCode...) that change this repo. To **use** the skill in an app, see [docs/INSTALL.md](docs/INSTALL.md) instead.

## Layout
- `skills/mobile-app-audit/SKILL.md`: the core, kept short. Details go in `references/`, loaded on demand.
- `cases/`: anonymised real cases. `evals/`: prompts, fixtures and must / must_not criteria built from them. `examples/`: a full report.
- `scripts/check-anonymised.sh`: anonymisation check. `scripts/install.sh`: installer for any agent.

## Rules
1. **Anonymise everything.** Cases come from private apps. No real app, client, class, field, endpoint, string, exact number or vendor name, in files or in commit messages. Follow [CONTRIBUTING.md](CONTRIBUTING.md#anonymise-everything). Run `scripts/check-anonymised.sh` before every commit; enable the hooks once with `git config core.hooksPath .githooks`.
2. **Stay agent-neutral** in `skills/`. Describe tools by what they do (see the table in `references/device-testing.md`), never by one agent's tool names. Agent-specific notes go in `docs/INSTALL.md`.
3. **Keep the core short.** New detail goes in a reference file; `SKILL.md` only points to it.
4. **Every check says how to verify it** (a command, tool or code path), not only what to look for.
5. **New case = new eval.** Add `cases/NNN-*.md` and an entry in `evals/evals.json` with a minimal fixture written from scratch.
6. **Release:** update `CHANGELOG.md`, bump the version in `.claude-plugin/plugin.json` and `.claude-plugin/marketplace.json`, and validate JSON with `python3 -m json.tool`.
7. **Honest claims.** Every number in a public text (README, CHANGELOG, results, badges, the skill description, posts) comes from a raw result file and states its runs, model and limits. Publish losses and ceilings too. Mark anything not verified as not verified. A store or security rule goes in only with its official source and a way to verify it. If a sentence says more than the data, rewrite it, even if the maintainer wrote it. Show this through our own evidence; never call other projects dishonest.
8. Do not push. The maintainer reviews and pushes.
