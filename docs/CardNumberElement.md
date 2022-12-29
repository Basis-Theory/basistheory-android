# CardNumberElement

The `CardNumberElement` can be used to securely collect credit card numbers within your application.
This element will render a text input within your view to accept a card number entered by your 
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

This element can be referenced directly when tokenizing through the
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

The element serves as a reference that can only be resolved back to the
original value by the `BasisTheoryElements` service when tokenizing. Your application is not given
direct access to the underlying plaintext value.

## Configuration

The `CardNumberElement` extends the `TextElement` view, so all
properties and attributes supported by [TextElement](/docs/TextElement.md) are also supported here.

By default, this element is configured with:

- The keyboard is configured to only allow numeric input
- A [mask](/docs/TextElement.md/#masks) is applied to format input values according to the [card brand](#card-brands)
- The [LuhnValidator](/docs/TextElement.md/#validators) is applied to restrict input to [Luhn-valid](https://en.wikipedia.org/wiki/Luhn_algorithm) cards

This component fully supports the same [style customizations](/docs/Styling.md) to match your branding
that are supported on the base [TextElement](/docs/TextElement.md).

## Card Brands

The first digits of the card number are analyzed to determine the card brand, which is made available 
to your application via the property:

| Name         | Type           | Description                                            |
|--------------|----------------|--------------------------------------------------------|
| cardMetadata | `CardMetadata` | Gets metadata about the card, derived from user input. |

### CardMetadata

| Name       | Type      | Description                                           |
|------------|-----------|-------------------------------------------------------|
| brand      | `String`  | The card [brand identifier](#brand-identifiers)       |
| cardMask   | `String`  | The card number mask corresponding to this card brand |
| cvcMask    | `String`  | The CVC mask corresponding to this card brand         |
| isComplete | `Boolean` | Whether this card number is complete for this brand   |

### ChangeEvent

The card brand is also included within [ChangeEvents](/docs/Events.md) published by the `CardNumberElement`
within an [EventDetails](/docs/Events.md/#eventdetails) record of the form:

| Property | Type     | Value                |
|----------|----------|----------------------|
| type     | `String` | `cardBrand`          |
| message  | `String` | The brand identifier |

### Brand Identifiers

- `visa`
- `mastercard`
- `americanExpress`
- `dinersClub`
- `discover`
- `jcb`
- `unionPay`
- `maestro`
- `elo`
- `mir`
- `hiper`
- `hipercard`