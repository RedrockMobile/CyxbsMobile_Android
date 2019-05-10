package com.mredrock.cyxbs.qa.network

/**
 * Created By jay68 on 2018/9/21.
 */
class NetworkState {
    companion object {
        const val LOADING = 1
        const val SUCCESSFUL = 2
        const val FAILED = 4
        const val CANNOT_LOAD_WITHOUT_LOGIN = 8
        const val NO_MORE_DATA = 32
    }
}