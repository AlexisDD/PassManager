package fr.alexis.passmanager.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

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

    void insert(Account account) {
        Database.databaseWriteExecutor.execute(() -> accountDao.insert(account));
    }
}
