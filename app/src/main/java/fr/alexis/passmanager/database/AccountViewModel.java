package fr.alexis.passmanager.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class AccountViewModel extends AndroidViewModel {

    private AccountRepository repository;

    private final LiveData<List<Account>> allAccounts;

    public AccountViewModel (Application application) {
        super(application);
        repository = new AccountRepository(application);
        allAccounts = repository.getAllAccounts();
    }

    public LiveData<List<Account>> getAllAccounts() { return allAccounts; }

    public void insert(Account account) { repository.insert(account); }
}