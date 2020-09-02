package com.mredrock.cyxbs.common.utils.encrypt

import okio.ByteString
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by zia on 2018/8/15.
 */
fun md5Encoding(s: String): String {
    try {
        val messageDigest = MessageDigest.getInstance("MD5")
        val md5bytes = messageDigest.digest(s.toByteArray(charset("UTF-8")))
        return ByteString.of(*md5bytes).hex()
    } catch (e: NoSuchAlgorithmException) {
        throw AssertionError(e)
    } catch (e: UnsupportedEncodingException) {
        throw AssertionError(e)
    }
}