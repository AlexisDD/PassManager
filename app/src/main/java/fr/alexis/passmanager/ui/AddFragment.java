package fr.alexis.passmanager.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.crypto.EncryptionService;
import fr.alexis.passmanager.database.Account;
import fr.alexis.passmanager.database.AccountViewModel;
import fr.alexis.passmanager.databinding.FragmentAddBinding;

public class AddFragment extends Fragment implements View.OnClickListener {

    private FragmentAddBinding binding;
    private AccountViewModel accountViewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        navController = NavHostFragment.findNavController(this);

        binding.buttonAdd.setOnClickListener(this);
    }

    // Click on the "Add" button to add an account
    @Override
    public void onClick(View view) {
        TextInputEditText accountInput = binding.accountInput;
        TextInputEditText descriptionInput = binding.descriptionInput;
        TextInputEditText passwordInput = binding.passwordInput;
        if(checkInputError(accountInput, binding.accountLayout, "Compte / identifiant invalide.")
            || checkInputError(descriptionInput, binding.descriptionLayout, "Description invalide.")
            || checkInputError(passwordInput, binding.passwordLayout, "Mot de passe invalide.")) {
            return;
        }

        byte[] cipherPassword = encryptPassword(passwordInput.getText().toString());
        if(cipherPassword == null) {
            // login
        } else {
            Account newAccount = new Account(descriptionInput.getText().toString(),
                    accountInput.getText().toString(),
                    cipherPassword);

            accountViewModel.insert(newAccount);
            navController.navigateUp();
            Snackbar snackBar = Snackbar.make(requireActivity().findViewById(R.id.main_parent),
                    R.string.add_account_success, Snackbar.LENGTH_LONG);
            snackBar.show();
        }
    }

    private byte[] encryptPassword(String plainPassword) {
        if(EncryptionService.getInstance().hasSecretKey()){
            try {
                EncryptionService encryptionService = EncryptionService.getInstance();
                return encryptionService.encrypt(plainPassword);
            } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | IOException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean checkInputError(EditText input, TextInputLayout layout, String errorText) {
        if(input.getText() == null
                || input.getText().toString().isEmpty()) {
            layout.setError(errorText);
            return true;
        }
        return false;
    }
}
