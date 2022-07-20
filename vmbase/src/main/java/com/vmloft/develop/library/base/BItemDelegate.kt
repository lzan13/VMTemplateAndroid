package com.vmloft.develop.library.base

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

import com.drakeet.multitype.ItemViewDelegate

/**
 * Create by lzan13 on 2020/02/15 17:20
 * 描述：MultiType 适配器 Item 的代理基类类
 */
abstract class BItemDelegate<T, VB : ViewBinding> : ItemViewDelegate<T, BItemDelegate.BItemHolder<VB>> {
    protected lateinit var mContext: Context

    // 将事件回调给 View 层监听接口
    protected var mItemListener: BItemListener<T>? = null
    protected var mItemLongListener: BItemLongListener<T>? = null
    protected lateinit var mEvent: MotionEvent


    constructor() : super()

//    constructor(longListener: BItemLongListener<T>) : super() {
//        mItemLongListener = longListener
//    }

    constructor(listener: BItemListener<T>? = null, longListener: BItemLongListener<T>? = null) : super() {
        mItemListener = listener
        mItemLongListener = longListener
    }

    override fun onBindViewHolder(holder: BItemHolder<VB>, item: T) {
        holder.binding.root.setOnClickListener {
            mItemListener?.onClick(it, item, getPosition(holder))
        }
        holder.binding.root.setOnTouchListener { _, event ->
            mEvent = event
            false
        }
        holder.binding.root.setOnLongClickListener {
            mItemLongListener?.onLongClick(it, mEvent, item, getPosition(holder)) ?: false
        }
        onBindView(holder, item)
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): BItemHolder<VB> {
        mContext = context
        var binding: VB = initVB(LayoutInflater.from(context), parent)
        return BItemHolder(binding)
    }

    /**
     * 初始化 ViewBinding
     */
    protected abstract fun initVB(inflater: LayoutInflater, parent: ViewGroup): VB

    /**
     * 绑定定数
     */
    protected abstract fun onBindView(holder: BItemHolder<VB>, item: T)

    /**
     * ----------------------------------
     * 定义通用的 ViewHolder
     */
    class BItemHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)

    /**
     * 回调给 View 层监听接口
     */
    interface BItemListener<T> {
        fun onClick(v: View, data: T, position: Int)
    }

    interface BItemLongListener<T> {
        fun onLongClick(v: View, event: MotionEvent, data: T, position: Int): Boolean
    }

}
