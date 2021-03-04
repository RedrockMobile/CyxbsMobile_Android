package com.mredrock.cyxbs.skin

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import com.mredrock.cyxbs.common.ui.BaseActivity

abstract class SkinBaseActivity : BaseActivity(){
//    private var mSkinFactory: MySkinFactory? = null
    override fun onCreate(savedInstanceState: Bundle?) {
//        SkinManager.instance.init(this)
//        /**在这里创建一个mSkinFactory，赋值给layoutInflater.factory，用于收集要换肤的信息 */
//        mSkinFactory = MySkinFactory()
//        layoutInflater.factory = mSkinFactory
//        super.getLayoutInflater().factory = mSkinFactory
        super.onCreate(savedInstanceState)
//        /**添加皮肤更新监听  */
//        SkinManager.instance.addSkinUpdateListener(this)


    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onCreateView(
            parent: View?,
            name: String,
            context: Context,
            attrs: AttributeSet
    ): View? {
        return super.onCreateView(parent, name, context, attrs)
    }
//
//    override fun onSkinUpdate() {
//        mSkinFactory!!.apply()
//    }
}