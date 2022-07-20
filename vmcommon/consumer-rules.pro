#######################################################
### 三方框架
#######################################################

### 地址日期选择器
-keep class com.aigestudio.wheelpicker.** { *;}

### AgentWeb 库混淆配置
-keep class com.just.agentweb.** {*;}
-dontwarn com.just.agentweb.**

### SVGA 动画库混淆配置
-keep class com.squareup.wire.** { *; }
-keep class com.opensource.svgaplayer.proto.** { *; }

