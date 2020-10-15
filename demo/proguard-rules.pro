# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build_new_new.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes SourceFile,LineNumberTable
-dontwarn android.support.**
-dontwarn com.squareup.**

# Google Service报错过滤
-dontwarn com.google.**
-dontwarn android.support.**
-dontwarn com.appsflyer.**
-dontwarn com.squareup.**

-keep class android.support.** { *; }
-keep class com.squareup.** { *; }

-keep class * extends android.app.Activity { *; }
-keep class * extends android.content.BroadcastReceiver { *; }

-keep class **.R$* { *; }

# glide
-keep class com.bumptech.glide.** {*;}

# Chartboost
-keep class com.chartboost.sdk.** { *; }

# AppsFlyer
-keep class com.appsflyer.** { *; }

# Facebook
-keep class com.facebook.** { *; }
-keep class bolts.** { *; }

# VK
-keep class com.vk.sdk.** { *; }

# WingA
-keep class com.wa.sdk.** { *; }


# Google
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }
-keep class com.android.vending.billing.** { *; }

# Twitter
-keep class com.twitter.sdk.android.** { *; }