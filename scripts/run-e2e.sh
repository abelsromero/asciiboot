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
  loops=0
  wait_time=1
  timeout_seconds=10
  while (( loops*wait_time < timeout_seconds)); do
    [[ "$(curl "http://localhost:8080/actuator/health/readiness" | jq -r '.status')" == "UP" ]] && return 0
    loops=$((loops+1))
    sleep "$wait_time"
  done
  if (( loops*wait_time > timeout_seconds )); then
    echo "[ERROR] Could not start server"
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
