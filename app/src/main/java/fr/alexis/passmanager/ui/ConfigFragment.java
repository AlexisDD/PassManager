package fr.alexis.passmanager.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.crypto.EncryptionService;
import fr.alexis.passmanager.crypto.EncryptionUtils;
import fr.alexis.passmanager.databinding.FragmentConfigBinding;

/**
 * Fragment to setup a master password.
 * Displays on first launch or after a reset.
 */
public class ConfigFragment extends Fragment {

    private FragmentConfigBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConfigBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextInputEditText masterInput = binding.masterPasswordInput;
        TextInputEditText confirmInput = binding.confirmMasterPasswordInput;

        binding.buttonSubmit.setOnClickListener(v -> {
            if(masterInput.getText() == null || confirmInput.getText() == null
                    || !masterInput.getText().toString().equals(confirmInput.getText().toString())) {
                binding.confirmMasterPasswordLayout.setError(getString(R.string.confirm_pw_error));
                return;
            }
            binding.confirmMasterPasswordLayout.setError(null);

            try {
                EncryptionService encryptionService = EncryptionService.getInstance();
                encryptionService.init(masterInput.getText().toString());

                String monText = EncryptionUtils.LOGIN_HASH;
                byte[] encryptedBytes = encryptionService.encrypt(monText);
                EncryptionUtils.writeSecretKeyToKeystore(encryptionService.getSecretKey());

                // Setting a preference to ask for configuration only on first launch
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("login", Base64.encodeToString(encryptedBytes, Base64.DEFAULT));
                editor.putBoolean("configured" ,true);
                editor.apply();

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_config_to_list);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | IOException e) {
                e.printStackTrace();
            }
        });
    }
}
