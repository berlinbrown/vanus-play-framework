#!/usr/bin/env bash
set -Eeuo pipefail
umask 027

# Production defaults. Override any value through the environment rather than
# editing this file on each host.
APP_NAME="${VANUS_APP_NAME:-vanus-server}"
APP_HOME="${VANUS_APP_HOME:-/opt/springboot/umbra/umbra}"
JAR_FILE="${VANUS_JAR:-$APP_HOME/vanusplay-assembly.jar}"
DOC_ROOT="${VANUS_DOC_ROOT:-$APP_HOME/data/server}"
LOG_FILE="${VANUS_LOG_FILE:-/var/log/umbra/vanus-server.boot.log}"

JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/temurin-21-jdk-amd64}"
JAVA_BIN="${JAVA_BIN:-$JAVA_HOME/bin/java}"
JAVA_XMS="${VANUS_JAVA_XMS:-110m}"
JAVA_XMX="${VANUS_JAVA_XMX:-240m}"

HOST="${VANUS_HOST:-127.0.0.1}"
PORT="${VANUS_PORT:-8087}"
RATE_LIMIT="${VANUS_RATE_LIMIT:-200}"
MAX_CONNECTIONS="${VANUS_MAX_CONNECTIONS:-256}"
MAX_IN_FLIGHT="${VANUS_MAX_IN_FLIGHT:-32}"
MESSAGES_URL="${VANUS_MESSAGES_URL:-http://127.0.0.1:8086/_vanus-ops-manage/_vanus-bot-messages}"
MESSAGES_TOKEN="${VANUS_MESSAGES_TOKEN:-dmFudXMtc2NhdHR5LW9wcy0yMDI2}"

fail() {
  printf '%s: %s\n' "$APP_NAME" "$*" >&2
  exit 1
}

[[ -x "$JAVA_BIN" ]] || fail "Java executable not found: $JAVA_BIN"
[[ -r "$JAR_FILE" ]] || fail "deployment JAR not readable: $JAR_FILE"
[[ -d "$DOC_ROOT" && -r "$DOC_ROOT" ]] || fail "document root not readable: $DOC_ROOT"

touch -- "$LOG_FILE" || fail "log file is not writable: $LOG_FILE"

export JAVA_HOME
export PATH="/bin:/usr/local/bin:/usr/bin:$JAVA_HOME/bin"

{
  printf '%s Starting %s\n' "$(date -u '+%Y-%m-%dT%H:%M:%SZ')" "$APP_NAME"
  printf 'Java: %s\n' "$JAVA_BIN"
  printf 'JAR: %s\nDocument root: %s\nListening: %s:%s\n' "$JAR_FILE" "$DOC_ROOT" "$HOST" "$PORT"
} >>"$LOG_FILE"

# Stay in the foreground so systemd or another service manager owns restarts,
# signals, and PID tracking. Extra server options may be supplied as arguments.
exec "$JAVA_BIN" \
  "-Xms$JAVA_XMS" \
  "-Xmx$JAVA_XMX" \
  -jar "$JAR_FILE" \
  --host "$HOST" \
  --port "$PORT" \
  --dir "$DOC_ROOT" \
  --rate-limit "$RATE_LIMIT" \
  --max-connections "$MAX_CONNECTIONS" \
  --max-in-flight "$MAX_IN_FLIGHT" \
  --messages-url "$MESSAGES_URL" \
  --messages-token "$MESSAGES_TOKEN" \
  "$@" \
  >>"$LOG_FILE" 2>&1
