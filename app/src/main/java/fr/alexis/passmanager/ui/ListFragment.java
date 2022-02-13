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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.databinding.FragmentListBinding;
import fr.alexis.passmanager.model.Password;

public class ListFragment extends Fragment {

    private final String TAG = "ListFragment";
    private FragmentListBinding binding;
    private PasswordAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        NavController navController = NavHostFragment.findNavController(this);

        boolean configured = sharedPreferences.getBoolean("configured", false);
        if(!configured) {
            navController.navigate(R.id.action_list_to_config);
        }
        RecyclerView recyclerView = binding.rvPasswords;

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        Password p1 = new Password("Nom1", "test@yahoo.fr", "ABCDEF");
        Password p2 = new Password("Nom2", "test@hotmail.fr", "ABCDEF");
        Password p3 = new Password("Nom3", "test@gmail.com", "ABCDEF");
        Password p4 = new Password("Nom4", "contact@yahoo.fr", "ABCDEF");
        Password p5 = new Password("Nom5", "abcdefghijk@yahoo.fr", "ABCDEF");
        Password p6 = new Password("Nom6", "azertyuiop@yahoo.fr", "ABCDEF");
        Password p7 = new Password("Nom7", "poiuytreza@yahoo.fr", "ABCDEF");
        Password p8 = new Password("Nom8", "test@yahoo.fr", "ABCDEF");
        Password p9 = new Password("Nom9", "mlkjhgfdsq@yahoo.fr", "ABCDEF");
        Password[] passwords = {p1, p2, p3, p4, p5, p6, p7, p8, p9};

        recyclerView.setAdapter(adapter = new PasswordAdapter(passwords));

        // If the dataset is empty, display a message.
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                if(adapter.getItemCount() == 0) {
                    binding.emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    binding.emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.fabAdd.setOnClickListener(v -> navController.navigate(R.id.action_list_to_add));
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