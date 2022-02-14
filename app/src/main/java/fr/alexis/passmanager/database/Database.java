package fr.alexis.passmanager.database;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {Account.class},
        version = 1)
public abstract class Database extends RoomDatabase {

    private static volatile Database INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract AccountDao accountDao();

    public static Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "db-mailunivtours")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}