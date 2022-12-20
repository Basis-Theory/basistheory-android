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

### Set Build Configuration

The following properties are required at build time
- `com.basistheory.android.example.apiUrl`: Either the dev (default) or production Basis Theory API URL
- `com.basistheory.android.example.apiKey`: A Basis Theory API key with `token:create` permission and a `REVEAL` transform on the `/` container

These properties can be set in many ways, in order of priority (see [this reference](https://tomgregory.com/gradle-project-properties-best-practices/#Why_do_we_need_project_properties_in_a_Gradle_build)):
1. Within the `./local.properties` file (this is specific to our setup)
2. Gradle command line args provided with `-P`
3. Java system properties using `-D`
4. Environment variables specified with the `ORG_GRADLE_PROJECT_` prefix
5. User specific `gradle.properties` file at `~/.gradle/gradle.properties`
6. Module level `gradle.properties` files in the root of a submodule
7. Root level `./gradle.properties` file in the root of a repository

It is recommended that you define these properties within the `./local.properties` file for 
local development by copying the `./local.properties.example` file to `./local.properties` and
setting the desired values.

These properties will be injected at build time into the example app and will be accessible through 
the `BuildConfig` class within the `example` module.

### Build the SDK and Install Dependencies

The following command builds both the example app and SDK library:

```shell
./gradlew build -x test
```

Note: The command `./gradlew build` is also valid, but it runs all unit tests too.

## Running tests

You can run all tests locally by simply executing the command:

```shell
make verify
```

For more fine-grained control when running test suites, see the sections below.

### Unit Tests

Unit tests are written against the `lib` module using [Robolectric](https://robolectric.org/). 
To run unit tests, execute the command:

```shell
./gradlew test
```

### Acceptance Tests

Acceptance tests rely upon the `example` module to provide an Android app that uses the `lib`
module, simulating a real user of our Android Library. Acceptance tests are written using 
[Espresso](https://developer.android.com/training/testing/espresso) and rely upon an emulator being 
configured and running on your local machine. An emulator should come pre-installed with 
Android Studio, or you can follow [these instructions](https://developer.android.com/studio/run/managing-avds) 
to set up a new virtual device to run on your machine.

Once the emulator is booted and available, you can run Espresso tests through Android Studio or by
executing the command:

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
