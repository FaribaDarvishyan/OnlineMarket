package com.example.OnlineMarket.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.OnlineMarket.data.room.dao.CartDAO;
import com.example.OnlineMarket.data.room.entities.Cart;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Cart.class},
        version = 1,
        exportSchema = false)
public abstract class CartRoomDataBase extends RoomDatabase {

    private static final String DATABASE_NAME = "online_market.db";
    private static final int NUMBER_OF_THREADS = 4;

    public static ExecutorService dataBaseWriteExecutor = Executors.newFixedThreadPool(4);

    public abstract CartDAO getCardDAO();

    public static CartRoomDataBase getDataBase(Context context) {
        return Room.databaseBuilder(context, CartRoomDataBase.class, DATABASE_NAME).build();
    }

}
