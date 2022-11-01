# Contributing

## Prerequisites

First ensure you have the JDK installed:

```shell
brew install openjdk@17
```

Next, ensure you have the Android SDK installed, which can be installed with [Android Studio](https://developer.android.com/about/versions/12/setup-sdk).
You will need to have the `adb` CLI available in your PATH, which can be done after installing
Android Studio by adding the following to your `~/.zshrc` or `~/.bash_profile`:

```shell
export PATH=~/Library/Android/sdk/tools:$PATH
export PATH=~/Library/Android/sdk/platform-tools:$PATH
```

### Build the SDK and Install Dependencies

The following command builds both the example app and SDK library:

```shell
./gradlew build
```

## Running tests

To run Espresso tests on a local Android emulator, run:

```shell
./gradlew connectedCheck
```

## Troubleshooting

If you receive a 400 error while tokenizing within the example app due to `'expires_at' must be a future datetime`,
it is likely due to skew between your local system clock and the Android emulator. To resync the clock
on the emulator with your local system, run:

```shell
make emulator-sync-clock
```