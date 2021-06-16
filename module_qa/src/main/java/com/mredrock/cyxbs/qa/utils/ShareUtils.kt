package com.mredrock.cyxbs.qa.utils

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mredrock.cyxbs.qa.config.CommentConfig
import com.mredrock.cyxbs.qa.qqconnect.BaseUiListener
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.Tencent

/**
 * @Author: xgl
 * @ClassName: ShareUtils
 * @Description:
 * @Date: 2021/2/1 21:39
 */
object ShareUtils {
    /*
    分享到qq好友
    */
    fun qqShare(tencent: Tencent, fragment: Fragment, title: String, content: String, url: String, imageUrl: String) {
        val params = Bundle()
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT) //分享的类型
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title) //分享标题
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, content) //要分享的内容摘要
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url) //内容地址
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl) //分享的图片URL
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, CommentConfig.APP_NAME) //应用名称
        tencent.shareToQQ(fragment.activity, params, BaseUiListener())
    }

    /*
    分享到qq好友
    */
    fun qqShare(tencent: Tencent, activity: Activity, title: String, content: String, url: String, imageUrl: String) {
        val params = Bundle()
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title) //分享标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content) //分享的内容摘要
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url) //分享的链接
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl) //分享的图片URL
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, CommentConfig.APP_NAME) //应用名称
        tencent.shareToQQ(activity, params, BaseUiListener())
    }


    /*
    分享到qq空间
     */
    fun qqQzoneShare(tencent: Tencent, fragment: Fragment, title: String, content: String, url: String, imageUrl: ArrayList<String>) {
        val QzoneType = QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE
        val params = Bundle()
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneType)
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title) //分享标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content) //分享的内容摘要
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url) //分享的链接
        //分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片
        // （注：图片最多支持9张图片，多余的图片会被丢弃），qq接口暂时只支持一张
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrl) //分享的图片URL
        tencent.shareToQzone(fragment.activity, params, BaseUiListener())
    }

    /*
  分享到qq空间
   */
    fun qqQzoneShare(tencent: Tencent, activity: Activity, title: String, content: String, url: String, imageUrl: ArrayList<String>) {
        val QzoneType = QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE
        val params = Bundle()
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneType)
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title) //分享标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content) //分享的内容摘要
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url) //分享的链接
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrl) //分享的图片URL
        tencent.shareToQzone(activity, params, BaseUiListener())
    }

}