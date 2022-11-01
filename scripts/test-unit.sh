#!/bin/bash
set -e

current_directory="$PWD"

cd $(dirname $0)/..

echo "Running unit tests..."

./gradlew test

result=$?

cd "$current_directory"

exit $result
