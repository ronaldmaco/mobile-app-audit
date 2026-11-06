# Contributing

Thanks for helping this auditor get sharper.

## Best contribution: a real case
Open an issue with the **Case report** template when the skill missed a bug, gave a wrong verdict, or a check is outdated. Include the stack, what happened, what the skill did and what it should have done. Remove secrets, customer data and private URLs.

A case PR adds `cases/NNN-*.md` and an eval in `evals/evals.json` with a fixture in `evals/fixtures/`.

### Anonymise everything
Cases come from real apps, often private or under NDA. Nothing in this repo, including commit messages, may let someone match a case to an app (for example by downloading an APK and searching for a class name).
- Change the domain (e.g. a banking app becomes a travel app), and rename every class, field, key, endpoint, screen and user-visible string.
- No exact counts, ids, versions, dates, URLs, bundle ids or team names. Proportions are fine ("about half").
- Name SDKs, CMS and vendors by kind ("an analytics SDK", "a headless CMS") unless the vendor *is* the bug.
- Never copy app code. Write a new fixture with the fewest lines that still show the bug.
- Start the case with `> Real case, anonymised.`

`scripts/check-anonymised.sh` runs in CI and, after `git config core.hooksPath .githooks`, before every commit. It checks for emails, bundle ids, URLs and secrets, plus your own private terms in `.private/denylist.txt` (git-ignored: put app, client and class names there, never in the repo).

## Pull requests
1. One change per PR (a new check, a fix, a new stack note).
2. Keep it stack-agnostic; stack specifics go in the "Stack notes" section.
3. Every new check must say how to verify it (command, tool or code path), not only what to look for.
4. Update `CHANGELOG.md` and bump the version in `.claude-plugin/plugin.json` and `.claude-plugin/marketplace.json`.

The maintainer decides what is merged, to keep the skill consistent.
