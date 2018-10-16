# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/dayaa/Library/Android/sdk/tools/proguard/proguard-android.txt
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
#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------1.实体类---------------------------------
-keep class com.mredrock.cyxbs.model.**{ *; }
-keep class com.mredrock.cyxbs.freshmanspecial.**{ *;}
-keep class com.bigkoo.pikerview.**{ *;}
-keep class com.mredrock.cyxbs.network.**{ *;}
-keep public class * extends android.support.v4.app.Fragment


-keep public class * extends android.app.Fragment
#-keepclasseswithmembernames class * { # 保持native方法不被混淆
 #   native <methods>;
#}
#-------------------------------------------------------------------------

#---------------------------------2.第三方包-------------------------------

-dontwarn com.fasterxml.jackson.**
-keep class com.fasterxml.jackson.** { *; }
-dontwarn com.mredrock.cyxbs.component.**
-dontwarn com.mredrock.cyxbs.network.**
-dontwarn com.mredrock.cyxbs.ui.**
-dontwarn com.mredrock.cyxbs.util.**
-dontwarn com.github.siyamed.shapeimageview.**
-dontwarn org.simpleframework.xml.stream.**
-keep class org.simpleframework.xml.** { *; }


-dontwarn com.a.a.**
-dontwarn com.autonavi.**
 -ignorewarnings


-keep class com.autonavi.** {*;}
-keep class com.a.a.** {*;}
#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#okhttp with retrofit

-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn com.squareup.okhttp3.**


-keep class okhttp3.** { *; }

-keep interface okhttp3.** { *; }

-dontwarn okhttp3.**

-dontwarn rx.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keep class sun.misc.Unsafe { *; }

-dontwarn okio.**
-dontwarn com.squareup.retrofit2.**
-dontwarn retrofit.appengine.UrlFetchClient

-keep class com.tbruyelle.rxpermissions.**{*;}
-keep class rxpermissions.**{*;}
-keep interface rxpermissions.**{*;}

#your package path where your gson models are stored
#-keep class com.mredrock.cyxbs.model** { *; }

#eventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
#gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

#rxjava
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
#rxandroid
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

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-keepclassmembers class rx.internal.util.unsafe.** {
    long producerIndex;
    long consumerIndex;
}
-dontwarn rx.internal.util.unsafe.**

#ucrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#nineoldandroids
-keep class com.nineoldandroids.** { *; }

#RxCache
-dontwarn io.rx_cache.internal.**
-keepclassmembers enum io.rx_cache.Source { *; }


##---------------End: proguard configuration for Gson  ----------

#-------------------------------------------------------------------------

#---------------------------------3.与js互相调用的类------------------------



#-------------------------------------------------------------------------

#---------------------------------4.反射相关的类和方法-----------------------



#----------------------------------------------------------------------------
#---------------------------------------------------------------------------------------------------

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------

#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.AppCompatActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    }
    -keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
    }
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }
    -keep class **.R$* {
     *;
    }
    -keepclassmembers class * {
        void *(**On*Event);
    }
    #----------------------------------------------------------------------------

    #---------------------------------webview------------------------------------
    -keepclassmembers class fqcn.of.javascript.interface.for.Webview {
       public *;
       }
       -keepclassmembers class * extends android.webkit.WebViewClient {
           public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
           public boolean *(android.webkit.WebView, java.lang.String);
       }
       -keepclassmembers class * extends android.webkit.WebViewClient {
           public void *(android.webkit.WebView, jav.lang.String);
       }
       #----------------------------------------------------------------------------
       #---------------------------------------------------------------------------------------------------

-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}

-dontshrink
-keep,allowshrinking class com.umeng.message.* {
    public <fields>;
    public <methods>;
}
-keep,allowshrinking class com.umeng.message.protobuffer.MessageResponse$PushResponse$Info {
    public <fields>;
    public <methods>;
}
-keep,allowshrinking class com.umeng.message.protobuffer.MessageResponse$PushResponse$Info$Builder {
    public <fields>;
    public <methods>;
}
-keep,allowshrinking class org.android.agoo.impl.*{
    public <fields>;
    public <methods>;
}
-keep,allowshrinking class org.android.agoo.service.* {*;}
-keep,allowshrinking class org.android.spdy.**{*;}
-keep public class com.mredrock.cyxbs.R$*{
    public static final int *;
}
