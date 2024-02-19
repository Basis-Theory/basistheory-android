-dontwarn javax.validation.valueextraction.UnwrapByDefault
-dontwarn javax.validation.valueextraction.ValueExtractor
-dontwarn aQute.bnd.annotation.spi.ServiceProvider
-dontwarn jakarta.validation.valueextraction.UnwrapByDefault
-dontwarn jakarta.validation.valueextraction.ValueExtractor

# Needed to keep generic signatures
-keepattributes Signature

# Used by Gson serialization and deserialization internally in BT's SDK
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# BT's SDK classes
-keep class com.basistheory.** {
    *;
}