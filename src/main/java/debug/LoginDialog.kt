package debug

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.User
import com.mredrock.cyxbs.common.utils.extensions.getScreenWidth
import com.mredrock.cyxbs.course.R
import com.mredrock.cyxbs.course.event.AddAffairEvent
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.dip

/**
 * 此类用于模拟登录，有些问题。为了方便测试使用，因此不用在意。
 *
 * Created by anriku on 2018/10/13.
 */
class LoginDialog(private val mContext: Context) {

    companion object {
        private const val TAG = "LoginDialog"
    }

    private val mDialog: Dialog by lazy(LazyThreadSafetyMode.NONE) { Dialog(mContext) }
    private val mLayoutInflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(mContext) }

    /**
     * 此方法用于在单独模块下模拟登录
     */
    @SuppressLint("InflateParams")
    fun analogLogin() {
        val itemView = mLayoutInflater.inflate(R.layout.course_login_dialog, null)
        val account = itemView.findViewById<EditText>(R.id.account)
        val password = itemView.findViewById<EditText>(R.id.password)
        val sure = itemView.findViewById<AppCompatButton>(R.id.sure)
        sure.setOnClickListener {
            mDialog.dismiss()
            BaseApp.user = User(stuNum = account.text.toString(), idNum = password.text.toString())
            //这里实际不是添加事务是为了进行UI的更新
            EventBus.getDefault().post(AddAffairEvent())
        }

        val params = ViewGroup.LayoutParams(mContext.getScreenWidth() * 3 / 4,
                mContext.dip(200))

        mDialog.apply {
            setContentView(itemView, params)
            show()
        }
    }
}