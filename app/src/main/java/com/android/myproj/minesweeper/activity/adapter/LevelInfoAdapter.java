package com.android.myproj.minesweeper.activity.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.game.logic.Level;

import java.util.Locale;

public class LevelInfoAdapter extends RecyclerView.Adapter<LevelInfoAdapter.LevelInfoHolder> {

    public final static int ITEM_COUNT = Level.values().length - 1;  // All except Custom Level

    private final static String FORMAT_DIMENSION = "%d Rows by %d Columns";
    private final static String FORMAT_MINE_COUNT = "%d Mines";
    private final static String FORMAT_MINE_PERC = "Mine Percentage : %.2f%%";

    private final Activity activity;

    public LevelInfoAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public LevelInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.activity);
        View view = inflater.inflate(R.layout.level_info_rv_item, parent, false);
        return new LevelInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LevelInfoHolder holder, int position) {
        Level level = Level.getLevelFromCode(position + 1);
        holder.tv_level_diff.setText(level.toString().toUpperCase(Locale.US));
        int rows = level.getRow();
        int cols = level.getCol();
        int mines = level.getMines();
        double tiles = rows * cols;
        holder.tv_level_dimensions.setText(String.format(Locale.US, FORMAT_DIMENSION, rows, cols));
        holder.tv_level_mine_count.setText(String.format(Locale.US, FORMAT_MINE_COUNT, mines));
        holder.tv_level_mine_ratio.setText(String.format(Locale.US, FORMAT_MINE_PERC, mines / tiles * 100));
    }

    @Override
    public int getItemCount() {
        return ITEM_COUNT;
    }

    public static class LevelInfoHolder extends RecyclerView.ViewHolder {

        private final TextView tv_level_diff;
        private final TextView tv_level_dimensions;
        private final TextView tv_level_mine_count;
        private final TextView tv_level_mine_ratio;

        public LevelInfoHolder(@NonNull View itemView) {
            super(itemView);
            this.tv_level_diff = itemView.findViewById(R.id.tv_level_diff);
            this.tv_level_dimensions = itemView.findViewById(R.id.tv_level_dimensions);
            this.tv_level_mine_count = itemView.findViewById(R.id.tv_level_mine_count);
            this.tv_level_mine_ratio = itemView.findViewById(R.id.tv_level_mine_ratio);
        }

    }

}
