package com.example.crabfood.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.crabfood.helpers.OptionChoiceConverters;
import com.example.crabfood.model.CartItemEntity;

@Database(entities = {CartItemEntity.class}, version = 1, exportSchema = false)
@TypeConverters({OptionChoiceConverters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "crabfood_db";
    private static AppDatabase instance;

    public abstract CartDao cartDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}