# Elements Events

Each Element type supports a standard set of events that can be subscribed to in order to receive
callbacks within your app.

## ChangeEvent

Raised whenever the element's value is changed.

### Schema

| Property   | Type                 | Description                                                               |
|------------|----------------------|---------------------------------------------------------------------------|
| isComplete | `Boolean`            | Whether the element has been completely filled (as specified by the mask) |
| isEmpty    | `Boolean`            | Whether the element is empty.                                             |
| isValid    | `Boolean`            | This an array of error objects that are emitted for any set validations.  |
| details    | `List<EventDetails>` |                                                                           |

### EventDetails

| Property | Type     | Description                                        |
|----------|----------|----------------------------------------------------|
| type     | `String` | The type of data represented by this detail object |
| message  | `String` | The content of this detail object                  |

### Usage

```kotlin
myElement.addChangeEventListener {
    // handle event
}
```

## FocusEvent

Raised whenever the element receives focus.

### Schema

*Empty*

### Usage

```kotlin
myElement.addFocusEventListener {
    // handle event
}
```

**BlurEvent**

Raised whenever the element loses focus.

### Schema

*Empty*

### Usage

```kotlin
myElement.addBlurEventListener {
    // handle event
}
```