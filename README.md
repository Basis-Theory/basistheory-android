# Basis Theory Android SDK

The [Basis Theory](https://basistheory.com) Android SDK makes it easy to securely collect sensitive 
data within your Android applications while keeping your mobile app out of compliance scope. 
[Elements](https://docs.basistheory.com/elements) are secure inputs that enable you to collect
sensitive data within your app and securely store this data within Basis Theory’s certified 
token vault.

Sensitive data entered by your end users into Elements can be indirectly referenced within API
calls, enabling you to tokenize this data without needing to directly marshall or access the raw
sensitive data. Your application code will only receive access to the created tokens, which can 
safely be sent to your backend systems and persisted for later use.

Get started with our [integration guides](https://developers.basistheory.com/) or browse the 
[SDK docs](https://docs.basistheory.com/elements).

## Installation

### Requirements

- Android 5.0+ (API level 21+)
- AndroidX

### Gradle

Add this dependency to your project's build file:

```groovy
  repositories {
      maven { url 'https://jitpack.io' }
  }

  dependencies {
      implementation 'com.github.basis-theory.basistheory-android:lib:0.11.0'
  }
```

## Features

- [TextElement](docs/TextElement.md) to securely collect text input
- [BasisTheoryElements](docs/BasisTheoryElements.md) service to tokenize sensitive data entered into Elements
- [Events](docs/Events.md) - subscribe to events raised by Elements

## Example Usage

A full example Android app can be viewed within the [example](example) module within this repo.

```kotlin
val nameElement = findViewById(R.id.name)
val phoneNumberElement = findViewById(R.id.phoneNumber)

val bt = BasisTheoryElements.builder()
    .apiUrl(BuildConfig.BASIS_THEORY_API_URL)
    .apiKey(BuildConfig.BASIS_THEORY_API_KEY)
    .build()

runBlocking {
    val tokenizeResponse = bt.tokenize(object {
        val type = "token"
        val data = object {
            val name = nameElement
            val phoneNumber = phoneNumberElement
        }
    })
}
```

Relevant highlights include:
- [MainActivity.kt](example/src/main/java/com/basistheory/example/MainActivity.kt)
- [MainActivity.xml](example/src/main/res/layout/activity_main.xml)
