package com.example.OnlineMarket.view.fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.example.OnlineMarket.R;
import com.example.OnlineMarket.adapters.CartProductAdapter;
import com.example.OnlineMarket.data.model.product.Product;
import com.example.OnlineMarket.data.repository.CartRepository;
import com.example.OnlineMarket.data.room.entities.Cart;
import com.example.OnlineMarket.databinding.FragmentCartBinding;
import com.example.OnlineMarket.utils.QueryPreferences;
import com.example.OnlineMarket.viewmodel.CartViewModel;

import java.util.List;

import static com.example.OnlineMarket.utils.SnakeBar.showAddSnakeBar;
import static com.example.OnlineMarket.view.fragment.FinishShoppingFragment.ARG_TOTAL_PRICE;

public class CartFragment extends Fragment {
    private FragmentCartBinding mBinding;
    private CartViewModel mViewModel;
    private CartProductAdapter mAdapter;
    private NavController mNavController;

    public CartFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        mAdapter = new CartProductAdapter(mViewModel);
        mViewModel.getCartsLiveData().observe(this, new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> carts) {
                mViewModel.setCartsSubject(carts);
                mViewModel.setProductsLiveData(carts);
                Log.d(CartRepository.TAG, "onChanged: " + carts.size());
                Log.d(CartRepository.TAG, "onChanged: cardProducts size:" + mViewModel.getCartProducts().size());
                Log.d(CartRepository.TAG, "onChanged: cardSubject size:" + mViewModel.getCartsSubject().size());
                mBinding.setViewModel(mViewModel);
                mAdapter.setItems(carts);
                mAdapter.notifyDataSetChanged();
            }
        });
        mViewModel.getProductLiveData().observe(this, new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                Log.d(CartRepository.TAG, "onChanged productLiveData: cardProducts size:" + mViewModel.getCartProducts().size());
                Log.d(CartRepository.TAG, "onChanged productLiveData: cardSubject size:" + mViewModel.getCartsSubject().size());
                if (mViewModel.addProductToCardProductsList(product)) {
                    mViewModel.setReadyToContinue(true);
                    mBinding.setViewModel(mViewModel);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);
        mBinding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.cartRecyclerView.setAdapter(mAdapter);
        mBinding.setViewModel(mViewModel);
        setListeners();


        return mBinding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
    }

    private void setListeners() {
        mBinding.continueBuying.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (QueryPreferences.getCustomerEmail(getContext()) == null) {
                    Snackbar snackbar = Snackbar.make(mBinding.getRoot(), R.string.login_before_perchase,
                            BaseTransientBottomBar.LENGTH_LONG);
                    showAddSnakeBar(snackbar, getActivity());
                    mNavController.navigate(R.id.action_cartFragment_to_profileFragment);
                } else {
                    mNavController.navigate(R.id.action_cartFragment_to_finishShoppingFragment);
                    Bundle bundle=new Bundle();
                    bundle.putLong(ARG_TOTAL_PRICE,mViewModel.calculateTotalPrice());
                    mNavController.navigate(R.id.action_cartFragment_to_finishShoppingFragment,bundle);
                }
            }
        });
    }
}
