package com.example.herem1t.rc_client.utils.crypto;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AesClient {

    private KeyGenerator keyGenerator;
    private Key key;

    public AesClient () throws NoSuchAlgorithmException {
        this.keyGenerator = KeyGenerator.getInstance("AES");
        this.key = keyGenerator.generateKey();
    }


    public Key getKey() {
        return key;
    }

    public static byte[] encrypt(byte[] msg, SecretKeySpec sks) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        return cipher.doFinal(msg);
    }

    public static byte[] decrypt(byte[] data, SecretKeySpec sks) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        return  cipher.doFinal(data);
    }

}
