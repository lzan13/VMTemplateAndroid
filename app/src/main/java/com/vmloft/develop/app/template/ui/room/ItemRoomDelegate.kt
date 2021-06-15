package com.vmloft.develop.app.template.ui.room

import com.vmloft.develop.app.template.R
import com.vmloft.develop.app.template.databinding.ItemRoomDelegateBinding
import com.vmloft.develop.app.template.request.bean.Room

import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * Create by lzan13 on 2021/05/25 17:56
 * 描述：展示房间信息 Item
 */
class ItemRoomDelegate(listener: BItemListener<Room>) : BItemDelegate<Room, ItemRoomDelegateBinding>(listener) {

    override fun layoutId(): Int = R.layout.item_room_delegate

    override fun onBindView(holder: BItemHolder<ItemRoomDelegateBinding>, item: Room) {
        holder.binding.data = item
        holder.binding.roomCoverMask.alpha = 0.75f
        if (item.owner.cover.isNullOrEmpty()) {
            IMGLoader.loadCover(holder.binding.roomCoverIV, item.owner.avatar, isRadius = true, radiusSize = 8)
        } else {
            IMGLoader.loadCover(holder.binding.roomCoverIV, item.owner.cover, isRadius = true, radiusSize = 8)
        }

        holder.binding.executePendingBindings()
    }
}
