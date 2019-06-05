package com.mredrock.cyxbs.common.utils.encrypt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.mredrock.cyxbs.common.BaseApp;
import com.mredrock.cyxbs.common.config.ConfigKt;
import com.mredrock.cyxbs.common.utils.LogUtils;
import com.mredrock.cyxbs.common.utils.extensions.SharedPreferencesKt;

import java.nio.charset.StandardCharsets;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */

public class UserInfoEncryption {

    private Encryptor encryptor;
    private boolean isSupportEncrypt = true;

    public UserInfoEncryption() {
        encryptor = new SerialAESEncryptor();
        try {
            encryptor.encrypt("abc".getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LogUtils.INSTANCE.e("CSET_UIE", "not support", e);
            isSupportEncrypt = false;
        }
        synchronized (UserInfoEncryption.class) {
            int currentVersion = SharedPreferencesKt.getDefaultSharedPreferences(BaseApp.Companion.getContext()).getInt(ConfigKt.SP_KEY_ENCRYPT_VERSION_USER, 0);
            if (currentVersion < ConfigKt.USER_INFO_ENCRYPT_VERSION) {
                onUpdate(currentVersion, ConfigKt.USER_INFO_ENCRYPT_VERSION);
            }
        }
    }

    public String encrypt(String json) {
        if (json == null) {
            json = "";
        }
        if (!isSupportEncrypt) {
            return json;
        }
        return Base64.encodeToString(encryptor.encrypt(json.getBytes(StandardCharsets.UTF_8)), Base64.DEFAULT);

    }

    public String decrypt(String base64Encrypted) {
        if (base64Encrypted == null || base64Encrypted.equals("")) {
            return "";
        }
        if (!isSupportEncrypt) {
            return base64Encrypted;
        }
        try {
            return new String(encryptor.decrypt(Base64.decode(base64Encrypted, Base64.DEFAULT)), StandardCharsets.UTF_8);
        } catch (DecryptFailureException e) {
            LogUtils.INSTANCE.e("CSET_UIE", "decrypt failure", e);
            return "";
        }
    }

    /**
     * if you getUpdateInfo the encrypt method in the future, please getUpdateInfo here for compatibility
     *
     * @param i old version
     * @param ii new version
     */
    public void onUpdate(int i, int ii) {
        SharedPreferences.Editor editor = BaseApp.Companion.getContext().getSharedPreferences(ConfigKt.DEFAULT_PREFERENCE_FILENAME, Context.MODE_PRIVATE).edit();
        if (i == 0 && ii == 1) {
            String unEncryptedJson = SharedPreferencesKt.sharedPreferences(BaseApp.Companion.getContext(), ConfigKt.DEFAULT_PREFERENCE_FILENAME).getString(ConfigKt.SP_KEY_USER, "");
            if (!"".equals(unEncryptedJson)) {
                String encryptedJson = encrypt(unEncryptedJson);
                editor.putString(ConfigKt.SP_KEY_USER, encryptedJson);
            }
        }
        editor.putInt(ConfigKt.SP_KEY_ENCRYPT_VERSION_USER, ii);
        editor.apply();
    }


}
