package de.wuapps.moredays.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseClient {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "moredays_db";
    private static DatabaseClient instance;
    private Context context;
    //our app database object
    private AppDatabase db;

    private DatabaseClient(Context context) {
        this.context = context;

        db = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).
                //addMigrations(MIGRATION_21_22, MIGRATION_22_23).
                //allowMainThreadQueries().
                addCallback(new RoomDatabase.Callback() {
                    public void onCreate (SupportSQLiteDatabase db) {
                        //create of the database, start with some data
                        iniDb(db, context);
                    }
                    public void onOpen (SupportSQLiteDatabase db) {
                        // do something every time database is open
                    };}).build();
    }

    private void iniDb(SupportSQLiteDatabase db, Context context){
        //db.execSQL(String.format("INSERT INTO Scale (uid, name) VALUES (%d, \'%s\')", 1, context.getString(R.string.wu_db_ini_weight)));
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return db;
    }
}
