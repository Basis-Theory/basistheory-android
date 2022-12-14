# Styling

Elements have been designed to take advantage of all pre-existing native layout properties
available on a [FrameLayout](https://developer.android.com/reference/android/widget/FrameLayout),
as well as several additional styling customizations typically available to a native
[EditText](https://developer.android.com/reference/android/widget/EditText) view.

## Styling in XML

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

See [TextElement](/docs/TextElement.md/#xml-attributes) for an exhaustive list of supported XML attributes.

## Styling Programmatically

```kotlin
val textElement = binding.myTextElement // or findViewById<TextElement>(R.id.myTextElement)
textElement.setPadding(5, 5, 5, 5)
textElement.hint = "Placeholder"
textElement.textColor = Color.CYAN
textElement.background = Color.WHITE.toDrawable()
```

See [TextElement](/docs/TextElement.md/#properties) for an exhaustive list of supported properties.

## Examples

### Default

![Default Text Element](/docs/img/text_element_default.png)

### Custom Background

![Styled Text Element](/docs/img/text_element_styled.png)


![Default Text Element](/docs/img/text_element_default.png)
