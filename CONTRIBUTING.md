# Contributing

## Prerequisites

First ensure you have the JDK installed:

```shell
brew install openjdk@17
```

and ensure you have 

### Build the SDK and Install Dependencies

The following command builds both the example app and SDK library:

```shell
./gradlew build
```

## Running tests

```shell
./gradlew :example:connectedAndroidTest
```