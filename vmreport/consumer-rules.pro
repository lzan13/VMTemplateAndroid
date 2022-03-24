
### Bugly 混淆配置
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}


### 友盟 SDK 混淆配置
-keep class com.umeng.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
