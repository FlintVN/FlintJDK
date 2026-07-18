#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
ROOT="$(cd -- "$SCRIPT_DIR/.." && pwd -P)"
SOURCE_ROOT="$ROOT/src"
OUTPUT_ROOT="$ROOT/bin"
RUN_ROOT="$OUTPUT_ROOT/run"
DEV_ROOT="$OUTPUT_ROOT/dev"

if [[ "${1:-}" == "--clean" ]]; then
  rm -rf -- "$OUTPUT_ROOT"
  echo 'Clean complete.'
  exit 0
fi
[[ $# -eq 0 ]] || { echo "Usage: $0 [--clean]" >&2; exit 2; }

for tool in javac jar; do
  command -v "$tool" >/dev/null 2>&1 || { echo "$tool was not found in PATH. Install JDK 17 or newer." >&2; exit 1; }
done

JAVAC_VERSION="$(javac -version 2>&1)"
JAVAC_MAJOR="$(printf '%s' "$JAVAC_VERSION" | sed -E 's/.* ([0-9]+).*/\1/')"
if [[ ! "$JAVAC_MAJOR" =~ ^[0-9]+$ ]] || (( JAVAC_MAJOR < 17 )); then
  echo "JDK 17 or newer is required; found: $JAVAC_VERSION" >&2
  exit 1
fi

MODULES=()
while IFS= read -r module; do MODULES+=("$module"); done < <(
  find "$SOURCE_ROOT" -mindepth 2 -maxdepth 2 -name module-info.java -print \
    | sed -E 's|/module-info\.java$||; s|.*/||' | sort
)
(( ${#MODULES[@]} > 0 )) || { echo "No Java modules found under: $SOURCE_ROOT" >&2; exit 1; }
MODULE_LIST="$(IFS=,; echo "${MODULES[*]}")"

rm -rf -- "$OUTPUT_ROOT"
mkdir -p -- "$RUN_ROOT" "$DEV_ROOT"

echo "Building runtime modules: $MODULE_LIST"
javac -Xlint:all -XDstringConcat=inline --release 17 -encoding UTF-8 \
  -d "$RUN_ROOT" --module "$MODULE_LIST" --module-source-path "$SOURCE_ROOT"

echo "Building development modules: $MODULE_LIST"
javac -g -Xlint:all -XDstringConcat=inline --release 17 -encoding UTF-8 \
  -d "$DEV_ROOT" --module "$MODULE_LIST" --module-source-path "$SOURCE_ROOT"

for module in "${MODULES[@]}"; do
  module_manifest="$ROOT/META-INF/$module.MF"
  [[ -f "$module_manifest" ]] || { echo "Module manifest not found: $module_manifest" >&2; exit 1; }
  jar cf0m "$RUN_ROOT/$module.jar" "$module_manifest" -C "$RUN_ROOT/$module" .
  cp -R -- "$SOURCE_ROOT/$module" "$DEV_ROOT/$module/src"
  jar cfm "$DEV_ROOT/$module.jar" "$module_manifest" -C "$DEV_ROOT/$module" .
done

echo "Build complete: $OUTPUT_ROOT"
