
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