package com.android.myproj.minesweeper.activity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.config.Key;
import com.android.myproj.minesweeper.game.history.GameHistoryList;
import com.android.myproj.minesweeper.game.history.GameHistoryVo;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.util.MySharedPreferencesUtil;
import com.android.myproj.minesweeper.util.TimeFormatUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CustomHistoryAdapter extends GameHistoryAdapter {

    private final static String DIM_FORMAT = "%d by %d Board (%d Mines)";  // row, col, mines

    public CustomHistoryAdapter(Activity activity) {
        super(activity, Level.CUSTOM);
    }

    @NonNull
    @Override
    public CustomHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.activity);
        View holderView = inflater.inflate(R.layout.history_rv_item_custom, parent, false);
        return new CustomHistoryAdapter.CustomHistoryHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameHistoryHolder holder, int position) {
        assert holder instanceof CustomHistoryHolder;
        super.onBindViewHolder(holder, position);

        CustomHistoryHolder newHolder = (CustomHistoryHolder) holder;
        GameHistoryVo gameHistory = this.gameHistoryList.getGameHistory(position, Level.CUSTOM);
        // Set dimension TextView
        newHolder.textViewDim.setText(String.format(Locale.US, DIM_FORMAT,
                gameHistory.getRows(), gameHistory.getCols(), gameHistory.getMines()));
    }

    public static class CustomHistoryHolder extends GameHistoryAdapter.GameHistoryHolder {

        private final TextView textViewDim;

        public CustomHistoryHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewDim = itemView.findViewById(R.id.tv_level_dim_custom);
        }

    }

}
