package fr.alexis.passmanager.crypto;

import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

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

    private static KeyStore initKeyStore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore;
        keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        return keyStore;
    }

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

    public static SecretKey readSecretKeyFromKeyStore(){
        try {
            KeyStore keyStore = initKeyStore();

            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) keyStore.getEntry("master_key", null);
            return entry.getSecretKey();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException ignored) {}

        return null;
    }

    public static boolean keyExists(){
        try {
            KeyStore keyStore = initKeyStore();

            return keyStore.containsAlias("master_key");
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException ignored) {}

        return false;
    }

    public static void deleteKey(){
        try {
            KeyStore keyStore = initKeyStore();

            keyStore.deleteEntry("master_key");
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException ignored) {}
    }

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
