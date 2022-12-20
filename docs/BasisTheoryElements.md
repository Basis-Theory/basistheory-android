# BasisTheoryElements

This class contains functions to interact with the Basis Theory API using element references.

## Instantiation

A new instance of the `BasisTheoryElements` service can be instantiated and configured using the
builder pattern:

```kotlin
val bt = BasisTheoryElements.builder()
    .apiUrl("https://api.basistheory.com") // optional
    .apiKey(myApiKey)
    .ioDispatcher(myDispatcher) // optional
    .build()
```

The following values are used by default if not specified when constructing an instance of the service:
- `apiUrl`: `https://api.basistheory.com`
- `apiKey`: `null`
- `ioDispatcher`: `Dispatchers.IO`

Note that `BasisTheoryElements` requires the use of a public API key during initialization
(an API key issued to a `public` [Application](https://developers.basistheory.com/concepts/access-controls/#what-are-applications)).
Click [here](https://portal.basistheory.com/applications/create?permissions=token%3Acreate&type=public.)
to create one in the Basis Theory portal.

## Usage

### tokenize

This function wraps the [tokenize API endpoint](https://docs.basistheory.com/#tokenize) 
providing added support for referencing instances of elements within your request payload. 

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
from references to element inputs. This enables your application to tokenize sensitive data values 
without needing to touch the raw data directly.
