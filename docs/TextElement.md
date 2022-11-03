# TextElement

The `basistheory-android` SDK contains the `TextElement` view to collect text data within your app.
This component allows you to fully customize the look and feel to match your brand, but it does 
not allow direct access the underlying values entered by a user, keeping your mobile application 
out of compliance scope.

## Supported XML Attributes

The `TextElement` extends the native [FrameLayout](https://developer.android.com/reference/android/widget/FrameLayout) 
view, so all standard layout-related XML attributes supported by 
[FrameLayout](https://developer.android.com/reference/android/widget/FrameLayout#xml-attributes) 
are supported. 

The following additional attributes are also supported:

| Fields              | Type                     | Description                                                                                                                                                                                                                                      |
|---------------------|--------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| text                | `string`                 | Sets the text value for the element. Note that a getter is not exposed on the TextElement to retrieve the underlying text value.                                                                                                                 |
| textColor           | `reference`&vert;`color` | The text color. <br/>May be a reference to another resource, in the form "@[+][package:]type/name" or a theme attribute in the form "?[package:]type/name". <br/>May be a color value, in the form of "#rgb", "#argb", "#rrggbb", or "#aarrggbb" |
| hint                | `string`                 | Placeholder text to display within the element.                                                                                                                                                                                                  |
| removeDefaultStyles | `boolean`                | Removes the default Android styling on the underlying EditText.                                                                                                                                                                                  |

## Basic Usage

To use the `TextElement` within your Android application, simply include the view within one of your 
Android application’s layouts.

```kotlin
<LinearFLayout
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
