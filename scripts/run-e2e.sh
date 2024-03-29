#!/usr/bin/env bash

set -uo pipefail

readonly PROJECT_VERSION="0.0.1-SNAPSHOT"

ASCIIBOOT_PID=""

build() {
  ./gradlew bootJar
}

server_start() {
  java -jar "build/libs/asciiboot-${PROJECT_VERSION}.jar" &
  ASCIIBOOT_PID=$!
  # wait until ready
  local loops=0
  local wait_time=3
  local timeout_ticks=10
  while (( loops*wait_time < timeout_ticks)); do
    server_status="$(curl "http://localhost:8080/actuator/health/readiness" | jq -r '.status')"
    [[ "$server_status" == "UP" ]] && return 0
    sleep "$wait_time"
    loops=$((loops+1))
  done
  if (( loops*wait_time >= timeout_ticks )); then
    echo "[ERROR] Could not start server"
    exit 1
  fi
}

server_stop() {
  kill "$ASCIIBOOT_PID"
}

test() {
  ./gradlew e2eTest
}

main() {
  # https://github.com/marketplace/actions/yet-another-setup-jq
  build
  server_start
  test
  server_stop
}

main
