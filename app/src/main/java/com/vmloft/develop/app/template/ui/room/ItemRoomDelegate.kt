package com.vmloft.develop.app.template.ui.room

import android.view.LayoutInflater
import android.view.ViewGroup

import com.vmloft.develop.app.template.databinding.ItemRoomDelegateBinding
import com.vmloft.develop.app.template.request.bean.Room
import com.vmloft.develop.library.common.base.BItemDelegate
import com.vmloft.develop.library.common.image.IMGLoader

/**
 * Create by lzan13 on 2021/05/25 17:56
 * 描述：展示房间信息 Item
 */
class ItemRoomDelegate(listener: BItemListener<Room>) : BItemDelegate<Room, ItemRoomDelegateBinding>(listener) {

    override fun initVB(inflater: LayoutInflater, parent: ViewGroup) = ItemRoomDelegateBinding.inflate(inflater, parent, false)

    override fun onBindView(holder: BItemHolder<ItemRoomDelegateBinding>, item: Room) {
        holder.binding.roomCoverMask.alpha = 0.75f
        if (item.owner.cover.isNullOrEmpty()) {
            IMGLoader.loadCover(holder.binding.roomCoverIV, item.owner.avatar, isRadius = true, radiusSize = 16)
        } else {
            IMGLoader.loadCover(holder.binding.roomCoverIV, item.owner.cover, isRadius = true, radiusSize = 16)
        }

        holder.binding.roomNameTV.text = item.title
        holder.binding.roomDescTV.text = item.desc

        IMGLoader.loadAvatar(holder.binding.roomOwnerAvatarIV, item.owner.avatar)
        holder.binding.roomOwnerNameTV.text = item.owner.nickname
        holder.binding.roomMemberTV.text = item.count.toString()

    }
}
