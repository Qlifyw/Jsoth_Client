package com.example.herem1t.rc_client.utils.crypto;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RsaClient {

    private KeyPairGenerator generator;
    private KeyPair keyPair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher cipher;

    public RsaClient(int keylength) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.generator = KeyPairGenerator.getInstance("RSA");
        this.generator.initialize(keylength);
        this.cipher = Cipher.getInstance("RSA/ECB/NoPadding");
        generateKeys();
    }

    private void generateKeys() {
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

    public byte[] encrypt(byte[] msg) throws InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(msg);
    }

    public byte[] decrypt(byte[] data)  throws InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

}
