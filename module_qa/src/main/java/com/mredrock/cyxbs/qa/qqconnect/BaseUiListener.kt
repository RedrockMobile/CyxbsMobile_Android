package com.mredrock.cyxbs.qa.qqconnect

import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError

/**
 * @Author: xgl
 * @ClassName: BaseUiListener
 * @Description:
 * @Date: 2021/1/31 20:34
 */
public class BaseUiListener :IUiListener {
    override fun onComplete(p0: Any?) {
    }

    override fun onError(p0: UiError?) {
    }

    override fun onCancel() {
    }

    override fun onWarning(p0: Int) {
    }
}