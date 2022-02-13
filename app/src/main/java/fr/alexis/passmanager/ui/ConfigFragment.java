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
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputEditText;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.crypto.EncryptionService;
import fr.alexis.passmanager.crypto.EncryptionUtils;
import fr.alexis.passmanager.databinding.FragmentConfigBinding;

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
                binding.confirmMasterPasswordLayout.setError("Les deux mots de passe ne sont pas les mêmes.");
                return;
            }
            binding.confirmMasterPasswordLayout.setError(null);

            try {
                EncryptionService encryptionService = EncryptionService.getInstance();
                encryptionService.init(masterInput.getText().toString());

                Log.d("KEYORIGINAL", Base64.encodeToString(encryptionService.getSecretKey().getEncoded(), Base64.DEFAULT));

                String monText = "BONJOUR VOICI MON TEXTE";

                byte[] encryptedBytes;
                try {
                    encryptedBytes = encryptionService.encrypt(monText);
                    Log.d("ConfigFragment-Encrypt",  Base64.encodeToString(encryptedBytes, Base64.DEFAULT));

                    EncryptionUtils.writeSecretKeyToKeystore(encryptionService.getSecretKey());
                    SecretKey s = EncryptionUtils.readSecretKeyFromKeyStore();
                    encryptionService.init(s);
                    byte[] decoded = encryptionService.decrypt(encryptedBytes);
                    Log.d("ConfigFragment-Decrypt",  new String(decoded));
                } catch (IllegalBlockSizeException | BadPaddingException e) {
                    e.printStackTrace();
                }



                // Setting a preference to ask for configuration only on first launch
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("configured" ,true);
                editor.apply();

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_config_to_list);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeyException e) {
                e.printStackTrace();
            }

        });

                /*SecretKey secretKey = EncryptionService.createSecretKey(masterInput.getText().toString());
                String encodedKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
                Log.d("ConfigFragment", encodedKey);

                final Cipher encryptionCipher;
                final Cipher decryptionCipher;
                PBEParameterSpec pbeParamSpec = new PBEParameterSpec("ad7QUAmilD0nhaf.1TuhpVm9mwt-88ZEMWR7".getBytes(), 65536);

                encryptionCipher = Cipher.getInstance("PBEWithSHA256And256BitAES-CBC-BC");
                decryptionCipher = Cipher.getInstance("PBEWithSHA256And256BitAES-CBC-BC");

                encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey, pbeParamSpec);
                decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, pbeParamSpec);

                String monText = "BONJOUR VOICI MON TEXTE";
                byte[] encryptedBytes = encryptionCipher.doFinal(monText.getBytes());
                Log.d("ConfigFragment-Encrypt",  Base64.encodeToString(encryptedBytes, Base64.DEFAULT));
                byte[] decoded = decryptionCipher.doFinal(encryptedBytes);
                Log.d("ConfigFragment-Decrypt",  new String(decoded));*/
    }
}
