package com.vmloft.develop.library.im.db

import androidx.room.*
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.data.bean.Post

import com.vmloft.develop.library.im.bean.IMMessage

/**
 * Create by lzan13 on 2022/8/16 14:41
 * 描述：消息数据 Dao
 */
@Dao
interface IMMessageDao {
    /**
     * 插入数据，如果已存在则会覆盖
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg message: IMMessage)

    /**
     * 删除指定数据
     */
    @Delete
    suspend fun delete(message: IMMessage)

    /**
     * 清空数据
     */
    @Query("DELETE FROM message")
    suspend fun clear()

    /**
     * 清空指定会话消息
     */
    @Query("DELETE FROM message WHERE chatId = :chatId")
    suspend fun clearMsg(chatId: String)

    /**
     * 更新数据
     */
    @Update
    suspend fun update(message: IMMessage)

    /**
     * 查询指定数据
     */
    @Query("SELECT * FROM message WHERE id = :id")
    suspend fun query(id: String): IMMessage?

    /**
     * 查询指定条件的数据
     */
    @Query("SELECT * FROM message WHERE chatId = :chatId and time < :time order by time desc limit :limit")
    suspend fun query(chatId: String, time: Long, limit: Int = CConstants.defaultLimit): List<IMMessage>

    /**
     * 查询全部数据
     */
    @Query("SELECT *  FROM message")
    suspend fun all(): List<IMMessage>
}