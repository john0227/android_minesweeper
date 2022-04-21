package com.android.myproj.minesweeper.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.config.Key;
import com.android.myproj.minesweeper.config.ResCode;
import com.android.myproj.minesweeper.game.history.GameHistoryList;
import com.android.myproj.minesweeper.util.LogService;
import com.android.myproj.minesweeper.util.MySharedPreferencesUtil;
import com.bumptech.glide.Glide;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity {

    private final static String SETTING_SORT_BY_TIME = "Time";
    private final static String SETTING_SORT_BY_DATE = "Date";
    private final static String SETTING_ORDER_ASCENDING = "Ascending";
    private final static String SETTING_ORDER_DESCENDING = "Descending";

    private FrameLayout rootLayout;

    private CheckedTextView check_sound;
    private CheckedTextView check_format_time;
    private Button btn_show_spinner;
    private TextView tv_sort_by;
    private CheckedTextView check_sort_custom_history;
    private CheckedTextView check_show_incomplete;
    private ImageView iv_toggle_flag;
    private ImageView iv_radio_flag;
    private RadioButton rbtn_toggle_flag;
    private RadioButton rbtn_radio_flag;

    private int resCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_setting);

            init();
            setting();
        } catch (Exception e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    private void init() {
        this.rootLayout = findViewById(R.id.rootLayout_setting);

        // Sound settings
        this.check_sound = findViewById(R.id.check_sound);
        // Time settings
        this.check_format_time = findViewById(R.id.check_format_time);
        // Sort History settings
        this.btn_show_spinner = findViewById(R.id.btn_show_spinner);
        this.tv_sort_by = findViewById(R.id.tv_sort_by);
        this.check_sort_custom_history = findViewById(R.id.check_sort_custom_history);
        this.check_show_incomplete = findViewById(R.id.check_show_incomplete);
        // How to Flag settings
        this.iv_toggle_flag = findViewById(R.id.iv_toggle_flag);
        this.iv_radio_flag = findViewById(R.id.iv_radio_flag);
        this.rbtn_toggle_flag = findViewById(R.id.rbtn_toggle_flag);
        this.rbtn_radio_flag = findViewById(R.id.rbtn_radio_flag);

        this.resCode = ResCode.SETTING_NO_CHANGE;
    }

    private void setting() {
        // Retrieve sound setting from previously saved SharedPreferences (true by default)
        SharedPreferences myPref = getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        this.check_sound.setChecked(myPref.getBoolean(Key.PREFERENCES_SOUND, true));
        this.check_sound.setOnClickListener(onSoundClick);

        // Retrieve time format setting from previously saved SharedPreferences (false by default)
        this.check_format_time.setChecked(myPref.getBoolean(Key.PREFERENCES_FORMAT_TIME, false));
        this.setFormatTimeText();
        this.check_format_time.setOnClickListener(onFormatTimeClick);

        // =========== Sort History Settings ===========
        // Add listener to show_spinner Button
        this.btn_show_spinner.setOnClickListener(onShowSpinnerClick);
        // Set text for show_spinner Button
        this.setSortByText(
                MySharedPreferencesUtil.getInt(this, Key.PREFERENCES_SORT_BY, GameHistoryList.SORT_BY_TIME),
                MySharedPreferencesUtil.getInt(this, Key.PREFERENCES_ORDER, GameHistoryList.ORDER_ASCENDING)
        );
        // Retrieve sort Custom Level History setting from SharedPreferences (false by default)
        this.check_sort_custom_history.setChecked(!myPref.getBoolean(Key.PREFERENCES_SORT_CUSTOM, false));
        this.check_sort_custom_history.setOnClickListener(onSortCustomHistoryClick);
        // Retrieve sort incomplete games setting from SharedPreferences (true by default)
        this.check_show_incomplete.setChecked(myPref.getBoolean(Key.PREFERENCES_SHOW_LOST_GAMES_BOTTOM, true));
        this.check_show_incomplete.setOnClickListener(onShowIncompleteGamesClick);
        // ======= End of Sort History Settings ========

        // Bind ImageViews to GIFs using Glide
        Glide.with(this)
                .load(AppCompatResources.getDrawable(this, R.drawable.checked_toggle_flag))
                .placeholder(R.drawable.checked_toggle_flag_error)
                .error(R.drawable.checked_toggle_flag_error)
                .into(this.iv_toggle_flag);
        Glide.with(this)
                .load(AppCompatResources.getDrawable(this, R.drawable.checked_radio_flag))
                .placeholder(R.drawable.checked_radio_flag_error)
                .error(R.drawable.checked_radio_flag_error)
                .into(this.iv_radio_flag);

        // Add listener to RadioButton for flag selection and toggle appropriate choice
        this.rbtn_toggle_flag.setOnCheckedChangeListener(onFlagClick);
        this.rbtn_toggle_flag.setChecked(MySharedPreferencesUtil.getBoolean(this, Key.PREFERENCES_FLAG_TOGGLE, true));
        ((RadioButton) findViewById(R.id.rbtn_radio_flag)).setChecked(!this.rbtn_toggle_flag.isChecked());

        // Set result code to no change
        setResult(this.resCode);
    }

    // Prerequisite: code is either ResCode.SETTING_FLAG_CHANGED or ResCode.SETTING_SOUND_CHANGED
    private void setResultCode(int code) {
        this.resCode = switch (this.resCode) {
            case ResCode.SETTING_NO_CHANGE -> code;
            case ResCode.SETTING_FLAG_CHANGED, ResCode.SETTING_SOUND_CHANGED
                    -> code == this.resCode ? code : ResCode.SETTING_ALL_CHANGED;
            case ResCode.SETTING_ALL_CHANGED -> ResCode.SETTING_ALL_CHANGED;
            default -> throw new RuntimeException("Invalid result code");
        };
        setResult(this.resCode);
    }

    private void setFormatTimeText() {
        if (this.check_format_time.isChecked()) {
            this.check_format_time.setText(R.string.time24_format_setting);
        } else {
            this.check_format_time.setText(R.string.time_format_setting);
        }
    }

    private void setSortByText(int sortBy, int order) {
        assert sortBy == GameHistoryList.SORT_BY_TIME || sortBy == GameHistoryList.SORT_BY_DATE;
        assert order == GameHistoryList.ORDER_ASCENDING || order == GameHistoryList.ORDER_DESCENDING;

        String sSortBy = sortBy == GameHistoryList.SORT_BY_TIME ? SETTING_SORT_BY_TIME : SETTING_SORT_BY_DATE;
        String sOrder = order == GameHistoryList.ORDER_ASCENDING ? SETTING_ORDER_ASCENDING : SETTING_ORDER_DESCENDING;
        this.tv_sort_by.setText(String.format(Locale.US, "%s (%s)", sSortBy, sOrder));
    }

    private void setEnabledForAllViews(boolean enabled) {
        this.check_sound.setEnabled(enabled);
        this.check_format_time.setEnabled(enabled);
        this.btn_show_spinner.setEnabled(enabled);
        this.rbtn_toggle_flag.setEnabled(enabled);
        this.rbtn_radio_flag.setEnabled(enabled);
    }

    private void defaultCheckboxAction(View view, String key, boolean invert) {
        assert view instanceof CheckedTextView;

        // Toggle checkbox
        ((CheckedTextView) view).toggle();

        // Update SharedPreferences
        MySharedPreferencesUtil.putBoolean(this, key, ((CheckedTextView) view).isChecked() ^ invert);
    }

    private final View.OnClickListener onSoundClick = view -> {
        this.defaultCheckboxAction(view, Key.PREFERENCES_SOUND, false);

        // Set result code
        setResultCode(ResCode.SETTING_SOUND_CHANGED);
    };

    private final View.OnClickListener onFormatTimeClick = view -> {
        this.defaultCheckboxAction(view, Key.PREFERENCES_FORMAT_TIME, false);
        this.setFormatTimeText();

        // Set result code
        setResultCode(ResCode.SETTING_SOUND_CHANGED);
    };

    private final View.OnClickListener onShowSpinnerClick = view -> {
        // Disable touches
        this.setEnabledForAllViews(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View menuSort = inflater.inflate(R.layout.menu_sort_history, this.rootLayout, false);

        // Create a Runnable for removing menu from screen
        Runnable removeView = () -> {
            this.rootLayout.removeView(this.rootLayout.findViewById(R.id.rootLayout_sort_menu));
            this.setEnabledForAllViews(true);
        };

        int sortBySaved = MySharedPreferencesUtil.getInt(this, Key.PREFERENCES_SORT_BY, GameHistoryList.SORT_BY_TIME);
        int orderSaved = MySharedPreferencesUtil.getInt(this, Key.PREFERENCES_ORDER, GameHistoryList.ORDER_ASCENDING);
        ((RadioGroup) menuSort.findViewById(R.id.radioGroup_sort_by)).check(
                sortBySaved == GameHistoryList.SORT_BY_TIME
                        ? R.id.rbtn_sort_time
                        : R.id.rbtn_sort_date
        );
        ((RadioGroup) menuSort.findViewById(R.id.radioGroup_order)).check(
                orderSaved == GameHistoryList.ORDER_ASCENDING
                        ? R.id.rbtn_order_ascending
                        : R.id.rbtn_order_descending
        );

        // Remove menu if CANCEL button is pressed
        menuSort.findViewById(R.id.btn_sort_cancel).setOnClickListener(view1 -> removeView.run());
        // Update SharedPreferences if DONE button is pressed
        menuSort.findViewById(R.id.btn_sort_done).setOnClickListener(view1 -> {
            int sortBy = GameHistoryList.SORT_BY_TIME, sortOrder = GameHistoryList.ORDER_ASCENDING;
            if (((RadioButton) menuSort.findViewById(R.id.rbtn_sort_date)).isChecked()) {
                sortBy = GameHistoryList.SORT_BY_DATE;
            }
            if (((RadioButton) menuSort.findViewById(R.id.rbtn_order_descending)).isChecked()) {
                sortOrder = GameHistoryList.ORDER_DESCENDING;
            }
            // Update SharedPreferences
            MySharedPreferencesUtil.putInt(this, Key.PREFERENCES_SORT_BY, sortBy);
            MySharedPreferencesUtil.putInt(this, Key.PREFERENCES_ORDER, sortOrder);
            // Update Comparator for GameHistoryList
            GameHistoryList.setOverallComparator(sortBy, sortOrder);
            // Update text for TextView
            this.setSortByText(sortBy, sortOrder);
            removeView.run();
        });
        // Remove menu if anywhere other than CardView is pressed
        menuSort.findViewById(R.id.btn_remove_sort_menu).setOnClickListener(view1 -> removeView.run());

        this.rootLayout.addView(menuSort);
    };

    private final View.OnClickListener onSortCustomHistoryClick = view -> {
        String key = Key.PREFERENCES_SORT_CUSTOM;
        this.defaultCheckboxAction(view, key, true);
        GameHistoryList.notifyPreferenceChange(key, !((CheckedTextView) view).isChecked());
    };

    private final View.OnClickListener onShowIncompleteGamesClick = view -> {
        String key = Key.PREFERENCES_SHOW_LOST_GAMES_BOTTOM;
        this.defaultCheckboxAction(view, key, false);
        GameHistoryList.notifyPreferenceChange(key, ((CheckedTextView) view).isChecked());
    };

    private final CompoundButton.OnCheckedChangeListener onFlagClick = (compoundButton, isChecked) -> {
        MySharedPreferencesUtil.putBoolean(SettingActivity.this, Key.PREFERENCES_FLAG_TOGGLE, isChecked);
        setResultCode(ResCode.SETTING_FLAG_CHANGED);
    };

}