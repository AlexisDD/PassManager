package fr.alexis.passmanager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.database.Account;

public class AccountAdapter extends ListAdapter<Account, AccountViewHolder> {

    public AccountAdapter(@NonNull DiffUtil.ItemCallback<Account> diffCallback) {
        super(diffCallback);
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
