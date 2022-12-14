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
- [CardNumberElement](docs/CardNumberElement.md) to securely collect credit card numbers
- [CardExpirationDateElement](docs/CardExpirationDateElement.md) to securely collect card expiration dates
- [CardVerificationCodeElement](docs/CardVerificationCode.md) to securely collect card verification codes
- [BasisTheoryElements](docs/BasisTheoryElements.md) service to tokenize sensitive data entered into Elements
- [Styling](docs/Styling.md) - custom styles and branding are fully supported
- [Events](docs/Events.md) - subscribe to events raised by Elements

## Example Usage

A full example Android app can be viewed within the [example](example) module within this repo.

For example, the following code collects and tokenizes a credit card:

```xml
<com.basistheory.android.view.CardNumberElement
    android:id="@+id/card_number"
    ...
/>
<com.basistheory.android.view.CardExpirationDateElement
    android:id="@+id/expiration_date"
    ...
/>
<com.basistheory.android.view.CardVerificationCodeElement
    android:id="@+id/cvc"
    ...
/>
```

```kotlin
val cardNumberElement = findViewById(R.id.card_number)
val cardExpirationDateElement = findViewById(R.id.expiration_date)
val cardVerificationCodeElement = findViewById(R.id.cvc)

val bt = BasisTheoryElements.builder()
    .apiUrl(BuildConfig.BASIS_THEORY_API_URL)
    .apiKey(BuildConfig.BASIS_THEORY_API_KEY)
    .build()

runBlocking {
    val tokenizeResponse = bt.tokenize(object {
        val type = "card"
        val data = object {
            val number = cardNumberElement
            val expiration_month = cardExpirationDateElement.month()
            val expiration_year = cardExpirationDateElement.year()
            val cvc = cardVerificationCodeElement
        }
    })
}
```

For more detailed usage examples, please refer to the [example app](example).