MAKEFLAGS += --silent

verify:
	./scripts/verify.sh

emulator-sync-clock:
	./scripts/emulator-sync-clock.sh
