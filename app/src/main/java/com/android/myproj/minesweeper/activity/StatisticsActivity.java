package com.android.myproj.minesweeper.activity;

import androidx.annotation.NonNull;
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
import com.android.myproj.minesweeper.util.StatUtil;

import org.json.JSONException;

import java.io.IOException;

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

    private void setting() {
        // Set ViewPager2 Object
        this.viewPager = findViewById(R.id.vp2_statistics);
        this.pagerAdapter = new ScreenSlidePagerAdapter(this);
        this.viewPager.setAdapter(this.pagerAdapter);
    }

    public void onResetClick(View view) {
        int index = viewPager.getCurrentItem();
        LogService.info(StatisticsActivity.this, "Clicked button in " + (index + 1) + "th page");

        boolean hasReset = false;
        if (index == 0) {
            // If player clicked RESET button in Overall Statistics, reset all statistics
            try {
                hasReset = StatUtil.resetAllStats(this);
            } catch (JSONException | IOException e) {
                LogService.error(this, "Unable to reset statistics", e);
            }
        } else {
            try {
                hasReset = StatUtil.resetStat(this, Level.getLevelFromCode(index));
            } catch (JSONException | IOException e) {
                LogService.error(this, "Unable to reset statistics", e);
            }
        }

        if (hasReset) {
            // Notify the adapter that there has been a change
            ((ScreenSlidePagerAdapter) this.pagerAdapter).notifyItemChangedAt(index);
            this.viewPager.setAdapter(this.pagerAdapter);
            this.viewPager.setCurrentItem(index, false);
        }
    }

    public class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return switch (position) {
                case 0 -> new OverallStatisticsFragment(StatisticsActivity.this);
                case 1, 2, 3 -> new LevelStatisticsFragment(StatisticsActivity.this, Level.getLevelFromCode(position));
                default -> throw new RuntimeException("Invalid Level received");
            };
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }

        public void notifyItemChangedAt(int position) {
            if (position == 0) {
                this.resetFragment(0, 1, 2, 3);
            } else {
                this.resetFragment(0, position);
            }
        }

        private void resetFragment(int... positions) {
            for (int position : positions) {
                this.createFragment(position);
            }
        }

    }

}
