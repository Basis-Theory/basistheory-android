# CardNumberElement

The `CardNumberElement` can be used to securely collect credit card numbers within your app.
This element will render a text input within your view to accept the card number entered by your 
end users.

This element was designed to be used in conjunction with the [CardExpirationDateElement](/docs/CardExpirationDateElement.md)
and [CardVerificationCodeElement](/docs/CardVerificationCodeElement.md) to collect and tokenize a
[card](https://docs.basistheory.com/#token-types-card) token.

## Basic Usage

To use the `CardNumberElement` within your Android application, simply include the view
within one of your Android application’s layouts.

```xml
<com.basistheory.android.view.CardNumberElement
    android:id="@+id/card_number"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

The `month()` and `year()` values can be referenced separately when tokenizing through the
[BasisTheoryElements](/docs/BasisTheoryElements.md) service class:

```kotlin
val cardNumberElement = findViewById<CardNumberElement>(R.id.card_number)
...

val tokenizeResponse = bt.tokenize(object {
    val type = "card"
    val data = object {
        val number = cardNumberElement
        val expiration_month = cardExpirationDateElement.month()
        val expiration_year = cardExpirationDateElement.year()
        val cvc = cardVerificationCodeElement
    }
})
```

The element view serves as a reference object that can only be resolved back to the
original value by the `BasisTheoryElements` service when tokenizing. Your application is not given
direct access to the underlying plaintext value.

## Configuration

The `CardNumberElement` extends the `TextElement` view, so all
properties and attributes supported by [TextElement](/docs/TextElement.md) are also supported here.

By default, this element is configured with:

- The keyboard is configured to only allow numeric input
- The first digits of the card are analyzed to determine the card brand
- A [mask](/docs/TextElement.md/#masks) is applied to format input values according to the card brand
- The [FutureDateValidator](/docs/TextElement.md/#validators) is applied to restrict input to future dates

This component fully supports the same [style customizations](/docs/Styling.md) to match your brand
that are supported on the base [TextElement](/docs/TextElement.md).
