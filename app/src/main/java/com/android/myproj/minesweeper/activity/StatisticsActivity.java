package com.android.myproj.minesweeper.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.game.statistics.LevelStatisticsFragment;
import com.android.myproj.minesweeper.game.statistics.OverallStatisticsFragment;
import com.android.myproj.minesweeper.util.LogService;

public class StatisticsActivity extends FragmentActivity {

    private static final int NUM_PAGES = 4;

    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    private int currentPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_statistics);

            setting();
        } catch (Exception e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    private void setting() {
        // Set ViewPager2 Object
        this.viewPager = findViewById(R.id.vp2_statistics);
        this.pagerAdapter = new ScreenSlidePagerAdapter(this);
        this.viewPager.setAdapter(pagerAdapter);

        // Store current position
        this.currentPos = 0;
    }

    public void onResetClick(View view) {
        int index = viewPager.getCurrentItem();

        LogService.info(StatisticsActivity.this, "Clicked button in " + (index + 1) + "th page");
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @Override
        public Fragment createFragment(int position) {
            LogService.info(StatisticsActivity.this, "Position : " + position);
            return switch (position) {
                case 0 -> new OverallStatisticsFragment();
                case 1 -> new LevelStatisticsFragment(Level.EASY);
                case 2 -> new LevelStatisticsFragment(Level.INTERMEDIATE);
                case 3 -> new LevelStatisticsFragment(Level.EXPERT);
                default -> throw new RuntimeException("Invalid Level received");
            };
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

}
