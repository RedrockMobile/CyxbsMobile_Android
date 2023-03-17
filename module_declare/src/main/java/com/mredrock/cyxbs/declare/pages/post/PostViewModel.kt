package com.mredrock.cyxbs.declare.pages.post

import com.mredrock.cyxbs.declare.pages.post.net.PostApiService
import com.mredrock.cyxbs.lib.base.ui.BaseViewModel
import com.mredrock.cyxbs.lib.utils.extensions.asFlow
import com.mredrock.cyxbs.lib.utils.network.ApiStatus
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * com.mredrock.cyxbs.declare.pages.post.PostViewModel.kt
 * CyxbsMobile_Android
 *
 * @author 寒雨
 * @since 2023/2/8 下午3:28
 */
class PostViewModel : BaseViewModel() {
    // livedata 是粘性事件，不适合在这里使用，所以直接使用 SharedFlow
    private val _postResultFlow: MutableSharedFlow<ApiStatus> = MutableSharedFlow()
    val postResultFlow: SharedFlow<ApiStatus>
        get() = _postResultFlow

    suspend fun post(title: String, choices: List<String>) {
        PostApiService.postVote(title, choices)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .asFlow()
            .collectLaunch {
                _postResultFlow.emit(it)
            }
    }
}