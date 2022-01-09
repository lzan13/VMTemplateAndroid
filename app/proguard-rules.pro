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

### 自身项目混淆配置
### 通过 proguard 等混淆/优化工具处理 apk 时，proguard 可能会将 R.java 删除，如果遇到这个问题，请添加如下配置
-keep public class com.vmloft.develop.app.template.R$*{
      public static final int*;
}

### 自身项目混淆配置
-keep class com.vmloft.develop.app.template.request.**{*;}
-keep public class com.vmloft.develop.app.template.R$*{
public static final int *;
}
