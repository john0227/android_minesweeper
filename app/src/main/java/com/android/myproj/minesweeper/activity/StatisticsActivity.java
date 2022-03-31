package com.android.myproj.minesweeper.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.util.LogService;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_statistics);
        } catch (Exception e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

}
