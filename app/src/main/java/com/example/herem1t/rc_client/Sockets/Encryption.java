package com.example.herem1t.rc_client.Sockets;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;


/**
 * Created by Herem1t on 10.03.2018.
 */

public class Encryption {

    private static final String LOG_TAG = "Client_debug";

    static PrivateKey privateKey;

    public static void main(String[] args) throws Exception {

        //String base64RSAmsg =  Base64.getEncoder().encodeToString(new Encryption().encryptRSA("Terminal> sudo postgres psql"));
        byte[] enc = new Encryption().encryptRSA("Terminal> sudo postgres psql");
        System.out.println(new String(enc));

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, Encryption.privateKey);
        byte[] dec = cipher.doFinal(enc);
        System.out.println(new String(dec));

    }

    public static byte[] RSA_enc(byte[] msg, Key key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(msg);
    }

    public static byte[] RSA_dec(byte[] data, Key key)  throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return  cipher.doFinal(data);
    }

    private byte[] encryptRSA(String msg) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        final int keySize = 2048;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);

        KeyPair keyPair = keyPairGenerator.genKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();

        Log.d(LOG_TAG,"Original key (byte): " +  privateKey.getEncoded().toString());

        String encoded = new String(Base64.encode(privateKey.getEncoded(), Base64.DEFAULT));
        Log.d(LOG_TAG,"Encoded base64 " + encoded);

        byte[] decoded = Base64.decode(encoded.getBytes(), Base64.DEFAULT);
        Log.d(LOG_TAG,"Decoded " + decoded.toString());

        String encoded_again = new String(Base64.encode(decoded, Base64.DEFAULT));
        Log.d(LOG_TAG,"Encode again " + encoded_again);
        Log.d(LOG_TAG,"Is equals? " + encoded.equals(encoded_again));

        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] hash = md.digest(encoded.getBytes());
        byte[] hash1 = md.digest(encoded_again.getBytes());
        String str = Base64.encodeToString(hash, Base64.DEFAULT);
        String str1 = Base64.encodeToString(hash1, Base64.DEFAULT);
        Log.d(LOG_TAG, str.equals(str1) + "");

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(msg.getBytes());
    }

    public static class Generate_RSA {
        private KeyPairGenerator generator;
        private KeyPair keyPair;
        private PrivateKey privateKey;
        private PublicKey publicKey;

        public Generate_RSA(int keylength) throws NoSuchAlgorithmException {
            this.generator = KeyPairGenerator.getInstance("RSA");
            this.generator.initialize(keylength);
            CreateKeys();
        }

        private void CreateKeys() {
            this.keyPair = this.generator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }
    }

    public static class Generate_AES {
        private KeyGenerator keyGenerator;
        private Key key;
        private Cipher cipher;

        public Generate_AES () throws NoSuchAlgorithmException, NoSuchPaddingException {
            this.keyGenerator = KeyGenerator.getInstance("AES");
            this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            this.key = keyGenerator.generateKey();
        }

        public Cipher getCipher() {
            return cipher;
        }

        public Key getKey() {
            return key;
        }
    }

}
