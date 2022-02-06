package com.mredrock.cyxbs.common.utils.encrypt

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object LogLocalAES {

    fun encrypt(password: String, input: String): String {

        //创建加密对象
        val cipher = Cipher.getInstance("AES")

        //指定秘钥
        val keySpec = SecretKeySpec(password.toByteArray(), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)

        //加密
        //通过Base64解决乱码问题，否则会出现异常（avax.crypto.IllegalBlockSizeException: last block incomplete in decryption）
        val encrypt = cipher.doFinal(input.toByteArray())
        return Base64.encodeToString(
            encrypt,
            Base64.DEFAULT
        )
    }

    fun dencrypt(password: String, input: String): String {
        //创建加密对象
        val cipher = Cipher.getInstance("AES")

        //指定秘钥
        val keySpec = SecretKeySpec(password.toByteArray(), "AES")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)

        //解密
        val dencrypt: ByteArray
        try {
            dencrypt = cipher.doFinal(Base64.decode(input, Base64.DEFAULT))
        } catch (e: Exception) {
            println(e)
            return ""
        }
        return String(dencrypt)
    }
}
/**
 * 下面一整个是对应的kotlin解密(非Android平台
 *
 */
//import java.io.File
//import java.io.InputStream
//import java.util.*
//import javax.crypto.Cipher
//import javax.crypto.spec.SecretKeySpec
//
//fun main(){
//    val inputStream: InputStream = File("src/cyxbs.log").inputStream()
//    var string = ""
//    inputStream.bufferedReader().useLines { lines ->
//        lines.iterator().forEach {
//            if (it == ""){
//                println(dencrypt("2020214629000000", string))
//                string = ""
//            }else{
//                string += it
//            }
//        }
//    }
//
//    val bai = "ZzqIQSkkORxQNRcQAvAZm+XgVnl00XPVV3BlPMJmMV0="
//    println(dencrypt( "2020214629000000",bai))
//}
//fun encrypt(password: String, input: String):String {
//
//    //创建加密对象
//    val cipher = Cipher.getInstance("AES")
//
//    //指定秘钥
//    val keySpec = SecretKeySpec(password.toByteArray(), "AES")
//    cipher.init(Cipher.ENCRYPT_MODE, keySpec)
//
//    //加密
//    val encrypt = cipher.doFinal(input.toByteArray())
//    return Base64.getEncoder().encodeToString(encrypt)//通过Base64解决乱码问题，否则会出现异常（avax.crypto.IllegalBlockSizeException: last block incomplete in decryption）
//}
//
//fun dencrypt(password: String, input: String):String {
//    //创建加密对象
//    val cipher = Cipher.getInstance("AES")
//
//    //指定秘钥
//    val keySpec = SecretKeySpec(password.toByteArray(), "AES")
//    cipher.init(Cipher.DECRYPT_MODE, keySpec)
//
//    //解密
//    var dencrypt:ByteArray
//    try {
//        dencrypt = cipher.doFinal(Base64.getDecoder().decode(input))
//    }catch (e: Exception){
//        println(e)
//        return ""
//    }
//    return String(dencrypt)
//}