package com.vmloft.develop.app.template.ui.main.mine.info

import android.graphics.Bitmap
import android.view.View

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.databinding.ActivityMineQrCodeBinding
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.request.viewmodel.UserViewModel
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.tools.utils.VMStr
import com.vmloft.develop.library.tools.utils.VMTheme

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2021/6/6
 * 描述：我的二维码界面
 */
@Route(path = AppRouter.appMineQRCode)
class MineQRCodeActivity : BVMActivity<ActivityMineQrCodeBinding, UserViewModel>() {

    lateinit var user: User

    override fun initVB() = ActivityMineQrCodeBinding.inflate(layoutInflater)

    override fun initVM(): UserViewModel = getViewModel()

    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.info_mine_qr)

        VMTheme.changeShadow(mBinding.qrContainerCL)

    }

    override fun initData() {
        user = SignManager.getCurrUser() ?: return finish()

        bindInfo()

        mViewModel.generateQRCode(user.id)

    }

    override fun onModelLoading(model: BViewModel.UIModel) {
        if (model.isLoading) {
            mBinding.qrCodeLoadingView.visibility = View.VISIBLE
        } else {
            mBinding.qrCodeLoadingView.visibility = View.GONE
        }
    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "generateQRCode") {
            model.data?.let { mBinding.qrCodeIV.setImageBitmap(it as Bitmap) }
        }
    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        IMGLoader.loadAvatar(mBinding.qrAvatarIV, user.avatar)

        when (user.gender) {
            1 -> mBinding.qrGenderIV.setImageResource(R.drawable.ic_gender_man)
            0 -> mBinding.qrGenderIV.setImageResource(R.drawable.ic_gender_woman)
            else -> mBinding.qrGenderIV.setImageResource(R.drawable.ic_gender_unknown)
        }
        mBinding.qrNameTV.text = if (user.nickname.isNullOrEmpty()) VMStr.byRes(R.string.info_nickname_default) else user.nickname
        mBinding.qrSignatureTV.text = if (user.signature.isNullOrEmpty()) VMStr.byRes(R.string.info_signature_default) else user.signature

    }


}