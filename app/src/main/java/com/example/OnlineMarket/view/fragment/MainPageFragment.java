package com.example.OnlineMarket.view.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.OnlineMarket.adapters.ImageSliderAdapter;
import com.example.OnlineMarket.data.model.Options;
import com.example.OnlineMarket.data.model.product.Product;
import com.example.OnlineMarket.utils.QueryPreferences;
import com.example.OnlineMarket.utils.SliderImageDecorator;
import com.example.OnlineMarket.viewmodel.MainPageViewModel;
import com.example.OnlineMarket.R;
import com.example.OnlineMarket.adapters.ProductAdapter;
import com.example.OnlineMarket.adapters.SearchViewAdapter;
import com.example.OnlineMarket.databinding.FragmentMainPageBinding;

import java.util.ArrayList;
import java.util.List;


public class MainPageFragment extends Fragment implements ProductAdapter.OnProductListener, SearchViewAdapter.OnProductListener  {
    public static final String TAG = "Main Page Fragment";
    private FragmentMainPageBinding mBinding;
    private MainPageViewModel mViewModel;
    private ProductAdapter mLatestAdapter;
    private ProductAdapter mPopularAdapter;
    private ProductAdapter mTopRatedAdapter;
    private ImageSliderAdapter mImageSliderAdapter;
    private SearchViewAdapter mSearchViewAdapter;
 //   private MainPageFragment mListener = this;
    private NavController mNavController;
    private SearchView mSearchView;


    public MainPageFragment() {
        // Required empty public constructor
    }

    public static MainPageFragment newInstance() {
        MainPageFragment fragment = new MainPageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(MainPageViewModel.class);
        mViewModel.setInitialData();

        initAdapters();
        setObservers();
    }

    private void initAdapters() {
        mLatestAdapter = new ProductAdapter(this);
        mPopularAdapter = new ProductAdapter(this);
        mTopRatedAdapter = new ProductAdapter(this);
        mSearchViewAdapter = new SearchViewAdapter(this);
        mImageSliderAdapter = new ImageSliderAdapter();
    }

    private void setObservers() {
        mViewModel.getLatestProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                Log.d(TAG, "set latest Observer " + products.get(products.size() - 1).getName());
                mLatestAdapter.setItems(products);
                mLatestAdapter.notifyDataSetChanged();
                setBinding();
            }
        });
        mViewModel.getPopularProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                Log.d(TAG, "set popular Observer " + products.get(products.size() - 1).getName());
                mPopularAdapter.setItems(products);
                QueryPreferences.setLastProductId(getActivity(), products.get(0).getId());
                mPopularAdapter.notifyDataSetChanged();
                setBinding();
            }
        });
        mViewModel.getTopRatedProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                Log.d(TAG, "set top rated Observer " + products.get(products.size() - 1).getName());
                mTopRatedAdapter.setItems(products);
                mTopRatedAdapter.notifyDataSetChanged();
                setBinding();
            }
        });
        mViewModel.getSearchedProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                Log.d(TAG, "set searched products: ");
                mSearchViewAdapter.setItems(products);
                mSearchViewAdapter.notifyDataSetChanged();
                mBinding.searchProgressBar.setVisibility(View.GONE);
            }
        });
        mViewModel.getOnSaleProducts().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                mImageSliderAdapter.setSliderItems(mViewModel.getOnSaleImageItems(products));
                mImageSliderAdapter.notifyDataSetChanged();
                SliderImageDecorator.SliderImageDecorator(mBinding.onSaleImageViewPager);
                setBinding();
            }
        });
    }
    private void setBinding() {
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_main_page,
                container,
                false);

        initUI();
        SliderImageDecorator.SliderImageDecorator(mBinding.onSaleImageViewPager);
        Log.d(TAG, "onCreateView: Adapters:" + mLatestAdapter == null ? "null" : "full OK");
        return mBinding.getRoot();
    }

    private void initUI() {
        mBinding.setViewModel(mViewModel);
        mBinding.newProductsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        mBinding.popularRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        mBinding.topRatedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        mBinding.searchRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mBinding.newProductsRecyclerView.setAdapter(mLatestAdapter);
        mBinding.topRatedRecyclerView.setAdapter(mTopRatedAdapter);
        mBinding.popularRecyclerView.setAdapter(mPopularAdapter);
        mBinding.searchRecyclerView.setAdapter(mSearchViewAdapter);
        mBinding.onSaleImageViewPager.setAdapter(mImageSliderAdapter);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(view);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem settingItem = menu.findItem(R.id.action_setting);
        mSearchView = (SearchView) searchItem.getActionView();
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mSearchViewAdapter.setItems(new ArrayList<>());
                mSearchViewAdapter.notifyDataSetChanged();
                mBinding.searchRecyclerView.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mBinding.searchRecyclerView.setVisibility(View.GONE);
                mBinding.searchProgressBar.setVisibility(View.GONE);
                return true;
            }
        });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Options options = new Options(query);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ProductListFragment.ARGS_OPTIONS, options);
                bundle.putString(ProductListFragment.ARGS_TITLE, query);
                mNavController.navigate(R.id.action_mainPageFragment_to_productListFragment, bundle);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals(""))
                    return false;
                mBinding.searchProgressBar.setVisibility(View.VISIBLE);
                mViewModel.setSearchedProducts(newText);
                return true;
            }
        });
        settingItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mNavController.navigate(R.id.action_mainPageFragment_to_settingsFragment);
                return true;
            }
        });
    }

    @Override
    public void onProductClicked(Product product) {
        Log.d(TAG, "onProductClicked: " + product.getName());
        Bundle bundle = new Bundle();
        bundle.putInt(ProductDetailsFragment.ARG_PRODUCT_ID, product.getId());
        bundle.putString(ProductDetailsFragment.ARG_PRODUCT_NAME, product.getName());
        mNavController.navigate(R.id.action_mainPageFragment_to_productDetailsFragment, bundle);

    }
}
