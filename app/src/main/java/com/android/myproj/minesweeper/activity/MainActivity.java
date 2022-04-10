package com.android.myproj.minesweeper.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.activity.fragment.nav.HomeFragment;
import com.android.myproj.minesweeper.activity.fragment.nav.StatFragment;
import com.android.myproj.minesweeper.util.LogService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends FragmentActivity {

    private static final int NUM_PAGES = 2;

    private StatFragment statFragment;
    private ViewPager2 viewPager2;
    private FragmentStateAdapter fragmentStateAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogService.info(this, "Launching Main Activity");
        try {
            setContentView(R.layout.activity_main);

            setting();
        } catch (Exception e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    @Override
    public void onBackPressed() {
        if (this.viewPager2.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            this.bottomNavigationView.setSelectedItemId(R.id.homeFragment);
        }
    }

    private void setting() {
        this.viewPager2 = findViewById(R.id.vp2_fragment);
        this.viewPager2.setUserInputEnabled(false);
        this.fragmentStateAdapter = new PagerAdapter(this);
        this.viewPager2.setAdapter(this.fragmentStateAdapter);

        this.bottomNavigationView = findViewById(R.id.nav_main);
        this.bottomNavigationView.setOnItemSelectedListener(onItemSelectedListener);
        this.bottomNavigationView.setOnItemReselectedListener(onItemReselectedListener);
    }

    private final NavigationBarView.OnItemSelectedListener onItemSelectedListener =
            new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.homeFragment) {
                viewPager2.setCurrentItem(0);
            } else if (item.getItemId() == R.id.statFragment) {
                viewPager2.setCurrentItem(1);
                if (statFragment != null) {
                    statFragment.updateView();
                }
            }
            return true;
        }
    };

    private final NavigationBarView.OnItemReselectedListener onItemReselectedListener =
            new NavigationBarView.OnItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {
            // Don't do anything
        }
    };

    public class PagerAdapter extends FragmentStateAdapter {

        public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            LogService.info(MainActivity.this, "========== Creating New Fragment: " + position + " ==========");
            switch (position) {
                case 0: return new HomeFragment();
                case 1: {
                    statFragment = new StatFragment();
                    return statFragment;
                }
                default: throw new RuntimeException("Invalid position for MainActivity ViewPager2 view");
            }
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }

    }

}