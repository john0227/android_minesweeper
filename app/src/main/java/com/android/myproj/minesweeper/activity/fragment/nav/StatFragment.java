package com.android.myproj.minesweeper.activity.fragment.nav;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.android.myproj.minesweeper.util.AlertDialogBuilderUtil;
import com.android.myproj.minesweeper.util.JSONUtil;
import com.android.myproj.minesweeper.util.LogService;
import com.android.myproj.minesweeper.util.StatUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class StatFragment extends Fragment implements View.OnClickListener {

    private static final int NUM_PAGES = 6;
    
    private Activity activity;
    private View rootLayout;

    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

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
    public void onClick(View view) {
        this.onResetButtonClick();
    }

    private void setting() {
        // Set ViewPager2 Object
        this.viewPager = this.rootLayout.findViewById(R.id.vp2_stat);
        this.pagerAdapter = new StatFragment.ScreenSlidePagerAdapter((FragmentActivity) this.activity);
        this.viewPager.setAdapter(this.pagerAdapter);

        // Attach TabLayout to ViewPager2 using TabLayoutMediator
        TabLayout tabLayout = this.rootLayout.findViewById(R.id.tabLayout_stat);
        new TabLayoutMediator(tabLayout, this.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("OVERALL");
            } else {
                tab.setText(Level.getLevelFromCode(position).toString());
            }
        }).attach();
    }

    private void onResetButtonClick() {
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
        ((StatFragment.ScreenSlidePagerAdapter) this.pagerAdapter).updateFragment(0, 1, 2, 3, 4, 5);
        this.viewPager.setCurrentItem(0, false);
    }

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
                case 1, 2, 3, 4, 5 -> new LevelStatisticsFragment(activity, Level.getLevelFromCode(pos), StatFragment.this);
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
                this.updateFragment(0, 1, 2, 3, 4, 5);
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
