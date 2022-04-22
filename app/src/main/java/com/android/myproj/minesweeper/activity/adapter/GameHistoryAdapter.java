package com.android.myproj.minesweeper.activity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class GameHistoryAdapter extends RecyclerView.Adapter<GameHistoryAdapter.GameHistoryHolder> {

    protected final Activity activity;
    protected final Level level;
    protected final GameHistoryList gameHistoryList;

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
        GameHistoryVo gameHistory = this.gameHistoryList.getGameHistory(position, this.level);

        // Set image if best time GameHistory
        if (gameHistory.isBestTime()) {
            holder.image.setImageResource(R.drawable.games_won);
        } else {
            holder.image.setImageDrawable(null);
        }

        // Set rank TextView
        holder.textViewRank.setText("" + (position + 1));

        // Set date TextView
        SimpleDateFormat simpleDateFormat;
        if (MySharedPreferencesUtil.getBoolean(this.activity, Key.PREFERENCES_FORMAT_TIME, false)) {
            simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy H:mm", Locale.US);
        } else {
            simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy h:mm a", Locale.US);
        }
        holder.textViewDate.setText(simpleDateFormat.format(gameHistory.getDate()));
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
        return this.gameHistoryList.size(this.level);
    }

    public static class GameHistoryHolder extends RecyclerView.ViewHolder {

        protected final ImageView image;
        protected final TextView textViewRank;
        protected final TextView textViewDate;
        protected final TextView textViewTime;

        public GameHistoryHolder(@NonNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.iv_history);
            this.textViewRank = itemView.findViewById(R.id.tv_rank_history);
            this.textViewDate = itemView.findViewById(R.id.tv_date_history);
            this.textViewTime = itemView.findViewById(R.id.tv_time_history);
        }

    }

}
