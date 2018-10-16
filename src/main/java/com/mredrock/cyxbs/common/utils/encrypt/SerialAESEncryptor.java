package com.mredrock.cyxbs.common.utils.encrypt;

import android.os.Build;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */

public class SerialAESEncryptor implements Encryptor {

    @Override
    public byte[] encrypt(byte[] orig) {
        try {
            SecretKeySpec sKeySpec = generateKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            return cipher.doFinal(orig);
        } catch (Exception e) {
            throw new RuntimeException("encrypt failure", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] encrypted) throws DecryptFailureException {
        try {
            SecretKeySpec sKeySpec = generateKey();
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new DecryptFailureException(e);
        }
    }

    private static SecretKeySpec generateKey() throws Exception {
        String key = Build.SERIAL;
        if (key == null || key.equals("")) {
            key = "huQVa6y^Rd0Z^e#K";
        }
        while (key.length() < 16) {
            key += key;
        }
        byte[] keyBytes = key.getBytes("US-ASCII");
        return new SecretKeySpec(
                InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(keyBytes, 16), "AES");
    }

}
