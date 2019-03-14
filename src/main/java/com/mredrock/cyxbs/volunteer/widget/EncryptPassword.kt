package com.mredrock.cyxbs.volunteer.widget

import android.os.Build
import com.mredrock.cyxbs.common.utils.LogUtils
import java.util.Base64
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class EncryptPassword {
    companion object {
        private val seckey = byteArrayOf(
                0x33, 0x21, 0x27, 0x21,
                0x26, 0x73, 0x12, 0x71,
                0x62, 0x42, 0x73, 0x74,
                0x72, 0x4c, 0x5f, 0x66)

        private val iv = byteArrayOf(
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00)

        fun encrypt(password: String): String {
            try {
                val bytestream = ByteArrayOutputStream()
                val oos = ObjectOutputStream(bytestream)
                oos.writeObject(password)

                val bintoken = bytestream.toByteArray()
                val sKey = SecretKeySpec(seckey, "AES")
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                cipher.init(Cipher.ENCRYPT_MODE, sKey, IvParameterSpec(iv))
                val sectoken = cipher.doFinal(bintoken)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val b64encoder = Base64.getUrlEncoder()
                    var encoding = b64encoder.encodeToString(sectoken)
//                    LogUtils.d("VolunteerLoginTest", encoding)
                    return encoding
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }
    }
}