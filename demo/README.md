# Demo: Event Finder

A tiny SwiftUI app written from scratch for the demo GIF (`assets/demo.gif`). It is not a real app and has no backend: `EventsAPI.swift` stands in for one and, like many real APIs, reads an empty venue filter as "any venue".

## The planted bug
The venue "Rooftop" has id 0. `ContentView.swift` skips ids that are not positive "before calling the API", so a user who follows only Rooftop sends an empty filter and sees every event. The same mechanism is real case [`cases/001`](../cases/001-dropped-filter-returns-everything.md).

## Run it
```bash
cd demo/EventFinder
xcodebuild -project EventFinder.xcodeproj -scheme EventFinder -destination 'platform=iOS Simulator,name=<an iPhone simulator>' -derivedDataPath /tmp/eventfinder-dd build
xcrun simctl install booted /tmp/eventfinder-dd/Build/Products/Debug-iphonesimulator/EventFinder.app
xcrun simctl launch booted demo.eventfinder
```
Turn on only "Rooftop": the list shows events at the other venues. Then ask your agent, with the skill installed: *"I only follow Rooftop, but I see concerts at other venues. Verify and fix."*

`fix.diff` is the fix shown in the GIF (`git apply fix.diff` from this folder).

## How the GIF was made
- Recorded on 2026-11-17 with `xcrun simctl io booted recordVideo` on an iPhone 17 simulator (iOS 27), Xcode 27.2.
- The steps (reproduce, root cause, fix, re-verify) were done by Claude Code (Opus 5.5) in the maintainer's session, following the skill. The simulator footage is real and only sped up; the text panels summarise those steps and were written afterwards.
- The bug was planted on purpose and the agent had the skill's author in the loop, so the GIF shows the method, not a measured result. Measured results are in `evals/results/`.
