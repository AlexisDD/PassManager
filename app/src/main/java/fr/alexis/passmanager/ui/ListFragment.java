package fr.alexis.passmanager.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.crypto.EncryptionUtils;
import fr.alexis.passmanager.database.AccountViewModel;
import fr.alexis.passmanager.database.Database;
import fr.alexis.passmanager.databinding.FragmentListBinding;

public class ListFragment extends Fragment {

    private final String TAG = "ListFragment";
    private FragmentListBinding binding;
    private AccountViewModel accountViewModel;
    private AccountAdapter adapter;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        navController = NavHostFragment.findNavController(this);
        RecyclerView recyclerView = binding.rvPasswords;
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        boolean configured = sharedPreferences.getBoolean("configured", false);
        if(!configured) {
            navController.navigate(R.id.action_list_to_config);
            return;
        } else {
            if(EncryptionUtils.checkAuthentication(navController, R.id.action_list_to_login))
                return;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter = new AccountAdapter(new AccountAdapter.AccountDiff(), this));

        accountViewModel.getAllAccounts().observe(getViewLifecycleOwner(), accounts -> {
            // Update the cached copy of the accounts in the adapter.
            checkIfEmpty(accounts.size());
            adapter.submitList(accounts);
        });

        binding.fabAdd.setOnClickListener(v -> {
            if(EncryptionUtils.checkAuthentication(navController, R.id.action_list_to_login)) {
                Toast.makeText(requireContext(),
                        getString(R.string.reconnect), Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            navController.navigate(R.id.action_list_to_add);
        });
    }

    private void checkIfEmpty(int size) {
        if(size == 0 && binding.rvPasswords.getVisibility() == View.VISIBLE) {
            binding.emptyView.setVisibility(View.VISIBLE);
            binding.rvPasswords.setVisibility(View.GONE);
        } else if(size != 0 && binding.emptyView.getVisibility() == View.VISIBLE) {
            binding.emptyView.setVisibility(View.GONE);
            binding.rvPasswords.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        EncryptionUtils.checkAuthentication(navController, R.id.action_list_to_login);
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            // Search an account, not implemented yet
            Toast.makeText(requireContext(), getString(R.string.not_yet_implemented), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_import) {
            // Import accounts, not implemented yet
            Toast.makeText(requireContext(), getString(R.string.not_yet_implemented), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_export) {
            // Export accounts, not implemented yet
            Toast.makeText(requireContext(), getString(R.string.not_yet_implemented), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_reset) {
            // Reset the application (erase all accounts & delete the master key)
            Database.databaseWriteExecutor.execute(() -> {
                Database db = Database.getDatabase(requireContext());
                db.clearAllTables();
                EncryptionUtils.deleteKey();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                sharedPreferences.edit().clear().apply();
            });
            navController.navigate(R.id.action_list_to_config);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete an account from the UI & the database.
     * @param accountId id of the account to remove
     */
    public void deleteAccount(int accountId) {
        accountViewModel.deleteById(accountId);
        Snackbar snackBar = Snackbar.make(requireActivity().findViewById(R.id.main_parent),
                R.string.delete_account_success, Snackbar.LENGTH_LONG);
        snackBar.show();
    }
}