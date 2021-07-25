package com.vmloft.develop.app.template.ui.main.home

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.request.bean.Match
import com.vmloft.develop.library.common.base.BViewModel
import com.vmloft.develop.library.common.request.RResult
import com.vmloft.develop.app.template.request.repository.MatchRepository
import com.vmloft.develop.library.common.common.CConstants
import com.vmloft.develop.library.common.utils.CUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：匹配 ViewModel
 */
class MatchViewModel(private val repository: MatchRepository) : BViewModel() {

    /**
     * 提交匹配信息
     */
    fun submitMatch(match: Match) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                repository.submitMatch(match)
            }
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "submitMatch")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 删除一条匹配
     */
    fun removeMatch(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.removeMatch(id)
            if (result is RResult.Success && result.data != null) {
                emitUIState(data = result.data, type = "removeMatch")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取匹配列表
     */
    fun getMatchList(gender: Int = -1, page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit, type: Int = 0) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repository.getMatchList(gender, page, limit)
            if (result is RResult.Success) {
                emitUIState(data = result.data, type = "matchList")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取缓存在本地的自己的匹配数据
     */
    fun getSelfMatch() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                repository.getSelfMatch()
            }
            if (result is RResult.Success && result.data != null) {
                emitUIState(data = result.data, type = "selfMatch")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 缓存自己的匹配数据到数据库
     */
    fun setSelfMatch(match: Match) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setSelfMatch(match)
        }
    }

    /**
     * 随机获取一条匹配
     */
    fun getOneMatch(gender: Int = -1) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                Thread.sleep(CUtils.random(5) * CConstants.timeSecond)
                repository.getOneMatch(gender)
            }
            if (result is RResult.Success && result.data != null) {
                emitUIState(data = result.data, type = "oneMatch")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

}