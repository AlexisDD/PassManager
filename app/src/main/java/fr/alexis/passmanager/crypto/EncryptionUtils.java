package fr.alexis.passmanager.crypto;

import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class EncryptionUtils {

    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final int KEY_AUTH_TIMEOUT = 120;
    public static String LOGIN_HASH = "bc7b8edf9b60a083b3cd648237c10897";

    /**
     * Initialize the Android KeyStore
     * The Android KeyStore is used to store cryptographic keys in a safe place on the device.
     * @return the instance of KeyStore
     */
    private static KeyStore initKeyStore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore;
        keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        return keyStore;
    }

    /**
     * Write the master secret key to the Android KeyStore.
     * Requires user authentication (biometric, pin, ..)
     * @param secretKey The master secret key to store
     */
    public static void writeSecretKeyToKeystore(SecretKey secretKey) {
        try {
            KeyStore keyStore = initKeyStore();
            KeyProtection.Builder keyProt = new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setRandomizedEncryptionRequired(false)
                    .setUserAuthenticationRequired(true)
                    .setInvalidatedByBiometricEnrollment(true);

            // Only if minSdk >= 30
            keyProt.setUserAuthenticationParameters(KEY_AUTH_TIMEOUT, KeyProperties.AUTH_BIOMETRIC_STRONG);

            keyStore.setEntry(
                    "master_key",
                    new KeyStore.SecretKeyEntry(secretKey),
                    keyProt.build());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException ignored) {}
    }

    /**
     * Read the master secret key from the Android KeyStore.
     * @return the master secret key
     */
    @Nullable
    public static SecretKey readSecretKeyFromKeyStore(){
        try {
            KeyStore keyStore = initKeyStore();

            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry("master_key", null);
            return entry.getSecretKey();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException ignored) {}

        return null;
    }

    /**
     * Check if the master key exists in the Android KeyStore.
     * @return <tt>true</tt> if the key exists
     */
    public static boolean keyExists(){
        try {
            KeyStore keyStore = initKeyStore();

            return keyStore.containsAlias("master_key");
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException ignored) {}

        return false;
    }

    /**
     * Delete the master secret key from the Android KeyStore.
     */
    public static void deleteKey(){
        try {
            KeyStore keyStore = initKeyStore();

            keyStore.deleteEntry("master_key");
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException ignored) {}
    }

    /**
     * Check if the secret key is accessible (i.e the user is authenticated),
     * else init the ciphers.
     * @param navController instance of NavController
     * @param actionId id of the fallback (i.e login) screen
     * @return <tt>true</tt> if the user is authenticated,
     * if an error occurs the user is took back to the <tt>actionId</tt> screen.
     */
    public static boolean checkAuthentication(NavController navController, int actionId) {
        if(!EncryptionService.getInstance().hasSecretKey()) {
            navController.navigate(actionId);
        } else {
            try {
                EncryptionService.getInstance().initCiphers();
                return false;
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
                navController.navigate(actionId);
            }
        }
        return true;
    }
}
