package fr.alexis.passmanager.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.NoSuchPaddingException;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.crypto.EncryptionService;
import fr.alexis.passmanager.databinding.FragmentLoginBinding;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private FragmentLoginBinding binding;
    private SharedPreferences prefs;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        navController = NavHostFragment.findNavController(this);

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
