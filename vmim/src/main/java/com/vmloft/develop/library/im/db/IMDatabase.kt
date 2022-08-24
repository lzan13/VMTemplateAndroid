package com.vmloft.develop.library.im.db


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.tencent.wcdb.database.SQLiteCipherSpec
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory

import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.im.bean.IMConversation
import com.vmloft.develop.library.im.bean.IMMessage
import com.vmloft.develop.library.im.common.IMConstants
import com.vmloft.develop.library.tools.VMTools

/**
 * Create by lzan13 on 2022/8/16
 * 描述：Room 数据库操作类
 */
@Database(
    entities = [
        IMConversation::class,
        IMMessage::class
    ], version = 1
)
@TypeConverters(IMDataConverter::class)
abstract class IMDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var instance: IMDatabase? = null

        fun getInstance(): IMDatabase = instance ?: synchronized(this) {
            instance ?: buildDatabase().also { instance = it }
        }

        fun close() {
            instance = null
        }

        private fun buildDatabase(): IMDatabase {
            // 自定义加密方式
            val cipher = SQLiteCipherSpec().setPageSize(4096).setKDFIteration(64000)
            // WCDB 打开数据库工厂类
            val factory = WCDBOpenHelperFactory()
                .passphrase(IMConstants.dbPass.toByteArray()) // 指定加密DB密钥，非加密DB去掉此行
                .cipherSpec(cipher) //指定加密方式，使用默认加密可以省略
                .writeAheadLoggingEnabled(true) // 打开WAL以及读写并发，可以省略让Room决定是否要打开
                .asyncCheckpointEnabled(true)  // 开异步Checkpoint优化，不需要可以省略

            // 实例化数据库 这里用用户id将数据进行区分
            return Room.databaseBuilder(VMTools.context, IMDatabase::class.java, IMConstants.dbName + SignManager.getSignId())
                .openHelperFactory(factory) // 使用 WCDB 打开 Room
                .fallbackToDestructiveMigration() // 重建数据库，和下边升级互斥，这种方式会清空数据库中的数据
//                .addMigrations(migration1To2, migration2To3) // 数据库升级，自己控制升级数据处理
                .build()
        }

//        val migration1To2: Migration = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE category ADD is_male INTEGER NOT NULL DEFAULT 0")
//            }
//        }

//        val migration2To3: Migration = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE user ADD address TEXT")
//            }
//        }
    }

    // 会话数据操作 Dao
    abstract fun conversationDao(): IMConversatinDao

    // 消息数据操作 Dao
    abstract fun messageDao(): IMMessageDao

}