package com.vmloft.develop.app.template.ui.settings

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivitySettingsMediaBinding
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.common.base.BActivity
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.common.CSPManager
import com.vmloft.develop.library.common.utils.showBar
import com.vmloft.develop.library.tools.utils.VMFile

import java.io.File

/**
 * Create by lzan13 on 2020/05/02 22:56
 * 描述：图片资源设置
 */
@Route(path = AppRouter.appSettingsMedia)
class MediaSettingsActivity : BActivity<ActivitySettingsMediaBinding>() {
    // 缓存地址
    private val cachePath = "${VMFile.cachePath}${CConstants.cacheImageDir}"

    override fun initVB()=ActivitySettingsMediaBinding.inflate(layoutInflater)

    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.settings_media)

        mBinding.pictureAutoLoadLV.setOnClickListener {
            CSPManager.setAutoLoad(!CSPManager.isAutoLoad())
            mBinding.pictureAutoLoadLV.isActivated = CSPManager.isAutoLoad()
        }

        mBinding.pictureSaveDICMLV.setOnClickListener {
            CSPManager.setSaveDICM(!CSPManager.isSaveDICM())
            mBinding.pictureSaveDICMLV.isActivated = CSPManager.isSaveDICM()
        }
        mBinding.pictureClearCacheLV.setOnClickListener {
            VMFile.deleteFolder(cachePath, false)
            showBar(R.string.media_clear_toast)
            bindCache()
        }

    }

    override fun initData() {
        mBinding.pictureAutoLoadLV.isActivated = CSPManager.isAutoLoad()
        mBinding.pictureSaveDICMLV.isActivated = CSPManager.isSaveDICM()

        bindCache()
    }

    private fun bindCache() {
        // 缓存大小
        var cacheStr = VMFile.formatSize(VMFile.getFolderSize(File(cachePath)))
        mBinding.pictureClearCacheLV.setCaption(cacheStr)
    }

}