#!/bin/bash
set -e

current_directory="$PWD"

cd $(dirname $0)

time {
    ./emulator-sync-clock.sh
    ./test-unit.sh
    ./test-acceptance.sh
}

cd "$current_directory"
