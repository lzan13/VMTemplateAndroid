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


### 友盟 SDK 混淆配置
-keep class com.umeng.** { *; }
-keep class com.uc.** { *; }
-keep class com.efs.** { *; }
-keepclassmembers class * {
     public<init>(org.json.JSONObject);
}
-keepclassmembers enum * {
      public static **[] values();
      public static ** valueOf(java.lang.String);
}


### 滚动选择器混淆配置
-keepattributes InnerClasses,Signature
-keepattributes *Annotation*
-keep class cn.addapp.pickers.entity.** { *;}


### 地址日期选择器
-keep class com.aigestudio.wheelpicker.** { *;}


### AgentWeb 库混淆配置
-keep class com.just.agentweb.** {*;}
-dontwarn com.just.agentweb.**


### UCloud ufile-sdk-java 库混淆配置
-keep class com.ucloud.ufile.** { *; }
-dontwarn com.ucloud.ufile.**


### 自己的工具库混淆配置
-keep class com.vmloft.develop.library.**{*;}

