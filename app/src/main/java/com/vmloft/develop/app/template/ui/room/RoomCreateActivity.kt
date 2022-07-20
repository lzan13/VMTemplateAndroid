package com.vmloft.develop.app.template.ui.room

import android.text.Editable
import android.text.TextWatcher

import com.alibaba.android.arouter.facade.annotation.Route

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ActivityRoomCreateBinding
import com.vmloft.develop.app.template.im.IMManager
import com.vmloft.develop.app.template.report.ReportConstants
import com.vmloft.develop.app.template.router.AppRouter
import com.vmloft.develop.library.base.BVMActivity
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.utils.errorBar
import com.vmloft.develop.library.data.common.CacheManager
import com.vmloft.develop.library.data.bean.Room
import com.vmloft.develop.library.data.viewmodel.RoomViewModel
import com.vmloft.develop.library.report.ReportManager
import com.vmloft.develop.library.tools.utils.VMReg
import com.vmloft.develop.library.tools.utils.VMStr

import org.koin.androidx.viewmodel.ext.android.getViewModel

/**
 * Create by lzan13 on 2021/05/25 22:56
 * 描述：创建房间
 */
@Route(path = AppRouter.appRoomCreate)
class RoomCreateActivity : BVMActivity<ActivityRoomCreateBinding, RoomViewModel>() {

    var title: String = ""
    var desc: String = ""
//    var welcome: String = ""

    override fun initVB() = ActivityRoomCreateBinding.inflate(layoutInflater)

    override fun initVM(): RoomViewModel = getViewModel()


    override fun initUI() {
        super.initUI()
        setTopTitle(R.string.room_create)

        setTopEndBtnEnable(false)
        setTopEndBtnListener(VMStr.byRes(R.string.btn_confirm)) { submit() }

        mBinding.titleET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                title = s.toString().trim()
                mBinding.titleCountTV.text = "${title.length}/16"
                verifyInputBox()
            }
        })
        mBinding.descET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                desc = s.toString().trim()
                mBinding.descCountTV.text = "${desc.length}/64"
                verifyInputBox()
            }
        })
    }

    override fun initData() {

    }

    override fun onModelRefresh(model: BViewModel.UIModel) {
        if (model.type == "createRoom") {
            val room = model.data as Room
            // 进入房间，进行下记录，这里是自己创建的
            CacheManager.putRoom(room)
            CacheManager.setLastRoom(room)
            IMManager.goChatRoom(room.id)
            finish()
        }
    }

    /**
     * 校验输入框内容
     */
    private fun verifyInputBox() { // 将用户名转为消息并修剪
        // 检查输入框是否为空
        setTopEndBtnEnable(!title.isNullOrEmpty() && !desc.isNullOrEmpty())
    }

    /**
     *
     */
    private fun submit() {
        if (!VMReg.isCommonReg(title, "^[\\s\\S]{2,16}$")) {
            return errorBar("标题长度必须在 2-16 之间")
        }
        if (!VMReg.isCommonReg(desc, "^[\\s\\S]{2,64}$")) {
            return errorBar("描述长度必须在 2-64 之间")
        }
//        if (!VMReg.isCommonReg(welcome, "^[\\s\\S]{2,16}$")) {
//            return errorBar("欢迎语长度必须在 2-64 之间")
//        }
        // 上报创建房间
        ReportManager.reportEvent(ReportConstants.eventRoomCreate)

        mViewModel.createRoom(title, desc)
    }
}