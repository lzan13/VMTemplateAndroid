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
import com.vmloft.develop.app.template.request.viewmodel.UserViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BVMActivity
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.image.IMGChoose
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.router.CRouter
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.common.ui.widget.CommonDialog
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

    private lateinit var mUser: User

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

        mBinding.infoCoverLV.setOnClickListener { chooseCover() }
        mBinding.infoAvatarLV.setOnClickListener { chooseAvatar() }
        mBinding.infoUsernameLV.setOnClickListener {
            if (mUser.username.isNullOrEmpty()) {
                CRouter.go(AppRouter.appEditUsername)
            } else {
                showBar(R.string.info_perfect_username_hint)
            }
        }
        mBinding.infoNicknameLV.setOnClickListener { CRouter.go(AppRouter.appEditNickname, str0 = mUser.nickname) }
        mBinding.infoSignatureLV.setOnClickListener { CRouter.go(AppRouter.appEditSignature, str0 = mUser.signature) }
        mBinding.infoPhoneLV.setOnClickListener { }
        mBinding.infoEmailLV.setOnClickListener { CRouter.go(AppRouter.appBindEmail) }
        mBinding.infoAuthStatusLV.setOnClickListener { checkPersonalAuth() }

        LDEventBus.observe(this, Constants.userInfoEvent, User::class.java, { user ->
            mUser = user
            bindInfo()
        })
    }

    override fun initData() {
        mUser = SignManager.getCurrUser() ?: return finish()

        bindInfo()
        bindPicker()

        mViewModel.userInfo()
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

        mBinding.infoUsernameLV.setCaption(if (mUser.username.isNullOrEmpty()) VMStr.byRes(R.string.info_not_set) else mUser.username)
        mBinding.infoNicknameLV.setCaption(if (mUser.nickname.isNullOrEmpty()) VMStr.byRes(R.string.info_nickname_default) else mUser.nickname)
        mBinding.infoSignatureLV.setCaption(if (mUser.signature.isNullOrEmpty()) VMStr.byRes(R.string.info_signature_default) else mUser.signature)
        mBinding.infoBirthdayLV.setCaption(mUser.birthday)

        if (mUser.gender == 1) {
            mBinding.infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_man))
        } else if (mUser.gender == 0) {
            mBinding.infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_woman))
        } else {
            mBinding.infoGenderLV.setCaption(VMStr.byRes(R.string.info_gender_unknow))
        }

        mBinding.infoPhoneLV.setCaption(if (mUser.phone.isNullOrEmpty()) VMStr.byRes(R.string.info_phone_null) else mUser.phone)
        mBinding.infoEmailLV.setCaption(if (mUser.email.isNullOrEmpty()) VMStr.byRes(R.string.info_email_null) else mUser.email)
        mBinding.infoAuthStatusLV.setCaption(if (mUser.idCardNumber.isNullOrEmpty()) VMStr.byRes(R.string.info_auth_no) else VMStr.byRes(R.string.info_auth_yes))
        mBinding.infoAddressLV.setCaption(if (mUser.address.isNullOrEmpty()) VMStr.byRes(R.string.info_not_set) else mUser.address)
        mBinding.infoProfessionLV.setCaption(if (mUser.profession.title.isNullOrEmpty()) VMStr.byRes(R.string.info_not_set) else mUser.profession.title)
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
            if (mUser.gender != 2) {
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
        val ymdArray = VMStr.strToArray(mUser.birthday, "-")
        VMLog.d("${mUser.birthday} ${ymdArray.size} $ymdArray")
        mBinding.pickerLayout.pickerBirthdayView.selectedYear = ymdArray[0].toInt()
        mBinding.pickerLayout.pickerBirthdayView.wheelYearPicker.setSelectedItemPosition(ymdArray[0].toInt() - 1900, false)
        mBinding.pickerLayout.pickerBirthdayView.selectedMonth = ymdArray[1].toInt()
        mBinding.pickerLayout.pickerBirthdayView.selectedDay = ymdArray[2].toInt()

        /**
         * 性别选择器
         */
        mBinding.pickerLayout.pickerGenderView.data = listOf("女", "男", "保密")
        mBinding.pickerLayout.pickerGenderView.setSelectedItemPosition(mUser.gender, false)

        /**
         * 地址选择器
         */

        /**
         * 职业选择器
         */
        mBinding.pickerLayout.pickerProfessionView.data = mProfessionList
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
        mBinding.pickerLayout.pickerProfessionView.data = list
        val position = mProfessionList.indexOf(mUser.profession)
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

        when (mUser.gender) {
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