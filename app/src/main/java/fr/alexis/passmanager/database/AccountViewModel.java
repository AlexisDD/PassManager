package fr.alexis.passmanager.database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

/**
 * View model to manage database interactions and store data independently of the UI.
 */
public class AccountViewModel extends AndroidViewModel {

    private final AccountRepository repository;

    private final LiveData<List<Account>> allAccounts;

    public AccountViewModel (Application application) {
        super(application);
        repository = new AccountRepository(application);
        allAccounts = repository.getAllAccounts();
    }

    public LiveData<List<Account>> getAllAccounts() { return allAccounts; }

    public ListenableFuture<Account> getAccountById(int id){
        return repository.getAccountById(id);
    }

    public void insert(Account account) { repository.insert(account); }

    public void deleteById(int id) {
        repository.deleteById(id);
    }
}