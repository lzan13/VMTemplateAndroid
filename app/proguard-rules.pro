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

# 是否混淆第三方jar
-dontskipnonpubliclibraryclasses
# 是否使用大小写混合
-dontusemixedcaseclassnames
-dontwarn
# 忽略警告，避免打包时某些警告出现
-ignorewarnings
# 混淆时是否记录日志
-verbose
# 指定代码的压缩级别
-optimizationpasses 5
# 混淆时是否做预校验
-dontpreverify

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *JavascriptInterface*

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# 保持枚举 enum 类不被混淆
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保持Parcelable不被混淆
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep public class * extends android.webkit.WebChromeClient

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keep public class * extends android.support.v4.widget
-keep class android.support.v4.** { *; }
-dontwarn android.support.**

-keepattributes Signature
-keepattributes InnerClasses

# Glide 混淆配置
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Bugly 混淆配置
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}


# 环信 SDK 混淆代码，新版 sdk 添加了 superrtc 包，要加上 rtc  keep 代码
-keep class com.hyphenate.** {*;}
-keep class com.superrtc.** {*;}
-dontwarn  com.hyphenate.**

-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
# 2.0.9后加入语音通话功能，如需使用此功能的api，加入以下keep
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}


-keep class com.vmloft.develop.library.**{*;}

# 自身项目混淆配置
-keep class com.vmloft.develop.app.match.request.**{*;}