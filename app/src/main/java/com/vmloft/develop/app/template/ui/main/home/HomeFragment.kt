package com.vmloft.develop.app.template.ui.main.home

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
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
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.report.ReportManager
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.tools.widget.barrage.VMBarrageView
import com.vmloft.develop.library.tools.widget.barrage.VMViewCreator

import kotlinx.android.synthetic.main.fragment_home.*

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/05/02 11:54
 * 描述：主页
 */
class HomeFragment : BVMFragment<MatchViewModel>() {

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


    override fun initVM(): MatchViewModel = getViewModel()

    override fun layoutId() = R.layout.fragment_home

    override fun initUI() {
        super.initUI()

        (mBinding as FragmentHomeBinding).viewModel = mViewModel

        initFilter()

        initEmotion()

        homeNextTV.setOnClickListener { mViewModel.getMatchList(selfMatch.filterGender, mPage) }

        // 匹配项点击处理
        homeDestinyLL.setOnClickListener { startMatch(0) }
        homeEmotionLL.setOnClickListener { startMatch(1) }
        homeFastLL.setOnClickListener { startMatch(2) }
        homeChatRoomLL.setOnClickListener { CRouter.go(AppRouter.appRoomList) }
        homeSecretLL.setOnClickListener { }
        homeWishLL.setOnClickListener { }
    }

    /**
     * 初始化数据
     */
    override fun initData() {
        mUser = SignManager.getCurrUser() ?: User()
        selfMatch = Match("selfMatch", mUser, 0, mUser.gender, "嗨😉 我是 ${mUser.nickname}")

        setupMatchFilter()
        setupMatchEmotion()

        // 获取自己的匹配数据
        mViewModel.getSelfMatch()

        LDEventBus.observe(this, Constants.userInfoEvent, User::class.java, {
            mUser = it
            selfMatch.gender = mUser.gender
            selfMatch.content = "嗨😉 我是 ${mUser.nickname}"

            homeEmotionET.setText(selfMatch.content)
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
            mViewModel.getMatchList(selfMatch.filterGender)
        }
        if (model.type == "matchList") {
            val paging = model.data as RPaging<Match>
            if (paging.currentCount + paging.page * paging.limit >= paging.totalCount) {
                mPage = CConstants.defaultPage
            } else {
                mPage++
            }
            dataList.clear()
            dataList.addAll(paging.data)
            dataList.forEach {
                if (it.user.id != mUser.id) {
                    CacheManager.putUser(it.user)
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
        setTopIconListener { homeFilterMaskLL.visibility = View.VISIBLE }
        homeFilterMaskLL.setOnClickListener { saveMatchFilter() }
        homeFilterAllLL.setOnClickListener { changeMatchFilter(-1) }
        homeFilterWomanLL.setOnClickListener { changeMatchFilter(0) }
        homeFilterManLL.setOnClickListener { changeMatchFilter(1) }
    }

    /**
     * 加载心情内容
     */
    private fun setupMatchFilter() {
        homeFilterAllLL.isSelected = selfMatch.filterGender == -1
        homeFilterWomanLL.isSelected = selfMatch.filterGender == 0
        homeFilterManLL.isSelected = selfMatch.filterGender == 1
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
        homeFilterMaskLL.visibility = View.GONE
        mViewModel.setSelfMatch(selfMatch)

        val params = mutableMapOf<String, Any>()
        params["filter"] = selfMatch.filterGender // 过滤选项 -1-不限 0-女 1-男
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
        view.setOnClickListener { homeEmotionMaskLL.visibility = View.VISIBLE }
        // 设置表情面板事件
        homeEmotionMaskLL.setOnClickListener { saveMatchEmotion() }
        homeEmotionHappyLL.setOnClickListener { changeMatchEmotion(0) }
        homeEmotionNormalLL.setOnClickListener { changeMatchEmotion(1) }
        homeEmotionSadLL.setOnClickListener { changeMatchEmotion(2) }
        homeEmotionAngerLL.setOnClickListener { changeMatchEmotion(3) }
        homeEmotionET.addTextChangedListener(object : TextWatcher {
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

        homeEmotionHappyLL.isSelected = selfMatch.emotion == 0
        homeEmotionNormalLL.isSelected = selfMatch.emotion == 1
        homeEmotionSadLL.isSelected = selfMatch.emotion == 2
        homeEmotionAngerLL.isSelected = selfMatch.emotion == 3
        homeEmotionET.setText(selfMatch.content)
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
        homeEmotionMaskLL.visibility = View.GONE
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
        homeBarrageViewLL.removeAllViews()

        // 重置弹幕控件
        barrageView = VMBarrageView(activity)
        val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        homeBarrageViewLL.addView(barrageView, lp)

        barrageView?.let {
            it.setCreator(ViewCreator()).setMaxSize(50).create(dataList).start()
        }
    }

    /**
     * 开始随机获取一个匹配对象
     */
    fun startMatch(type: Int = 0) {
        // 首先提交自己的匹配数据
        mViewModel.submitMatch(selfMatch)
        AppRouter.goMatch(type, selfMatch.filterGender)
        // 上报匹配
        if (type == 0) {
            ReportManager.reportEvent(ReportConstants.eventDestinyMatch)
        } else if (type == 2) {
            ReportManager.reportEvent(ReportConstants.eventFastChat)
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.let { CUtils.setDarkMode(it, false) }
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

            view.setOnClickListener { AppRouter.goUserInfo(bean.user) }
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        activity?.let {
            CUtils.setDarkMode(it, false)
        }
    }
}