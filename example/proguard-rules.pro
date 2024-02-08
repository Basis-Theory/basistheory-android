-dontwarn javax.validation.valueextraction.UnwrapByDefault
-dontwarn javax.validation.valueextraction.ValueExtractor

# Needed to keep generic signatures
-keepattributes Signature

# Used by Gson serialization and deserialization internally in BT's SDK
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# BT's SDK classes
-keep class com.basistheory.** {
    *;
}

# Needed for Android Instrumentation Tests
-dontwarn com.google.errorprone.annotations.InlineMe
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.FeatureDescriptor
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor
-dontwarn java.lang.ClassValue
-dontwarn javax.lang.model.element.Modifier