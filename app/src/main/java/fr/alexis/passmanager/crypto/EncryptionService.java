package fr.alexis.passmanager.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionService {

    private static EncryptionService instance;
    private static final String PBEAlgorithm = "PBEWithSHA256And256BitAES-CBC-BC";
    private static final byte[] salt = "ad7QUAmilD0nhaf.1TuhpVm9mwt-88ZEMWR7".getBytes();
    private static final int iterationCount = 65536;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int TAG_LENGTH_BIT = 128;
    private static final byte[] iv = getRandomIV();
    private SecretKey secretKey;
    private Cipher encryptionCipher;
    private Cipher decryptionCipher;

    private EncryptionService() { }

    public static EncryptionService getInstance() {
        if(instance == null) {
            instance = new EncryptionService();
        }
        return instance;
    }

    public void init(String password) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeyException {
        secretKey = createSecretKey(password);
        initCiphers();
    }

    public void init(SecretKey secretKey) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeyException {
        this.secretKey = secretKey;
        initCiphers();
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public boolean hasSecretKey() {
        return secretKey != null;
    }

    public byte[] encrypt(String plainText) throws IllegalBlockSizeException, BadPaddingException {
        return encryptionCipher.doFinal(plainText.getBytes());
    }

    public byte[] decrypt(byte[] cipherText) throws IllegalBlockSizeException, BadPaddingException {
        return decryptionCipher.doFinal(cipherText);
    }

    public boolean checkKey(byte[] cipherText) {
        try {
            decryptionCipher.doFinal(cipherText);
            return true;
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            return false;
        }
    }

    private void initCiphers() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);

        encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");

        encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
    }


    public static SecretKey createSecretKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBEAlgorithm);
        SecretKey s =  keyFac.generateSecret(pbeKeySpec);

        return new SecretKeySpec(s.getEncoded(), "AES");
    }

    private static byte[] getRandomIV() {
        byte[] nonce = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }
}
