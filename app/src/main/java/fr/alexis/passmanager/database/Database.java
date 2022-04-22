package fr.alexis.passmanager.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Instance of Android Room Database to store accounts information.
 */
@androidx.room.Database(entities = {Account.class},
        version = 1)
public abstract class Database extends RoomDatabase {

    private static volatile Database INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract AccountDao accountDao();

    public static Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "db-passmanager")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}