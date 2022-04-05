package com.android.myproj.minesweeper.activity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.game.statistics.LevelStatisticsFragment;
import com.android.myproj.minesweeper.game.statistics.OverallStatisticsFragment;
import com.android.myproj.minesweeper.game.statistics.StatisticsFragment;
import com.android.myproj.minesweeper.shape.MyProgressBar;
import com.android.myproj.minesweeper.util.ConvertUnitUtil;
import com.android.myproj.minesweeper.util.LogService;
import com.android.myproj.minesweeper.util.StatUtil;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends FragmentActivity {

    private static final int NUM_PAGES = 4;

    // To be used when drawing rectangle
    private final float PROGRESS_BAR_HEIGHT_DP = 5;
    private final float BAR_TO_BUTTON_RATIO = 0.7f;  // i.e. progressBarLength = 0.7 * buttonWidth
    private float progressBarHeightPx;
    private float buttonWidth;
    private float progressBarPadding;  // startPos = button.getX + progressBarPadding
    private float progressBarBottom;

    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private FrameLayout scrollbarContainer;
    private LinearLayout btnContainer;
    private List<Button> levelButtons;
    private MyProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_statistics);

            setting();
            methodAfterSetting();
        } catch (Exception e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewPager.unregisterOnPageChangeCallback(changeButtonColor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.registerOnPageChangeCallback(changeButtonColor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewPager.unregisterOnPageChangeCallback(changeButtonColor);
    }

    private void setting() {
        this.scrollbarContainer = findViewById(R.id.frame_scrollbar_container);
        this.btnContainer = findViewById(R.id.linearL_button_container_stat);

        // Add Level buttons to buttonList
        this.levelButtons = new ArrayList<>();
        this.levelButtons.add(findViewById(R.id.btn_overall_stat));
        this.levelButtons.add(findViewById(R.id.btn_easy_stat));
        this.levelButtons.add(findViewById(R.id.btn_intermediate_stat));
        this.levelButtons.add(findViewById(R.id.btn_expert_stat));

        // Set ViewPager2 Object
        this.viewPager = findViewById(R.id.vp2_statistics);
        this.pagerAdapter = new ScreenSlidePagerAdapter(this);
        this.viewPager.setAdapter(this.pagerAdapter);
        this.viewPager.registerOnPageChangeCallback(changeButtonColor);

        // Create MyProgressBar object
        this.progressBar = new MyProgressBar(this, this.scrollbarContainer);
        // Store necessary dimension
        this.progressBarHeightPx = ConvertUnitUtil.convertDpToPx(this, this.PROGRESS_BAR_HEIGHT_DP);
        // Prepare to draw progress bar
        new Handler().postDelayed(
                () -> this.progressBar.drawProgressBar(
                        this.progressBarPadding,
                        this.progressBarBottom - this.progressBarHeightPx
                ),
                50
        );
    }

    private void methodAfterSetting() {
        new Handler().postDelayed(() ->
        LogService.info(this, ""+this.levelButtons.get(0).getWidth())
                , 20);
    }

    public void onResetClick(View view) {
        int index = viewPager.getCurrentItem();

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
            // Notify the user that the stats have been reset
            Toast.makeText(this, "Statistics have been reset", Toast.LENGTH_SHORT).show();
        } else {
            // Notify the user that there was nothing to reset
            Toast.makeText(this, "Nothing to reset", Toast.LENGTH_SHORT).show();
        }
    }

    private final ViewPager2.OnPageChangeCallback changeButtonColor = new ViewPager2.OnPageChangeCallback() {

        // private int direction = 1;
        // private int prevPos = 0;

        /*
        @Override
        public void onPageScrollStateChanged (int state) {
            if (state == ViewPager2.SCROLL_STATE_IDLE) {
                this.prevPos = viewPager.getCurrentItem();
            }
        }
        */

        /*
        private void updateDirection(int pos) {
            if (pos < prevPos) {
                this.direction = -1;
            } else {
                this.direction = 1;
            }
        }
        */

        @Override
        public void onPageScrolled(int pos, float posOffset, int posOffsetPx) {
            this.drawRect(pos, posOffset);
        }

        @Override
        public void onPageSelected(int pos) {
            super.onPageSelected(pos);
            for (int i = 0; i < levelButtons.size(); i++) {
                if (i == pos) {
                    levelButtons.get(i).setTextColor(
                            ContextCompat.getColor(StatisticsActivity.this, R.color.purple_700));
                } else {
                    levelButtons.get(i).setTextColor(Color.WHITE);
                }
            }
            LogService.info(StatisticsActivity.this, "Selected Page: " + pos);
            this.storeDimension(pos);
            this.drawRect(pos, 0);
        }


        private Button getButtonAtPos(int pos) {
            return switch (pos) {
                case 0 -> findViewById(R.id.btn_overall_stat);
                case 1 -> findViewById(R.id.btn_easy_stat);
                case 2 -> findViewById(R.id.btn_intermediate_stat);
                case 3 -> findViewById(R.id.btn_expert_stat);
                default -> throw new RuntimeException("Invalid ViewPager2 position");
            };
        }

        private void storeDimension(int pos) {
            // Store necessary dimension
            buttonWidth = getButtonAtPos(pos).getWidth();
            progressBarPadding = (1 - BAR_TO_BUTTON_RATIO) / 2 * buttonWidth;
            progressBarBottom = btnContainer.getBottom();
            progressBar.setDimension(BAR_TO_BUTTON_RATIO * buttonWidth, progressBarHeightPx);
        }

        private void drawRect(int pos, float posOffset) {
            // Calculate corner points of MyRectF to pass to MyProgressBar
            float left = this.getButtonAtPos(pos).getX() + progressBarPadding + buttonWidth * posOffset;
            float top = progressBarBottom - progressBarHeightPx;
            progressBar.drawProgressBar(left, top);
        }

    };

    public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        StatisticsFragment[] fragments = new StatisticsFragment[NUM_PAGES];

        public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int pos) {
            LogService.info(StatisticsActivity.this, "Creating Fragments");
            StatisticsFragment fragment = switch (pos) {
                case 0 -> new OverallStatisticsFragment(StatisticsActivity.this);
                case 1, 2, 3 -> new LevelStatisticsFragment(StatisticsActivity.this, Level.getLevelFromCode(pos));
                default -> throw new RuntimeException("Invalid Level received");
            };
            this.fragments[pos] = fragment;
            return fragment;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }

        public void notifyItemChangedAt(int position) {
            if (position == 0) {
                this.updateFragment(0, 1, 2, 3);
            } else {
                this.updateFragment(0, position);
            }
        }

        private void updateFragment(int... positions) {
            for (int position : positions) {
                this.fragments[position].showStat();
                this.notifyItemChanged(position);
            }
        }

    }

}
