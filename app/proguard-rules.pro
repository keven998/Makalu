# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Program Files (x86)\Android\android-studio\sdk/tools/proguard/proguard-android.txt
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

#EVENTBUS
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}


#支付宝
#-libraryjars libs/alipaySDK-20151112.jar
-dontwarn com.alipay.**
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context,android.util.AttributeSet);
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context,android.util.AttributeSet,int);
}


#gilde
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Gradle Retrolambda Plugin
# https://github.com/evant/gradle-retrolambda#user-content-proguard
-dontwarn java.lang.invoke.*

##---------------android  support----------
-dontwarn android.support.**



-keep class com.igexin.**
-keep class com.igexin.**{*;}
-keep class rx.internal.**
-keep class com.squareup.**
-keep class com.amap.api.**
-keep class com.alibaba.fastjson.**
-keep class butterknife.internal.**
-keep class okio.**


#butterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}


-keep class org.lucasr.twowayview.** { *; }
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.FragmentActivity


#fastjson
-dontwarn com.alibaba.fastjson.**

-keep class com.alibaba.fastjson.** { *; }

# 高德地图
# -libraryjars /libs/android-support-v4.jar  -dontwarn android.support.v4.**

#-libraryjars   libs/AMap_2DMap_v2.6.0_20150916.jar
#
#-libraryjars   libs/AMap_ Location_v1.4.0.1_20150910.jar

-dontwarn com.amap.api.**

#-dontwarn com.a.a.**

-dontwarn com.autonavi.**

-keep class com.amap.api.**  {*;}

-keep class com.autonavi.**  {*;}

#-keep class com.a.a.**  {*;}


# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }




## ----------------------------------
##      UIL相关
## ----------------------------------
-keep class com.nostra13.universalimageloader.** { *; }
-keepclassmembers class com.nostra13.universalimageloader.** {*;}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}

##------------------------------------
## UMeng相关
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
#keep 枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}




-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.andr.vending.licensing.ILicensingServiceoid

-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**

#-libraryjars libs/qiniu-android-sdk-7.0.1.jar

-keep enum com.facebook.**


-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**

-keep class com.facebook.**
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**

-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}

-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class com.tencent.mm.** {*;}

-keep public class com.xuejian.client.lxp.R$*{
    public static final int *;
}



-keep class com.igexin.** {
    *;
}

#OKHttp
-keepattributes *Annotation*
-dontwarn rx.**

-dontwarn okio.**


-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
#okio

-keep class com.squareup.okhttp.** { *;}
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**


-keep class android.support.** {
    *;
}








#代码混淆压缩比
-optimizationpasses 5

#不进行预效验
-dontpreverify
-verbose
-printmapping proguardMapping.txt
-dontwarn
-dontskipnonpubliclibraryclassmembers

#不进行优化 优化具有潜在风险
-dontoptimize
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保留注解参数
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Signature
-dontskipnonpubliclibraryclasses
-dontusemixedcaseclassnames

#native方法
-keepclasseswithmembernames class * {
    native <methods>;
}

#这些类型必须被原样的保留，不能移除或者重命名
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider


-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    *** get*();
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$CREATOR *;
}

-keepclassmembers class **.R$* {
   *;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class * implements java.io.Serializable {*;}

-keepclassmembers class * {
    void *(**On*Listener);
}

-keepclassmembers class * {
   public **** is*(***);
}

# 实体类
-keep class com.xuejian.client.lxp.bean.** { *; }
-keep class com.xuejian.client.lxp.common.gson.** { *; }
-keep class com.xuejian.client.lxp.db.** { *; }

#禁止log
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}

-keepclassmembers class * extends android.webkit.webViewClient {
    *;
}

-keep class com.xuejian.client.lxp.common.widget.** { *; }
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-keep class com.igexin.**{*;}
-dontwarn com.igexin.**
-dontwarn rx.internal.**
-dontwarn com.squareup.**
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


-keep class com.jakewharton.rxbinding.** { *; }
-keep class org.greenrobot.** { *; }
-keep class io.reactivex.** { *; }
-keep class com.github.techery.** { *; }

-keep class com.aizou.core.utils.SharePrefUtil { *; }
-keepclasseswithmembernames class com.lv.utils.SharePrefUtil {
*;
}

#Rxjava Rx android
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}