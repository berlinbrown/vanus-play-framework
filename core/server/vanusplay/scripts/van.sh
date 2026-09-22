#!/usr/bin/env bash
set -Eeuo pipefail

# Keep the caller's working directory unchanged so relative document roots remain intuitive.
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd -P)"
DEFAULT_PROJECT_DIR="$(cd -- "$SCRIPT_DIR/.." && pwd -P)"

project_dir="${VANUS_PROJECT_DIR:-$DEFAULT_PROJECT_DIR}"
jar_path="${VANUS_JAR:-}"
java_bin="${JAVA_BIN:-java}"
build_jar=false
declare -a server_args=()

usage() {
  cat <<'EOF'
Usage: scripts/van.sh [launcher options] [server options]

Launcher options:
  --project-dir PATH       Vanus project directory
  --jar PATH               Executable Vanus assembly JAR
  --java PATH              Java executable or command name
  --build                  Clean, test, and build the deployment JAR first
  --doc-root PATH          Document root; may be repeated
  --messages-url URL       Vanus messages JSON endpoint
  --messages-token TOKEN   Token for the messages endpoint
  --help                    Show this help
  --                        Forward all remaining arguments unchanged

Other arguments are forwarded to WebServerMain, including -p, --host, --quiet,
--cors, --dir-listing, --rate-limit, and the connection/resource limit options.

Environment defaults:
  VANUS_PROJECT_DIR, VANUS_JAR, JAVA_BIN, VANUS_DOC_ROOT,
  VANUS_MESSAGES_URL, VANUS_MESSAGES_TOKEN

Examples:
  scripts/van.sh --doc-root ./public -p 8080
  scripts/van.sh --jar /opt/vanus/vanusplay.jar --doc-root /srv/vanus/public
  scripts/van.sh --build --messages-url http://127.0.0.1:8086/_vanus-ops-manage/_vanus-bot-messages --messages-token TOKEN
EOF
}

need_value() {
  if (($# < 2)) || [[ -z "$2" ]]; then
    printf 'van.sh: missing value for %s\n' "$1" >&2
    exit 2
  fi
}

while (($#)); do
  case "$1" in
    --project-dir)
      need_value "$@"; project_dir="$2"; shift 2 ;;
    --jar)
      need_value "$@"; jar_path="$2"; shift 2 ;;
    --java)
      need_value "$@"; java_bin="$2"; shift 2 ;;
    --build)
      build_jar=true; shift ;;
    --doc-root)
      need_value "$@"; server_args+=(--dir "$2"); shift 2 ;;
    --messages-url|--messages-token)
      need_value "$@"; server_args+=("$1" "$2"); shift 2 ;;
    --help|-\?)
      usage; exit 0 ;;
    --)
      shift; server_args+=("$@"); break ;;
    *)
      server_args+=("$1"); shift ;;
  esac
done

if [[ ! -d "$project_dir" ]]; then
  printf 'van.sh: project directory does not exist: %s\n' "$project_dir" >&2
  exit 2
fi
project_dir="$(cd -- "$project_dir" && pwd -P)"

if [[ -z "$jar_path" ]]; then
  jar_path="$project_dir/target/scala-3.8.4/vanusplay-assembly.jar"
elif [[ "$jar_path" != /* ]]; then
  jar_path="$PWD/$jar_path"
fi

if [[ -n "${VANUS_DOC_ROOT:-}" ]]; then
  server_args=(--dir "$VANUS_DOC_ROOT" "${server_args[@]}")
fi
if [[ -n "${VANUS_MESSAGES_URL:-}" ]]; then
  server_args=(--messages-url "$VANUS_MESSAGES_URL" "${server_args[@]}")
fi
if [[ -n "${VANUS_MESSAGES_TOKEN:-}" ]]; then
  server_args=(--messages-token "$VANUS_MESSAGES_TOKEN" "${server_args[@]}")
fi

if [[ "$build_jar" == true ]]; then
  if ! command -v sbt >/dev/null 2>&1; then
    printf 'van.sh: sbt is required for --build\n' >&2
    exit 127
  fi
  (cd -- "$project_dir" && sbt clean test assembly)
fi

if [[ ! -r "$jar_path" ]]; then
  printf 'van.sh: assembly JAR not found or unreadable: %s\n' "$jar_path" >&2
  printf 'Run with --build or provide --jar PATH.\n' >&2
  exit 2
fi

if [[ "$java_bin" == */* ]]; then
  if [[ ! -x "$java_bin" ]]; then
    printf 'van.sh: Java executable is not executable: %s\n' "$java_bin" >&2
    exit 126
  fi
elif ! command -v "$java_bin" >/dev/null 2>&1; then
  printf 'van.sh: Java command not found: %s\n' "$java_bin" >&2
  exit 127
fi

java_version_output="$("$java_bin" -version 2>&1)" || {
  printf 'van.sh: unable to run Java: %s\n' "$java_bin" >&2
  exit 126
}
java_major="$(sed -nE 's/.*version "([0-9]+).*/\1/p' <<<"$java_version_output" | head -n 1)"
if [[ ! "$java_major" =~ ^[0-9]+$ ]] || ((java_major < 21)); then
  printf 'van.sh: Java 21 or newer is required; detected:\n%s\n' "$java_version_output" >&2
  exit 1
fi

exec "$java_bin" -jar "$jar_path" "${server_args[@]}"
