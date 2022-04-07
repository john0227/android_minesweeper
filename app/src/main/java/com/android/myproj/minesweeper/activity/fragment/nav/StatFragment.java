package com.android.myproj.minesweeper.activity.fragment.nav;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.util.Supplier;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.activity.fragment.statistics.LevelStatisticsFragment;
import com.android.myproj.minesweeper.activity.fragment.statistics.OverallStatisticsFragment;
import com.android.myproj.minesweeper.activity.fragment.statistics.StatisticsFragment;
import com.android.myproj.minesweeper.config.JSONKey;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.shape.MyProgressBar;
import com.android.myproj.minesweeper.util.AlertDialogBuilderUtil;
import com.android.myproj.minesweeper.util.ConvertUnitUtil;
import com.android.myproj.minesweeper.util.JSONUtil;
import com.android.myproj.minesweeper.util.LogService;
import com.android.myproj.minesweeper.util.StatUtil;

import java.util.ArrayList;
import java.util.List;

public class StatFragment extends Fragment implements View.OnClickListener {

    private static final int NUM_PAGES = 4;
    
    private Activity activity;
    private View rootLayout;

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

    private boolean isAttached;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.isAttached = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this.isAttached) {
            this.activity = getActivity();
        }

        try {
            this.rootLayout = inflater.inflate(R.layout.fragment_statistics, container, false);

            setting();
        } catch (Exception e) {
            LogService.error(this.activity, e.getMessage(), e);
        }
        return this.rootLayout;
    }

    @Override
    public void onPause() {
        super.onPause();
        viewPager.unregisterOnPageChangeCallback(changeButtonColor);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.registerOnPageChangeCallback(changeButtonColor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewPager.unregisterOnPageChangeCallback(changeButtonColor);
    }

    @Override
    public void onClick(View view) {
        int index = viewPager.getCurrentItem();
        Supplier<Integer> reset = (index == 0)
                ? () -> StatUtil.resetAllStats(this.activity)
                : () -> StatUtil.resetStat(this.activity, Level.getLevelFromCode(index));

        if (JSONUtil.existsSavedGame(this.activity)) {
            int levelCode = (int) JSONUtil.readKeyFromFile(this.activity, JSONKey.KEY_LEVEL);
            if (index == 0) {
                // Reset Overall Statistics
                resetAllStatIf(levelCode, reset);
            } else if (levelCode == index) {
                // Level trying to reset has an ongoing game
                resetStatIf(levelCode, reset);
            } else {
                // Level trying to reset does not have an ongoing game
                this.toastResetResult(index, reset.get());
            }
        } else {
            this.toastResetResult(index, reset.get());
        }
    }

    private void setting() {
        this.scrollbarContainer = this.rootLayout.findViewById(R.id.frame_scrollbar_container);
        this.btnContainer = this.rootLayout.findViewById(R.id.linearL_button_container_stat);

        // Add Level buttons to buttonList
        this.levelButtons = new ArrayList<>();
        this.levelButtons.add(this.rootLayout.findViewById(R.id.btn_overall_stat));
        this.levelButtons.add(this.rootLayout.findViewById(R.id.btn_easy_stat));
        this.levelButtons.add(this.rootLayout.findViewById(R.id.btn_intermediate_stat));
        this.levelButtons.add(this.rootLayout.findViewById(R.id.btn_expert_stat));

        // Set ViewPager2 Object
        this.viewPager = this.rootLayout.findViewById(R.id.vp2_statistics);
        this.pagerAdapter = new StatFragment.ScreenSlidePagerAdapter((FragmentActivity) this.activity);
        this.viewPager.setAdapter(this.pagerAdapter);
        this.viewPager.registerOnPageChangeCallback(changeButtonColor);

        // Create MyProgressBar object
        this.progressBar = new MyProgressBar(this.activity, this.scrollbarContainer);
        // Store necessary dimension
        this.progressBarHeightPx = ConvertUnitUtil.convertDpToPx(this.activity, this.PROGRESS_BAR_HEIGHT_DP);
        // Prepare to draw progress bar
        new Handler().postDelayed(
                () -> this.progressBar.drawProgressBar(
                        this.progressBarPadding,
                        this.progressBarBottom - this.progressBarHeightPx
                ),
                50
        );
    }

    private void resetAllStatIf(int levelCode, Supplier<Integer> reset) {
        if (StatUtil.isResettable(this.activity)
                && !StatUtil.isResettable(this.activity, Level.getLevelFromCode(levelCode))) {
            this.toastResetResult(0, reset.get());
            return;
        }
        this.resetStatIf(levelCode, reset);
    }

    private void resetStatIf(int levelCode, Supplier<Integer> reset) {
        if (StatUtil.isResettable(this.activity, Level.getLevelFromCode(levelCode))) {
            this.showResetAlertDialog(reset);
            return;
        }
        this.toastResetResult(-1, StatUtil.RES_NOTHING_TO_RESET);
    }

    private void toastResetResult(int index, int result) {
        if (StatUtil.equalsResCode(result, StatUtil.RES_RESET)) {
            // Notify the adapter that there has been a change
            ((StatFragment.ScreenSlidePagerAdapter) this.pagerAdapter).notifyItemChangedAt(index);
            // Notify the user that the stats have been reset
            Toast.makeText(this.activity, "Statistics have been reset", Toast.LENGTH_SHORT).show();
        } else if (StatUtil.equalsResCode(result, StatUtil.RES_NO_RESET)) {
            // Notify the user that nothing was reset
            Toast.makeText(this.activity, "Statistics was not reset", Toast.LENGTH_SHORT).show();
        } else if (StatUtil.equalsResCode(result, StatUtil.RES_NOTHING_TO_RESET)) {
            // Notify the user that there was nothing to reset
            Toast.makeText(this.activity, "Nothing to reset", Toast.LENGTH_SHORT).show();
        }

        if (StatUtil.equalsResCode(result, StatUtil.RES_ERROR_WHILE_RESETTING)) {
            // Notify the user that there was an error while resetting
            Toast.makeText(this.activity, "Something went wrong while resetting", Toast.LENGTH_SHORT).show();
        }
    }

    private void showResetAlertDialog(Supplier<Integer> reset) {
        String title = "You have an ongoing game.";
        String message = "Are you sure you want to reset? " +
                "The result of the ongoing game will not be reflected in your statistics if you reset.";
        String posText = "Reset";
        String negText = "Go Back";
        DialogInterface.OnClickListener posAction =
                (dialogInterface, i) -> toastResetResult(viewPager.getCurrentItem(), reset.get());
        DialogInterface.OnClickListener negAction =
                (dialogInterface, i) -> toastResetResult(-1, StatUtil.RES_NO_RESET);
        DialogInterface.OnCancelListener cancelAction =
                dialogInterface -> toastResetResult(-1, StatUtil.RES_NO_RESET);


        AlertDialogBuilderUtil.buildAlertDialog(this.activity, title, message, posText, negText,
                posAction, negAction, cancelAction, true
        ).show();
    }

    public void updateView() {
        ((StatFragment.ScreenSlidePagerAdapter) this.pagerAdapter).updateFragment(0, 1, 2, 3);
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
                    levelButtons.get(i).setTextColor(ContextCompat.getColor(activity, R.color.purple_700));
                } else {
                    levelButtons.get(i).setTextColor(Color.WHITE);
                }
            }
            this.storeDimension(pos);
            this.drawRect(pos, 0);
        }


        private Button getButtonAtPos(int pos) {
            return switch (pos) {
                case 0 -> rootLayout.findViewById(R.id.btn_overall_stat);
                case 1 -> rootLayout.findViewById(R.id.btn_easy_stat);
                case 2 -> rootLayout.findViewById(R.id.btn_intermediate_stat);
                case 3 -> rootLayout.findViewById(R.id.btn_expert_stat);
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
            StatisticsFragment fragment = switch (pos) {
                case 0 -> new OverallStatisticsFragment(activity, StatFragment.this);
                case 1, 2, 3 -> new LevelStatisticsFragment(activity, Level.getLevelFromCode(pos), StatFragment.this);
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
                StatisticsFragment fragment = fragments[position];
                if (fragment != null) {
                    try {
                        fragment.showStat();
                        this.notifyItemChanged(position);
                    } catch (Exception e) {
                        LogService.error(activity, "Error while resetting Stat at page: " + position, e);
                    }
                }
            }
        }

    }
    
}
