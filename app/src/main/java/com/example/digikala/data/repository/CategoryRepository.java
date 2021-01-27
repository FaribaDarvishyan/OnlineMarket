package com.example.digikala.data.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.digikala.data.model.product.Category;
import com.example.digikala.data.remote.NetworkParams;
import com.example.digikala.data.remote.retrofit.RetrofitInstance;
import com.example.digikala.data.remote.retrofit.WooCommerceAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    public static final String TAG = "Category Repository";
    private static com.example.digikala.data.repository.CategoryRepository sInstance;
    private WooCommerceAPI mWooCommerceAPI;

    private final MutableLiveData<List<Category>> mCategoriesItems;
    private final MutableLiveData<List<Category>> mDefaultCategories;
    private final MutableLiveData<List<Category>> mSubCategories;
    private final MutableLiveData<List<Category>> mSubCategoriesById;

    public static CategoryRepository getInstance() {
        if (sInstance == null)
            sInstance = new CategoryRepository();
        return sInstance;
    }

    public CategoryRepository() {
        mWooCommerceAPI = RetrofitInstance.getInstance().create(WooCommerceAPI.class);
        mCategoriesItems = new MutableLiveData<>();
        mDefaultCategories = new MutableLiveData<>();
        mSubCategories = new MutableLiveData<>();
        mSubCategoriesById = new MutableLiveData<>();
    }

    public MutableLiveData<List<Category>> getCategoriesItems() {
        return mCategoriesItems;
    }

    public MutableLiveData<List<Category>> getDefaultCategories() {
        return mDefaultCategories;
    }

    public MutableLiveData<List<Category>> getSubCategories() {
        return mSubCategories;
    }


    public void setAllCategoriesItems() {
        Log.d(TAG, "setAllCategoriesItems: ");
        mWooCommerceAPI.getCategories(NetworkParams.BASE_OPTIONS)
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                        mCategoriesItems.setValue(response.body());
                        List<Category> defaultCategories = new ArrayList<>();
                        List<Category> subCategories = new ArrayList<>();
                        for (int i = 0; i < response.body().size(); i++) {
                            Log.d(TAG, "onResponse: " + response.body().get(i).toString());
                            if (response.body().get(i).getParent() == 0)
                                defaultCategories.add(response.body().get(i));
                            else
                                subCategories.add(response.body().get(i));
                        }
                        mSubCategories.setValue(subCategories);
                        mDefaultCategories.setValue(defaultCategories);
                        Log.d(TAG, "onResponse: " + mSubCategories.getValue().size());
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {

                    }
                });
    }
}
