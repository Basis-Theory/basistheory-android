#!/bin/bash
set -e

adb shell su root date -u @$(date +%s)