package fr.alexis.passmanager.ui;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import fr.alexis.passmanager.R;

public class AccountViewHolder extends RecyclerView.ViewHolder {
    private final TextView itemDescription;
    private final TextView itemAccount;
    private final ImageView itemIcon;
    private final ImageButton itemButtonView;
    private final ImageButton itemButtonMore;

    public AccountViewHolder(View v) {
        super(v);

        this.itemDescription = v.findViewById(R.id.item_description);
        this.itemAccount = v.findViewById(R.id.item_account);
        this.itemIcon = v.findViewById(R.id.item_icon);
        this.itemButtonView = v.findViewById(R.id.item_view);
        this.itemButtonMore = v.findViewById(R.id.item_more);
    }

    public TextView getItemAccount() {
        return itemAccount;
    }

    public TextView getItemDescription() {
        return itemDescription;
    }

    public ImageView getItemIcon() {
        return itemIcon;
    }

    public ImageButton getItemButtonMore() {
        return itemButtonMore;
    }

    public ImageButton getItemButtonView() {
        return itemButtonView;
    }
}
