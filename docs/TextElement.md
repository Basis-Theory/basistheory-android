# TextElement

The `TextElement` view can be used to collect any text data within your mobile app.
This component allows you to fully customize the look and feel to match your brand, but it does 
not allow direct access the underlying values entered by a user, keeping your mobile application 
out of compliance scope.

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
| mask                | `List<Any>`              | Restricts and formats input entered into this Element. See [Masks](#masks) below for details.                                                                                                                                                                          |
| removeDefaultStyles | `boolean`                | Removes the default Android styling on the underlying EditText.                                                                                                                                                                                                        |


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

Element masks enable user input to be restricted and formatted. Masks accept a list of character-wise 
values defining the allowable input for each character position. Each character position either  
defines a static value in the mask (specified as a `Char` or single character `String`) or a 
range of allowable characters (specified as a `Regex` object).

Masks also support a less verbose alternative to specify a range of allowable characters using the 
wildcard characters, which may be preferable when defining a mask in XML layout files:

- `#`: numeric value, equivalent to `Regex("""\d""")`
- `x`: alphabetic value, equivalent to `Regex("[A-Za-z]")`
- `*`: any value, equivalent to `Regex(".")`

For example, to mask a US Social Security Number of the form `123-45-6789`, you can specify the 
mask in either of the following formats:

```kotlin
val digit = Regex("""\d""")
element.mask = listOf(digit, digit, digit, "-", digit, digit, "-", digit, digit, digit, digit)
```

or

```kotlin
element.mask = Mask("###-##-####")
```

## Basic Usage

To use the `TextElement` within your Android application, simply include the view within one of your 
Android application’s layouts.

```kotlin
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.basistheory.android.TextElement
        android:id="@+id/name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</LinearLayout>
```

![Default Text Element](/docs/img/text_element_default.png)

## Styling Customization

The `TextElement` has been designed to take advantage of all pre-existing native layout properties
available on a [FrameLayout](https://developer.android.com/reference/android/widget/FrameLayout), 
as well as several additional styling customizations typically available to a native
[EditText](https://developer.android.com/reference/android/widget/EditText) view.

### Styling in XML

```kotlin
<com.basistheory.android.TextElement
    android:id="@+id/name"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/my_custom_background"
    android:padding="5dp"
    bt:text="Initial Value"
    bt:hint="Placeholder"
    bt:removeDefaultStyles="true"
    bt:textColor="@color/purple_200" />
```

### Styling Programmatically

```swift
val textElement = findViewById<TextElement>(R.id.myTextElement)
textElement.setPadding(5, 5, 5, 5)
textElement.hint = "Placeholder"
textElement.textColor = Color.CYAN
textElement.background = Color.WHITE.toDrawable()
```

![Styled Text Element](/docs/img/text_element_styled.png)
