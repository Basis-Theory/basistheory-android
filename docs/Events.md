# Elements Events

Each Element type supports a standard set of events that can be subscribed to in order to receive
callbacks within your app.


## ChangeEvent

Raised whenever the element's value is changed.

### Schema

| Property | Description                                                              |
|----------|--------------------------------------------------------------------------|
| complete | If the element value is well-formed and is ready to be submitted.        |
| empty    | Whether the element is empty.                                            |
| errors   | This an array of error objects that are emitted for any set validations. |

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