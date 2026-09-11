package com.cyxbs.pages.sport.viewModel

import androidx.lifecycle.viewModelScope
import com.cyxbs.components.account.api.AccountState
import com.cyxbs.components.account.api.IAccountService
import com.cyxbs.components.base.ui.BaseViewModel
import com.cyxbs.components.config.service.impl
import com.cyxbs.components.utils.extensions.logg
import com.cyxbs.pages.sport.model.NoticeItem
import com.cyxbs.pages.sport.model.SportRepository
import com.cyxbs.pages.sport.widget.SportDetailUiState
import com.cyxbs.pages.sport.widget.toDetailUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * @Desc : 体育打卡页面 ViewModel
 * @Author : xt
 *
 * 管理详情、说明弹窗和下拉刷新状态，并响应登录状态变化
 */
class SportViewModel : BaseViewModel() {

    val noticeData: SharedFlow<Result<List<NoticeItem>>?> get() = _noticeData
    private val _noticeData = MutableSharedFlow<Result<List<NoticeItem>>?>(replay = 1)

    val uiState: StateFlow<SportDetailUiState> get() = _uiState
    private val _uiState = MutableStateFlow<SportDetailUiState>(SportDetailUiState.Loading)

    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing.asStateFlow()

    /**
     * 请求最新体育打卡详情，并更新页面状态
     * @param isFirstLoading 是否将页面先置为加载状态
     * @return 是否成功启动刷新请求
     */
    fun refresh(isFirstLoading: Boolean = false): Boolean {
        if (_isRefreshing.value) return false

        _isRefreshing.value = true

        viewModelScope.launch {
            if (isFirstLoading) {
                _uiState.value = SportDetailUiState.Loading
            }
            SportRepository.getSportDetailData()
                .onSuccess { bean ->
                    _uiState.value = bean.toDetailUiState()
                }
                .onFailure {
                    _uiState.value = SportDetailUiState.Error
                    logg("${it.stackTraceToString()}")
                }
            _isRefreshing.value = false
        }

        return true
    }

    init {
        IAccountService::class.impl().state
            .onEach {
                when (it) {
                    is AccountState.Login -> refresh(isFirstLoading = true)
                    else -> Unit
                }
            }.launchIn(viewModelScope)
    }

    init {
        getNoticeInfo()
    }

    // 请求体育打卡规则说明，供说明弹窗展示
    fun getNoticeInfo() {
        viewModelScope.launch {
            SportRepository.getSportNoticeData()
                .onSuccess { _noticeData.emit(Result.success(it)) }
                .onFailure { _noticeData.emit(Result.failure(it)) }
        }
    }
}
