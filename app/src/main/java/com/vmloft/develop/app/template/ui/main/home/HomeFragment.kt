package com.vmloft.develop.app.template.ui.main.home

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.FragmentHomeBinding
import com.vmloft.develop.app.template.request.bean.Match
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.app.template.report.ReportConstants
import com.vmloft.develop.app.template.request.viewmodel.MatchViewModel
import com.vmloft.develop.app.template.ui.widget.MatchEmotionDialog
import com.vmloft.develop.app.template.ui.widget.MatchGenderDialog
import com.vmloft.develop.library.base.BVMFragment
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.mqtt.MQTTConstants
import com.vmloft.develop.library.mqtt.MQTTHelper
import com.vmloft.develop.library.report.ReportManager
import com.vmloft.develop.library.tools.widget.barrage.VMBarrageView
import com.vmloft.develop.library.tools.widget.barrage.VMViewCreator

import org.json.JSONObject

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：主页
 */
class HomeFragment : BVMFragment<FragmentHomeBinding, MatchViewModel>() {

    override var isDarkStatusBar: Boolean = false

    private lateinit var mUser: User

    // 记录自身匹配数据
    private lateinit var selfMatch: Match

    // 弹幕控件
    private var barrageView: VMBarrageView<Match>? = null

    // 弹幕数据
    private val dataList: MutableList<Match> = mutableListOf()

    private var mPage: Int = CConstants.defaultPage

    // 顶部心情控件
    private lateinit var emotionIV: ImageView
    private lateinit var emotionTV: TextView

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = FragmentHomeBinding.inflate(inflater, parent, false)

    override fun initVM(): MatchViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

        setTopIcon(R.drawable.ic_filter)
        setTopIconListener { showMatchGenderDialog() }

        // 下一波
        mBinding.homeNextTV.setOnClickListener { mViewModel.matchList(selfMatch.filterGender, selfMatch.type, mPage) }

        // 匹配项点击处理
        // 缘分匹配
        mBinding.matchDestinyCL.setOnClickListener { startMatch(0) }
        // 快速聊天
        mBinding.matchFastCL.setOnClickListener { startMatch(1) }
        // 解忧房
        mBinding.homeChatRoomLL.setOnClickListener { CRouter.go(AppRouter.appRoomList) }
        // 秘密枯井
        mBinding.homeSecretLL.setOnClickListener { CRouter.go(AppRouter.appMatchSecret) }
        // 心愿古树
        mBinding.homeWishLL.setOnClickListener { }

        // 监听用户信息变化
        LDEventBus.observe(this, Constants.Event.userInfo, User::class.java) {
            mUser = it
            selfMatch.user = mUser
            selfMatch.gender = mUser.gender
            if (selfMatch.content.isNullOrEmpty() && mUser.nickname.isNotEmpty()) {
                selfMatch.content = "嗨 ${mUser.nickname} 来啦 😉"
            }
            saveMatchEmotion()
            bindInfo()
        }
        // 监听自己的匹配信息变化
        LDEventBus.observe(this, Constants.Event.matchInfo, Match::class.java) {
            selfMatch = it
            // 绑定心情信息
            bindEmotionInfo()
            // 发送匹配信息
            sendMatchInfo()
        }
        // 配置信息变化
        LDEventBus.observe(this, CConstants.clientConfigEvent, Int::class.java) {
            setupConfig()
        }

        // 订阅 MQTT 事件
        LDEventBus.observe(this, MQTTConstants.Topic.newMatchInfo, String::class.java) {
            val match = JsonUtils.fromJson<Match>(it, Match::class.java)
            addBarrage(match)
        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        mUser = SignManager.getCurrUser() ?: User()
        selfMatch = SignManager.getSelfMatch()

        // 请求匹配数据集
        mViewModel.matchList(selfMatch.filterGender)
        // 获取 MQTT Token 链接MQTT 云服务
        mViewModel.mqttUserToken(mUser.id)

        setupEmotion()
        bindInfo()
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        mBinding.loadingView.visibility = if (model.isLoading) View.VISIBLE else View.GONE
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "matchList") {
            val paging = model.data as RPaging<Match>
            setupBarrage(paging)
        } else if (model.type == "mqttUserToken") {
            val token = model.data as String
            MQTTHelper.connect(mUser.id, token, MQTTConstants.Topic.newMatchInfo)
        }
    }

    /**
     * 绑定信息展示
     */
    private fun bindInfo() {
        // VIP 不需要显示可用匹配次数
        if (mUser.role.identity < 100) {
            mBinding.matchDestinyCountTV.text = mUser.matchCount.toString()
            mBinding.matchDestinyCountTV.visibility = View.VISIBLE
            mBinding.matchFastCountTV.text = mUser.matchCount.toString()
            mBinding.matchFastCountTV.visibility = View.VISIBLE
        } else {
            mBinding.matchDestinyCountTV.visibility = View.GONE
            mBinding.matchFastCountTV.visibility = View.GONE
        }
    }

    /**
     * 装载右上角心情信息
     */
    private fun setupEmotion() {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_match_emtoion_view, null)
        emotionIV = view.findViewById(R.id.emotionIV)
        emotionTV = view.findViewById(R.id.emotionTV)

        setTopEndView(view)

        bindEmotionInfo()

        view.setOnClickListener {
            mDialog = MatchEmotionDialog(requireContext())
            (mDialog as MatchEmotionDialog).show(Gravity.BOTTOM)
        }
    }

    /**
     * 绑定心情信息
     */
    private fun bindEmotionInfo() {
        val emotionResId = when (selfMatch.emotion) {
            0 -> R.drawable.ic_emotion_happy
            1 -> R.drawable.ic_emotion_normal
            2 -> R.drawable.ic_emotion_sad
            3 -> R.drawable.ic_emotion_anger
            else -> R.drawable.ic_emotion_happy
        }
        emotionIV.setImageResource(emotionResId)
        val emotionStr = when (selfMatch.emotion) {
            0 -> R.string.emotion_happy
            1 -> R.string.emotion_normal
            2 -> R.string.emotion_sad
            3 -> R.string.emotion_anger
            else -> R.string.emotion_happy
        }
        emotionTV.setText(emotionStr)
    }

    /**
     * 显示匹配性别对话框
     */
    private fun showMatchGenderDialog() {
        mDialog = MatchGenderDialog(requireContext())
        (mDialog as MatchGenderDialog).show(Gravity.BOTTOM)
    }

    /**
     * 保存匹配心情数据
     */
    private fun saveMatchEmotion() {
        SignManager.setSelfMatch(selfMatch)

        val params = mutableMapOf<String, Any>()
        params["emotion"] = selfMatch.emotion // 心情 0-开心 1-平淡 2-难过 3-愤怒
        ReportManager.reportEvent(ReportConstants.eventChangeEmotion, params)
    }

    /**
     * 发送匹配信息
     */
    private fun sendMatchInfo() {
        if (selfMatch.user.nickname.isEmpty()) return

        // 提交自己的匹配信息到服务器
        mViewModel.submitMatch(selfMatch)

        val json = JSONObject()
        json.put("content", selfMatch.content)
        json.put("emotion", selfMatch.emotion)
        json.put("gender", selfMatch.gender)
        json.put("type", selfMatch.type)
        val jsonUser = JSONObject()
        jsonUser.put("avatar", mUser.avatar)
        jsonUser.put("id", mUser.id)
        jsonUser.put("nickname", mUser.nickname)
        jsonUser.put("username", mUser.username)
        json.put("user", jsonUser)
        MQTTHelper.sendMsg(MQTTConstants.Topic.newMatchInfo, json.toString())
    }

    /**
     * 加载配置信息
     */
    private fun setupConfig() {
        mBinding.homeChatRoomLL.visibility = if (ConfigManager.clientConfig.homeRoomEntry) View.VISIBLE else View.GONE
        mBinding.homeSecretLL.visibility = if (ConfigManager.clientConfig.homeSecretEntry) View.VISIBLE else View.GONE
        mBinding.homeWishLL.visibility = if (ConfigManager.clientConfig.homeWishEntry) View.VISIBLE else View.GONE
    }

    /**
     * 开始随机获取一个匹配对象
     */
    private fun startMatch(type: Int = 0) {
        if (mUser.avatar.isNullOrEmpty() || mUser.nickname.isNullOrEmpty()) {
            return CRouter.go(AppRouter.appPersonalInfoGuide)
        }
        mUser.matchCount--
        // 跳转动画匹配界面
        CRouter.go(AppRouter.appMatchAnim, type)
        // 上报匹配
        if (type == 0) {
            ReportManager.reportEvent(ReportConstants.eventDestinyMatch)
        } else if (type == 1) {
            ReportManager.reportEvent(ReportConstants.eventFastChat)
        }
        bindInfo()
    }

    override fun onResume() {
        super.onResume()
        barrageView?.resume()
    }

    override fun onPause() {
        super.onPause()
        barrageView?.pause()
    }

    override fun onDestroy() {
        barrageView?.stop()
        MQTTHelper.unsubscribe(MQTTConstants.Topic.newMatchInfo)
        super.onDestroy()
    }

    /**
     * --------------------------------- 弹幕相关 ---------------------------------
     */
    private fun addBarrage(match: Match?) {
        if (match == null) return
        // 排除自己
        if (match.user.id == mUser.id) return

        if (dataList.contains(match)) {
            dataList.remove(match)
        }
        dataList.add(match)
        CacheManager.putUser(match.user)

        // 添加到弹幕中
        barrageView?.addBarrage(match)
    }

    /**
     * 装载弹幕
     */
    private fun setupBarrage(paging: RPaging<Match>) {
        if (paging.currentCount + paging.page * paging.limit >= paging.totalCount) {
            mPage = CConstants.defaultPage
        } else {
            mPage++
        }
        dataList.clear()
        paging.data.map {
            // 因为有注销功能，查询到的匹配信息可能没有用户信息，前端这里做下保护
            if (it.user != null && it.user.id.isNotEmpty()) {
                dataList.add(it)
                if (it.user.id != mUser.id) {
                    CacheManager.putUser(it.user)
                }
            }
        }

        barrageView?.stop()
        barrageView = null
        mBinding.homeBarrageViewLL.removeAllViews()

        // 重置弹幕控件
        barrageView = VMBarrageView(activity)
        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        mBinding.homeBarrageViewLL.addView(barrageView, lp)

        barrageView?.let {
            it.setCreator(ViewCreator()).setMaxSize(50).create(dataList).start()
        }
    }

    /**
     * 弹幕构建器
     */
    class ViewCreator : VMViewCreator<Match> {
        override fun layoutId(): Int = R.layout.item_barrage_view

        override fun onBind(view: View, bean: Match) {
            val barrageItemIV = view.findViewById<ImageView>(R.id.barrageItemIV)
            val barrageItemTV = view.findViewById<TextView>(R.id.barrageItemTV)

            val emotionResId = when (bean.emotion) {
                0 -> R.drawable.ic_emotion_happy
                1 -> R.drawable.ic_emotion_normal
                2 -> R.drawable.ic_emotion_sad
                3 -> R.drawable.ic_emotion_anger
                else -> R.drawable.ic_emotion_happy
            }
            barrageItemIV.setImageResource(emotionResId)
//            IMGLoader.loadAvatar(barrageItemIV, bean.user.avatar)
            barrageItemTV.text = bean.content

            view.setOnClickListener { CRouter.go(AppRouter.appUserInfo, obj0 = bean.user) }
        }
    }
}