package com.mredrock.cyxbs.lib.base.operations

import androidx.appcompat.app.AppCompatActivity
import com.mredrock.cyxbs.lib.base.ui.BaseUi
import com.mredrock.cyxbs.lib.utils.extensions.RxjavaLifecycle

/**
 *
 * 业务层的 Activity
 *
 * 主要用于书写与业务相耦合的代码，比如需要使用到 api 模块时
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/8 19:16
 */
abstract class OperationActivity : AppCompatActivity(), BaseUi, RxjavaLifecycle {

}