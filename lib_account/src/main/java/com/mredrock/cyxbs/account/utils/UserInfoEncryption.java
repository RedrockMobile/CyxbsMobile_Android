package com.mredrock.cyxbs.account.utils;

import static com.mredrock.cyxbs.account.AccountService.SP_KEY_USER_V2;
import android.content.SharedPreferences;
import android.util.Base64;
import com.mredrock.cyxbs.config.sp.SpTableKt;
import java.nio.charset.StandardCharsets;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */

public class UserInfoEncryption {

    private final Encryptor encryptor;
    private boolean isSupportEncrypt = true;
    
    //SharedPreferences key for encrypt version of user
    private static final String SP_KEY_ENCRYPT_VERSION_USER = "encrypt_version_user";
    
    public UserInfoEncryption() {
        encryptor = new SerialAESEncryptor();
        try {
            encryptor.encrypt("abc".getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            isSupportEncrypt = false;
        }
        synchronized (UserInfoEncryption.class) {
            int currentVersion = SpTableKt.getDefaultSp().getInt(SP_KEY_ENCRYPT_VERSION_USER, 0);
            //SharedPreferences value for encrypt version of user
            int USER_INFO_ENCRYPT_VERSION = 1;
            if (currentVersion < USER_INFO_ENCRYPT_VERSION) {
                onUpdate(currentVersion, USER_INFO_ENCRYPT_VERSION);
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
        SharedPreferences.Editor editor = SpTableKt.getDefaultSp().edit();
        if (i == 0 && ii == 1) {
            String unEncryptedJson = SpTableKt.getDefaultSp().getString(SP_KEY_USER_V2, "");
            if (!"".equals(unEncryptedJson)) {
                String encryptedJson = encrypt(unEncryptedJson);
                editor.putString(SP_KEY_USER_V2, encryptedJson);
            }
        }
        editor.putInt(SP_KEY_ENCRYPT_VERSION_USER, ii);
        editor.apply();
    }


}
