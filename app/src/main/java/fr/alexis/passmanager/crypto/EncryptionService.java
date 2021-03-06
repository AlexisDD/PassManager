package fr.alexis.passmanager.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Singleton utility class used to perform cryptographic (AES algorithm) operations as
 * secret key generation, encryption, decryption, ...
 */
public class EncryptionService {

    private static EncryptionService instance;

    private static final String PBEAlgorithm = "PBEWithSHA256And256BitAES-CBC-BC";
    private static final byte[] salt = "ad7QUAmilD0nhaf.1TuhpVm9mwt-88ZEMWR7".getBytes();
    private static final int iterationCount = 65536;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int TAG_LENGTH_BIT = 128;
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

    public void init(SecretKey secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        this.secretKey = secretKey;
        initCiphers();
    }

    public void destroy() {
        instance = null;
    }

    public SecretKey getSecretKey() {
        return secretKey;
    }

    public boolean hasSecretKey() {
        return secretKey != null;
    }

    /**
     * Encrypt a plain text using the master secret key.
     * @param plainText text to encrypt
     * @return cipher text (byte array)
     */
    public byte[] encrypt(String plainText) throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, IOException {
        byte[] iv = getRandomIV();
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

        byte[] cipher = encryptionCipher.doFinal(plainText.getBytes());

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        b.write(iv);
        b.write(cipher);
        return b.toByteArray();
    }

    /**
     * Decrypt a cipher text to the original plain text using the master secret key.
     * @param cipherText text (byte array) to decrypt
     * @return plain text (byte array)
     */
    public byte[] decrypt(byte[] cipherText) throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        byte[] iv = Arrays.copyOfRange(cipherText , 0, IV_LENGTH_BYTE);
        byte[] toDecrypt = Arrays.copyOfRange(cipherText, 12, cipherText.length);

        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

        return decryptionCipher.doFinal(toDecrypt);
    }

    /**
     * Check if the key is the valid master key used to encrypt passwords.
     * @param cipherText test cipher text
     * @return <tt>true</tt> if the key is the master key, <tt>false</tt> otherwise
     */
    public boolean checkKey(byte[] cipherText) {
        try {
            byte[] iv = Arrays.copyOfRange(cipherText , 0, IV_LENGTH_BYTE);
            byte[] toDecrypt = Arrays.copyOfRange(cipherText, IV_LENGTH_BYTE, cipherText.length);

            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);
            decryptionCipher.doFinal(toDecrypt);

            return true;
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            return false;
        }
    }

    /**
     * Init the ciphers with the master key.
     */
    public void initCiphers() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = getRandomIV();
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
        decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

    }

    /**
     * Create a 256-bit AES key from a password.
     * @param password plain password
     * @return instance of the created SecretKey
     */
    public static SecretKey createSecretKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount);
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance(PBEAlgorithm);
        SecretKey s =  keyFac.generateSecret(pbeKeySpec);

        return new SecretKeySpec(s.getEncoded(), "AES");
    }

    /**
     * Get a random initialization vector for the AES-256 algorithm
     * @return random IV
     */
    private static byte[] getRandomIV() {
        byte[] nonce = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }
}
