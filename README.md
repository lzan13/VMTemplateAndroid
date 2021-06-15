VMMatchAndroid
=======
一套包含了社区匹配聊天语音以及直播相关的社交系统模板项目

> 项目资源均来自于互联网，如果有侵权请联系我

### 开发环境
项目基本属于在最新的`Android`开发环境下开发，全局使用`Kotlin`语言

项目已经适配`Android6.x`以上的动态权限适配，以及`7.x`的文件选择，和`8.x`的通知提醒，`10.x`的文件选择等；

- Mac OS 11.1
- Android Studio 4.2


### 项目模块儿
- `app`是项目主模块儿，这部分主要包含了项目的业务逻辑，比如匹配、内容发布，信息展示等
- `vmcommon`是项目基础模块儿，这部分主要包含了项目的基类封装，依赖管理，包括网络请求，图片加载等工具类
- `vmim`聊天与通话模块儿，这是为了方便大家引用到自己的项目中做的一步封装，不用再去复杂的复制代码和资源等，只需要将`vmim`以`module`导入到自己的项目中就行了，具体使用方式参见项目`app`模块儿；


### 功能与 TODO
**IM部分功能**
- [x] 链接监听
- [x] 登录注册
- [x] 会话功能
    - [x] 置顶
    - [x] 标为未读
    - [x] 删除与清空
    - [x] 草稿功能
- [x] 聊天功能
    - [x]消息类型
        - [x] 文本消息
        - [x] 图片消息
            - [x] 查看大图
            - [x] 保存图片
    - [x] 消息处理
        - [x] 删除
        - [x] 撤回
        - [x] 复制（仅文本可复制）
    - [x] 昵称头像处理（通过回调实现）
    - [x] 头像点击（回调到 App 层）
    - [x] 语音实时通话功能
        - [x] 1V1音频通话
        - [x] 静音、扬声器播放
        - [x] 音效变声
- [x] 解忧茶室
    - [x] 创建房间
    - [x] 发送消息
        - [x] 文本消息
        - [x] 鼓励消息
    - [x] 上下麦处理
    - [x] 音效变声（彩蛋功能）



**App部分功能**
- [x] 登录注册（包括业务逻辑和 IM 逻辑）
- [x] 首页
    - [x] 自己的状态
    - [x] 拉取其他人的状态信息
    - [x] 心情匹配
    - [x] 解忧聊天室
- [x] 聊天（这里直接加载 IM 模块儿）
- [x] 发现
    - [x] 发布内容
    - [x] 喜欢操作
    - [x] 详情展示
        - [x] 喜欢操作
        - [ ] 评论获取
        - [ ] 添加评论
- [x] 我的
    - [x] 个人信息展示
    - [x] 上传头像、封面
    - [x] 设置昵称、签名、职业、地址、生日、性别等
    - [x] 个人发布与喜欢内容展示
- [x] 设置
    - [x] 个人信息设置
    - [x] 深色模式适配
    - [x] 通知设置
    - [x] 资源加载设置
    - [x] 关于
        - [ ] 检查更新
        - [x] 问题反馈
    - [x] 环境切换
    - [x] 退出

**发布功能**
- [x] 多渠道打包
- [x] 签名配置
- [x] 开发与线上环境配置
- [x] 敏感信息保护


### 配置运行
1. 首先复制`config.default.gradle`到`config.gradle`
2. 配置下`config.gradle`内相关字段
3. 正式打包需要自己生成签名文件，然后修改下`config.gradle`的`signings`签名信息
4. 需配合服务器端一起使用，修改上边`config.gradle`配置文件的`baseDebugUrl`和`baseReleaseUrl`


### 参与贡献
如果你有什么好的想法，或者好的实现，可以通过下边的步骤参与进来，让我们一起把这个项目做得更好，欢迎参与 😁

1. `Fork`本仓库
2. 新建`feature_xxx`分支 (单独创建一个实现你自己想法的分支)
3. 提交代码
4. 新建`Pull Request`
5. 等待`Review & Merge`


### 其他
- 服务器 [github/vmtemplateserver](https://github.com/lzan13/vmtemplateserver) [gitee/vmtemplateserver](https://gitee.com/lzan13/vmtemplateserver) 服务器端项目使用`eggjs`框架实现 
- [项目介绍](https://blog.melove.net/develop-open-source-app-and-server-template/) 项目整体介绍说明

**下载体验**
这就是一个使用当前模板运营的一个项目
- [APK 包下载](http://app.melove.net/squx)
- [应用市场下载](https://play.google.com/store/apps/details?id=com.vmloft.develop.app.template)

**项目截图**
这里简单截取了几个界面，更多功能自己去发现吧
<div align="center">
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/matchHome.jpg" width="295px" height="640px" alt="matchHome"/>
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/matchExplore.jpg" width="295px" height="640px" alt="matchExplore"/>
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/matchMsg.jpg" width="295px" height="640px" alt="matchMsg"/>
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/matchMine.jpg" width="295px" height="640px" alt="matchMine"/>
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/matchChat.jpg" width="295px" height="640px" alt="matchChat"/>
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/matchChatFast.png" width="295px" height="640px" alt="matchChatFast"/>
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/matchCall.png" width="295px" height="640px" alt="matchCall"/>
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/matchRoom.png" width="295px" height="640px" alt="matchAbout"/>
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/matchInfo.jpg" width="295px" height="640px" alt="matchInfo"/>
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/matchAbout.jpg" width="295px" height="640px" alt="matchAbout"/>
</div>


### 加群交流
QQ群: 901211985  个人QQ: 1565176197
<div align="center">
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/dev/imGroup.jpg" width="270px" height="370px" alt="QQ 交流群"/>
    <img src="https://gitee.com/lzan13/VMPictureBed/raw/master/images/social/qqQR1565176197.png" width="256px" height="332px" alt="个人 QQ"/>
</div>


