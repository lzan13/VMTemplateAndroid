package com.vmloft.develop.app.template.request.viewmodel

import androidx.lifecycle.viewModelScope
import com.vmloft.develop.app.template.request.bean.Match
import com.vmloft.develop.app.template.request.repository.InfoRepository
import com.vmloft.develop.library.request.RResult
import com.vmloft.develop.app.template.request.repository.MatchRepository
import com.vmloft.develop.library.base.BViewModel
import com.vmloft.develop.library.base.common.CConstants
import com.vmloft.develop.library.base.utils.CUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Create by lzan13 on 2020/4/20 17:28
 * 描述：匹配 ViewModel
 */
class MatchViewModel(private val repo: MatchRepository, private val infoRepo: InfoRepository) : BViewModel() {

    /**
     * 提交匹配信息
     */
    fun submitMatch(match: Match) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                repo.submitMatch(match)
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
            val result = repo.removeMatch(id)
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
    fun matchList(gender: Int = 2, type: Int = 0, page: Int = CConstants.defaultPage, limit: Int = CConstants.defaultLimit) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = repo.matchList(gender, type, page, limit)
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
    fun selfMatch() {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                repo.getSelfMatch()
            }
            if (result is RResult.Success) {
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
            repo.setSelfMatch(match)
        }
    }

    /**
     * 随机获取一条匹配
     */
    fun randomMatch(gender: Int = -1, type: Int = 0) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = withContext(Dispatchers.IO) {
                Thread.sleep(CUtils.random(5) * CConstants.timeSecond)
                repo.randomMatch(gender, type)
            }
            if (result is RResult.Success && result.data != null) {
                emitUIState(data = result.data, type = "randomMatch")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

    /**
     * 获取 MQTT 链接 Token
     */
    fun mqttUserToken(id: String) {
        viewModelScope.launch(Dispatchers.Main) {
            emitUIState(true)
            val result = infoRepo.mqttUserToken(id)
            if (result is RResult.Success && result.data != null) {
                emitUIState(data = result.data, type = "mqttUserToken")
                return@launch
            } else if (result is RResult.Error) {
                emitUIState(isSuccess = false, code = result.code, error = result.error)
            }
        }
    }

}