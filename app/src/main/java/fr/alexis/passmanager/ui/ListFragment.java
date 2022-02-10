package fr.alexis.passmanager.ui;

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
        RecyclerView recyclerView = binding.rvPasswords;

        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        Password p1 = new Password("GitHub", "alexisdesaintdenis@yahoo.fr", "ABCDEF");
        Password p2 = new Password("OVH", "alexisdesaintdenis@hotmail.fr", "ABCDEF");
        Password p3 = new Password("Google", "alexisdd50@gmail.com", "ABCDEF");
        Password p4 = new Password("PayPal", "contact@yahoo.fr", "ABCDEF");
        Password p5 = new Password("Yahoo", "abcdefghijk@yahoo.fr", "ABCDEF");
        Password p6 = new Password("Hotmail", "alexisdesaintdenis@yahoo.fr", "ABCDEF");
        Password p7 = new Password("Riot", "alexisdesaintdenis@yahoo.fr", "ABCDEF");
        Password p8 = new Password("Coinbase", "test@yahoo.fr", "ABCDEF");
        Password p9 = new Password("Binance", "alexisdesaintdenis@yahoo.fr", "ABCDEF");
        Password p10 = new Password("Matmut", "webmaster@yahoo.fr", "ABCDEF");
        Password[] passwords = {p1, p2, p3, p4, p5, p6, p7, p8, p9, p10};

        recyclerView.setAdapter(adapter = new PasswordAdapter(passwords));

        binding.fabAdd.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_list_to_add);
        });
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