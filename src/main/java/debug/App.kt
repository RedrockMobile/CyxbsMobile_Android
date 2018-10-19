package debug

import com.google.gson.Gson
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.bean.User

/**
 * Created By jay68 on 2018/8/21.
 */
class App : BaseApp() {
    override fun onCreate() {
        super.onCreate()
        user = Gson().fromJson("{\"id\":2828,\"stuNum\":\"2016210409\",\"idNum\":\"188674\",\"introduction\":\"AnAndroidDeveloper\",\"username\":\"李吉\",\"nickname\":\"Jay\",\"gender\":\"男\",\"photo_thumbnail_src\":\"http://wx.idsbllp.cn/cyxbsMobile/Public/photo/thumbnail/1520430636_1133226083.png\",\"photo_src\":\"http://wx.idsbllp.cn/cyxbsMobile/Public/photo/1520430636_1133226083.png\",\"updated_time\":\"2018-05-0523:15:35\",\"phone\":\"15923532402\",\"qq\":\"1432562823\"}", User::class.java)
    }
}