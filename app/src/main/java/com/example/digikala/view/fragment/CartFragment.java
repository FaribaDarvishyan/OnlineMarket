package com.example.digikala.view.fragment;

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
import com.example.digikala.R;
import com.example.digikala.adapters.CartProductAdapter;
import com.example.digikala.data.model.product.Product;
import com.example.digikala.data.repository.CartRepository;
import com.example.digikala.data.room.entities.Cart;
import com.example.digikala.databinding.FragmentCartBinding;
import com.example.digikala.utils.QueryPreferences;
import com.example.digikala.viewmodel.CartViewModel;

import java.util.List;

import static com.example.digikala.utils.SnakeBar.showAddSnakeBar;

public class CartFragment extends Fragment {
    private FragmentCartBinding mBinding;
    private CartViewModel mViewModel;
    private CartProductAdapter mAdapter;
    private NavController mNavController;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
       CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(CartViewModel.class);
        mAdapter = new CartProductAdapter(mViewModel);
        mViewModel.getCartsLiveData().observe(this, new Observer<List<Cart>>() {
            @Override
            public void onChanged(List<Cart> carts) {
                mViewModel.setCartsSubject(carts);
                Log.d(CartRepository.TAG, "onChanged: " + carts.size());
                mBinding.setViewModel(mViewModel);
                mViewModel.setProductsList(carts);
            }
        });
        mViewModel.getProductLiveData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                Log.d(CartRepository.TAG, "onChanged: product list size " + products.size());
                mBinding.setViewModel(mViewModel);
                mAdapter.setItems(products);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
                    Snackbar snackbar = Snackbar.make(mBinding.getRoot(), "جهت خرید لطفا وارد شوید", BaseTransientBottomBar.LENGTH_LONG);
                    showAddSnakeBar(snackbar,getActivity());
                    mNavController.navigate(R.id.action_cartFragment_to_profileFragment);
                }
            }
        });
    }
}