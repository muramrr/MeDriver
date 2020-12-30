# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.mmdev.me.driver.**$$serializer { *; }
-keep class com.revenuecat.purchases.*
-keep class com.mmdev.me.driver.domain.fuel.history.data.ConsumptionBound { *; }
-keep class com.mmdev.me.driver.domain.fuel.history.data.DistanceBound { *; }
-keep class com.mmdev.me.driver.domain.fuel.prices.data.FuelPrice { *; }
-keep class com.mmdev.me.driver.domain.fuel.prices.data.FuelStation { *; }
-keep class com.mmdev.me.driver.domain.vehicle.data.Regulation { *; }