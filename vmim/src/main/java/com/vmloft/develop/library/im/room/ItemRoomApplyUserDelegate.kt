package com.vmloft.develop.library.im.room

import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.im.R
import com.vmloft.develop.library.im.bean.IMUser
import com.vmloft.develop.library.im.databinding.ImItemRoomApplyUserDelegateBinding

/**
 * Create by lzan13 on 2021/01/05 17:56
 * 描述：申请上麦 Item
 */
class ItemRoomApplyUserDelegate(listener: BItemListener<IMUser>) : BItemDelegate<IMUser, ImItemRoomApplyUserDelegateBinding>(listener) {

    override fun layoutId(): Int = R.layout.im_item_room_apply_user_delegate

    override fun onBindView(holder: BItemHolder<ImItemRoomApplyUserDelegateBinding>, item: IMUser) {
        holder.binding.data = item

        holder.binding.executePendingBindings()
    }
}
