package fr.alexis.passmanager.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.common.util.concurrent.ListenableFuture;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.crypto.EncryptionService;
import fr.alexis.passmanager.database.Account;
import fr.alexis.passmanager.database.AccountViewModel;
import fr.alexis.passmanager.databinding.FragmentLoginBinding;
import fr.alexis.passmanager.databinding.FragmentSeeBinding;

public class SeeFragment extends Fragment {

    private FragmentSeeBinding binding;
    private AccountViewModel accountViewModel;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSeeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(!EncryptionService.getInstance().hasSecretKey()) {
            //login
            return;
        }
        EncryptionService encryptionService = EncryptionService.getInstance();

        navController = NavHostFragment.findNavController(this);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        Bundle args = getArguments();

        if(args != null && args.containsKey("account_id")) {
            int accountId = args.getInt("account_id");
            ListenableFuture<Account> accountListenable = accountViewModel.getAccountById(accountId);
            try {
                Account account = accountListenable.get();
                binding.titleAccount.setText(account.getDescription());
                binding.accountInput.setText(account.getAccount());
                byte[] decrypted = encryptionService.decrypt(account.getCipherPassword());
                binding.passwordInput.setText(new String(decrypted, StandardCharsets.UTF_8));
            } catch (ExecutionException | InterruptedException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
                errorNavigateBack();
            }
        } else {
            errorNavigateBack();
        }
    }

    private void errorNavigateBack() {
        Toast.makeText(requireContext(), getString(R.string.account_not_found), Toast.LENGTH_SHORT).show();
        navController.navigateUp();
    }
}
