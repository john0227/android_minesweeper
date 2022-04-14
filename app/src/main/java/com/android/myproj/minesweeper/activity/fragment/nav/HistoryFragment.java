package com.android.myproj.minesweeper.activity.fragment.nav;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.activity.fragment.history.GameHistoryFragment;
import com.android.myproj.minesweeper.config.JSONKey;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.util.HistoryUtil;
import com.android.myproj.minesweeper.util.JSONUtil;
import com.android.myproj.minesweeper.util.LogService;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;

import java.io.IOException;

public class HistoryFragment extends Fragment implements View.OnClickListener {

    private final static int NUM_PAGES = 4;

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

    @Override
    public void onClick(View view) {
        this.onResetButtonClick();
    }

    private void setting() {
        // Set ViewPager2 Object
        this.viewPager = this.rootLayout.findViewById(R.id.vp2_history);
        this.pagerAdapter = new HistoryFragment.ScreenSlidePagerAdapter((FragmentActivity) this.activity);
        this.viewPager.setAdapter(this.pagerAdapter);

        // Attach TabLayout to ViewPager2 using TabLayoutMediator
        TabLayout tabLayout = this.rootLayout.findViewById(R.id.tabLayout_history);
        new TabLayoutMediator(tabLayout, this.viewPager,
                (tab, position) -> tab.setText(Level.getLevelFromCode(position + 1).toString())
        ).attach();
    }

    private void onResetButtonClick() {
        int index = viewPager.getCurrentItem();

        try {
            if (HistoryUtil.isResettable(Level.getLevelFromCode(index + 1))) {
                // Reset the history of viewed level if applicable
                JSONUtil.createDefaultHistory(this.activity, JSONKey.KEYS_SAVED_HISTORY[index]);
                HistoryUtil.resetHistory(Level.getLevelFromCode(index + 1));
                ((HistoryFragment.ScreenSlidePagerAdapter) this.pagerAdapter).notifyItemChangedAt(index);
                Toast.makeText(activity, "Reset history", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Nothing to reset", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException | IOException e) {
            LogService.error(this.activity, "Error while resetting history", e);
        }
    }

    public void updateView() {
        ((HistoryFragment.ScreenSlidePagerAdapter) this.pagerAdapter).notifyItemChangedAt(0, 1, 2, 3);
        this.viewPager.setCurrentItem(0, false);
    }

    public class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        GameHistoryFragment[] fragments = new GameHistoryFragment[NUM_PAGES];

        public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int pos) {
            GameHistoryFragment fragment = switch (pos) {
                case 0, 1, 2, 3 -> new GameHistoryFragment(activity, HistoryFragment.this, Level.getLevelFromCode(pos + 1));
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