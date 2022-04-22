package com.android.myproj.minesweeper.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.config.Key;
import com.android.myproj.minesweeper.config.ResCode;
import com.android.myproj.minesweeper.game.history.GameHistoryVo;
import com.android.myproj.minesweeper.game.logic.Game;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.game.logic.Tile;
import com.android.myproj.minesweeper.game.logic.TileValue;
import com.android.myproj.minesweeper.util.AlertDialogBuilderUtil;
import com.android.myproj.minesweeper.util.ConvertUnitUtil;
import com.android.myproj.minesweeper.util.DelayUtil;
import com.android.myproj.minesweeper.util.HistoryUtil;
import com.android.myproj.minesweeper.util.JSONUtil;
import com.android.myproj.minesweeper.util.LogService;
import com.android.myproj.minesweeper.util.MusicPlayer;
import com.android.myproj.minesweeper.util.MySharedPreferencesUtil;
import com.android.myproj.minesweeper.util.StatUtil;
import com.android.myproj.minesweeper.util.Stopwatch;
import com.otaliastudios.zoom.ZoomLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MinesweeperGameActivity extends AppCompatActivity {

    // Views
    private ImageButton[] imageButtons;
    private ImageButton ibtn_setting;
    private ImageButton ibtn_hint;
    private RadioButton rbtn_flag;
    private TextView tv_mine_count;
    private ToggleButton tbtn_sel_flag;

    // Dependent Objects
    private ActivityResultLauncher<Intent> resultLauncher;
    private MusicPlayer[] musicPlayers;
    private JSONObject savedState;
    private Handler gameHandler;

    // Project Objects
    private Game game;
    private Stopwatch stopwatch;
    private Level level;

    // Java-Provided Objects/Variables
    private boolean isFlag;
    private boolean hasStarted;
    private boolean isGameOver;
    private boolean isClickable;
    private boolean playSound;
    private boolean noHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.retrieveLevel();

            setContentView(R.layout.activity_game);

            LogService.info(this, JSONUtil.readJSONFile(this).toString());
            init();
            setting();
            generateGameScreen();
            LogService.info(this, JSONUtil.readJSONFile(this).toString());
        } catch (Exception e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (this.hasStarted && !this.isGameOver) {
            this.saveGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        LogService.info(this, "===== Pausing Game =====");

        // Pause timer
        this.stopwatch.pauseTimer();
        // Cleanup additional resources
        this.cleanupMusicPlayers();
        this.cleanupHandlers();

        // Save game if applicable
        if (this.hasStarted && !this.isGameOver) {
            this.saveGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Resume timer
        this.stopwatch.resumeTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogService.info(this, "===== Destroying Game =====");
        cleanup();
    }

    private void retrieveLevel() throws JSONException {
        Intent intent = getIntent();
        int levelCode = intent.getIntExtra(Key.LEVEL_KEY, Key.RETRIEVE_LEVEL_CODE);

        // If player pressed one of EASY, INTERMEDIATE, EXPERT buttons in MainActivity
        if (levelCode != Key.RETRIEVE_LEVEL_CODE) {
            this.level = Level.getLevelFromCode(levelCode);
            return;
        }

        // If player pressed RESUME button in MainActivity
        this.savedState = JSONUtil.readJSONFile(this);
        this.level = Level.restoreLevel(savedState);

        // Update level code in this intent
        intent.removeExtra(Key.LEVEL_KEY);
        intent.putExtra(Key.LEVEL_KEY, levelCode);
    }

    private void init() {
        this.imageButtons = new ImageButton[level.getRow() * level.getCol()];
        this.ibtn_setting = findViewById(R.id.ibtn_setting);
        this.ibtn_hint = findViewById(R.id.ibtn_hint);
        this.rbtn_flag = findViewById(R.id.rbtn_flag);
        this.tv_mine_count = findViewById(R.id.tv_mine_count);
        this.tbtn_sel_flag = findViewById(R.id.tbtn_sel_flag);

        this.musicPlayers = new MusicPlayer[this.level.getMines()];
        this.gameHandler = new Handler(Looper.getMainLooper());

        this.game = new Game(this.level);
        this.stopwatch = new Stopwatch(findViewById(R.id.tv_time));

        this.isFlag = false;
        this.hasStarted = false;
        this.isGameOver = false;
        this.isClickable = true;
        this.noHint = true;

        // Retrieve sound setting from previously saved SharedPreferences (true by default)
        this.playSound = MySharedPreferencesUtil.getBoolean(this, Key.PREFERENCES_SOUND, true);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                activityResultCallback
        );
    }

    private void setting() {
        // Initialize the MINE_COUNT TextView
        this.tv_mine_count.setText("" + level.getMines());

        // Add Listener to Setting ImageButton
        this.ibtn_setting.setOnClickListener(onSettingClick);

        // Add Listener to Hint ImageButton
        this.ibtn_hint.setOnClickListener(onHintClick);

        // Change TextView to show appropriate level
        ((TextView) findViewById(R.id.tv_level)).setText(this.level.toString());

        ConstraintLayout parent = findViewById(R.id.layout_parent);
        // Retrieve flag setting (toggle by default)
        if(MySharedPreferencesUtil.getBoolean(this, Key.PREFERENCES_FLAG_TOGGLE, true)) {
            // Add Listener to SelFlag ToggleButton
            this.tbtn_sel_flag.setOnCheckedChangeListener(onToggleFlagChange);
            // Remove RadioGroup from View
            parent.removeView(findViewById(R.id.rg_sel_flag));
            this.rbtn_flag = null;
            LogService.info(this, "Removed radiobutton");
        } else {
            // Add Listener to Flag RadioButton
             this.rbtn_flag.setOnCheckedChangeListener(onRadioFlagChange);
            // Remove tbtn_sel_flag from View
            parent.removeView(this.tbtn_sel_flag);
            this.tbtn_sel_flag = null;
            LogService.info(this, "Removed togglebutton");
        }

        // Initialize MusicPlayers
        for (int i = 0; i < this.musicPlayers.length; i++) {
            this.musicPlayers[i] = new MusicPlayer();
        }

        this.generateMinesweeperBoard();
    }

    private void generateMinesweeperBoard() {
        int tileNum = 0;
        int tileDim = 30;  // in DP
        int padding = 0;  // in DP (start and end)

        ZoomLayout zoomLayout = findViewById(R.id.zoomLayout_game);
        if (this.level.getRow() >= 25 || this.level.getCol() >= 15) {
            zoomLayout.setZoomEnabled(true);
            padding = (int) ConvertUnitUtil.convertPxToDP(this, 50);
        }

        LinearLayout container = findViewById(R.id.linearL_mine_container);
        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                (int) ConvertUnitUtil.convertPxToDP(this, tileDim * this.level.getCol()) + 2 * padding,
                (int) ConvertUnitUtil.convertPxToDP(this, tileDim)
        );
        LinearLayout.LayoutParams topBottomChildLayoutParams = new LinearLayout.LayoutParams(
                (int) ConvertUnitUtil.convertPxToDP(this, tileDim * this.level.getCol()) + 2 * padding,
                (int) ConvertUnitUtil.convertPxToDP(this, tileDim) + padding
        );
        LinearLayout.LayoutParams tileParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        // Add minesweeper tiles (as ImageButtons) to the layout
        for (int r = 0; r < this.level.getRow(); r++) {
            LinearLayout rowContainer = new LinearLayout(this);
            if (r == 0) {
                rowContainer.setPadding(padding, padding, padding, 0);
                rowContainer.setLayoutParams(topBottomChildLayoutParams);
            } else if (r == this.level.getRow() - 1) {
                rowContainer.setPadding(padding, 0, padding, padding);
                rowContainer.setLayoutParams(topBottomChildLayoutParams);
            } else {
                rowContainer.setPadding(padding, 0, padding, 0);
                rowContainer.setLayoutParams(childLayoutParams);
            }
            container.addView(rowContainer);
            for (int c = 0; c < level.getCol(); c++) {
                // Create Tiles as ImageButton
                ImageButton tile = new ImageButton(this);
                tile.setTag(tileNum);
                tile.setId(View.generateViewId());
                tile.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.covered, null));
                tile.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tile.setImageTintList(AppCompatResources.getColorStateList(this, R.color.purple_tint));
                tile.setImageTintMode(PorterDuff.Mode.LIGHTEN);
                tile.setPadding(0, 0, 0, 0);
                tile.setOnClickListener(onTileClick);

                // Add to activity_easy_level
                tile.setLayoutParams(tileParams);
                rowContainer.addView(tile);
                imageButtons[tileNum++] = tile;
            }
        }
    }

    private void generateGameScreen() throws JSONException, IOException {
        // Retrieve and restore saved data if any
        if (this.savedState == null) {
            this.savedState = JSONUtil.readJSONFile(this);
        }
        if (JSONUtil.existsSavedGame(this)) {
            LogService.info(this, "Starting game...There is saved data");
            this.restoreGame();
            JSONUtil.clearSavedGame(this);
        } else {
            LogService.info(this, "Starting game...There is no Saved data");
            this.game = new Game(level);
            this.stopwatch = new Stopwatch(findViewById(R.id.tv_time));
        }
    }

    private void startGame(int index) {
        MinesweeperGameActivity.this.hasStarted = true;
        stopwatch.startTimer();

        // Generate mine cells
        this.game.start(index);

        // Update Games Started statistics
        try {
            LogService.info(this, "Updating games started");
            JSONObject savedData = JSONUtil.readJSONFile(this);
            StatUtil.updateGameStarted(savedData, this.level);
            JSONUtil.writeToJSONFile(this, savedData);
        } catch (JSONException | IOException e) {
            LogService.error(this, "Could not update Games Started", e);
        }
    }

    private AlertDialog.Builder buildGameoverAlert(boolean hasWon) {
        DialogInterface.OnClickListener posAction = (dialogInterface, i) -> {
            cleanup();
            createNewGame(true);
        };
        DialogInterface.OnClickListener negAction = (dialogInterface, i) -> {
            cleanup();
            finish();
        };

        String title, message;
        if (hasWon) {
            title = "YOU WON :) Your time was " + this.stopwatch.toString();
            message = "Congratulations! Play again?";
        } else {
            title = "GAME OVER :(";
            message = "Good luck next time. Try again?";
        }

        return AlertDialogBuilderUtil.buildAlertDialog(this, title, message,
                "New Game", "Main Menu", posAction, negAction, false);
    }

    private void updateSoundSetting() {
        MySharedPreferencesUtil.putBoolean(this, Key.PREFERENCES_SOUND, this.playSound);
    }

    private void saveGame() {
        LogService.info(this, "saveGame() called...saving data...");
        try {
            JSONObject savedGameState = JSONUtil.readJSONFile(this);
            this.game.save(savedGameState);
            this.stopwatch.saveStopwatch(savedGameState);
            this.level.saveLevel(savedGameState);
            JSONUtil.writeToJSONFile(MinesweeperGameActivity.this, savedGameState);
        } catch (JSONException | IOException e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    private void restoreGame() {
        LogService.info(this, "Restoring saved data");
        try {
            JSONObject savedState = JSONUtil.readJSONFile(this);
            // Restore game and display tiles accordingly
            this.game = Game.restore(savedState);
            for (Tile t : this.game.getVisibleTiles()) {
                this.hasStarted = true;
                setImage(this.imageButtons[game.getTileIndex(t)], game.getTileValue(t));
            }
            for (Tile t : this.game.getFlaggedTiles()) {
                setImage(this.imageButtons[game.getTileIndex(t)], TileValue.FLAGGED);
            }
            // Restore stopwatch
            this.stopwatch.restoreStopwatch(savedState);
            this.stopwatch.startTimer();
            // Restore leftover mine count
            this.tv_mine_count.setText("" + this.game.getLeftoverMine());
        } catch (JSONException e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    private void setImage(ImageButton imageButton, TileValue tileValue) {
        switch (tileValue) {
            case COVERED -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.covered, null));
            case FLAGGED -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.flagged, null));
            case ZERO -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine_0, null));
            case ONE -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine_1, null));
            case TWO -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine_2, null));
            case THREE -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine_3, null));
            case FOUR -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine_4, null));
            case FIVE -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine_5, null));
            case SIX -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine_6, null));
            case SEVEN -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine_7, null));
            case EIGHT -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine_8, null));
            case MINE -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine, null));
            case MINE_EXPLODE -> imageButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.mine_explode, null));
            default -> LogService.error(this, "Tile[index=" + imageButton.getTag() + "] does not have valid TileValue");
        }
    }

    private void animateExplosion(int index) {
        // Disallow restarting during animation
        isClickable = false;

        musicPlayers[9].playMusic(this, R.raw.explosion, playSound);

        // 1. Explode the selected MINE cell and reveal all other MINE cells
        List<Integer> indices = this.game.getMineIndices();
        for (int i : indices) {
            setImage(this.imageButtons[i], TileValue.MINE);
        }
        setImage(this.imageButtons[index], TileValue.MINE_EXPLODE);

        // 2. Explode the rest of MINE cells
        long delay;
        if (playSound) {
            delay = level.getAnimationDelay();
        } else {
            delay = 60;
        }
        new CountDownTimer(500, 500) {
            public void onFinish() {
                gameHandler.post(new Runnable() {
                    int i = 0;
                    final int interval = indices.size() > 50 ? 2 : 1;

                    @Override
                    public void run() {
                        setImage(MinesweeperGameActivity.this.imageButtons[indices.get(i)], TileValue.MINE_EXPLODE);
                        if (playSound && i % interval == 0) {
                            musicPlayers[i].playMusic(MinesweeperGameActivity.this, R.raw.explosion, true);
                        }
                        i++;

                        if (i >= 30) {
                            musicPlayers[i - 30].destroyPlayer();
                        }

                        if (i < indices.size()) {
                            gameHandler.postDelayed(this, delay);
                        } else {
                            DelayUtil.delayTask(() -> {
                                isClickable = true;
                                buildGameoverAlert(false).show();
                            }, 500);
                        }
                    }
                });
            }
            public void onTick(long millisUntilFinished) {}
        }.start();
    }

    private void cleanup() {
        this.cleanupTimer();
        this.cleanupMusicPlayers();
        this.cleanupHandlers();
    }
    private void cleanupTimer() {
        this.stopwatch.destroyTimer();
    }
    private void cleanupMusicPlayers() {
        for (MusicPlayer player : this.musicPlayers) {
            player.destroyPlayer();
        }
    }
    private void cleanupHandlers() {
        // Remove callbacks to Handler if exists
        if (this.isGameOver) {
            if (this.gameHandler != null) {
                this.gameHandler.removeCallbacksAndMessages(null);
            }
        }
    }

    private void createNewGame(boolean restart) {
        if (restart) {
            LogService.info(MinesweeperGameActivity.this, "Clearing saved data...");
            // Set isGameOver to true
            isGameOver = true;
            // Garbage collection
            cleanup();
            try {
                // Delete any saved data
                JSONUtil.clearSavedGame(MinesweeperGameActivity.this);
            } catch (JSONException | IOException e) {
                LogService.error(MinesweeperGameActivity.this, e.getMessage(), e);
            }
        }
        LogService.info(MinesweeperGameActivity.this, "Creating new game...");
        // Recreate Activity
        MinesweeperGameActivity.this.recreate();
    }
    
    private void gameOverAction(boolean hasWon, int indexLastSelected) {
        this.isGameOver = true;

        // Reset zoom
        ZoomLayout zoomLayout = findViewById(R.id.zoomLayout_game);
        zoomLayout.zoomBy(1 / zoomLayout.getZoom(), true);
        zoomLayout.setZoomEnabled(false);

        if (hasWon) {
            musicPlayers[0].playMusic(MinesweeperGameActivity.this, R.raw.game_won, playSound);
            stopwatch.pauseTimer();
            tv_mine_count.setText("YOU WON :)");
            try {
                // Update all statistics and history
                StatUtil.updateAllStat(this, this.level, this.stopwatch.getTotalTimeInSeconds(), this.noHint);
                HistoryUtil.saveGameHistory(this, this.level, this.stopwatch);
            } catch (JSONException | IOException e) {
                LogService.error(this, "Could not save statistics", e);
            }
            buildGameoverAlert(true).show();
        } else {
            // Update win rate and current win streak and history
            JSONObject savedData = JSONUtil.readJSONFile(this);
            try {
                StatUtil.updateWinRate(savedData, this.level);
                StatUtil.resetCurrStreak(savedData, this.level);
                HistoryUtil.saveGameHistory(savedData, this.level, GameHistoryVo.GAME_LOST);
                JSONUtil.writeToJSONFile(this, savedData);
            } catch (JSONException | IOException e) {
                LogService.error(this, "Was unable to update win rate and current win streak", e);
            }
            // Destroy stopwatch
            stopwatch.destroyTimer();
            // Set text
            tv_mine_count.setText("GAME OVER :(");
            animateExplosion(indexLastSelected);
        }
    }

    private final CompoundButton.OnCheckedChangeListener onRadioFlagChange = (button, isChecked) -> isFlag = isChecked;
    private final CompoundButton.OnCheckedChangeListener onToggleFlagChange = (button, isChecked) -> {
        isFlag = isChecked;
        if (isChecked) {
            button.setBackgroundResource(R.drawable.flagged);
        } else {
            button.setBackgroundResource(R.drawable.mouse_pointer);
        }
    };

    private PopupMenu.OnMenuItemClickListener onMenuItemClickListener = menuItem -> {
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menuItem.setActionView(new View(getApplicationContext()));

        String selMenu = menuItem.getTitle().toString();
        switch (selMenu) {
            case "Setting" -> resultLauncher.launch(new Intent(this, SettingActivity.class));
            case "New Game" -> createNewGame(true);
            case "Main Menu" -> {
                LogService.info(this, "===== Returning to Main Screen =====");
                finish();
            }
            case "Sound" -> {
                menuItem.setChecked(!playSound);
                playSound = !playSound;
                updateSoundSetting();
                menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return false;
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return false;
                    }
                });
                return false;
            }
            default -> LogService.error(this, "Selected menu from SETTING popup menu is invalid: " + selMenu);
        }

        return true;
    };

    private final View.OnClickListener onSettingClick = view -> {
        if (!isClickable) {
            return;
        }

        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(MinesweeperGameActivity.this, ibtn_setting);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.menu_setting, popupMenu.getMenu());
        popupMenu.getMenu().getItem(3).setChecked(playSound);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);

        // Showing the popup menu
        popupMenu.show();
    };

    private final View.OnClickListener onHintClick = view -> {
        // If game has not yet started, start game
        if (!MinesweeperGameActivity.this.hasStarted) {
            MinesweeperGameActivity.this.hasStarted = true;
            stopwatch.startTimer();
            this.game.start(-1);
        }

        Tile tileToReveal = this.game.showHint();
        if (tileToReveal == null) {
            // there is no uncover-able Tile
            return;
        }

        noHint = false;
        setImage(this.imageButtons[game.getTileIndex(tileToReveal)], game.getTileValue(tileToReveal));
    };

    private final View.OnClickListener onTileClick = view -> {
        // If game is over, don't allow player to click tiles
        if (isGameOver) {
            return;
        }

        if (!(view instanceof ImageButton)) {
            LogService.error(this, "[#onTileClick] Button clicked is not an instance of ImageButton");
            return;
        }

        ImageButton tile = (ImageButton) view;
        int index = (int) tile.getTag();

        // If the player has clicked his/her first cell
        if (!MinesweeperGameActivity.this.hasStarted) {
            this.startGame(index);
        }

        // If FLAG RadioButton is turned on, flag the selected cell (if possible) and exit
        if (isFlag) {
            List<Tile> flaggedTiles = this.game.flagTile(index);
            if (flaggedTiles.size() > 0) {
                musicPlayers[0].playMusic(this, R.raw.click, playSound);
            }
            tv_mine_count.setText("" + this.game.getLeftoverMine());
            for (Tile ft : flaggedTiles) {
                if (game.isTileFlagged(ft)) {
                    setImage(this.imageButtons[game.getTileIndex(ft)], TileValue.FLAGGED);
                } else if (game.isTileCovered(ft)) {
                    setImage(this.imageButtons[game.getTileIndex(ft)], TileValue.COVERED);
                }
            }
            return;
        }

        List<Tile> tiles = this.game.selectTile(index);
        // If nothing was validly selected, don't do anything
        if (tiles.size() == 0) {
            return;
        }

        // If selected tile is a MINE cell
        if (game.getTileValue(tiles.get(0)) == TileValue.MINE) {
            gameOverAction(false, index);
            return;
        }

        // Uncover selected tiles
        musicPlayers[0].playMusic(this, R.raw.click, playSound);
        for (Tile t : tiles) {
            setImage(this.imageButtons[game.getTileIndex(t)], game.getTileValue(t));
        }

        // Check if all non-mine cells are uncovered
        if (game.hasWon()) {
            gameOverAction(true, index);
        }
    };

    private final ActivityResultCallback<ActivityResult> activityResultCallback = result -> {
        LogService.info(MinesweeperGameActivity.this, "Returned from setting to game");
        switch (result.getResultCode()) {
            case ResCode.SETTING_ALL_CHANGED, ResCode.SETTING_FLAG_CHANGED -> createNewGame(false);
            case ResCode.SETTING_SOUND_CHANGED -> playSound = MySharedPreferencesUtil.getBoolean(
                        MinesweeperGameActivity.this,
                        Key.PREFERENCES_SOUND,
                        true
                );
            default -> {}
        }
    };

}
