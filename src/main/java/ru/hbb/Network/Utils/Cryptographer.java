package ru.hbb.Network.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Cryptographer {

    private Cipher cipher;
    public final String transformation;
    public final String algorithm;
    public final int key_len;

    public Cryptographer(String transformation, String algorithm, int key_len) {
        this.transformation = transformation;
        this.algorithm = algorithm;
        this.key_len = key_len;
    }

    public SecretKeySpec createSecretKey() {
        SecretKeySpec sks = null;
        byte[] bytes  = new byte[key_len];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        try {
            MessageDigest md;
            byte[] key;
            md = MessageDigest.getInstance(algorithm);
            key = md.digest(bytes);
            key = Arrays.copyOf(key, key_len);
            sks = new SecretKeySpec(key, transformation);
        } catch (NoSuchAlgorithmException e) { }
        return sks;
    }

    public byte[] encrypt(SecretKeySpec secretKey, byte[] plainText) {
        try {
            cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encode(cipher.doFinal(plainText));
        } catch (Exception e) { }
        return null;
    }

    public byte[] decrypt(SecretKeySpec secretKey, byte[] encryptedText) {
        try {
            cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        } catch (Exception e) {}
        return null;
    }

    public String getKeyString(SecretKeySpec key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public SecretKeySpec getKeyObject(String key) {
        byte[] decodedKey = Base64.getDecoder().decode(key);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, transformation);
    }

}
