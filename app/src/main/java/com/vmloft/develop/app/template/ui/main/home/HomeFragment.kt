package com.vmloft.develop.app.template.ui.main.home

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.CacheManager
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.FragmentHomeBinding
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.app.template.request.bean.Match
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.app.template.ui.widget.MatchDialog
import com.vmloft.develop.library.common.base.BVMFragment
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.common.utils.errorBar
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.widget.VMFloatMenu
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
    private lateinit var homeBarrageView: VMBarrageView<Match>

    // 弹幕数据
    private val mDataList: MutableList<Match> = mutableListOf()

    private var mPage: Int = CConstants.defaultPage
    private val mLimit: Int = CConstants.defaultLimit

    // 弹出菜单
    private lateinit var floatMenu: VMFloatMenu
    private var viewX: Int = 0
    private var viewY: Int = 0

    private lateinit var emotionIV: ImageView
    private lateinit var emotionTV: TextView


    override fun initVM(): MatchViewModel = getViewModel()

    override fun layoutId() = R.layout.fragment_home

    override fun initUI() {
        super.initUI()

        (mBinding as FragmentHomeBinding).viewModel = mViewModel

        homeRandomLL.setOnClickListener { startMatch(0) }
        homeEmotionLL.setOnClickListener { CRouter.go(AppRouter.appMatch) }
        homeChatRoomLL.setOnClickListener { CRouter.go(AppRouter.appRoomList) }
        homeFastLL.setOnClickListener { startMatch(1) }
        homeSecretLL.setOnClickListener { }
        homeWishLL.setOnClickListener { }
    }


    override fun initData() {
        mUser = SignManager.getCurrUser() ?: User()
        selfMatch = Match("", mUser, 0, 0, "")
        // 请求匹配数据集
        mViewModel.getMatchList(mPage, mLimit)

        viewX = VMDimen.screenWidth

        homeBarrageView = view?.findViewById(R.id.homeBarrageView) ?: VMBarrageView(activity)
        homeBarrageView.setCreator(ViewCreator())
            .setMaxSize(50)
            .create(mDataList)
            .start()

        setupEmotion()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "submitMatch") {
            selfMatch = model.data as Match
        }
        if (model.type == "matchOne") {
            val match = model.data as Match
            CacheManager.putUser(match.user)
            IMManager.goChat(match.user.id)
        }
        if (model.type == "matchList") {
            val paging = model.data as RPaging<Match>
            if (paging.currentCount + paging.page * paging.limit >= paging.totalCount) {
                mPage = CConstants.defaultPage
            } else {
                mPage++
            }
            paging.data.forEach {
                if (it.user.id != mUser.id) {
                    CacheManager.putUser(it.user)
                    homeBarrageView.addBarrage(it)
                }
            }
        }
    }

    /**
     * 加载心情 View
     */
    private fun setupEmotion() {
        floatMenu = VMFloatMenu(requireActivity())
        floatMenu.setItemClickListener(object : VMFloatMenu.IItemClickListener() {
            override fun onItemClick(id: Int) {
                selfMatch.emotion = id
                bindEmotion()
            }
        })

        val view = LayoutInflater.from(context).inflate(R.layout.widget_top_emtoion_view, null)
        setTopEndView(view)

        emotionIV = view.findViewById(R.id.emotionIV)
        emotionTV = view.findViewById(R.id.emotionTV)
        bindEmotion()

        view.setOnClickListener { showFloatMenu(view) }
    }

    private fun bindEmotion() {
        emotionIV.setImageResource(if (selfMatch.emotion == 0) R.drawable.ic_emotion_happy else if (selfMatch.emotion == 1) R.drawable.ic_emotion_normal else R.drawable.ic_emotion_sad)
        emotionTV.setText(if (selfMatch.emotion == 0) R.string.emotion_happy else if (selfMatch.emotion == 1) R.string.emotion_normal else R.string.emotion_sad)
    }

    /**
     * 弹出菜单
     */
    private fun showFloatMenu(view: View) {
        floatMenu.clearAllItem()
        floatMenu.addItem(VMFloatMenu.ItemBean(0, "开心", itemIcon = R.drawable.ic_emotion_happy))
        floatMenu.addItem(VMFloatMenu.ItemBean(1, "平淡", itemIcon = R.drawable.ic_emotion_normal))
        floatMenu.addItem(VMFloatMenu.ItemBean(2, "难过", itemIcon = R.drawable.ic_emotion_sad))

        floatMenu.showAtLocation(view, viewX, viewY)
    }

    /**
     * 开始随机获取一个匹配对象
     */
    fun startMatch(type: Int = 0) {
        if (!selfMatch.content.isNullOrEmpty()) {
            // 首先提交自己的匹配数据
            mViewModel.submitMatch(selfMatch.content, selfMatch.emotion, selfMatch.type)
            if (type == 0) {
                mViewModel.getMatchOne()
            } else {
                CRouter.go(AppRouter.appMatchFast)
            }
            return
        }

        mDialog = MatchDialog(requireContext())
        (mDialog as MatchDialog).let { dialog ->
            dialog.setPositive(listener = {
                val content = dialog.inputContent
                if (content.isNullOrEmpty()) {
                    errorBar(R.string.input_not_null)
                } else {
                    selfMatch.content = content
                    // 首先提交自己的匹配数据
                    mViewModel.submitMatch(content, selfMatch.emotion, selfMatch.type)
                    if (type == 0) {
                        mViewModel.getMatchOne()
                    }else{
                        CRouter.go(AppRouter.appMatchFast)
                    }
                }
            })
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()
        homeBarrageView.resume()
    }

    override fun onPause() {
        super.onPause()
        homeBarrageView.pause()
    }

    override fun onDestroy() {
        homeBarrageView.stop()
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