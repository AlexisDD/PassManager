package fr.alexis.passmanager.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

@Dao
public interface  AccountDao {

    @Insert
    void insert(Account account);

    @Insert
    void insertAll(Account... accounts);

    @Delete
    void delete(Account account);

    @Query("SELECT * FROM account WHERE id = :id")
    ListenableFuture<Account> getAccountById(int id);

    @Query("SELECT * FROM account")
    LiveData<List<Account>> getAllAccounts();

    @Query("DELETE FROM account WHERE id = :id")
    void deleteById(int id);
}
