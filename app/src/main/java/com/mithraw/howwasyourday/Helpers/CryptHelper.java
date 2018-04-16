package com.mithraw.howwasyourday.Helpers;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class CryptHelper {
    public static SecretKeySpec getSecretKey() throws NoSuchAlgorithmException, java.io.UnsupportedEncodingException {
        byte[] key = "Fabien Moraldo est le développeur de cette application".getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        return new SecretKeySpec(key, "AES");
    }

    public static CipherOutputStream getCipherOutputStream(OutputStream outputStream) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, CryptHelper.getSecretKey());
            return new CipherOutputStream(outputStream, cipher);
        } catch (Exception e) {

        }
        return null;
    }
    public static CipherInputStream getCipherInputStream(InputStream inputStream) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, CryptHelper.getSecretKey());
            return new CipherInputStream(inputStream, cipher);
        } catch (Exception e) {

        }
        return null;
    }
}
