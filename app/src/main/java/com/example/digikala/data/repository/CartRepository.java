package com.example.digikala.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.digikala.data.model.product.Product;
import com.example.digikala.data.remote.NetworkParams;
import com.example.digikala.data.remote.retrofit.RetrofitInstance;
import com.example.digikala.data.remote.retrofit.WooCommerceAPI;
import com.example.digikala.data.room.CartRoomDataBase;
import com.example.digikala.data.room.dao.CartDAO;
import com.example.digikala.data.room.entities.Cart;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    public static final String TAG = "Cart Repository";
    private static CartRepository sInstance;
    private CartDAO mCartDAO;
    private final MutableLiveData<List<Product>> mProductLiveData;
    private WooCommerceAPI mWooCommerceAPI;

    public static synchronized CartRepository getInstance(Context context) {
        if (sInstance == null)
            sInstance = new CartRepository(context);
        return sInstance;
    }
    public LiveData<List<Product>> getProductLiveData() {
        return mProductLiveData;
    }

    public CartRepository(Context context) {
        CartRoomDataBase cartRoomDataBase = CartRoomDataBase.getDataBase(context);
        mCartDAO = cartRoomDataBase.getCardDAO();
        mProductLiveData = new MutableLiveData<>();
        mWooCommerceAPI = RetrofitInstance.getInstance().create(WooCommerceAPI.class);
    }

    public void setProductLiveData(List<Cart> carts) {
        List<Product> list = new ArrayList<>();
        mProductLiveData.setValue(list);
        for (int i = 0; i < carts.size(); i++) {
            mWooCommerceAPI.getProductById(carts.get(i).getProductid(), NetworkParams.BASE_OPTIONS)
                    .enqueue(new Callback<Product>() {
                        @Override
                        public void onResponse(Call<Product> call, Response<Product> response) {
                            if (response.isSuccessful()) {
                                list.add(response.body());
                                Log.d(TAG, "onResponse: product name fetched from cart " + response.body().getName());
                                mProductLiveData.setValue(list);
                            }
                        }

                        @Override
                        public void onFailure(Call<Product> call, Throwable t) {

                        }
                    });
        }
    }


    public LiveData<List<Cart>> getCartLiveData() {
        return mCartDAO.getCarts();
    }

    public Cart getCart(int productId) {
        return mCartDAO.getCart(productId);
    }

    public void updateCart(Cart cart) {
        CartRoomDataBase.dataBaseWriteExecutor.execute(() -> mCartDAO.updateCarts(cart));
    }

    public void deleteCart(Cart cart) {
        CartRoomDataBase.dataBaseWriteExecutor.execute(() -> mCartDAO.deleteCarts(cart));
    }

    public void insertCart(Cart cart) {
        CartRoomDataBase.dataBaseWriteExecutor.execute(() -> mCartDAO.insertCarts(cart));
    }

    public void deleteAllCarts() {
        CartRoomDataBase.dataBaseWriteExecutor.execute(() -> mCartDAO.deleteAllCarts());
    }


}