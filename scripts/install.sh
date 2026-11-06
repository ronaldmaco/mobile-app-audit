#!/usr/bin/env bash
# Install mobile-app-audit for any agent that reads SKILL.md.
#
#   scripts/install.sh                 # user-wide: ~/.agents/skills (Codex, Cursor, Gemini CLI, OpenCode...)
#   scripts/install.sh --agent claude  # ~/.claude/skills (Claude Code without the plugin)
#   scripts/install.sh --agent copilot # ~/.copilot/skills (GitHub Copilot)
#   scripts/install.sh --agent gemini  # ~/.gemini/skills
#   scripts/install.sh --project DIR   # DIR/.agents/skills (one repo, shared with the team)
#   add --link to symlink instead of copy (follows updates of this clone)
set -euo pipefail
SRC="$(cd "$(dirname "$0")/.." && pwd)/skills/mobile-app-audit"
DEST="$HOME/.agents/skills"; MODE=copy
while [ $# -gt 0 ]; do
  case "$1" in
    --agent) case "$2" in
        claude) DEST="$HOME/.claude/skills";; copilot) DEST="$HOME/.copilot/skills";;
        gemini) DEST="$HOME/.gemini/skills";; codex|cursor|opencode|agents) DEST="$HOME/.agents/skills";;
        *) echo "Unknown agent: $2"; exit 1;; esac; shift 2;;
    --project) DEST="$(cd "$2" && pwd)/.agents/skills"; shift 2;;
    --link) MODE=link; shift;;
    -h|--help) sed -n '2,10p' "$0"; exit 0;;
    *) echo "Unknown option: $1"; exit 1;;
  esac
done

mkdir -p "$DEST"
TARGET="$DEST/mobile-app-audit"
rm -rf "$TARGET"
if [ "$MODE" = link ]; then ln -s "$SRC" "$TARGET"; else cp -R "$SRC" "$TARGET"; fi
echo "Installed ($MODE): $TARGET"

# Several agents read more than one folder (Cursor reads ~/.agents, ~/.claude and ~/.codex).
# Two copies with the same name can load an old version. Warn about the others.
for d in "$HOME/.agents/skills" "$HOME/.claude/skills" "$HOME/.codex/skills" "$HOME/.copilot/skills" "$HOME/.gemini/skills" "$HOME/.cursor/skills" "$HOME/.config/opencode/skills"; do
  other="$d/mobile-app-audit"
  [ "$other" = "$TARGET" ] && continue
  [ -e "$other" ] && echo "Warning: another copy exists at $other. Keep one copy per agent to avoid loading an old version."
done
v=$(grep -m1 '"version"' "$(dirname "$SRC")/../.claude-plugin/plugin.json" | sed 's/[^0-9.]//g')
echo "Version $v. Restart your agent, then ask it to audit or verify a bug in your app."
