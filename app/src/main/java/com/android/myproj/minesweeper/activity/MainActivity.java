package com.android.myproj.minesweeper.activity;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.util.LogService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends FragmentActivity {

    private BottomNavigationView bottomNavigationView;
    private NavController navController;

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

    private void setting() {
        this.bottomNavigationView = findViewById(R.id.nav_main);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_nav_host_fragment);
        this.navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(this.bottomNavigationView, this.navController);
    }

}