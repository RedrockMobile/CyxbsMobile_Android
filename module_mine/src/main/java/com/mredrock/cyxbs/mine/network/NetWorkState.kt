package com.mredrock.cyxbs.mine.network

/**
 * @class
 * @author Copy by YYQF
 * @data 2021/10/5
 * @description
 **/
class NetworkState {
    companion object {
        const val LOADING = 1
        const val SUCCESSFUL = 2
        const val FAILED = 4
        const val CANNOT_LOAD_WITHOUT_LOGIN = 8
        const val NO_MORE_DATA = 32
    }
}