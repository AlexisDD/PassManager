package fr.alexis.passmanager.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Random;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.database.AccountViewModel;
import fr.alexis.passmanager.databinding.FragmentListBinding;
import fr.alexis.passmanager.database.Account;

public class ListFragment extends Fragment {

    private final String TAG = "ListFragment";
    private FragmentListBinding binding;
    private AccountViewModel accountViewModel;
    private AccountAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        NavController navController = NavHostFragment.findNavController(this);
        RecyclerView recyclerView = binding.rvPasswords;
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        boolean configured = sharedPreferences.getBoolean("configured", false);
        if(!configured) {
            navController.navigate(R.id.action_list_to_config);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter = new AccountAdapter(new AccountAdapter.AccountDiff()));

        accountViewModel.getAllAccounts().observe(getViewLifecycleOwner(), accounts -> {
            // Update the cached copy of the accounts in the adapter.
            checkIfEmpty(accounts.size());
            adapter.submitList(accounts);
        });

        binding.fabAdd.setOnClickListener(v -> navController.navigate(R.id.action_list_to_add));
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
            Log.d(TAG, "Rechercher");
            // Rechercher dans la liste
        } else if (id == R.id.action_import) {
            Log.d(TAG, "Importer");
            // Import mots de passes
        } else if (id == R.id.action_export) {
            Log.d(TAG, "Exporter");
            // Exporter mots de passes
        } else if (id == R.id.action_reset) {
            Log.d(TAG, "Reset");
            // Réinitialiser
        }

        return super.onOptionsItemSelected(item);
    }
}