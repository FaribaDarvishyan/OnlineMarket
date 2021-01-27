package com.example.digikala.data.remote.retrofit;
import com.example.digikala.data.model.product.Category;
import com.example.digikala.data.model.product.Product;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface WooCommerceAPI {

    @GET("products")
    Call<List<Product>> getProducts(@QueryMap Map<String, String> options);
    @GET("products")
    Call<List<Product>> getAllProducts();
    @GET("products")
    Call<List<Product>> getPopularProduct();

    @GET("products/{productId}")
    Call<Product> getProductById(@Path("productId")int productId,@QueryMap Map<String, String> options);

    @GET("products/categories?per_page=100")
    Call<List<Category>> getCategories(@QueryMap Map<String, String> options);


}
