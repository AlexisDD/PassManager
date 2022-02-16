package fr.alexis.passmanager.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.Executor;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.crypto.EncryptionService;
import fr.alexis.passmanager.crypto.EncryptionUtils;
import fr.alexis.passmanager.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private FragmentLoginBinding binding;
    private SharedPreferences prefs;
    private NavController navController;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        navController = NavHostFragment.findNavController(this);

        // Check for biometric login
        int canBiometricAuth = BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        if(canBiometricAuth == BiometricManager.BIOMETRIC_SUCCESS) {
            executor = ContextCompat.getMainExecutor(context);
            biometricPrompt = new BiometricPrompt(this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    Toast.makeText(context,
                            getString(R.string.auth_success), Toast.LENGTH_SHORT).show();
                    EncryptionService encryptionService = EncryptionService.getInstance();
                    SecretKey secretKey = EncryptionUtils.readSecretKeyFromKeyStore();
                    try {
                        encryptionService.init(secretKey);
                        navController.navigate(R.id.action_login_to_list);
                    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
                        encryptionService.destroy();
                    }
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.biometric_title))
                    .setSubtitle(getString(R.string.biometric_subtitle))
                    .setNegativeButtonText(getString(R.string.biometric_cancel))
                    .build();
            biometricPrompt.authenticate(promptInfo);

        }

        binding.buttonSubmit.setOnClickListener(this);
    }

    // Click on login button
    @Override
    public void onClick(View view) {
        String cipherLoginPhrase = prefs.getString("login", "");
        binding.masterPasswordLayout.setError(null);
        if(binding.masterPasswordInput.getText() == null
                || binding.masterPasswordInput.getText().toString().isEmpty()) {
            binding.masterPasswordLayout.setError(getString(R.string.invalid_password));
        } else {
            String plainPassword = binding.masterPasswordInput.getText().toString();
            EncryptionService encryptionService = EncryptionService.getInstance();
            try {
                encryptionService.init(plainPassword);
                if(encryptionService.checkKey(Base64.decode(cipherLoginPhrase, Base64.DEFAULT))) {
                    navController.navigate(R.id.action_login_to_list);
                } else {
                    binding.masterPasswordLayout.setError(getString(R.string.invalid_password));
                    encryptionService.destroy();
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }
}
