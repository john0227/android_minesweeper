package com.android.myproj.minesweeper.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.game.statistics.LevelStatisticsFragment;
import com.android.myproj.minesweeper.game.statistics.OverallStatisticsFragment;
import com.android.myproj.minesweeper.util.LogService;

public class StatisticsActivity extends FragmentActivity {

    private static final int NUM_PAGES = 4;

    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

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

    @Override
    protected void onPause() {
        super.onPause();
        viewPager.unregisterOnPageChangeCallback(updateHeight);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.registerOnPageChangeCallback(updateHeight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager.unregisterOnPageChangeCallback(updateHeight);
    }

    private void setting() {
        viewPager = findViewById(R.id.vp2_statistics);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.registerOnPageChangeCallback(updateHeight);
    }

    private ViewPager2.OnPageChangeCallback updateHeight = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);

            int item = viewPager.getCurrentItem();
            LogService.info(StatisticsActivity.this, "Showing " + (item + 1) + "th item");
            LinearLayout view = switch (item) {
                                    case 0 -> view = findViewById(R.id.linearL_overall_statistics);
                                    default -> view = findViewById(R.id.linearL_statistics);
                                };
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(view.getLayoutParams());
            viewPager.setLayoutParams(layoutParams);
        }
    };

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
