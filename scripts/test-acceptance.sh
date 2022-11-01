#!/bin/bash
set -e

current_directory="$PWD"

cd $(dirname $0)/..

echo "Running acceptance tests..."

./gradlew connectedCheck

result=$?

cd "$current_directory"

exit $result
