package com.vmloft.develop.app.template.ui.main.mine.info

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.Constants
import com.vmloft.develop.library.common.common.PermissionManager
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityPersonalInfoBinding
import com.vmloft.develop.library.common.event.LDEventBus
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.app.template.request.bean.Profession
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.image.IMGChoose
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.widget.CommonDialog
import com.vmloft.develop.library.tools.utils.VMColor
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.logger.VMLog

import kotlinx.android.synthetic.main.activity_personal_info.*
import kotlinx.android.synthetic.main.widget_picker_layout.*

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/08/01 22:56
 * 描述：个人信息编辑
 */
@Route(path = AppRouter.appPersonalInfo)
class PersonalInfoActivity : BVMActivity<InfoViewModel>() {

    private lateinit var mUser: User

    private lateinit var mCoverIV: ImageView
    private lateinit var mAvatarIV: ImageView

    private val mProfessionList = mutableListOf<Profession>()

    override fun initVM(): InfoViewModel = getViewModel()

    override fun layoutId(): Int = R.layout.activity_personal_info

    override fun initUI() {
        super.initUI()
        (mBinding as ActivityPersonalInfoBinding).viewModel = mViewModel

        setTopTitle(R.string.settings_info)

        mCoverIV = IMGLoader.createView(this)
        val coverSize = VMDimen.dp2px(40)
        mCoverIV.layoutParams = LinearLayout.LayoutParams(coverSize, coverSize)
        infoCoverLV.setRightView(mCoverIV)

        mAvatarIV = IMGLoader.createView(this)
        val avatarSize = VMDimen.dp2px(36)
        mAvatarIV.layoutParams = LinearLayout.LayoutParams(avatarSize, avatarSize)
        infoAvatarLV.setRightView(mAvatarIV)

        infoCoverLV.setOnClickListener { chooseCover() }
        infoAvatarLV.setOnClickListener { chooseAvatar() }
        infoUsernameLV.setOnClickListener {
            if (mUser.username.isNullOrEmpty()) CRouter.go(AppRouter.appEditUsername)
        }
        infoNicknameLV.setOnClickListener { AppRouter.goEditNickname(mUser.nickname) }
        infoSignatureLV.setOnClickListener { AppRouter.goEditSignature(mUser.signature) }
        infoPhoneLV.setOnClickListener { }
        infoEmailLV.setOnClickListener { CRouter.go(AppRouter.appBindEmail) }
        infoAuthStatusLV.setOnClickListener { checkPersonalAuth() }

        LDEventBus.observe(this, Constants.userInfoEvent, User::class.java, { user ->
            mUser = user
            bindInfo()
        })
    }

    override fun initData() {
        mUser = SignManager.getCurrUser() ?: return finish()

        bindInfo()
        bindPicker()

        mViewModel.getUserInfo()
        mViewModel.loadProfessionList()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "userInfo") {
            SignManager.setCurrUser(model.data as User)
        }
        if (model.type == "professionList") {
            mProfessionList.addAll((model.data as RPaging<Profession>).data)
            bindProfessionData()
        }
        if (model.type == "updateInfo") {
            SignManager.setCurrUser(model.data as User)
        }
        if (model.type == "updateCover") {
            SignManager.setCurrUser(model.data as User)
        }
        if (model.type == "updateAvatar") {
            SignManager.setCurrUser(model.data as User)
        }
    }

    override fun onResume() {
        super.onResume()
        bindInfo()
    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        IMGLoader.loadCover(mCoverIV, mUser.cover, isRadius = true)
        IMGLoader.loadAvatar(mAvatarIV, mUser.avatar)

        infoUsernameLV.setCaption(if (mUser.username.isNullOrEmpty()) VMStr.byRes(R.string.info_not_set) else mUser.username)
        infoNicknameLV.setCaption(if (mUser.nickname.isNullOrEmpty()) VMStr.byRes(R.string.info_nickname_default) else mUser.nickname)
        infoSignatureLV.setCaption(if (mUser.signature.isNullOrEmpty()) VMStr.byRes(R.string.info_signature_default) else mUser.signature)
        infoBirthdayLV.setCaption(mUser.birthday)

        if (mUser.gender == 1) {
            infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_man))
        } else if (mUser.gender == 0) {
            infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_woman))
        } else {
            infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_unknow))
        }

        infoPhoneLV.setCaption(if (mUser.phone.isNullOrEmpty()) VMStr.byRes(R.string.info_phone_null) else mUser.phone)
        infoEmailLV.setCaption(if (mUser.email.isNullOrEmpty()) VMStr.byRes(R.string.info_email_null) else mUser.email)
        infoAuthStatusLV.setCaption(if (mUser.idCardNumber.isNullOrEmpty()) VMStr.byRes(R.string.info_auth_no) else VMStr.byRes(R.string.info_auth_yes))
        infoAddressLV.setCaption(if (mUser.address.isNullOrEmpty()) VMStr.byRes(R.string.info_not_set) else mUser.address)
        infoProfessionLV.setCaption(if (mUser.profession == null) VMStr.byRes(R.string.info_not_set) else mUser.profession?.title)
    }

    /**
     * 初始化生日选择
     */
    private fun bindPicker() {
        infoBirthdayLV.setOnClickListener {
            pickerTitleTV.setText(R.string.info_birthday)
            pickerMaskLL.visibility = View.VISIBLE
            pickerBirthdayView.visibility = View.VISIBLE
            pickerGenderView.visibility = View.GONE
            pickerAreaView.visibility = View.GONE
            pickerProfessionView.visibility = View.GONE
        }
        infoGenderLV.setOnClickListener {
            pickerTitleTV.setText(R.string.info_gender)
            pickerMaskLL.visibility = View.VISIBLE
            pickerBirthdayView.visibility = View.GONE
            pickerGenderView.visibility = View.VISIBLE
            pickerAreaView.visibility = View.GONE
            pickerProfessionView.visibility = View.GONE
        }
        infoAddressLV.setOnClickListener {
            pickerTitleTV.setText(R.string.info_address)
            pickerMaskLL.visibility = View.VISIBLE
            pickerBirthdayView.visibility = View.GONE
            pickerGenderView.visibility = View.GONE
            pickerAreaView.visibility = View.VISIBLE
            pickerProfessionView.visibility = View.GONE
        }
        infoProfessionLV.setOnClickListener {
            pickerTitleTV.setText(R.string.info_profession)
            pickerMaskLL.visibility = View.VISIBLE
            pickerBirthdayView.visibility = View.GONE
            pickerGenderView.visibility = View.GONE
            pickerAreaView.visibility = View.GONE
            pickerProfessionView.visibility = View.VISIBLE
        }

        pickerMaskLL.setOnClickListener { pickerMaskLL.visibility = View.GONE }
        pickerConfirmTV.setOnClickListener {
            when {
                pickerBirthdayView.isShown -> updateBirthday()
                pickerGenderView.isShown -> updateGender()
                pickerAreaView.isShown -> updateAddress()
                pickerProfessionView.isShown -> updateProfession()
            }
            pickerMaskLL.visibility = View.GONE
        }

        /**
         * 生日选择器
         */
        pickerBirthdayView.wheelYearPicker.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
        pickerBirthdayView.wheelMonthPicker.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
        pickerBirthdayView.wheelDayPicker.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)

        pickerBirthdayView.isCyclic = true
        pickerBirthdayView.itemTextColor = VMColor.byRes(R.color.app_desc)
        pickerBirthdayView.selectedItemTextColor = VMColor.byRes(R.color.app_main)
        pickerBirthdayView.setIndicator(true)
        pickerBirthdayView.indicatorSize = VMDimen.dp2px(1)
        pickerBirthdayView.indicatorColor = VMColor.byRes(R.color.app_accent)
        pickerBirthdayView.setCurtain(false)
        pickerBirthdayView.setAtmospheric(true)

        pickerBirthdayView.setYearFrame(1900, 3000)
        val ymdArray = VMStr.strToArray(mUser.birthday, "-")
        VMLog.d("${mUser.birthday} ${ymdArray.size} $ymdArray")
        pickerBirthdayView.selectedYear = ymdArray[0].toInt()
        pickerBirthdayView.wheelYearPicker.setSelectedItemPosition(ymdArray[0].toInt() - 1900, false)
        pickerBirthdayView.selectedMonth = ymdArray[1].toInt()
        pickerBirthdayView.selectedDay = ymdArray[2].toInt()

        /**
         * 性别选择器
         */
        pickerGenderView.data = listOf("女", "男", "保密")
        pickerGenderView.setSelectedItemPosition(mUser.gender, false)

        /**
         * 地址选择器
         */

        /**
         * 职业选择器
         */
        pickerProfessionView.data = mProfessionList
    }

    /**
     * 检查进行个人认证
     */
    private fun checkPersonalAuth() {
        if (mUser.realName.isNullOrEmpty() || mUser.idCardNumber.isNullOrEmpty()) {
            CRouter.go(AppRouter.appPersonalAuth)
        }
    }

    /**
     * 绑定职业选择器数据
     */
    private fun bindProfessionData() {
        val list = mProfessionList.map { it.title }
        pickerProfessionView.data = list
        var position = 0
        mUser.profession?.let {
            position = mProfessionList.indexOf(it)
        }
        pickerProfessionView.setSelectedItemPosition(position, false)
    }

    /**
     * 选择封面
     */
    private fun chooseCover() {
        if (!PermissionManager.storagePermission(this)) return

        IMGChoose.singleCrop(this) { result ->
            mViewModel.updateCover(result)
        }
    }

    /**
     * 选择头像
     */
    private fun chooseAvatar() {
        if (!PermissionManager.storagePermission(this)) return

        IMGChoose.singleCrop(this) { result ->
            mViewModel.updateAvatar(result)
        }
    }

    /**
     * 更新生日
     */
    private fun updateBirthday() {
        var birthday = "${pickerBirthdayView.currentYear}-"
        birthday += if (pickerBirthdayView.currentMonth <= 9) {
            "0${pickerBirthdayView.currentMonth}-"
        } else {
            "${pickerBirthdayView.currentMonth}-"
        }
        birthday += if (pickerBirthdayView.currentDay <= 9) {
            "0${pickerBirthdayView.currentDay}"
        } else {
            "${pickerBirthdayView.currentDay}"
        }

        infoBirthdayLV.setCaption(birthday)

        val params: MutableMap<String, Any> = mutableMapOf()
        params["birthday"] = birthday
        mViewModel.updateInfo(params)
    }

    /**
     * 更新性别
     */
    private fun updateGender() {
        val gender = pickerGenderView.currentItemPosition

        when (mUser.gender) {
            1 -> infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_man))
            0 -> infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_woman))
            else -> infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_unknow))
        }

        val params: MutableMap<String, Any> = mutableMapOf()
        params["gender"] = gender
        mViewModel.updateInfo(params)
    }

    /**
     * 更新地址
     */
    private fun updateAddress() {
        val address = "${pickerAreaView.getProvince()}-${pickerAreaView.getCity()}-${pickerAreaView.getArea()}"
        val params: MutableMap<String, Any> = mutableMapOf()
        params["address"] = address
        mViewModel.updateInfo(params)
    }

    /**
     * 更新职业
     */
    private fun updateProfession() {
        val profession = mProfessionList[pickerProfessionView.currentItemPosition].id

        val params: MutableMap<String, Any> = mutableMapOf()
        params["profession"] = profession
        mViewModel.updateInfo(params)
    }

    /**
     * 完善资料对话框
     */
    private fun showDialog() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.info_perfect_hint)
            dialog.setNegative("")
            dialog.setPositive(VMStr.byRes(R.string.vm_i_know))
            dialog.show()
        }
    }

    override fun onBackPressed() {
        if (mUser.avatar.isNullOrEmpty() || mUser.nickname.isNullOrEmpty()) {
            showDialog()
        } else {
            super.onBackPressed()
        }
    }
}