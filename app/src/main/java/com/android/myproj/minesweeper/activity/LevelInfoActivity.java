package com.android.myproj.minesweeper.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.activity.adapter.LevelInfoAdapter;
import com.android.myproj.minesweeper.util.LogService;

public class LevelInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_level_info);

            setting();
        } catch (Exception e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    private void setting() {
        LevelInfoAdapter adapter = new LevelInfoAdapter(this);
        RecyclerView recyclerView = findViewById(R.id.rv_level_info);
        // Set LayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        // Set Adapter
        recyclerView.setAdapter(adapter);
    }

}