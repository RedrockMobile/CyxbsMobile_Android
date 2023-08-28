package com.mredrock.cyxbs.ufield.lyt.repository

import com.mredrock.cyxbs.lib.utils.network.ApiWrapper
import com.mredrock.cyxbs.ufield.lyt.bean.ItemActivityBean
import com.mredrock.cyxbs.ufield.lyt.network.SearchApiService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 *  description :
 *  author : lytMoon
 *  date : 2023/8/22 11:13
 *  email : yytds@foxmail.com
 *  version ： 1.0
 */
object SearchRepository {


    /**
     * 负责搜索界面
     *  涉及到四个搜索接口，这里用一个方法
     *       activity_type：活动类型  all(所有), culture(文娱), sports(体育), education(教育)
     *       activity_num：活动数量
     *       order_by：按照什么顺序返回数据 支持create_timestamp(发布时间, 时间戳大的在返回数组的前面)和watch(想看人数, 人数多的在前面)还有start_timestamp(开始时间, 时间戳大的在前)。
     *       contain_keyword：搜索拿到的关键词
     */
    fun receiveSearchData(
        type: String,
        num: Int,
        orderBy: String,
        keyword: String
    ):  Single<ApiWrapper<List<ItemActivityBean.ItemAll>>> {
        return SearchApiService
            .INSTANCE
            .getSearchData(type, num, orderBy, keyword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }


}