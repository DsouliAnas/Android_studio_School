package com.example.school_project;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.util.Base64;

import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class EncryptionHelper {

    private static final String KEY_ALIAS = "encryption_key";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    private KeyStore keyStore;

    public EncryptionHelper() throws Exception {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
    }

    // Generate a new encryption key
    private void generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        keyGenerator.init(
                new KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build());
        keyGenerator.generateKey();
    }

    // Get the encryption key
    private Key getKey() throws Exception {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey();
        }
        return keyStore.getKey(KEY_ALIAS, null);
    }

    // Encrypt the password
    public String encrypt(String input) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, (SecretKey) getKey());
        byte[] iv = cipher.getIV();
        byte[] encryption = cipher.doFinal(input.getBytes());
        byte[] combined = new byte[iv.length + encryption.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryption, 0, combined, iv.length, encryption.length);
        return Base64.encodeToString(combined, Base64.DEFAULT);
    }

    // Decrypt the password
    public String decrypt(String encryptedInput) throws Exception {
        byte[] decodedData = Base64.decode(encryptedInput, Base64.DEFAULT);
        byte[] iv = new byte[12];
        System.arraycopy(decodedData, 0, iv, 0, 12);
        byte[] encryptedData = new byte[decodedData.length - 12];
        System.arraycopy(decodedData, 12, encryptedData, 0, encryptedData.length);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.DECRYPT_MODE, (SecretKey) getKey(), spec);
        byte[] decrypted = cipher.doFinal(encryptedData);
        return new String(decrypted);
    }
}
