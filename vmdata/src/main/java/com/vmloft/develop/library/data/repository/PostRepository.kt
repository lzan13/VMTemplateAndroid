package com.vmloft.develop.library.data.repository

import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.data.bean.Comment
import com.vmloft.develop.library.data.bean.Post
import com.vmloft.develop.library.data.db.AppDatabase
import com.vmloft.develop.library.request.BaseRepository
import com.vmloft.develop.library.request.RPaging
import com.vmloft.develop.library.request.RResult

/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：帖子相关请求
 */
class PostRepository : BaseRepository() {

    /**
     * 创建帖子
     */
    suspend fun createPost(content: String, category: String, attachments: List<String>): RResult<Post> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.postAPI.createPost(content, category, attachments)) }
    }

    /**
     * 删除帖子
     */
    suspend fun deletePost(id: String): RResult<Any> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.postAPI.deletePost(id)) }
    }

    /**
     * 帖子信息
     */
    suspend fun postInfo(id: String): RResult<Post> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.postAPI.postInfo(id)) }
    }

    /**
     * 获取列表
     */
    suspend fun postList(page: Int, limit: Int, isService: Boolean = true, owner: String = ""): RResult<RPaging<Post>> {
        if (isService) {
            return getPostListFromServer(page, limit, owner)
        } else {
            // 先从本地获取，方便页面快速展示
            val list = AppDatabase.getInstance().postDao().all()
            if (list.isEmpty()) {
                return getPostListFromServer(page, limit, owner)
            }
            return RResult.Success(data = RPaging(list.size, list.size, page, limit, list))
        }
    }

    /**
     * 从服务器查询 Post 数据
     */
    private suspend fun getPostListFromServer(page: Int, limit: Int, owner: String = ""): RResult<RPaging<Post>> {
        val result = safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.postAPI.postList(page, limit, owner)) }
        // 保留最新的20条数据，清空数据库其他非屏蔽类数据
        if (result is RResult.Success) {
            val paging = result.data!!
            // 首页获取全部数据才进行缓存处理，其他情况不处理
            if (page == CConstants.defaultPage && owner == "") {
                // 清空已有数据，这里清空的是非屏蔽数据
                AppDatabase.getInstance().postDao().clear()
                // 保存最新数据
                AppDatabase.getInstance().postDao().insert(*paging.data.toTypedArray())
            }
            // 这里在过滤下被屏蔽的数据
            val shieldedList = AppDatabase.getInstance().postDao().queryShielded()
            val list = paging.data as MutableList<Post>
            // 移除被屏蔽的数据
            list.removeAll(shieldedList)
            // 构造一个新的 data 进行返回
            return RResult.Success(data = RPaging(paging.currentCount, paging.totalCount, paging.page, paging.limit, list))
        }
        return result
    }

    /**
     * 获取列表
     */
    suspend fun shieldPost(post: Post): RResult<Any> {
        AppDatabase.getInstance().postDao().update(post)
        return RResult.Success("")
    }

    /**
     * 创建评论
     */
    suspend fun createComment(content: String, post: String, user: String): RResult<Comment> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.postAPI.createComment(content, post, user)) }
    }

    /**
     * 删除评论
     */
    suspend fun deleteComment(id: String): RResult<Any> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.postAPI.deleteComment(id)) }
    }

    /**
     * 获取评论列表
     */
    suspend fun getCommentList(post: String, page: Int, limit: Int): RResult<RPaging<Comment>> {
        return safeRequest { executeResponse(com.vmloft.develop.library.data.api.APIRequest.postAPI.getCommentList(post, page, limit)) }
    }

}