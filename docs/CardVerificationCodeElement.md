# CardVerificationCodeElement

The `CardVerificationCodeElement` view can be used to collect any text data within your mobile app.
This component allows you to fully customize the look and feel to match your brand, but it does 
not allow direct access the underlying values entered by a user, keeping your mobile application 
out of compliance scope.

## Basic Usage

To use the `TextElement` within your Android application, simply include the view within one of your
Android application’s layouts.

```kotlin
<com.basistheory.android.TextElement
    android:id="@+id/name"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

Properties can be programmatically initialized within your views within either the
`onCreate` or `onCreateView` lifecycle methods.

## Configuration

The `TextElement` extends the native [FrameLayout](https://developer.android.com/reference/android/widget/FrameLayout)
view, so all standard properties and attributes supported by
[FrameLayout](https://developer.android.com/reference/android/widget/FrameLayout#xml-attributes)
are supported by `TextElement`.

### Properties

The following additional properties are supported when programmatically interacting with a `TextElement`:

| Name                | Type                     | Description                                                                                                                                                                                                                                                            |
|---------------------|--------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| text                | `string`                 | Sets the text value for the element. Note that a getter is not exposed on the TextElement to retrieve the underlying text value.                                                                                                                                       |
| textColor           | `reference`&vert;`color` | The text color. <br/><br/>May be a reference to another resource, in the form `"@[+][package:]type/name"` or a theme attribute in the form `"?[package:]type/name"`. <br/><br/>May be a color value, in the form of `"#rgb"`, `"#argb"`, `"#rrggbb"`, or `"#aarrggbb"` |
| hint                | `string`                 | Placeholder text to display within the element when empty.                                                                                                                                                                                                             |
| removeDefaultStyles | `boolean`                | Removes the default Android styling on the underlying EditText.                                                                                                                                                                                                        |
| mask                | `ElementMask`            | Restricts and formats input entered into this Element. See [Masks](#masks) below for details.                                                                                                                                                                          |
| transform           | `ElementTransform`       | Transforms the value of this Element prior to tokenization. See [Transforms](#transforms) below for details.                                                                                                                                                           |
| validator           | `ElementValidator`       | Validates the value of this Element within ChangeEvents. See [Validators](#validators) below for details.                                                                                                                                                              |


### XML Attributes

The following additional XML attributes are also supported when defining a `TextElement` in a layout XML file:

| Name                | Type                     | Description                                                                                                                                                                                                                                                |
|---------------------|--------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| text                | `string`                 | Sets the text value for the element. Note that a getter is not exposed on the TextElement to retrieve the underlying text value.                                                                                                                           |
| textColor           | `reference`&vert;`color` | The text color. <br/><br/>May be a reference to another resource, in the form "@[+][package:]type/name" or a theme attribute in the form "?[package:]type/name". <br/><br/>May be a color value, in the form of "#rgb", "#argb", "#rrggbb", or "#aarrggbb" |
| hint                | `string`                 | Placeholder text to display within the Element.                                                                                                                                                                                                            |
| mask                | `string`                 | A string defining the [mask](#masks) applied to this Element, e.g. (`###-##-####`).                                                                                                                                                                        |
| removeDefaultStyles | `boolean`                | Removes the default Android styling on the underlying EditText.                                                                                                                                                                                            |

### Masks

Element masks enable user input to be restricted and formatted to meet a pre-defined format. A mask
can be defined programmatically using the `ElementMask` class, or in an XML layout file as a pattern `String`.

The `ElementMask` class supports two constructors:

1. `ElementMask(characterMasks: List<Any>)`: a list of character-wise values defining the allowable input for each character position. 
Each character position either defines a static value in the mask (specified as a `Char` or single 
character `String`) or a range of allowable characters (specified as a `Regex` object).

For example, to support US Social Security Numbers of the form `123-45-6789`, you can specify the
mask as:

```kotlin
val digit = Regex("""\d""")
element.mask = ElementMask(listOf(digit, digit, digit, "-", digit, digit, "-", digit, digit, digit, digit))
```

2. `ElementMask(pattern: String)`: specifies a range of allowable characters using the wildcard characters:
- `#`: numeric value, equivalent to `Regex("""\d""")`
- `x`: alphabetic value, equivalent to `Regex("[A-Za-z]")`
- `*`: any value, equivalent to `Regex(".")`

For example, to support US Social Security Numbers of the form `123-45-6789`, you can specify the
mask as:

```kotlin
element.mask = ElementMask("###-##-####")
```

### Transforms

Element transforms define functions to mutate the value of the Element prior to tokenization and 
when computing properties published within [ChangeEvents](/docs/Events.md).

The following types of transforms are supported:

#### RegexReplaceElementTransform

Replaces all matches of the given regular expression with the replacement text.

```kotlin
// removes all non-numeric characters
element.transform = RegexReplaceElementTransform(
    regex = Regex("[^\\d]"),
    replacement = ""
)
```

### Validators

Element validators define functions to determine whether the value of the Element is considered valid.
Validators are executed on the [transformed](#transforms) Element value.

The validation state of an Element is only used when computing an [ChangeEvents](/docs/Events.md)
and all actions taken in response to an invalid state (e.g. styling changes, 
displaying validation errors, disabling submit buttons) are expected to be implemented within your 
app in response to `ChangeEvents`. See [this example](/example/src/main/java/com/basistheory/android/example/viewmodel/CardFragmentViewModel.kt)
for one potential pattern for implementing validation within your app.

The following types of validators are supported:

#### RegexValidator

Matches the input value against the given regular expression.

```kotlin
// validates that the value only contains 3-4 digits
element.validator = RegexValidator(
    regex = Regex("^\\d{3,4}$")
)
```

#### LuhnValidator

Validates that the input value is a [Luhn-valid](https://en.wikipedia.org/wiki/Luhn_algorithm)
credit card number. This is the default validator for the [CardNumberElement](/docs/CardNumberElement.md).

```kotlin
element.validator = LuhnValidator()
```

#### FutureDateValidator

Validates that the input value is a future date of the form `MM/yy`. The default
credit card number. This is the default validator for the [CardExpirationDateElement](/docs/CardExpirationDateElement.md).

```kotlin
element.validator = FutureDateValidator()
```
