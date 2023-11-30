package com.mredrock.cyxbs.login.page.useragree

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.config.route.USER_PROTOCOL
import com.mredrock.cyxbs.lib.base.ui.BaseActivity
import com.mredrock.cyxbs.lib.utils.extensions.gone
import com.mredrock.cyxbs.lib.utils.extensions.setOnSingleClickListener
import com.mredrock.cyxbs.lib.utils.network.CommonApiService
import com.mredrock.cyxbs.lib.utils.network.DownMessage
import com.mredrock.cyxbs.lib.utils.network.DownMessageParams
import com.mredrock.cyxbs.lib.utils.network.commonApi
import com.mredrock.cyxbs.lib.utils.network.mapOrInterceptException
import com.mredrock.cyxbs.login.R
import com.mredrock.cyxbs.login.page.login.adapter.UserAgreementAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

@Route(path = USER_PROTOCOL)
class UserAgreeActivity : BaseActivity() {

    private val mBack by R.id.login_view_user_agree_back.view<View>()
    private val mRecyclerView by R.id.login_rv_user_agree.view<RecyclerView>()
    private val mProgressBar by R.id.login_pb_user_agree_loader.view<ProgressBar>()

    private var mDownMessage: DownMessage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity_user_agree)

        mBack.setOnSingleClickListener {
            finish()
        }

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        // 因为界面简单，所以就没有必要使用 ViewModel，使用最原始的保存数据方式就可以了
        if (savedInstanceState == null) {
            getDownMessage()
        } else {
            // savedInstanceState 不为 null 时说明界面重新被异常摧毁重建了
            val downMessage =
                savedInstanceState.getSerializable(this::mDownMessage.name) as DownMessage?
            if (downMessage != null) {
                mRecyclerView.adapter = UserAgreementAdapter(downMessage.textList)
            } else {
                getDownMessage()
            }
        }
    }

    private fun getDownMessage() {
        val startTime = System.currentTimeMillis()
        CommonApiService::class.commonApi
            .getDownMessage(DownMessageParams("zscy-main-userAgreement"))
            .subscribeOn(Schedulers.io())
            .delay(
                // 有时候网路慢会转一下圈圈，但是有时候网络快，圈圈就像是闪了一下，像bug，就让它最少转一秒吧
                (System.currentTimeMillis() - startTime).let { if (it > 1000) 0 else it },
                TimeUnit.MILLISECONDS
            ).observeOn(AndroidSchedulers.mainThread())
            .mapOrInterceptException {
                toast("网络似乎开小差了~")
            }.safeSubscribeBy {
                mDownMessage = it
                mProgressBar.gone()
                mRecyclerView.adapter = UserAgreementAdapter(it.textList)
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(this::mDownMessage.name, mDownMessage)
    }
}