package fr.alexis.passmanager.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Representation of an account in the database (pair name-cipher password).
 */
@Entity
public class Account {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    private String account;
    private byte[] cipherPassword;

    public Account(String description, String account, byte[] cipherPassword){
        this.description = description;
        this.account = account;
        this.cipherPassword = cipherPassword;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public byte[] getCipherPassword() {
        return cipherPassword;
    }

    public void setCipherPassword(byte[] cipherPassword) {
        this.cipherPassword = cipherPassword;
    }
}
