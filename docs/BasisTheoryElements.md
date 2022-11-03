# BasisTheoryElements

This is a static class containing services available to be used with elements.

## Instantiation

A new instance of the `BasisTheoryElements` service can be instantiated and configured using the
builder pattern, for example:

```kotlin
val bt = BasisTheoryElements.builder()
    .apiUrl("https://api.basistheory.com")
    .apiKey(myApiKey)
    .ioDispatcher(myDispatcher)
    .build()
```

The following values are used by default if not specified when constructing an instance of the service:
- `apiUrl`: `https://api.basistheory.com`
- `apiKey`: `null`
- `ioDispatcher`: `Dispatchers.IO`

## Usage

### tokenize

This function wraps our **`[tokenize API](https://docs.basistheory.com/#tokenize)`** providing added
support for referencing instances of Elements within your request payload. 

```kotlin
val tokenizeResponse = bt.tokenize(object {
    val type = "token"
    val data = object {
        val name = nameElement // an instance of TextElement
        val phoneNumber = phoneNumberElement // an instance of TextElement
        val note = "Non sensitive value" // plaintext strings can also be included in the token body
    }
    val expires_at = "2022-11-03T19:14:21.4333333Z" // all standard token attributes are supported 
})
```

As you can see from this example, the `tokenize` function is capable of resolving the raw data 
from references to Element inputs. This enables your app to tokenize sensitive data values without 
needing to touch the raw data directly.
