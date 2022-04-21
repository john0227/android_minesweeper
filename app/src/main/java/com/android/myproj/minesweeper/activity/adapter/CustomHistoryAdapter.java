package com.android.myproj.minesweeper.activity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.config.Key;
import com.android.myproj.minesweeper.game.history.GameHistoryList;
import com.android.myproj.minesweeper.game.history.GameHistoryVo;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.util.MySharedPreferencesUtil;
import com.android.myproj.minesweeper.util.TimeFormatUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CustomHistoryAdapter extends RecyclerView.Adapter<CustomHistoryAdapter.CustomHistoryHolder> {

    private final static String DIM_FORMAT = "%d by %d Board (%d Mines)";  // row, col, mines

    private final Activity activity;
    private final GameHistoryList gameHistoryList;

    public CustomHistoryAdapter(Activity activity) {
        this.activity = activity;
        this.gameHistoryList = GameHistoryList.getInstance();
    }

    @NonNull
    @Override
    public CustomHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.activity);
        View holderView = inflater.inflate(R.layout.history_rv_item_custom, parent, false);
        return new CustomHistoryAdapter.CustomHistoryHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHistoryHolder holder, int position) {
        // Set order TextView
        holder.textViewOrder.setText("" + (position + 1));

        GameHistoryVo gameHistory = this.gameHistoryList.getGameHistory(position, Level.CUSTOM);
        // Set date TextView
        SimpleDateFormat simpleDateFormat;
        if (MySharedPreferencesUtil.getBoolean(this.activity, Key.PREFERENCES_FORMAT_TIME, false)) {
            simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy H:mm", Locale.US);
        } else {
            simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.US);
        }
        holder.textViewDate.setText(simpleDateFormat.format(gameHistory.getDate()));
        // Set dimension TextView
        holder.textViewDim.setText(String.format(Locale.US, DIM_FORMAT,
                gameHistory.getRows(), gameHistory.getCols(), gameHistory.getMines()));
        // Set time TextView
        // Set time TextView
        if (gameHistory.getMinute() == GameHistoryVo.GAME_NOT_RESUMED) {
            holder.textViewTime.setText("INCOMPLETE");
        } else if (gameHistory.getMinute() == GameHistoryVo.GAME_LOST) {
            holder.textViewTime.setText("GAME LOST");
        } else {
            holder.textViewTime.setText(TimeFormatUtil.formatTime(
                    gameHistory.getMinute(), gameHistory.getSecond(), gameHistory.getMillis())
            );
        }
    }

    @Override
    public int getItemCount() {
        return this.gameHistoryList.size(Level.CUSTOM);
    }

    public static class CustomHistoryHolder extends RecyclerView.ViewHolder {

        private final TextView textViewOrder;
        private final TextView textViewDate;
        private final TextView textViewDim;
        private final TextView textViewTime;

        public CustomHistoryHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewOrder = itemView.findViewById(R.id.tv_order_history_custom);
            this.textViewDate = itemView.findViewById(R.id.tv_date_history_custom);
            this.textViewDim = itemView.findViewById(R.id.tv_level_dim_custom);
            this.textViewTime = itemView.findViewById(R.id.tv_time_history_custom);
        }

    }

}
