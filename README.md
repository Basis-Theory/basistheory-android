# Basis Theory Android SDK

The [Basis Theory](https://basistheory.com) Android SDK makes it easy to securely collect sensitive 
data within your Android applications while keeping your mobile app out of compliance scope. 
[Elements](https://docs.basistheory.com/elements) are secure inputs that enable you to collect
sensitive data within your app and securely store this data within Basis Theory’s certified 
token vault.

Sensitive data entered into elements by your end users can be indirectly referenced within API
calls, enabling you to tokenize this data without needing to directly marshall or access raw
sensitive data. Your application code will only receive access to the created tokens, which can 
safely be sent to your backend systems and persisted for later use.

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
- [CardVerificationCodeElement](docs/CardVerificationCodeElement.md) to securely collect card verification codes
- [BasisTheoryElements](docs/BasisTheoryElements.md) service to tokenize sensitive data entered into Elements
- [Styling](docs/Styling.md) - custom styles and branding are fully supported
- [Events](docs/Events.md) - subscribe to events raised by Elements

## Example Usage

Simply include one or more elements within your app's views:

```xml
<com.basistheory.android.view.CardNumberElement
    android:id="@+id/card_number"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

<com.basistheory.android.view.CardExpirationDateElement
    android:id="@+id/expiration_date"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

<com.basistheory.android.view.CardVerificationCodeElement
    android:id="@+id/cvc"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

Then tokenize the user input by referencing these elements. This can be wired up in response to a 
button click, or any other user action. 

```kotlin
val cardNumberElement = findViewById(R.id.card_number)
val cardExpirationDateElement = findViewById(R.id.expiration_date)
val cardVerificationCodeElement = findViewById(R.id.cvc)

val bt = BasisTheoryElements.builder()
    .apiKey(myPublicApiKey)
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
    
    // send the tokens within tokenizeResponse to your backend
}
```

Note that the Android SDK requires the use of a public API key during initialization 
(an API key issued to a `public` [Application](https://developers.basistheory.com/concepts/access-controls/#what-are-applications)).
Click [here](https://portal.basistheory.com/applications/create?permissions=token%3Acreate&type=public.) 
to create one in the Basis Theory portal.

A full example Android app is included within the [example](example) module within this repo.
