package com.android.myproj.minesweeper.activity.fragment.nav;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.activity.adapter.GameHistoryAdapter;
import com.android.myproj.minesweeper.activity.fragment.history.GameHistoryFragment;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.shape.MyProgressBar;
import com.android.myproj.minesweeper.util.ConvertUnitUtil;
import com.android.myproj.minesweeper.util.LogService;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private final static int NUM_PAGES = 3;

    private Activity activity;
    private View rootLayout;

    // To be used when drawing rectangle
    private final float PROGRESS_BAR_HEIGHT_DP = 5;
    private final float BAR_TO_BUTTON_RATIO = 0.7f;  // i.e. progressBarLength = 0.7 * buttonWidth
    private float progressBarHeightPx;
    private float buttonWidth;
    private float progressBarPadding;  // startPos = button.getX + progressBarPadding
    private float progressBarBottom;

    private RecyclerView gameHistoryRecView;
    private GameHistoryAdapter gameHistoryAdapter;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.isAttached) {
            this.activity = getActivity();
        }

        try {
            this.rootLayout = inflater.inflate(R.layout.fragment_history, container, false);
            this.setting();

        } catch (Exception e) {
            LogService.error(this.activity, e.getMessage(), e);
        }

        return this.rootLayout;
    }

    private void setting() {
        this.scrollbarContainer = this.rootLayout.findViewById(R.id.frame_scrollbar_container);
        this.btnContainer = this.rootLayout.findViewById(R.id.linearL_button_container_history);

        // Add Level buttons to buttonList
        this.levelButtons = new ArrayList<>();
        this.levelButtons.add(this.rootLayout.findViewById(R.id.btn_easy_history));
        this.levelButtons.add(this.rootLayout.findViewById(R.id.btn_intermediate_history));
        this.levelButtons.add(this.rootLayout.findViewById(R.id.btn_expert_history));
//        for (Button button : this.levelButtons) {
//            button.setOnClickListener(this);
//        }

        // Set ViewPager2 Object
        this.viewPager = this.rootLayout.findViewById(R.id.vp2_history);
        this.pagerAdapter = new HistoryFragment.ScreenSlidePagerAdapter((FragmentActivity) this.activity);
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
                5
        );
    }

    public void updateView() {
        ((HistoryFragment.ScreenSlidePagerAdapter) this.pagerAdapter).notifyItemChangedAt(0, 1, 2);
        this.viewPager.setCurrentItem(0, false);
        new Handler().postDelayed(
                () -> this.progressBar.drawProgressBar(
                        this.progressBarPadding,
                        this.progressBarBottom - this.progressBarHeightPx
                ),
                5
        );
    }

    private final ViewPager2.OnPageChangeCallback changeButtonColor = new ViewPager2.OnPageChangeCallback() {

        @Override
        public void onPageScrolled(int pos, float posOffset, int posOffsetPx) {
            this.drawProgressBar(pos, posOffset);
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
            this.drawProgressBar(pos, 0);
        }


        private Button getButtonAtPos(int pos) {
            return switch (pos) {
                case 0 -> rootLayout.findViewById(R.id.btn_easy_history);
                case 1 -> rootLayout.findViewById(R.id.btn_intermediate_history);
                case 2 -> rootLayout.findViewById(R.id.btn_expert_history);
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

        private void drawProgressBar(int pos, float posOffset) {
            // Calculate corner points of MyRectF to pass to MyProgressBar
            float left = this.getButtonAtPos(pos).getX() + progressBarPadding + buttonWidth * posOffset;
            float top = progressBarBottom - progressBarHeightPx;
            progressBar.drawProgressBar(left, top);
        }

    };

    public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        GameHistoryFragment[] fragments = new GameHistoryFragment[NUM_PAGES];

        public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int pos) {
            GameHistoryFragment fragment = switch (pos) {
                case 0, 1, 2 -> new GameHistoryFragment(activity, Level.getLevelFromCode(pos + 1));
                default -> throw new RuntimeException("Invalid Level received");
            };
            this.fragments[pos] = fragment;
            return fragment;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }

        public void notifyItemChangedAt(int... positions) {
            for (int position : positions) {
                GameHistoryFragment fragment = fragments[position];
                if (fragment != null) {
                    try {
                        fragment.notifyAdapter();
                        this.notifyItemChanged(position);
                    } catch (Exception e) {
                        LogService.error(activity, "Error while updating History at page: " + position, e);
                    }
                }
            }
        }

    }

}