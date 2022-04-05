package com.android.myproj.minesweeper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.config.Key;
import com.android.myproj.minesweeper.config.ResCode;
import com.android.myproj.minesweeper.util.LogService;
import com.android.myproj.minesweeper.util.MySharedPreferencesUtil;
import com.bumptech.glide.Glide;

public class SettingActivity extends AppCompatActivity {

    private CheckedTextView check_sound;
    private ImageView iv_toggle_flag;
    private ImageView iv_radio_flag;
    private RadioButton rbtn_toggle_flag;

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
        this.check_sound = findViewById(R.id.check_sound);
        this.iv_toggle_flag = findViewById(R.id.iv_toggle_flag);
        this.iv_radio_flag = findViewById(R.id.iv_radio_flag);
        this.rbtn_toggle_flag = findViewById(R.id.rbtn_toggle_flag);

        this.resCode = ResCode.SETTING_NO_CHANGE;
    }

    private void setting() {
        // Retrieve sound setting from previously saved SharedPreferences (true by default)
        SharedPreferences myPref = getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        this.check_sound.setChecked(myPref.getBoolean(Key.PREFERENCES_SOUND, true));
        this.check_sound.setOnClickListener(onSoundClick);

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

    private final View.OnClickListener onSoundClick = view -> {
        // Toggle checkbox
        ((CheckedTextView) view).toggle();

        // Update SharedPreferences
        MySharedPreferencesUtil.putBoolean(
                SettingActivity.this,
                Key.PREFERENCES_SOUND,
                ((CheckedTextView) view).isChecked()
        );

        // Set result code
        setResultCode(ResCode.SETTING_SOUND_CHANGED);
    };

    private final CompoundButton.OnCheckedChangeListener onFlagClick = (compoundButton, isChecked) -> {
        MySharedPreferencesUtil.putBoolean(SettingActivity.this, Key.PREFERENCES_FLAG_TOGGLE, isChecked);
        setResultCode(ResCode.SETTING_FLAG_CHANGED);
    };

}