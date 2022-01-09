package com.vmloft.develop.app.template.ui.main.home

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.FragmentHomeBinding
import com.vmloft.develop.app.template.request.bean.Match
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMFragment
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.app.template.report.ReportConstants
import com.vmloft.develop.app.template.request.viewmodel.MatchViewModel
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.report.ReportManager
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.tools.utils.logger.VMLog
import com.vmloft.develop.library.tools.widget.barrage.VMBarrageView
import com.vmloft.develop.library.tools.widget.barrage.VMViewCreator

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

        initFilter()

        initEmotion()

        // 下一波
        mBinding.homeNextTV.setOnClickListener { mViewModel.matchList(selfMatch.filterGender, selfMatch.type, mPage) }

        // 匹配项点击处理
        // 缘分匹配
        mBinding.homeDestinyLL.setOnClickListener { startMatch(0) }
        // 快速聊天
        mBinding.homeFastLL.setOnClickListener { startMatch(1) }
        // 解忧房
        mBinding.homeChatRoomLL.setOnClickListener { CRouter.go(AppRouter.appRoomList) }
        // 秘密枯井
        mBinding.homeSecretLL.setOnClickListener { CRouter.go(AppRouter.appMatchSecret) }
        // 心愿古树
        mBinding.homeWishLL.setOnClickListener { }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        mUser = SignManager.getCurrUser() ?: User()
        selfMatch = Match("selfMatch", mUser, gender = mUser.gender, filterGender = -1)

        setupMatchFilter()
        setupMatchEmotion()

        // 获取自己的匹配数据
        mViewModel.selfMatch()

        LDEventBus.observe(this, Constants.userInfoEvent, User::class.java, {
            mUser = it
            selfMatch.gender = mUser.gender
            if (selfMatch.content.isNullOrEmpty() && mUser.nickname.isNotEmpty()) {
                selfMatch.content = "嗨😉 我是 ${mUser.nickname}"
            }

            mBinding.homeEmotionET.setText(selfMatch.content)
        })
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "selfMatch") {
            model.data?.let {
                selfMatch = it as Match
                setupMatchFilter()
                setupMatchEmotion()
            }

            // 请求匹配数据集
            mViewModel.matchList(selfMatch.filterGender)
        }
        if (model.type == "matchList") {
            val paging = model.data as RPaging<Match>
            if (paging.currentCount + paging.page * paging.limit >= paging.totalCount) {
                mPage = CConstants.defaultPage
            } else {
                mPage++
            }
            dataList.clear()
            paging.data.map {
                // 因为有注销功能，查询到的匹配信息可能没有用户信息，前端这里做下保护
                if (it.user != null && it.user.id.isNullOrEmpty()) {
                    dataList.add(it)
                    if (it.user.id != mUser.id) {
                        CacheManager.putUser(it.user)
                    }
                }
            }
            setupBarrage()
        }
    }

    /**
     * 初始化过滤相关
     */
    private fun initFilter() {
        setTopIcon(R.drawable.ic_filter)
        setTopIconListener { mBinding.homeFilterMaskLL.visibility = View.VISIBLE }
        mBinding.homeFilterMaskLL.setOnClickListener { saveMatchFilter() }
        mBinding.homeFilterAllLL.setOnClickListener { changeMatchFilter(-1) }
        mBinding.homeFilterWomanLL.setOnClickListener { changeMatchFilter(0) }
        mBinding.homeFilterManLL.setOnClickListener { changeMatchFilter(1) }
    }

    /**
     * 加载心情内容
     */
    private fun setupMatchFilter() {
        mBinding.homeFilterAllLL.isSelected = selfMatch.filterGender == -1
        mBinding.homeFilterWomanLL.isSelected = selfMatch.filterGender == 0
        mBinding.homeFilterManLL.isSelected = selfMatch.filterGender == 1
    }

    /**
     * 修改匹配过滤设置
     */
    private fun changeMatchFilter(gender: Int) {
        selfMatch.filterGender = gender
        setupMatchFilter()
        saveMatchFilter()
    }

    /**
     * 保存匹配过滤设置
     */
    private fun saveMatchFilter() {
        mBinding.homeFilterMaskLL.visibility = View.GONE
        mViewModel.setSelfMatch(selfMatch)

        val params = mutableMapOf<String, Any>()
        params["filter"] = selfMatch.filterGender // 过滤选项 0-女 1-男 2-不限
        ReportManager.reportEvent(ReportConstants.eventChangeFilter, params)
    }

    /**
     * 初始化心情相关
     */
    private fun initEmotion() {
        val view = LayoutInflater.from(context).inflate(R.layout.widget_top_emtoion_view, null)
        emotionIV = view.findViewById(R.id.emotionIV)
        emotionTV = view.findViewById(R.id.emotionTV)
        setTopEndView(view)
        view.setOnClickListener { mBinding.homeEmotionMaskLL.visibility = View.VISIBLE }
        // 设置表情面板事件
        mBinding.homeEmotionMaskLL.setOnClickListener { saveMatchEmotion() }
        mBinding.homeEmotionHappyLL.setOnClickListener { changeMatchEmotion(0) }
        mBinding.homeEmotionNormalLL.setOnClickListener { changeMatchEmotion(1) }
        mBinding.homeEmotionSadLL.setOnClickListener { changeMatchEmotion(2) }
        mBinding.homeEmotionAngerLL.setOnClickListener { changeMatchEmotion(3) }
        mBinding.homeEmotionET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                selfMatch.content = s.toString().trim()
            }
        })
    }

    /**
     * 加载心情内容
     */
    private fun setupMatchEmotion() {
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

        mBinding.homeEmotionHappyLL.isSelected = selfMatch.emotion == 0
        mBinding.homeEmotionNormalLL.isSelected = selfMatch.emotion == 1
        mBinding.homeEmotionSadLL.isSelected = selfMatch.emotion == 2
        mBinding.homeEmotionAngerLL.isSelected = selfMatch.emotion == 3
        mBinding.homeEmotionET.setText(selfMatch.content)
    }

    /**
     * 修改匹配心情
     */
    private fun changeMatchEmotion(emotion: Int) {
        selfMatch.emotion = emotion
        setupMatchEmotion()
        saveMatchEmotion()
    }

    /**
     * 保存匹配心情数据
     */
    private fun saveMatchEmotion() {
        mBinding.homeEmotionMaskLL.visibility = View.GONE
        mViewModel.setSelfMatch(selfMatch)
        // 隐藏键盘
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 切换软键盘的显示与隐藏
        // imm.toggleSoftInputFromWindow(mInputET.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN, InputMethodManager.HIDE_NOT_ALWAYS);
        // 隐藏软键盘
        imm.hideSoftInputFromWindow(view?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        val params = mutableMapOf<String, Any>()
        params["emotion"] = selfMatch.emotion // 心情 0-开心 1-平淡 2-难过 3-愤怒
        ReportManager.reportEvent(ReportConstants.eventChangeEmotion, params)
    }

    /**
     * 装载弹幕
     */
    fun setupBarrage() {
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
     * 开始随机获取一个匹配对象
     */
    private fun startMatch(type: Int = 0) {
        if (mUser.avatar.isNullOrEmpty() || mUser.nickname.isNullOrEmpty()) {
            return CRouter.go(AppRouter.appPersonalInfoGuide)
        }
        // 跳转动画匹配界面
        CRouter.go(AppRouter.appMatchAnim, type, obj0 =  selfMatch)
        // 上报匹配
        if (type == 0) {
            ReportManager.reportEvent(ReportConstants.eventDestinyMatch)
        } else if (type == 1) {
            ReportManager.reportEvent(ReportConstants.eventFastChat)
        }
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
     * -----------------------------------------------------------------
     * 弹幕构建器
     */
    class ViewCreator : VMViewCreator<Match> {
        override fun layoutId(): Int = R.layout.item_barrage_view

        override fun onBind(view: View, bean: Match) {
            val barrageItemIV = view.findViewById<ImageView>(R.id.barrageItemIV)
            val barrageItemTV = view.findViewById<TextView>(R.id.barrageItemTV)

            IMGLoader.loadAvatar(barrageItemIV, bean.user.avatar)
            barrageItemTV.text = bean.content

            view.setOnClickListener { CRouter.go(AppRouter.appUserInfo, obj0 =  bean.user) }
        }
    }
}