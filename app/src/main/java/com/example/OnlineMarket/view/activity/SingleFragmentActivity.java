package com.example.OnlineMarket.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.OnlineMarket.R;
import com.example.OnlineMarket.databinding.ActivitySingleFragmentBinding;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    public static final String FRAGMENT_TAG = "Single Fragment Activity";

    public ActivitySingleFragmentBinding mBinding;
    private NavController mNavController;
    private AppBarConfiguration mAppBarConfiguration;

    public abstract Fragment createFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Digikala);
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_single_fragment);

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentById(R.id.nav_host_fragment);
        if (fragment == null) {
            fragmentManager
                    .beginTransaction()
                    .add(R.id.nav_host_fragment, createFragment(), FRAGMENT_TAG)
                    .commit();
        }
            //Set bottom navigation
            mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
            NavigationUI.setupWithNavController(mBinding.bottomNavigation, mNavController);
            //Set navigation up button
           mAppBarConfiguration = new AppBarConfiguration.Builder(mNavController.getGraph())
                .setOpenableLayout(mBinding.drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this,
                mNavController,
                mBinding.drawerLayout);
        //set drawer navigation
        NavigationUI.setupWithNavController(mBinding.slideNavigation, mNavController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration);
    }
}
