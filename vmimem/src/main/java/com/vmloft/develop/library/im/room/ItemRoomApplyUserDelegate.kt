package com.vmloft.develop.library.im.room

import android.view.LayoutInflater
import android.view.ViewGroup

import com.vmloft.develop.library.base.BItemDelegate
import com.vmloft.develop.library.data.bean.User
import com.vmloft.develop.library.image.IMGLoader
import com.vmloft.develop.library.im.databinding.ImItemRoomApplyUserDelegateBinding

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：申请上麦 Item
 */
class ItemRoomApplyUserDelegate(listener: BItemListener<User>) : BItemDelegate<User, ImItemRoomApplyUserDelegateBinding>(listener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ImItemRoomApplyUserDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ImItemRoomApplyUserDelegateBinding>, item: User) {
        IMGLoader.loadAvatar(holder.binding.imApplyAvatarIV, item.avatar)
    }
}
