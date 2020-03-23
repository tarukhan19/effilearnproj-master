package com.effseele.effilearn.RoomDatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {SavedOption.class}, version =8)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
