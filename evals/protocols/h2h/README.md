# Head-to-head 2.6.2

Protocol for comparing this skill with other mobile QA skills and with no skill. **Status: run on 2026-11-18.** Results: `evals/results/2.6.2/claude-code/summary.md`.

## Conditions
| Condition | Installed in the run's clean config dir |
|---|---|
| `ours` | `skills/mobile-app-audit` at the commit recorded in each run |
| `w2m` | all skills of web-to-mobile-magic-plugin (GitHub `suntay44`, MIT) at `e6eb780`, with its `references/` and `scripts/` in the layout the skills expect; slash commands not installed |
| `asr` | all skills of appstore-review-skill (GitHub `devsemih`, MIT) at `57948c1`, with its `references/` and `scripts/` (store task only) |
| `none` | no skill |

Skills load as the agent decides: no prompt names a skill. Which skills loaded is recorded per run.

## Tasks
| Task | Fixture (written from scratch for this test) | Prompt |
|---|---|---|
| Bug verify + fix | `evals/fixtures/h2h-bug` (Kotlin/JVM, Gradle) | `prompts/bug.txt` |
| Unfinished app audit | `evals/fixtures/h2h-unfinished` (Expo / React Native, no `node_modules`) | `prompts/unfinished.txt` |
| Store readiness | `evals/fixtures/h2h-store` (SwiftUI sources, Info.plist, privacy manifest) | `prompts/store.txt` |

## Criteria, fixed before any run
Must / must_not criteria per task and the hidden tests of the bug task were kept private until the runs were
done, so no condition could be tuned to them. Now public in `hidden/`. Their hashes, recorded on 2026-11-18 before any run:

```
72d54503e3046b8d648aa350f653e3de26092827189785fb29cde66fa8f8890e  criteria.md
48ebb0f4b09f29ff941b7c809f7c1ed3af8288a9878b48f8e98c06803c2454bb  HiddenSessionTest.kt
```

Check: `shasum -a 256 evals/protocols/h2h/hidden/*`.

## Setup
- Claude Code headless, Claude Opus 5.5, effort medium, same flags in every condition; caps: 60 turns,
  USD 1.50 and 15 minutes per run.
- Each run in a macOS sandbox: reads only its workspace (a fresh git copy of the fixture), its own
  config dir, the JDK, an offline Gradle cache and a Node install; network only to the model API;
  no user instruction files or memories.
- 3 runs per task and condition, in random order (seed recorded): 30 sessions.
- Bug task, automatic part: the agent's `android/src/main/**` is copied onto a fresh fixture with the
  original tests and the hidden tests, then `./gradlew test`. Changes to visible tests are counted.
- Grading: a judge session per answer, blind (labels shuffled, skill names and file names scrubbed;
  writing style can still reveal a condition). Raw answers, verdicts and the key are published.

## Known limits, stated before the run
- The fixtures and criteria were written by the maintainer's agent, which knows this skill well. They
  were not shown to or reviewed by the other projects' authors.
- `w2m` targets web-to-mobile migration and Expo / Swift apps; the bug task is outside its stated scope.
  `asr` covers only store review, so it runs only on the store task.
- 3 runs per cell: differences of one run are noise.
