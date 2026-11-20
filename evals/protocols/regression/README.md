# Held-out regression round (2.7)

Protocol for the next measured round, written and committed **before any case is picked or run**.
**Status: draft, not run.** Nothing in this folder is a result.

Why this round: in 2.6.2 the model without any skill passed every task
([results](../../results/2.6.2/claude-code/summary.md)). Those fixtures were written by the maintainer's
agent, which knows the skill, and they were too easy to separate the conditions. This round uses bugs that
really happened, that the skill was not written from, and the same cases with and without the skill.

## Rules fixed now
1. **Held out.** The skill is frozen at a commit recorded below before the first case is picked. No case may
   come from `cases/001`-`012` or from any bug the maintainer discussed while writing the skill. Nothing in
   `skills/` changes until every run is graded; a fix found during the round ships in the next release and is
   measured in the round after it.
2. **Picked by a rule, not by outcome.** Cases are picked with the selection rule below, in the order it gives,
   until the target count is reached. No case is dropped after a run. No condition is run before the case list
   and the key are hashed, so no case can be kept or dropped because the baseline passed or failed it.
3. **Same cases, every condition.** Each case runs with the skill and with no skill, same prompt, same
   workspace, same caps.
4. **Everything is published:** case list, prompts, hidden tests, criteria, raw final answers, verdicts and the
   scoring script, including cases where the skill loses or both conditions pass.
5. **Hashes before runs.** Case list, prompts, hidden tests and criteria are kept private until the runs end;
   their sha256 is committed first. Check later with `shasum -a 256 evals/protocols/regression/hidden/*`.

## Cases
Target: 12 cases, at least 4 Android (Kotlin or Java), 4 iOS (Swift), the rest Flutter or React Native.
If the selection rule cannot fill a stack, the round runs with fewer and says so; it is not filled with
cases written from scratch.

A case is eligible when all of these hold:
- A real user-visible bug in a mobile app, with a written report (issue, ticket or support message) and a
  merged fix.
- The fix comes with, or allows, a test that fails before it and passes after it, runnable offline in the
  sandbox (Gradle JVM tests, `swift test` or `xcodebuild test` on a simulator, `flutter test`, `jest`).
- The repository at the commit before the fix builds offline with a recorded dependency cache.

Sources, in this order:
1. **Open-source mobile apps** under an OSI licence: closed issues labelled as a bug with a linked fix
   commit, newest first, from repositories with at least one release in the last 12 months. The case states
   its origin (repository, issue, fix commit) and its licence. The search query and date are recorded in
   `selection.md` so anyone can rerun it.
2. **The maintainer's own apps:** bugs fixed after the freeze commit, anonymised under
   [CONTRIBUTING.md](../../../CONTRIBUTING.md#anonymise-everything); the fixture is the smallest code that keeps
   the bug, and states that it is rewritten.

**Hard cases first**, judged from the issue and the fix commit only, never from a run. A candidate is hard when
at least one holds, and candidates meeting more of them come first (newest first within the same count):
- the fix changes non-test code in 2 or more files;
- the report says it happens only sometimes or only under a condition (background or resume, slow or no
  network, rotation, process death, app update, time zone, locale or calendar, two devices or accounts,
  concurrency, a specific OS version or device);
- the visible symptom is far from the cause (another screen, another layer, the backend or a cache).

**Spread and leaks** (added 2026-11-19, after the first candidate lists and before any hash or run):
- At most 2 cases from the same repository.
- End-user apps only; a bug in a library or SDK counts only when an app's issue reports it and the fix lands in
  that app's repository.
- The hidden test checks behaviour the user would see, through the code's public surface. When the fix's own
  test depends on its internal shape (mocks of private services, new function names), a behaviour test is
  written instead, before any run, and the case is marked "hidden test written by the maintainer". Cases with
  no test in their fix are marked the same way. Results are reported for both groups apart.
- A report that names the cause or the fix (by its author, a maintainer or an agent) is skipped, not edited:
  the prompt must describe only what the user saw. Issues labelled as AI-generated are skipped.
- An issue reopened or reported again as not fixed after the merge is skipped: the right answer is unclear.
- Stack balance: 4 Android, 4 iOS, 4 Flutter or React Native, each stack filled in its own order. A case whose
  repository at the commit before the fix does not build offline, or whose hidden test does not fail before
  the fix and pass after it, is dropped and the next one in that stack's order takes its place; every drop is
  published with its reason.

**War stories are not cases.** Incidents in news, outage reports, Stack Overflow, Reddit, X and forums often
have no public fix or test, so they cannot be graded automatically. They are collected privately as material
for the 2.7 flows and for fixtures written from scratch, which are reported apart and labelled as such. One
becomes a held-out case only if it leads to an open-source fix that meets every rule above.

The prompt is the user's report as written (translated if needed, nothing added). The agent gets the
repository at the commit before the fix, without the fix commit or later history.

## Conditions
| Condition | Installed in the run's clean config dir |
|---|---|
| `ours` | `skills/mobile-app-audit` at the freeze commit |
| `none` | no skill |

No prompt names the skill; it loads as the agent decides, and whether it loaded is recorded per run. Runs
where `ours` did not load are reported apart and still count for `ours`.

## Grading
- **Primary, automatic:** the fix-commit test (hidden from the agent) plus the repository's own suite, run on a
  fresh copy with only the agent's changes to non-test files applied. A case run passes when the hidden test
  passes and no test that passed before now fails.
- **Secondary, judged:** per case, must / must_not written before the runs: names the root cause the fix
  commit addresses; does not claim a run or a test it did not do; does not weaken or delete a test. Graded blind
  (labels shuffled, skill and file names scrubbed), one judge per answer. Counts are reported per item.
- **Recorded, not scored:** turns, wall time, whether a regression test was added, whether the agent ran the
  app or a test on its own.

## Claim rule (fixed now)
- Per case and condition: pass if at least 2 of 3 runs pass.
- The skill is called **better on this set** only if, among cases where the two conditions differ, `ours` wins
  enough for a one-sided sign test at p < 0.05 (at least 5 of 5, 6 of 6, 7 of 7, 7 of 8, 8 of 9, 9 of 10, 9 of 11 or 10 of 12 such
  cases; with 4 or fewer differing cases no claim is possible),
  and no stack has a net loss. **Worse** is the mirror rule. Anything else is published as **no measurable
  difference**, with the per-case table.
- Turns and time are reported, never used to claim a win.

## Setup
- Claude Code headless, model and effort recorded per run, same flags in both conditions; caps of 60 turns and
  20 minutes per run.
- A macOS sandbox per run: a fresh copy of the case repository, its own config dir, the stack's toolchain and
  an offline dependency cache; network only to the model API; no user instruction files or memories.
- 3 runs per case and condition, in random order, seed recorded.
- Other agents (OpenCode, Gemini CLI, Codex) may run the same cases later; each is reported as its own table.

## Separate eval in the same release: background, return, tap again
Not part of the held-out set: it is written from scratch for the condition added in 2.6.2 and is reported
apart. Promised in a public reply to the person who suggested it.
- Fixture: a small app with a save action and a fake server; no idempotency key or in-flight guard, so a
  second tap after returning from the background saves twice.
- Hidden test: simulates a slow save, the app going to the background and returning, and a second tap; passes
  only with exactly one entry on the server **and** that entry shown with no loading state left.
- must: checks the server and the screen after return; fixes with an idempotency key or a guard on the action;
  reruns the same scenario on the fixed code. must_not: passes on "the request finished" alone; claims a
  device run it did not do.
- Limit, stated now: the sandbox runs this as a test with a simulated lifecycle, not on a device.

## Known limits, stated before the run
- One model family for runs and judges; 3 runs per cell; 12 cases at most. Differences of one case are noise.
- Open-source bugs may be in the model's training data with their fixes. Newer issues are preferred for that
  reason; the issue dates are published so readers can weigh it.
- The maintainer picks the search query and writes the criteria. The query, the order and every candidate that
  was skipped (with the eligibility rule it failed) are published in `selection.md`.

## Record
| Item | Value |
|---|---|
| Freeze commit | `513e115` (2026-11-19; last change to `skills/` in `e4d20ff`) |
| Hashes of hidden files | not set |
| Selection | done 2026-11-19 (queries and candidate lists kept private until the hashes are set) |
| Case checks (offline build, fails before / passes after) | paused 2026-11-20: 8 checked, 7 kept, 1 dropped (dependencies no longer resolvable); 5 places left to fill |
| Runs | not started |
