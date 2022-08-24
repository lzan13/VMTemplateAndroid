package com.vmloft.develop.library.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.tencent.wcdb.database.SQLiteCipherSpec
import com.tencent.wcdb.room.db.WCDBOpenHelperFactory

import com.vmloft.develop.library.data.bean.*
import com.vmloft.develop.library.data.common.DConstants
import com.vmloft.develop.library.data.common.SignManager
import com.vmloft.develop.library.tools.VMTools

/**
 * Create by lzan13 on 2020/8/9 14:35
 * 描述：Room 数据库操作类
 */
@Database(
    entities = [
        Category::class,
        Config::class,
        Gift::class,
        Match::class,
        Post::class,
        Profession::class,
        Version::class
    ], version = 21
)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase().also { instance = it }
            }

        fun close() {
            instance = null
        }

        private fun buildDatabase(): AppDatabase {
            // 自定义加密方式
            val cipher = SQLiteCipherSpec().setPageSize(4096).setKDFIteration(64000)
            // WCDB 打开数据库工厂类
            val factory = WCDBOpenHelperFactory()
                .passphrase(DConstants.dbPass.toByteArray()) // 指定加密DB密钥，非加密DB去掉此行
                .cipherSpec(cipher) //指定加密方式，使用默认加密可以省略
                .writeAheadLoggingEnabled(true) // 打开WAL以及读写并发，可以省略让Room决定是否要打开
                .asyncCheckpointEnabled(true)  // 开异步Checkpoint优化，不需要可以省略

            // 实例化数据库 这里用用户id将数据进行区分
            return Room.databaseBuilder(VMTools.context, AppDatabase::class.java, DConstants.dbName + SignManager.getSignId())
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

    // 分类数据操作 Dao
    abstract fun categoryDao(): CategoryDao

    // 配置数据操作 Dao
    abstract fun configDao(): ConfigDao

    // 礼物数据操作 Dao
    abstract fun giftDao(): GiftDao

    // 匹配数据操作 Dao
    abstract fun matchDao(): MatchDao

    // 帖子数据操作 Dao
    abstract fun postDao(): PostDao

    // 职业数据操作 Dao
    abstract fun professionDao(): ProfessionDao

    // 版本检查数据操作 Dao
    abstract fun versionDao(): VersionDao

}