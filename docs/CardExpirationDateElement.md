# CardExpirationDateElement

The `CardExpirationDateElement` can be used to securely collect credit card expiration dates within
your app. This element will render a text input within your view that accepts input in the form `MM/yy`.

This element was designed to be used in conjunction with the [CardNumberElement](/docs/CardNumberElement.md)
and [CardVerificationCodeElement](/docs/CardVerificationCodeElement.md) to collect and tokenize a
[card](https://docs.basistheory.com/#token-types-card) token.

## Basic Usage

To use the `CardExpirationDateElement` within your Android application, simply include the view 
within one of your Android application’s layouts.

```xml
<com.basistheory.android.view.CardExpirationDateElement
    android:id="@+id/expiration_date"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

The month and year values can be referenced separately when tokenizing through the 
[BasisTheoryElements](/docs/BasisTheoryElements.md) service class:

```kotlin
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

The `month()` and `year()` functions expose a reference object that can only be resolved back to the 
original values by the `BasisTheoryElements` service when tokenizing. These methods do not 
provide your application with direct access to the underlying plaintext values. 

## Configuration

The `CardExpirationDateElement` extends the `TextElement` view, so all
properties and attributes supported by [TextElement](/docs/TextElement.md) are also supported here.

By default, this element is configured with:

- The keyboard is configured to only allow numeric input
- A [mask](/docs/TextElement.md/#masks) is applied to format input values as `MM/yy`
- The [FutureDateValidator](/docs/TextElement.md/#validators) is applied to restrict input to future dates

This component fully supports the same [style customizations](/docs/Styling.md) to match your branding
that are supported on the base [TextElement](/docs/TextElement.md).
