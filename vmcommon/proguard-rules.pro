#######################################################
### 基本配置
#######################################################
### 指定压缩级别
-optimizationpasses 5

### 不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

### 混淆时采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

### 把混淆类中的方法名也混淆了
-useuniqueclassmembernames

### 指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses

### 不进行优化，建议使用此选项，
-dontoptimize
### 不做预检验，preverify是proguard的四大步骤之一,可以加快混淆速度
-dontpreverify

### 忽略警告
-ignorewarnings

### 混淆时不使用大小写混合，混淆后的类名为小写(大小写混淆容易导致class文件相互覆盖）
-dontusemixedcaseclassnames

### 优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

### 将文件来源重命名为“SourceFile”字符串
#-renamesourcefileattribute SourceFile
### 保留行号
-keepattributes SourceFile,LineNumberTable
### 保持泛型
-keepattributes Signature
### 保持注解
-keepattributes *Annotation*,InnerClasses

###  保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**


#######################################################
### 默认保留
#######################################################
### 保留R下面的资源
-keep class **.R$* {*;}
### 不混淆资源类下static的
-keepclassmembers class **.R$* {
    public static <fields>;
}

### 保留四大组件，自定义的Application,Fragment等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View


###  保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

###  对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

### 保留在Activity中的方法参数是view的方法，
### 这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
### 表示不混淆Activity中参数是View的方法，因为有这样一种用法，在XML中配置android:onClick=”buttonClick”属性，
### 当用户点击该按钮时就会调用Activity中的buttonClick(View view)方法，如果这个方法被混淆的话就找不到了
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

###  保持枚举 enum 类不被混淆
###  For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

###  保持Parcelable不被混淆
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

###  保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

###  保持自定义控件类不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

### webView需要进行特殊处理
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keep public class * extends android.webkit.WebChromeClient

-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, jav.lang.String);
}
#在app中与HTML5的JavaScript的交互进行特殊处理
#我们需要确保这些js要调用的原生方法不能够被混淆，于是我们需要做如下处理：
-keepclassmembers class com.ljd.example.JSInterface {
    <methods>;
}
###（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }


#######################################################
### androidx
#######################################################
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**
-printconfiguration
-keep,allowobfuscation @interface androidx.annotation.Keep

-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}


#######################################################
### 混淆输出配置
#######################################################
### 混淆映射，生成映射文件
-verbose
-printmapping proguardMapping.txt
### 输出apk包内所有的class的内部结构
-dump dump.txt
### 未混淆的类和成员
-printseeds seeds.txt
### 列出从apk中删除的代码
-printusage unused.txt


#######################################################
### 三方框架
#######################################################

### Glide 混淆配置
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

### 知乎开源图片选择器 Matisse 配置
-dontwarn com.squareup.picasso.**

### LiveEventBus 混淆配置
-dontwarn com.jeremyliao.liveeventbus.**
-keep class com.jeremyliao.liveeventbus.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class androidx.arch.core.** { *; }


### ARouter 混淆配置
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider


### Bugly 混淆配置
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}


### JPush 推送混淆
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }


### UMeng 混淆配置
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
### 您如果使用了稳定性模块可以加入该混淆
#-keep class com.uc.** {*;}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


### Mob 混淆配置
-keep class com.mob.**{*;}
-keep class cn.smssdk.**{*;}
-dontwarn com.mob.**


### 自己的工具库混淆配置
-keep class com.vmloft.develop.library.**{*;}


### 滚动选择器混淆配置
-keepattributes InnerClasses,Signature
-keepattributes *Annotation*
-keep class cn.addapp.pickers.entity.** { *;}

### 地址日期选择器
-keep class com.aigestudio.wheelpicker.** { *;}


### AgentWeb 库混淆配置
-keep class com.just.agentweb.** {
    *;
}
-dontwarn com.just.agentweb.**
