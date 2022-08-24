package com.vmloft.develop.app.template.ui.main.mine.info

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityPersonalInfoBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.PermissionManager
import com.vmloft.develop.library.base.event.LDEventBus
import com.vmloft.develop.library.base.router.CRouter
import com.vmloft.develop.library.base.utils.showBar
import com.vmloft.develop.library.base.widget.CommonDialog
import com.vmloft.develop.library.data.bean.Profession
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.data.common.DConstants
import com.vmloft.develop.library.data.viewmodel.UserViewModel
import com.vmloft.develop.library.image.IMGChoose
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.tools.utils.VMColor
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.logger.VMLog

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2020/08/01 22:56
 * 描述：个人信息编辑
 */
@Route(path = AppRouter.appPersonalInfo)
class PersonalInfoActivity : BVMActivity<ActivityPersonalInfoBinding, UserViewModel>() {

    private lateinit var selfUser: User

    private lateinit var mCoverIV: ImageView
    private lateinit var mAvatarIV: ImageView

    private val mProfessionList = mutableListOf<Profession>()

    override fun initVB() = ActivityPersonalInfoBinding.inflate(layoutInflater)

    override fun initVM(): UserViewModel = getViewModel()

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.settings_info)

        mCoverIV = IMGLoader.createView(this)
        val coverSize = VMDimen.dp2px(40)
        mCoverIV.layoutParams = LinearLayout.LayoutParams(coverSize, coverSize)
        mBinding.infoCoverLV.setRightView(mCoverIV)

        mAvatarIV = IMGLoader.createView(this)
        val avatarSize = VMDimen.dp2px(36)
        mAvatarIV.layoutParams = LinearLayout.LayoutParams(avatarSize, avatarSize)
        mBinding.infoAvatarLV.setRightView(mAvatarIV)

        val qrIV = IMGLoader.createView(this)
        val qrSize = VMDimen.dp2px(16)
        qrIV.layoutParams = LinearLayout.LayoutParams(qrSize, qrSize)
        qrIV.setImageResource(R.drawable.ic_qrcode)
        mBinding.infoQRCodeLV.setRightView(qrIV)

        mBinding.infoCoverLV.setOnClickListener { chooseCover() }
        mBinding.infoAvatarLV.setOnClickListener { chooseAvatar() }
        mBinding.infoUsernameLV.setOnClickListener {
            if (selfUser.username.isNullOrEmpty()) {
                CRouter.go(AppRouter.appEditUsername)
            } else {
                showBar(R.string.info_perfect_username_hint)
            }
        }
        mBinding.infoQRCodeLV.setOnClickListener {
            if (selfUser.username.isNullOrEmpty()) {
                showBar(R.string.info_qr_username_hint)
            } else {
                CRouter.go(AppRouter.appMineQRCode)
            }
        }
        mBinding.infoNicknameLV.setOnClickListener { CRouter.go(AppRouter.appEditNickname, str0 = selfUser.nickname) }
        mBinding.infoSignatureLV.setOnClickListener { CRouter.go(AppRouter.appEditSignature, str0 = selfUser.signature) }
        mBinding.infoPhoneLV.setOnClickListener { }
        mBinding.infoEmailLV.setOnClickListener { CRouter.go(AppRouter.appBindEmail) }

        // 监听用户信息变化
        LDEventBus.observe(this, DConstants.Event.userInfo, User::class.java) { user ->
            selfUser = user
            bindInfo()
        }
    }

    override fun initData() {
        selfUser = SignManager.getSignUser()

        bindInfo()
        bindPicker()

        mViewModel.userInfo()
        mViewModel.professionList()
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "userInfo") {
            SignManager.setSignUser(model.data as User)
        }
        if (model.type == "professionList") {
            mProfessionList.addAll((model.data as RPaging<Profession>).data)
            bindProfessionData()
        }
        if (model.type == "updateInfo") {
            SignManager.setSignUser(model.data as User)
        }
        if (model.type == "updateCover") {
            SignManager.setSignUser(model.data as User)
        }
        if (model.type == "updateAvatar") {
            SignManager.setSignUser(model.data as User)
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
        IMGLoader.loadCover(mCoverIV, selfUser.cover, isRadius = true)
        IMGLoader.loadAvatar(mAvatarIV, selfUser.avatar)

        mBinding.infoUsernameLV.setCaption(if (selfUser.username.isNullOrEmpty()) VMStr.byRes(R.string.info_not_set) else selfUser.username)
        mBinding.infoNicknameLV.setCaption(if (selfUser.nickname.isNullOrEmpty()) VMStr.byRes(R.string.info_nickname_default) else selfUser.nickname)
        mBinding.infoSignatureLV.setCaption(if (selfUser.signature.isNullOrEmpty()) VMStr.byRes(R.string.info_signature_default) else selfUser.signature)
        mBinding.infoBirthdayLV.setCaption(selfUser.birthday)

        if (selfUser.gender == 1) {
            mBinding.infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_man))
        } else if (selfUser.gender == 0) {
            mBinding.infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_woman))
        } else {
            mBinding.infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_unknow))
        }

        mBinding.infoPhoneLV.setCaption(if (selfUser.phone.isNullOrEmpty()) VMStr.byRes(R.string.info_phone_null) else selfUser.phone)
        mBinding.infoEmailLV.setCaption(if (selfUser.email.isNullOrEmpty()) VMStr.byRes(R.string.info_email_null) else selfUser.email)
        mBinding.infoAddressLV.setCaption(if (selfUser.address.isNullOrEmpty()) VMStr.byRes(R.string.info_not_set) else selfUser.address)
        mBinding.infoProfessionLV.setCaption(if (selfUser.profession.title.isNullOrEmpty()) VMStr.byRes(R.string.info_not_set) else selfUser.profession.title)
    }

    /**
     * 初始化生日选择
     */
    private fun bindPicker() {
        mBinding.infoBirthdayLV.setOnClickListener {
            mBinding.pickerLayout.pickerTitleTV.setText(R.string.info_birthday)
            mBinding.pickerLayout.pickerMaskLL.visibility = View.VISIBLE
            mBinding.pickerLayout.pickerBirthdayView.visibility = View.VISIBLE
            mBinding.pickerLayout.pickerGenderView.visibility = View.GONE
            mBinding.pickerLayout.pickerAreaView.visibility = View.GONE
            mBinding.pickerLayout.pickerProfessionView.visibility = View.GONE
        }
        mBinding.infoGenderLV.setOnClickListener {
            if (selfUser.gender != 2) {
                showBar(R.string.info_perfect_gender_hint)
                return@setOnClickListener
            }
            mBinding.pickerLayout.pickerTitleTV.setText(R.string.info_gender)
            mBinding.pickerLayout.pickerMaskLL.visibility = View.VISIBLE
            mBinding.pickerLayout.pickerBirthdayView.visibility = View.GONE
            mBinding.pickerLayout.pickerGenderView.visibility = View.VISIBLE
            mBinding.pickerLayout.pickerAreaView.visibility = View.GONE
            mBinding.pickerLayout.pickerProfessionView.visibility = View.GONE
        }
        mBinding.infoAddressLV.setOnClickListener {
            mBinding.pickerLayout.pickerTitleTV.setText(R.string.info_address)
            mBinding.pickerLayout.pickerMaskLL.visibility = View.VISIBLE
            mBinding.pickerLayout.pickerBirthdayView.visibility = View.GONE
            mBinding.pickerLayout.pickerGenderView.visibility = View.GONE
            mBinding.pickerLayout.pickerAreaView.visibility = View.VISIBLE
            mBinding.pickerLayout.pickerProfessionView.visibility = View.GONE
        }
        mBinding.infoProfessionLV.setOnClickListener {
            mBinding.pickerLayout.pickerTitleTV.setText(R.string.info_profession)
            mBinding.pickerLayout.pickerMaskLL.visibility = View.VISIBLE
            mBinding.pickerLayout.pickerBirthdayView.visibility = View.GONE
            mBinding.pickerLayout.pickerGenderView.visibility = View.GONE
            mBinding.pickerLayout.pickerAreaView.visibility = View.GONE
            mBinding.pickerLayout.pickerProfessionView.visibility = View.VISIBLE
        }

        mBinding.pickerLayout.pickerMaskLL.setOnClickListener { mBinding.pickerLayout.pickerMaskLL.visibility = View.GONE }
        mBinding.pickerLayout.pickerConfirmTV.setOnClickListener {
            when {
                mBinding.pickerLayout.pickerBirthdayView.isShown -> updateBirthday()
                mBinding.pickerLayout.pickerGenderView.isShown -> showChangeGenderDialog() // updateGender()
                mBinding.pickerLayout.pickerAreaView.isShown -> updateAddress()
                mBinding.pickerLayout.pickerProfessionView.isShown -> updateProfession()
            }
            mBinding.pickerLayout.pickerMaskLL.visibility = View.GONE
        }

        /**
         * 生日选择器
         */
        mBinding.pickerLayout.pickerBirthdayView.wheelYearPicker.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
        mBinding.pickerLayout.pickerBirthdayView.wheelMonthPicker.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
        mBinding.pickerLayout.pickerBirthdayView.wheelDayPicker.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)

        mBinding.pickerLayout.pickerBirthdayView.isCyclic = true
        mBinding.pickerLayout.pickerBirthdayView.itemTextColor = VMColor.byRes(R.color.app_desc)
        mBinding.pickerLayout.pickerBirthdayView.selectedItemTextColor = VMColor.byRes(R.color.app_main)
        mBinding.pickerLayout.pickerBirthdayView.setIndicator(true)
        mBinding.pickerLayout.pickerBirthdayView.indicatorSize = VMDimen.dp2px(1)
        mBinding.pickerLayout.pickerBirthdayView.indicatorColor = VMColor.byRes(R.color.app_accent)
        mBinding.pickerLayout.pickerBirthdayView.setCurtain(false)
        mBinding.pickerLayout.pickerBirthdayView.setAtmospheric(true)

        mBinding.pickerLayout.pickerBirthdayView.setYearFrame(1900, 3000)
        val ymdArray = VMStr.strToArray(selfUser.birthday, "-")
        VMLog.d("${selfUser.birthday} ${ymdArray.size} $ymdArray")
        mBinding.pickerLayout.pickerBirthdayView.selectedYear = ymdArray[0].toInt()
        mBinding.pickerLayout.pickerBirthdayView.wheelYearPicker.setSelectedItemPosition(ymdArray[0].toInt() - 1900, false)
        mBinding.pickerLayout.pickerBirthdayView.selectedMonth = ymdArray[1].toInt()
        mBinding.pickerLayout.pickerBirthdayView.selectedDay = ymdArray[2].toInt()

        /**
         * 性别选择器
         */
        mBinding.pickerLayout.pickerGenderView.data = listOf("貌美如花", "英俊潇洒", "保密")
        mBinding.pickerLayout.pickerGenderView.setSelectedItemPosition(selfUser.gender, false)

        /**
         * 地址选择器
         */

        /**
         * 职业选择器
         */
        mBinding.pickerLayout.pickerProfessionView.data = mProfessionList
    }

    /**
     * 绑定职业选择器数据
     */
    private fun bindProfessionData() {
        val list = mProfessionList.map { it.title }
        mBinding.pickerLayout.pickerProfessionView.data = list
        val position = mProfessionList.indexOf(selfUser.profession)
        mBinding.pickerLayout.pickerProfessionView.setSelectedItemPosition(position, false)
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
        var birthday = "${mBinding.pickerLayout.pickerBirthdayView.currentYear}-"
        birthday += if (mBinding.pickerLayout.pickerBirthdayView.currentMonth <= 9) {
            "0${mBinding.pickerLayout.pickerBirthdayView.currentMonth}-"
        } else {
            "${mBinding.pickerLayout.pickerBirthdayView.currentMonth}-"
        }
        birthday += if (mBinding.pickerLayout.pickerBirthdayView.currentDay <= 9) {
            "0${mBinding.pickerLayout.pickerBirthdayView.currentDay}"
        } else {
            "${mBinding.pickerLayout.pickerBirthdayView.currentDay}"
        }

        mBinding.infoBirthdayLV.setCaption(birthday)

        val params: MutableMap<String, Any> = mutableMapOf()
        params["birthday"] = birthday
        mViewModel.updateInfo(params)
    }

    /**
     * 更新性别
     */
    private fun updateGender() {
        val gender = mBinding.pickerLayout.pickerGenderView.currentItemPosition

        when (selfUser.gender) {
            1 -> mBinding.infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_man))
            0 -> mBinding.infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_woman))
            else -> mBinding.infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_unknow))
        }

        val params: MutableMap<String, Any> = mutableMapOf()
        params["gender"] = gender
        mViewModel.updateInfo(params)
    }

    /**
     * 更新地址
     */
    private fun updateAddress() {
        val address = "${mBinding.pickerLayout.pickerAreaView.getProvince()}-${mBinding.pickerLayout.pickerAreaView.getCity()}-${mBinding.pickerLayout.pickerAreaView.getArea()}"
        val params: MutableMap<String, Any> = mutableMapOf()
        params["address"] = address
        mViewModel.updateInfo(params)
    }

    /**
     * 更新职业
     */
    private fun updateProfession() {
        val profession = mProfessionList[mBinding.pickerLayout.pickerProfessionView.currentItemPosition].id

        val params: MutableMap<String, Any> = mutableMapOf()
        params["profession"] = profession
        mViewModel.updateInfo(params)
    }

    /**
     * 修改性别对话框
     */
    private fun showChangeGenderDialog() {
        mDialog = CommonDialog(this)
        (mDialog as CommonDialog).let { dialog ->
            dialog.setContent(R.string.info_perfect_gender_hint)
            dialog.setPositive(listener = {
                updateGender()
            })
            dialog.show()
        }
    }

}