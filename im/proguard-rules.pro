# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk-windows/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
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
-dontwarn com.igexin.**
-keep class com.igexin.**{*;}
-dontwarn rx.internal.**
-dontwarn com.squareup.**
-dontwarn com.lv.**
-dontwarn com.amap.api.**
-dontwarn com.alibaba.fastjson.**
-dontwarn butterknife.internal.**
-dontwarn okio.**
-keep class org.lucasr.twowayview.** { *; }
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.FragmentActivity