# Results: 2.6.0 on Claude Code (co-install activation probe)

Question: with this skill installed **next to other mobile skills**, which skill loads for which request?

| | |
|---|---|
| Date | 2026-11-17 |
| Agent / model | Claude Code 2.1.321 headless, Claude Opus 5.5 |
| Skill under test | `mobile-app-audit` at commit `4088885` (2.6.0) |
| Installed next to it | `mobile-automation` from mobile-mcp (commit `ad3b423`), `agent-device` and `dogfood` from agent-device (commit `a30accf`); their real `SKILL.md` files, skills only, no MCP server or CLI |
| Setup | Same sandbox and caps as the 2.5.0 second trigger check: fresh session per run, workspace = copy of `fixtures/e014`, capped at 4 turns, USD 0.30 and 150 s. 15 prompts x 3 runs = 45 sessions in random order (seed 176217) |
| Loaded | The transcript shows the skill tool called for a skill, or its `SKILL.md` read, within the 4 turns |
| Raw data | [`coinstall-results.tsv`](coinstall-results.tsv) (one row per session) |

The prompts were written and fixed before any run, in the same session as the 2.6.0 references. Expected values: b1-b8 are bug reports or audits (this skill should load), d1-d4 are device-only actions (it should not), g1-g3 are dogfood-style QA requests (measured only, no expected value: both this skill and `dogfood` fit them).

## Score

| Group | This skill loaded | Other skills loaded |
|---|---|---|
| Bug reports and audits (b1-b8, should load) | **20/24** | none in the 4 misses |
| Device-only actions (d1-d4, should not load) | **0/12** | `agent-device` 5, `mobile-automation` 3, none 4 |
| Dogfood-style QA (g1-g3, measured) | 0/9 | `dogfood` 9/9 |

No session timed out; 23 of 45 ended at the 4-turn cap. Total cost USD 4.61.

## Per prompt

| Id | Prompt | Expected | This skill | Loaded instead |
|---|---|---|---|---|
| b1 | Users on Android say the order history screen is empty after they log in again. It worked last week. Can you find out why and fix it? | yes | 3/3 | |
| b2 | QA reports that on iOS the Pay button sometimes stays disabled after a failed payment. Not every time. Please investigate and fix. | yes | 3/3 | |
| b3 | App Review rejected our build again under guideline 2.1. They say they can't log in. What's wrong? | yes | 3/3 | |
| b4 | We shipped a fix for the duplicate charge bug last week but customers are still sending videos of it. Why is it still happening? | yes | 3/3 | |
| b5 | Since yesterday the app crashes at launch for some users on the newest iOS. Find out what happened. | yes | 2/3 | none (1) |
| b6 | La app muestra precios en cero para algunos usuarios fuera del país. ¿Puedes averiguar la causa y arreglarlo? | yes | 3/3 | |
| b7 | Before we submit to Google Play, check that the app is ready and nothing will get it rejected. | yes | 3/3 | |
| b8 | Do a security review of the checkout flow in this app before release. | yes | **0/3** | none (3) |
| d1 | Take a screenshot of the app running on the iOS simulator. | no | 0/3 | none (3) |
| d2 | Install the debug APK on the Android emulator and open the app. | no | 0/3 | `agent-device` (3) |
| d3 | On the Android emulator, tap Login, type test@example.com in the email field and tell me what appears. | no | 0/3 | `mobile-automation` (3) |
| d4 | Record a short video of the onboarding screens on the iPhone simulator. | no | 0/3 | `agent-device` (2), none (1) |
| g1 | Dogfood this app on iOS and list any bugs or UX issues you find. | measure | 0/3 | `dogfood` (3) |
| g2 | Do an exploratory QA pass of the Android app and report the issues. | measure | 0/3 | `dogfood` (3) |
| g3 | Bug hunt the checkout flow on mobile. | measure | 0/3 | `dogfood` (3) |

## How to read this

- **No collisions seen.** In these 45 sessions the neighbours never took a bug report from this skill, and this skill never took a device-only action from them. Each session loaded at most one skill.
- **Loss: security review (b8), 0/3.** No skill loaded; the model read the code on its own. The neighbours did not take it, so the miss is this skill's description, which lists security among its areas but does not name a security review as a reason to load. After this run the description was changed to name a security review; see the second run below.
- **b5 (launch crash after an OS update), 2/3.** In the miss the model read the project with the shell and finished without loading any skill.
- **Dogfood-style requests go to `dogfood`, 9/9.** Requests that say dogfood, exploratory QA or bug hunt load that skill, not this one. That is a fair split of work, but if both are installed, this skill's method (impact map, evidence, verified fixes) is not used for those requests unless the user asks for it.
- **Limits.** 3 runs per prompt, one agent, one model, one workspace. "Not loaded" means not within 4 turns. The prompts were written by the maintainer of this skill. There was no run of the same prompts with this skill alone, so the probe cannot say whether the neighbours changed its activation rate; it only shows which skill won. Neighbour skills were loaded as `SKILL.md` files only, without their tools, which may change how often they are chosen. This measures which skill loads, not the quality of the answers.

## Second run: 2.6.1 description (security-review fix)

Same agent, model, sandbox, workspace, neighbours and caps as above. 21 prompts x 3 runs = 63 sessions in random order (seed 772696). Raw data: [`coinstall-2-results.tsv`](coinstall-2-results.tsv).

What changed: the description now says the skill is also for "a security review of an app or one of its flows". The probe file records commit `4088885`, but the harness copies the working tree, and the tested `SKILL.md` was the uncommitted one with the new description (last modified before the run started; sha256 prefix `9992858485eb`; the maintainer notes, an HTML comment, were updated after the run, so the committed file's hash differs).

Six prompts were added after the b8 miss and the description change, and fixed before any run with them:

| Id | Prompt | Expected |
|---|---|---|
| s1 | Before release, check this app for security holes: how tokens are stored, what the WebView can reach, and whether any keys ship in the build. | yes |
| s2 | Look at the login and payment code of this mobile app like an attacker would. What could someone abuse? | yes |
| s3 | Revisa la seguridad de la app antes de publicarla: almacenamiento local, sesiones y llamadas al servidor. | yes |
| x1 | Explain what a CSRF token is and when a web form needs one. | no |
| x2 | Write a Python function that hashes passwords with bcrypt and verifies them, with a short test. | no |
| x3 | Is it safe to run a Docker container as root? Give me a short answer and the safer alternative. | no |

| Group | This skill loaded | Other skills loaded |
|---|---|---|
| Bug reports and audits (b1-b8) | **24/24** (first run 20/24) | none |
| New security reviews (s1-s3) | **9/9** | none |
| Device-only actions (d1-d4) | **0/12** | `agent-device` 5, `mobile-automation` 2 (one run loaded both), none 6 |
| Security tasks that are not a mobile app review (x1-x3) | **0/9** | none |
| Dogfood-style QA (g1-g3) | 0/9 | `dogfood` 9/9 |

No session timed out; 38 of 63 ended at the 4-turn cap. Total cost USD 6.52.

- **b8 (security review) went from 0/3 to 3/3**, but the new wording was written to fix exactly this prompt, so b8 alone does not show the fix generalises. s1-s3 were not used for tuning: 9/9.
- **No over-triggering seen** on security tasks outside a mobile app review (x1-x3, 0/9) or on device-only actions (0/12).
- **b5 went from 2/3 to 3/3** with no change aimed at it; with 3 runs per prompt, a one-run difference is within noise.
- **Limits.** Same as the first run, plus: s1-s3 and x1-x3 were written by the maintainer in the same session as the fix, after seeing the b8 miss. x1-x3 are short general questions, an easy "should not" case; a harder one (a security review of a web backend in a mobile repo) was not tested.
