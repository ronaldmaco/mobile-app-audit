#!/usr/bin/env bash
# Install mobile-app-audit for any agent that reads SKILL.md.
#
#   scripts/install.sh                 # user-wide: ~/.agents/skills (Codex, Cursor, Gemini CLI, OpenCode...)
#   scripts/install.sh --agent claude  # ~/.claude/skills (Claude Code without the plugin)
#   scripts/install.sh --agent copilot # ~/.copilot/skills (GitHub Copilot)
#   scripts/install.sh --agent gemini  # ~/.gemini/skills
#   scripts/install.sh --project DIR   # DIR/.agents/skills (one repo, shared with the team)
#   add --link to symlink instead of copy (follows updates of this clone)
#   add --force to replace a different skill that is also named mobile-app-audit
set -euo pipefail
SRC="$(cd "$(dirname "$0")/.." && pwd)/skills/mobile-app-audit"
DEST="$HOME/.agents/skills"; MODE=copy; FORCE=no
while [ $# -gt 0 ]; do
  case "$1" in
    --agent) case "$2" in
        claude) DEST="$HOME/.claude/skills";; copilot) DEST="$HOME/.copilot/skills";;
        gemini) DEST="$HOME/.gemini/skills";; codex|cursor|opencode|agents) DEST="$HOME/.agents/skills";;
        *) echo "Unknown agent: $2"; exit 1;; esac; shift 2;;
    --project) DEST="$(cd "$2" && pwd)/.agents/skills"; shift 2;;
    --link) MODE=link; shift;;
    --force) FORCE=yes; shift;;
    -h|--help) sed -n '2,11p' "$0"; exit 0;;
    *) echo "Unknown option: $1"; exit 1;;
  esac
done

# Another project also ships a skill named mobile-app-audit. Every version of this one names its
# author in SKILL.md, so a folder without that line is a different skill: never replace it silently.
ours() { grep -q 'ronaldmaco' "$1/SKILL.md" 2>/dev/null; }

mkdir -p "$DEST"
TARGET="$DEST/mobile-app-audit"
if [ -e "$TARGET" ] && ! ours "$TARGET" && [ "$FORCE" != yes ]; then
  echo "Stopped: $TARGET holds a different skill that is also named mobile-app-audit."
  echo "Both cannot live in the same folder. Keep it, or replace it with: $0 --force (plus your other options)."
  exit 2
fi
rm -rf "$TARGET"
if [ "$MODE" = link ]; then ln -s "$SRC" "$TARGET"; else cp -R "$SRC" "$TARGET"; fi
echo "Installed ($MODE): $TARGET"

# Several agents read more than one folder (Cursor reads ~/.agents, ~/.claude and ~/.codex).
# Two copies with the same name can load an old version. Warn about the others.
for d in "$HOME/.agents/skills" "$HOME/.claude/skills" "$HOME/.codex/skills" "$HOME/.copilot/skills" "$HOME/.gemini/skills" "$HOME/.cursor/skills" "$HOME/.config/opencode/skills"; do
  other="$d/mobile-app-audit"
  [ "$other" = "$TARGET" ] && continue
  [ -e "$other" ] || continue
  if ours "$other"; then
    echo "Warning: another copy exists at $other. Keep one copy per agent to avoid loading an old version."
  else
    echo "Note: $other is a different skill with the same name. An agent that reads both folders may load either one."
  fi
done
v=$(grep -m1 '"version"' "$(dirname "$SRC")/../.claude-plugin/plugin.json" | sed 's/[^0-9.]//g')
echo "Version $v. Restart your agent, then ask it to audit or verify a bug in your app."
