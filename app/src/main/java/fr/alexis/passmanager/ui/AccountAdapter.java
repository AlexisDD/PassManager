package fr.alexis.passmanager.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.database.Account;

public class AccountAdapter extends ListAdapter<Account, AccountViewHolder> {

    private ListFragment listFragment;

    public AccountAdapter(@NonNull DiffUtil.ItemCallback<Account> diffCallback, ListFragment listFragment) {
        super(diffCallback);
        this.listFragment = listFragment;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_password, viewGroup, false);

        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder viewHolder, final int position) {
        Account account = getItem(position);

        viewHolder.getItemDescription().setText(account.getDescription());
        if(account.getAccount() != null)
            viewHolder.getItemAccount().setText(account.getAccount());

        viewHolder.getItemButtonView().setOnClickListener(view -> {
            NavController navController = NavHostFragment.findNavController(listFragment);
            Bundle args = new Bundle();
            args.putInt("account_id", account.getId());
            navController.navigate(R.id.action_list_to_see, args);
        });
        viewHolder.getItemButtonMore().setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.item_account_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.delete_account) {
                    listFragment.deleteAccount(account.getId());
                }
                return true;
            });
            popupMenu.show();
        });
    }

    static class AccountDiff extends DiffUtil.ItemCallback<Account> {

        @Override
        public boolean areItemsTheSame(@NonNull Account oldItem, @NonNull Account newItem) {
            return oldItem.getId()== newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Account oldItem, @NonNull Account newItem) {
            return oldItem.getAccount().equals(newItem.getAccount())
                    && oldItem.getDescription().equals(newItem.getAccount());
        }
    }
}
