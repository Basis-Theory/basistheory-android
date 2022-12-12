# Android SDK Example App

This example Android app demonstrates how Elements can be integrated into your Android applications.


## Setup

1. Clone this repository 
2. Open the project in Android Studio
3. Install and start a [virtual device](https://developer.android.com/studio/run/managing-avds) in Android Studio
4. Copy the `local.properties.example` file to `local.properties` and set the following properties:
    - `sdk.dir`: Local path to the Android SDK on your machine, e.g. `/Users/me/Library/Android/sdk`
    - `com.basistheory.android.example.apiUrl`: `https://api.basistheory.com`
    - `com.basistheory.android.example.apiKey`: A Basis Theory API key with `token:create` permission on the `/` container

5. Build and run the `example` module on your virtual device

<img width="384" height="42" src="https://raw.githubusercontent.com/basis-theory/basistheory-android/master/docs/img/example_app_run.png" />


## Examples

### [Collect Cards](example/src/main/java/com/basistheory/android/example/view/card/CardFragment.kt)

<img width="384" height="384" src="https://raw.githubusercontent.com/basis-theory/basistheory-android/master/docs/img/example_app_collect_cards.png" />

### [Collect Social Security Numbers](example/src/main/java/com/basistheory/android/example/view/social_security_number/SocialSecurityNumberFragment.kt)

<img width="358" height="272" src="https://raw.githubusercontent.com/basis-theory/basistheory-android/master/docs/img/example_app_collect_ssns.png" />

### [Custom Forms](example/src/main/java/com/basistheory/android/example/view/custom_form/CustomFormFragment.kt)

<img width="358" height="385" src="https://raw.githubusercontent.com/basis-theory/basistheory-android/master/docs/img/example_app_custom_forms.png" />
