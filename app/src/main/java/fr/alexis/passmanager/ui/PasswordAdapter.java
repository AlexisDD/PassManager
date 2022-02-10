package fr.alexis.passmanager.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fr.alexis.passmanager.R;
import fr.alexis.passmanager.model.Password;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordViewHolder>{

        private final Password[] localPasswords;

        public PasswordAdapter(Password[] dataSet) {
            localPasswords = dataSet;
        }

        @NonNull
        @Override
        public PasswordViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_password, viewGroup, false);

            return new PasswordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PasswordViewHolder viewHolder, final int position) {
            Password password = localPasswords[position];

            viewHolder.getItemDescription().setText(password.getDescription());
            if(password.getAccount() != null)
                viewHolder.getItemAccount().setText(password.getAccount());
        }

        @Override
        public int getItemCount() {
            return localPasswords.length;
        }
}
