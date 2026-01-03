# Add project specific ProGuard rules here.

# Keep Room entities
-keep class com.umbral.data.local.entity.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager.ViewWithFragmentComponentBuilderEntryPoint { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }

# Keep ML Kit classes
-keep class com.google.mlkit.** { *; }

# Keep ZXing classes
-keep class com.google.zxing.** { *; }

# Keep Timber
-dontwarn org.jetbrains.annotations.**

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
