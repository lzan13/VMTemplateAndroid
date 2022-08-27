package com.vmloft.develop.app.template.ui.main.home

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.constant.RefreshState

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.FragmentHomeBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.report.ReportConstants
import com.vmloft.develop.app.template.ui.widget.MatchEmotionDialog
import com.vmloft.develop.app.template.ui.widget.MatchGenderDialog
import com.vmloft.develop.library.base.BVMFragment
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.common.CSPManager
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.FormatUtils
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.common.config.ConfigManager
import com.vmloft.develop.library.common.utils.JsonUtils
import com.vmloft.develop.library.common.widget.refresh.RefreshMultiListener
import com.vmloft.develop.library.data.bean.Match
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.DConstants
import com.vmloft.develop.library.data.viewmodel.MatchViewModel
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.report.ReportManager
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.tools.utils.VMColor
import com.vmloft.develop.library.tools.utils.VMDate
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.widget.barrage.VMBarrageView
import com.vmloft.develop.library.tools.widget.barrage.VMViewCreator
import com.vmloft.develop.library.tools.widget.guide.GuideItem
import com.vmloft.develop.library.tools.widget.guide.VMGuide
import com.vmloft.develop.library.tools.widget.guide.VMGuideView
import com.vmloft.develop.library.tools.widget.guide.VMShape

import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：主页
 */
class HomeFragment : BVMFragment<FragmentHomeBinding, MatchViewModel>() {

    override var isDarkStatusBar: Boolean = false

    private lateinit var selfUser: User

    // 记录自身匹配数据
    private lateinit var selfMatch: Match

    // 弹幕控件
    private var barrageView: VMBarrageView<Match>? = null

    // 弹幕数据
    private val dataList: MutableList<Match> = mutableListOf()

    private var mPage: Int = CConstants.defaultPage

    // 顶部心情控件
    private lateinit var emotionView: View
    private lateinit var emotionIV: ImageView
    private lateinit var emotionTV: TextView

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup?) = FragmentHomeBinding.inflate(inflater, parent, false)

    override fun initVM(): MatchViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

        setTopIcon(R.drawable.ic_filter)
        setTopIconListener { showMatchGenderDialog() }

        setupRefresh()

        // 匹配项点击处理
        // 快速聊天
        mBinding.chatRandomCL.setOnClickListener { startMatch(0) }
        // 快速聊天
        mBinding.chatFastCL.setOnClickListener { startMatch(1) }
        // 二层休闲游戏
        mBinding.relaxationGameCL.setOnClickListener {
            mBinding.twoFloorHeader.openTwoLevel(true)
        }
        // 解忧房
        mBinding.chatRoomCL.setOnClickListener {
            if (selfUser.avatar.isEmpty() || selfUser.nickname.isEmpty()) {
                CRouter.go(AppRouter.appPersonalInfoGuide)
            } else {
                goRoomList()
            }
        }

        // 监听用户信息变化
        LDEventBus.observe(this, DConstants.Event.userInfo, User::class.java) {
            selfUser = it
            bindInfo()

            selfMatch.user = selfUser
            selfMatch.gender = selfUser.gender
            if (selfMatch.content.isEmpty()) {
                if (selfUser.nickname.isEmpty()) {
                    selfMatch.content = "嗨 我是一只小透明 😉"
                } else {
                    selfMatch.content = "嗨 ${selfUser.nickname} 前来报道 😉"
                }
                saveMatchEmotion()
            }
        }
        // 监听自己的匹配信息变化
        LDEventBus.observe(this, DConstants.Event.matchInfo, Match::class.java) {
            selfMatch = it
            // 绑定心情信息
            bindEmotionInfo()
            // 发送匹配信息
            sendMatchInfo()
        }

        // 订阅心情变化事件
//        LDEventBus.observe(this, IMConstants.Common.signalMatchInfo, String::class.java) {
//            val match = JsonUtils.fromJson<Match>(it)
//            addBarrage(match)
//        }
    }

    /**
     * 装载下拉刷新，包括二级刷新
     */
    private fun setupRefresh() {
        mBinding.refreshLayout.setOnRefreshListener { mViewModel.matchList(selfMatch.filterGender, page = mPage) }
        mBinding.refreshLayout.setOnMultiListener(object : RefreshMultiListener {
            override fun onHeaderMoving(header: RefreshHeader, isDragging: Boolean, percent: Float, offset: Int, headerHeight: Int, maxDragHeight: Int) {
                val refreshHeight = mBinding.refreshLayout.layout.height
                val coverHeight = mBinding.twoFloorCoverRL.height
                val translationY = (offset - coverHeight).coerceAtMost(refreshHeight - coverHeight).toFloat()

                mBinding.twoFloorCoverRL.translationY = translationY
                mBinding.includeTopBar.commonTopLL.alpha = 1 - percent.coerceAtMost(1f)
            }

            override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
//                if (oldState == RefreshState.TwoLevel) {
//                    mBinding.twoFloorCoverCL.animate().alpha(0f).duration = 1000
//                }
            }
        })

        mBinding.twoFloorHeader.setOnTwoLevelListener { refreshLayout ->
            CRouter.go(AppRouter.appAppletList)
            false //true 将会展开二楼状态 false 关闭刷新
        }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        selfUser = SignManager.getSignUser()
        selfMatch = SignManager.getSelfMatch()

        // 请求匹配数据集
        mViewModel.matchList(selfMatch.filterGender)

        setupEmotion()
        bindInfo()

        checkGuide()

        setupConfig()
    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (model.type == "matchList" && !model.isLoading) {
            mBinding.refreshLayout.finishRefresh()
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "matchList") {
            val paging = model.data as RPaging<Match>
            setupBarrage(paging)
        }
    }

    /**
     * 绑定信息展示
     */
    private fun bindInfo() {
        // VIP 不需要显示可用匹配次数
        if (selfUser.role.identity < 100) {
            mBinding.randomCountTV.text = selfUser.matchCount.toString()
            mBinding.randomCountTV.visibility = View.VISIBLE

            mBinding.fastCountTV.text = selfUser.fastCount.toString()
            mBinding.fastCountTV.visibility = View.VISIBLE
        } else {
            mBinding.fastCountTV.visibility = View.GONE
            mBinding.randomCountTV.visibility = View.GONE
        }
    }

    /**
     * 检查引导
     */
    private fun checkGuide() {
        if (!CSPManager.isNeedGuide(this@HomeFragment::class.java.simpleName)) return

        val list = mutableListOf<GuideItem>()
        list.add(GuideItem(emotionView, VMStr.byRes(R.string.guide_home_emotion), shape = VMShape.guideShapeCircle, offX = VMDimen.dp2px(96), offY = VMDimen.dp2px(16)))
        if (ConfigManager.appConfig.homeConfig.randomEntry) {
            list.add(GuideItem(mBinding.chatRandomCL, VMStr.byRes(R.string.guide_home_chat_random), shape = VMShape.guideShapeCircle, offY = VMDimen.dp2px(32)))
        }
        if (ConfigManager.appConfig.homeConfig.chatFastEntry) {
            list.add(GuideItem(mBinding.chatFastCL, VMStr.byRes(R.string.guide_home_chat_fast), shape = VMShape.guideShapeCircle, offY = VMDimen.dp2px(32)))
        }
        if (ConfigManager.appConfig.homeConfig.relaxationEntry) {
            list.add(GuideItem(mBinding.relaxationGameCL, VMStr.byRes(R.string.guide_home_relaxation_world), shape = VMShape.guideShapeCircle, offX = VMDimen.dp2px(16), offY = VMDimen.dp2px(32)))
        }
        if (ConfigManager.appConfig.homeConfig.roomEntry) {
            list.add(GuideItem(mBinding.chatRoomCL, VMStr.byRes(R.string.guide_home_chat_room), shape = VMShape.guideShapeCircle, offX = VMDimen.dp2px(80), offY = VMDimen.dp2px(32)))
        }

        VMGuide.Builder(requireActivity()).setOneByOne(true).setGuideViews(list).setGuideListener(object : VMGuideView.GuideListener {
            override fun onFinish() {
                CSPManager.setNeedGuide(this@HomeFragment::class.java.simpleName, false)
            }

            override fun onNext(index: Int) {}
        }).build().show()
    }

    /**
     * 跳转房间列表
     */
    private fun goRoomList() {
        if (selfUser.banned == 1 && selfUser.bannedTime > VMDate.currentMilli()) {
            mDialog = CommonDialog(requireContext())
            (mDialog as CommonDialog).let { dialog ->
                dialog.setContent(VMStr.byResArgs(R.string.tips_banned, selfUser.bannedReason, FormatUtils.defaultTime(selfUser.bannedTime)))
                dialog.setNegative("")
                dialog.setPositive(VMStr.byRes(R.string.btn_i_known))
                dialog.show()
            }
            return
        }
        CRouter.go(AppRouter.appRoomList)
    }

    /**
     * 装载右上角心情信息
     */
    private fun setupEmotion() {
        emotionView = LayoutInflater.from(context).inflate(R.layout.widget_match_emtoion_view, null)
        emotionIV = emotionView.findViewById(R.id.emotionIV)
        emotionTV = emotionView.findViewById(R.id.emotionTV)

        setTopEndView(emotionView)

        bindEmotionInfo()

        emotionView.setOnClickListener {
            mDialog = MatchEmotionDialog(requireContext())
            (mDialog as MatchEmotionDialog).show(Gravity.BOTTOM)
        }
    }

    /**
     * 绑定心情信息
     */
    private fun bindEmotionInfo() {
        val emotionResId = when (selfMatch.emotion) {
            0 -> R.drawable.ic_emotion_hahaha
            1 -> R.drawable.ic_emotion_koubi
            2 -> R.drawable.ic_emotion_liulei
            3 -> R.drawable.ic_emotion_fanu
            else -> R.drawable.ic_emotion_hahaha
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
        // 提交自己的匹配信息到服务器
        mViewModel.submitMatch(selfMatch)
    }

    /**
     * 加载配置信息
     */
    private fun setupConfig() {
        mBinding.chatRandomCL.visibility = if (ConfigManager.appConfig.homeConfig.randomEntry) View.VISIBLE else View.GONE
        mBinding.chatFastCL.visibility = if (ConfigManager.appConfig.homeConfig.chatFastEntry) View.VISIBLE else View.GONE
        mBinding.relaxationGameCL.visibility = if (ConfigManager.appConfig.homeConfig.relaxationEntry) View.VISIBLE else View.GONE
        mBinding.chatRoomCL.visibility = if (ConfigManager.appConfig.homeConfig.roomEntry) View.VISIBLE else View.GONE
    }

    /**
     * 开始随机获取一个匹配对象
     */
    private fun startMatch(type: Int = 0) {
        if (selfUser.avatar.isNullOrEmpty() || selfUser.nickname.isNullOrEmpty()) {
            return CRouter.go(AppRouter.appPersonalInfoGuide)
        }
        if (selfUser.banned == 1 && selfUser.bannedTime > VMDate.currentMilli()) {
            mDialog = CommonDialog(requireContext())
            (mDialog as CommonDialog).let { dialog ->
                dialog.setContent(VMStr.byResArgs(R.string.tips_banned, selfUser.bannedReason, FormatUtils.defaultTime(selfUser.bannedTime)))
                dialog.setNegative("")
                dialog.setPositive(VMStr.byRes(R.string.btn_i_known))
                dialog.show()
            }
            return
        }
        if (type == 1) {
            selfMatch.type = type
            sendMatchInfo()
        }
        // 检查匹配条件并上报统计
        if (type == 0 && (selfUser.matchCount > 0 || selfUser.score > 0)) {
            if (selfUser.matchCount > 0) {
                selfUser.matchCount--
            } else if (selfUser.score > 0) {
                selfUser.score--
            }
            ReportManager.reportEvent(ReportConstants.eventDestinyMatch)
        } else if (type == 1 && (selfUser.fastCount > 0 || selfUser.score > 0)) {
            if (selfUser.fastCount > 0) {
                selfUser.fastCount--
            } else if (selfUser.score > 0) {
                selfUser.score--
            }
            ReportManager.reportEvent(ReportConstants.eventFastChat)
        } else {
            // 显示不满足条件弹窗
            showCannotMatchDialog()
            return
        }

        bindInfo()

        // 跳转动画匹配界面
        CRouter.go(AppRouter.appMatchAnim, type)
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
        super.onDestroy()
    }

    /**
     * 匹配条件不满足弹窗
     */
    private fun showCannotMatchDialog() {
        mDialog = CommonDialog(requireContext())
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.match_cannot_tips)
            dialog.setPositive {
                CRouter.go(AppRouter.appGold)
            }
            dialog.show()
        }
    }

    /**
     * --------------------------------- 弹幕相关 ---------------------------------
     */
    private fun addBarrage(match: Match?) {
        if (match == null) return
        // 排除自己
        if (match.user.id == selfUser.id) return

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
        val countStr = paging.totalCount.toString()
        val infoStr = VMStr.byResArgs(R.string.match_info, countStr)
        val infoSP = SpannableString(infoStr)

        var start = infoStr.indexOf(countStr)
        var end = start + countStr.length
        //设置高亮样式
        infoSP.setSpan(ForegroundColorSpan(VMColor.byRes(R.color.app_title_display)), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mBinding.matchInfoTV.text = infoSP

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
                if (it.user.id != selfUser.id) {
                    CacheManager.putUser(it.user)
                }
            }
        }

        barrageView?.stop()
        barrageView = null
        mBinding.barrageViewLL.removeAllViews()

        // 重置弹幕控件
        barrageView = VMBarrageView(activity)
        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        mBinding.barrageViewLL.addView(barrageView, lp)

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
            val avatarIV = view.findViewById<ImageView>(R.id.avatarIV)
            val emotionIV = view.findViewById<ImageView>(R.id.emotionIV)
            val barrageTV = view.findViewById<TextView>(R.id.barrageTV)

            val emotionResId = when (bean.emotion) {
                0 -> R.drawable.ic_emotion_hahaha
                1 -> R.drawable.ic_emotion_koubi
                2 -> R.drawable.ic_emotion_liulei
                3 -> R.drawable.ic_emotion_fanu
                else -> R.drawable.ic_emotion_hahaha
            }
            emotionIV.setImageResource(emotionResId)
            IMGLoader.loadAvatar(avatarIV, bean.user.avatar)
            barrageTV.text = bean.content

            view.setOnClickListener { CRouter.go(AppRouter.appUserInfo, obj0 = bean.user) }
        }
    }
}