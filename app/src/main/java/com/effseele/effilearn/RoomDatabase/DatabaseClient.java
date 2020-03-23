package com.effseele.effilearn.RoomDatabase;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private Context mCtx;
    private static DatabaseClient mInstance;

    //our app database object
    private AppDatabase appDatabase;

    private DatabaseClient(Context mCtx) {
        this.mCtx = mCtx;
      //  appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "SavedOptionDto").build();
        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "savedoption").allowMainThreadQueries().fallbackToDestructiveMigration().build();

    }

    public static synchronized DatabaseClient getInstance(Context mCtx)
    {
        if (mInstance == null) {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
