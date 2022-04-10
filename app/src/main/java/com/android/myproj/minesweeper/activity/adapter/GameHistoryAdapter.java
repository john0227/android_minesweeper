package com.android.myproj.minesweeper.activity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.activity.MainActivity;
import com.android.myproj.minesweeper.game.history.GameHistoryVo;
import com.android.myproj.minesweeper.game.history.GameHistoryList;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.util.LogService;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class GameHistoryAdapter extends RecyclerView.Adapter<GameHistoryAdapter.GameHistoryHolder> {

    private final Activity activity;
    private final Level level;
    private final GameHistoryList gameHistoryList;

    public GameHistoryAdapter(Activity activity, Level level) {
        this.activity = activity;
        this.level = level;
        this.gameHistoryList = GameHistoryList.getInstance();
    }

    @NonNull
    @Override
    public GameHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.activity);
        View holderView = inflater.inflate(R.layout.history_rv_item, parent, false);
        return new GameHistoryHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameHistoryHolder holder, int position) {
        // Set image if first ranked GameHistory
        if (position == 0) {
            holder.image.setImageResource(R.drawable.games_won);
        }

        // Set rank TextView
        holder.textViewRank.setText("" + (position + 1));

        GameHistoryVo gameHistory = this.gameHistoryList.getGameHistory(position, this.level);
        // Set date TextView
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
        holder.textViewDate.setText(simpleDateFormat.format(gameHistory.getDate()));
        // Set time TextView
        holder.textViewTime.setText(String.format(Locale.US, "%d:%02d:%03d",
                gameHistory.getMinute(), gameHistory.getSecond(), gameHistory.getMillis()));
    }

    @Override
    public int getItemCount() {
        return this.gameHistoryList.size(this.level);
    }

    public static class GameHistoryHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        private final TextView textViewRank;
        private final TextView textViewDate;
        private final TextView textViewTime;

        public GameHistoryHolder(@NonNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.iv_history);
            this.textViewRank = itemView.findViewById(R.id.tv_rank_history);
            this.textViewDate = itemView.findViewById(R.id.tv_date_history);
            this.textViewTime = itemView.findViewById(R.id.tv_time_history);
        }

    }

}
