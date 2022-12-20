package com.mredrock.cyxbs.api.todo

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * Author: RayleighZ
 * Time: 2021-08-09 15:49
 */
interface ITodoService : IProvider{
    fun getTodoFeed(): Fragment
}