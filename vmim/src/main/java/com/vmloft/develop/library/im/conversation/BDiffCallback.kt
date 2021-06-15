package com.vmloft.develop.library.im.conversation

import androidx.recyclerview.widget.DiffUtil

/**
 * Create by lzan13 2021/05/26
 * 描述：数据差异比较处理类
 */
abstract class BDiffCallback<T>(val oldList: List<T>, val newList: List<T>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return equalsItem(oldItemPosition, newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return equalsContent(oldItemPosition, newItemPosition)

    }

    fun getOldItem(position: Int): T {
        return oldList[position]
    }

    fun getNewItem(position: Int): T {
        return newList[position]
    }

    /**
     * 比较两个 Item 是不是同一个
     */
    abstract fun equalsItem(oldPosition: Int, newPosition: Int): Boolean

    /**
     * 比较 Item 的内容是不是相同
     */
    abstract fun equalsContent(oldPosition: Int, newPosition: Int): Boolean
}