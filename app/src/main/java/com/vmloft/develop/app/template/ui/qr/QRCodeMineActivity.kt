package com.vmloft.develop.app.template.ui.qr


import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.common.SignManager
import com.vmloft.develop.app.template.request.bean.User
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BaseActivity
import com.vmloft.develop.library.common.image.IMGLoader
import com.vmloft.develop.library.common.utils.CUtils
import com.vmloft.develop.library.tools.utils.VMDimen
import com.vmloft.develop.library.tools.utils.VMSystem
import com.vmloft.develop.library.tools.utils.VMTheme

import kotlinx.android.synthetic.main.activity_qr_code_mine.*


/**
 * Create by lzan13 on 2021/6/6
 * 描述：二维码扫描界面
 */
@Route(path = AppRouter.appQRMine)
class QRCodeMineActivity : BaseActivity() {

    lateinit var user: User

    override fun layoutId(): Int = R.layout.activity_qr_code_mine


    override fun initUI() {
        super.initUI()

        setTopTitle(R.string.qr_mine)

        VMTheme.changeShadow(qrContainerCL)

    }

    override fun initData() {
        user = SignManager.getCurrUser() ?: return finish()

        bindInfo()

        generateQRCode()

    }

    /**
     * 绑定信息
     */
    private fun bindInfo() {
        IMGLoader.loadAvatar(qrAvatarIV, user.avatar)

        when (user.gender) {
            1 -> qrGenderIV.setImageResource(R.drawable.ic_gender_man)
            0 -> qrGenderIV.setImageResource(R.drawable.ic_gender_woman)
            else -> qrGenderIV.setImageResource(R.drawable.ic_gender_unknown)
        }

        qrNameTV.text = user.nickname
        qrSignatureTV.text = user.signature

    }

    /**
     * 生成二维码
     */
    private fun generateQRCode() {
        val content = CUtils.base64Encode(user.id)
        val size = VMDimen.screenWidth - VMDimen.dp2px(24) * 2 - VMDimen.dp2px(16) * 4
        VMSystem.runTask {
            val bitmap = QRCodeEncoder.syncEncodeQRCode(content, size)
            VMSystem.runInUIThread({
                qrCodeIV.setImageBitmap(bitmap)
            })
        }
    }
}