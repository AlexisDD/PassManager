package fr.alexis.passmanager.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

/**
 * Interface between the database and the program.
 */
public class AccountRepository {

    private AccountDao accountDao;
    private LiveData<List<Account>> allAccounts;

    AccountRepository(Application application) {
        Database db = Database.getDatabase(application);
        accountDao = db.accountDao();
        allAccounts = accountDao.getAllAccounts();
    }

    LiveData<List<Account>> getAllAccounts() {
        return allAccounts;
    }

    ListenableFuture<Account> getAccountById(int id){
        return accountDao.getAccountById(id);
    }

    void insert(Account account) {
        Database.databaseWriteExecutor.execute(() -> accountDao.insert(account));
    }

    public void deleteById(int id) {
        Database.databaseWriteExecutor.execute(() -> accountDao.deleteById(id));
    }
}
