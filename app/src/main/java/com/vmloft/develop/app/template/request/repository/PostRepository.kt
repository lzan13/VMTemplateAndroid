package com.vmloft.develop.app.template.request.repository

import com.vmloft.develop.app.template.request.api.APIRequest
import com.vmloft.develop.app.template.request.bean.Comment
import com.vmloft.develop.library.common.request.BaseRepository
import com.vmloft.develop.library.common.request.RPaging
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.bean.Post

/**
 * Create by lzan13 on 2020/08/03 09:08
 * 描述：帖子相关请求
 */
class PostRepository : BaseRepository() {

    /**
     * 创建帖子
     */
    suspend fun createPost(title: String, content: String, category: String, attachments: List<String>): RResult<Any> {
        return safeRequest(call = { requestCreatePost(title, content, category, attachments) })
    }

    private suspend fun requestCreatePost(title: String, content: String, category: String, attachments: List<String>): RResult<Any> =
        executeResponse(APIRequest.postAPI.createPost(title, content, category, attachments))

    /**
     * 获取列表
     */
    suspend fun getPostList(page: Int, limit: Int, owner: String = ""): RResult<RPaging<Post>> {
        return safeRequest(call = { requestPostList(page, limit, owner) })
    }

    private suspend fun requestPostList(page: Int, limit: Int, owner: String): RResult<RPaging<Post>> =
        executeResponse(APIRequest.postAPI.getPostList(page, limit, owner))

    /**
     * 创建评论
     */
    suspend fun createComment(content: String, post: String, user: String): RResult<Comment> {
        return safeRequest(call = { requestCreateComment(content, post, user) })
    }

    private suspend fun requestCreateComment(content: String, post: String, user: String): RResult<Comment> =
        executeResponse(APIRequest.postAPI.createComment(content, post, user))

    /**
     * 获取评论列表
     */
    suspend fun getCommentList(post: String, page: Int, limit: Int): RResult<RPaging<Comment>> {
        return safeRequest(call = { requestCommentList(post, page, limit) })
    }

    private suspend fun requestCommentList(post: String, page: Int, limit: Int): RResult<RPaging<Comment>> =
        executeResponse(APIRequest.postAPI.getCommentList(post, page, limit))

}