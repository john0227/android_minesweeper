package com.android.myproj.minesweeper.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.logic.Game;
import com.android.myproj.minesweeper.logic.Level;
import com.android.myproj.minesweeper.logic.Tile;
import com.android.myproj.minesweeper.logic.TileValue;
import com.android.myproj.minesweeper.util.JSONKey;
import com.android.myproj.minesweeper.util.JSONUtil;
import com.android.myproj.minesweeper.util.Key;
import com.android.myproj.minesweeper.util.LogService;
import com.android.myproj.minesweeper.util.MusicPlayer;
import com.android.myproj.minesweeper.util.Stopwatch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
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
    private MusicPlayer musicPlayer;
    private JSONObject savedState;

    // Project Objects
    private Game game;
    private Stopwatch stopwatch;
    private Level level;

    // Java-Provided Objects/Variables
    private List<MusicPlayer> playersToDestroy;
    private boolean isFlag;
    private boolean hasStarted;
    private boolean isGameOver;
    private boolean isClickable;
    private boolean playSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.retrieveLevel();

            switch (this.level) {
                case EASY -> setContentView(R.layout.activity_easy_level);
                case INTERMEDIATE -> setContentView(R.layout.activity_intermediate_level);
                case EXPERT -> setContentView(R.layout.activity_expert_level);
                default -> throw new RuntimeException("Invalid level: " + this.level);
            }

            init();
            setting();
            activateGame();
        } catch (Exception e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (this.hasStarted && !this.isGameOver) {
            this.saveGame();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Pause timer
        this.stopwatch.pauseTimer();
        // Release any active MusicPlayer
        for (MusicPlayer musicPlayer : this.playersToDestroy) {
            musicPlayer.destroyPlayer();
        }

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
        levelCode = this.savedState.getInt(JSONKey.KEY_LEVEL);
        this.level = Level.getLevelFromCode(levelCode);

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

        this.musicPlayer = new MusicPlayer();

        this.game = new Game(this.level);
        this.stopwatch = new Stopwatch(findViewById(R.id.tv_time));

        this.playersToDestroy = new ArrayList<>();
        this.isFlag = false;
        this.hasStarted = false;
        this.isGameOver = false;
        this.isClickable = true;

        // Retrieve sound setting from previously saved SharedPreferences (true by default)
        SharedPreferences myPref = getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        this.playSound = myPref.getBoolean(Key.PREFERENCES_SOUND, true);
    }

    private void setting() {
        int tileNum = 0;

        // Initialize the MINE_COUNT TextView
        this.tv_mine_count.setText("" + level.getMines());

        // Add Listener to Setting ImageButton
        this.ibtn_setting.setOnClickListener(onSettingClick);

        // Add Listener to Hint ImageButton
        this.ibtn_hint.setOnClickListener(onHintClick);

        // Add Listener to Flag RadioButon
        // this.rbtn_flag.setOnCheckedChangeListener(onRadioFlagChange);

        // Add Listener to SelFlag ToggleButton
        this.tbtn_sel_flag.setOnCheckedChangeListener(onToggleFlagChange);

        // Add this.musicPlayer to list to destroy later
        this.playersToDestroy.add(this.musicPlayer);

        // Add minesweeper tiles (as ImageButtons) to the layout
        String PREFIX_LAYOUT_ID = "linearLayout";
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        for (int r = 0; r < level.getRow(); r++) {
            int resID = getResources().getIdentifier(PREFIX_LAYOUT_ID + r, "id", getPackageName());
            LinearLayout layout_parent = findViewById(resID);
            for (int c = 0; c < level.getCol(); c++) {
                // Create Tiles as ImageButton
                ImageButton tile = new ImageButton(this);
                tile.setTag(tileNum);
                int id = View.generateViewId();
                tile.setId(id);
                tile.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.covered, null));
                tile.setScaleType(ImageView.ScaleType.CENTER_CROP);
                tile.setPadding(0, 0, 0, 0);
                tile.setOnClickListener(onTileClick);

                // Add to activity_easy_level
                tile.setLayoutParams(lp);
                layout_parent.addView(tile);
                imageButtons[tileNum++] = tile;
            }
        }
    }

    private void activateGame() throws IOException {
        // Retrieve and restore saved data if any
        if (this.savedState == null) {
            this.savedState = JSONUtil.readJSONFile(this);
        }
        if (JSONUtil.existsSavedData(this)) {
            LogService.info(this, "Saved data");
            this.restoreGame();
            JSONUtil.clearSavedData(this);
        } else {
            LogService.info(this, "No Saved data");
            this.game = new Game(level);
            this.stopwatch = new Stopwatch(findViewById(R.id.tv_time));
        }
    }

    private AlertDialog.Builder buildGameoverAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MinesweeperGameActivity.this);

        builder.setTitle(message);
        builder.setMessage("Try again?");

        builder.setPositiveButton("New Game", (dialogInterface, i) -> {
            cleanup();
            createNewGame();
        });
        builder.setNegativeButton("Main Menu", (dialogInterface, i) -> {
            cleanup();
            finish();
        });

        // Alert Dialog 이외의 공간을 터치 했을때 Alert 팝업이 안사라지도록 함
        // 무조건 +ve, -ve, neutral 버튼을 눌러야 Alert 팝업이 사라짐
        builder.setCancelable(false);
        // If the above is set to false and none of the +ve, -ve, neutral buttons are initialized,
        // The user will not be able to make the Alert Dialog disappear

        return builder;
    }

    private void updateSoundSetting() {
        SharedPreferences myPref = getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor myPrefEditor = myPref.edit();
        myPrefEditor.putBoolean(Key.PREFERENCES_SOUND, this.playSound);
        myPrefEditor.apply();
    }

    private void saveGame() {
        LogService.info(this, "saveGame() called...saving data...");
        try {
            JSONObject savedGameState = this.game.save();
            savedGameState.put(JSONKey.KEY_LEVEL, level.getCode());
            savedGameState.put(JSONKey.KEY_SECONDS, this.stopwatch.getTimeSeconds());
            savedGameState.put(JSONKey.KEY_MINUTES, this.stopwatch.getTimeMinutes());
            JSONUtil.writeToJSONFile(this, savedGameState.toString());
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
            this.stopwatch = new Stopwatch(
                    findViewById(R.id.tv_time),
                    savedState.getInt(JSONKey.KEY_MINUTES),
                    savedState.getInt(JSONKey.KEY_SECONDS)
            );
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

        musicPlayer.playMusic(this, R.raw.explosion, playSound);

        // 1. Explode the selected MINE cell and reveal all other MINE cells
        List<Integer> indices = this.game.getMineIndices();
        for (int i : indices) {
            setImage(this.imageButtons[i], TileValue.MINE);
        }
        setImage(this.imageButtons[index], TileValue.MINE_EXPLODE);

        // 2. Explode the rest of MINE cells
        final long delay = level.getAnimationDelay();
        new CountDownTimer(500, 500) {
            public void onFinish() {
                Handler setImageHandler = new Handler(Looper.getMainLooper());
                setImageHandler.post(new Runnable() {
                    int i = 0;
                    @Override
                    public void run() {
                        setImage(MinesweeperGameActivity.this.imageButtons[indices.get(i)], TileValue.MINE_EXPLODE);
                        i++;

                        if (i < indices.size()) {
                            setImageHandler.postDelayed(this, delay);
                        }
                    }
                });

                if (playSound) {
                    Handler musicHandler = new Handler(Looper.getMainLooper());
                    musicHandler.post(new Runnable() {
                        int i = 0;

                        @Override
                        public void run() {
                            if (level == Level.EASY || i % 2 == 0) {
                                MusicPlayer tempPlayer = new MusicPlayer();
                                tempPlayer.playMusic(MinesweeperGameActivity.this, R.raw.explosion, playSound);
                                playersToDestroy.add(tempPlayer);
                            }
                            i++;

                            if (i < indices.size()) {
                                musicHandler.postDelayed(this, delay);
                            }
                        }
                    });
                }
            }
            public void onTick(long millisUntilFinished) {}
        }.start();

        Runnable run = () -> {
            isClickable = true;
            buildGameoverAlert("GAME OVER :(").show();
        };
        if (playSound) {
            new Handler(Looper.getMainLooper()).postDelayed(run, 2000 + delay * level.getMines());
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(run, 1000 + delay * level.getMines());
        }
    }

    private void cleanup() {
        // Destroy stopwatch and existing MusicPlayers
        stopwatch.destroyTimer();
        for (MusicPlayer player : this.playersToDestroy) {
            player.destroyPlayer();
        }
    }

    private void createNewGame() {
        LogService.info(MinesweeperGameActivity.this, "Clearing saved data...Creating new game...");

        // Set isGameOver to true
        isGameOver = true;

        // Garbage collection
        cleanup();
        try {
            // Delete any saved data
            JSONUtil.clearSavedData(MinesweeperGameActivity.this);
        } catch (IOException ioe) {
            LogService.error(MinesweeperGameActivity.this, ioe.getMessage(), ioe);
        }
        // Recreate Activity
        MinesweeperGameActivity.this.recreate();
    }

    private final CompoundButton.OnCheckedChangeListener onRadioFlagChange = (button, isChecked) -> {
        isFlag = isChecked;
    };

    private final CompoundButton.OnCheckedChangeListener onToggleFlagChange = (button, isChecked) -> {
        isFlag = isChecked;
        if (isChecked) {
            button.setBackgroundResource(R.drawable.flagged);
        } else {
            button.setBackgroundResource(R.drawable.mouse_pointer);
        }
    };

    private final View.OnClickListener onSettingClick = view -> {
        if (!isClickable) {
            return;
        }

        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(MinesweeperGameActivity.this, ibtn_setting);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.menu_setting, popupMenu.getMenu());
        popupMenu.getMenu().getItem(2).setChecked(playSound);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                menuItem.setActionView(new View(getApplicationContext()));

                String selMenu = menuItem.getTitle().toString();
                if (selMenu.equals("New Game")) {
                    createNewGame();
                } else if (selMenu.equals("Main Menu")) {
                    cleanup();
                    finish();
                } else if (selMenu.equals("Sound")) {
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
                } else {
                    LogService.error(
                            MinesweeperGameActivity.this,
                            "Selected menu from SETTING popup menu is invalid: " + selMenu
                    );
                }

                return true;
            }
        });
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
            MinesweeperGameActivity.this.hasStarted = true;
            stopwatch.startTimer();
            this.game.start(index);
        }

        // If FLAG RadioButton is turned on, flag the selected cell (if possible) and exit
        if (isFlag) {
            List<Tile> flaggedTiles = this.game.flagTile(index);
            if (flaggedTiles.size() > 0) {
                musicPlayer.playMusic(this, R.raw.click, playSound);
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
            this.isGameOver = true;  // finish()
            stopwatch.destroyTimer();

            // Set text
            tv_mine_count.setText("GAME OVER :(");
            animateExplosion(index);
            return;
        }

        // Uncover selected tiles
        musicPlayer.playMusic(this, R.raw.click, playSound);
        for (Tile t : tiles) {
            setImage(this.imageButtons[game.getTileIndex(t)], game.getTileValue(t));
        }

        // Check if all non-mine cells are uncovered
        if (game.hasWon()) {
            this.isGameOver = true;
            musicPlayer.playMusic(MinesweeperGameActivity.this, R.raw.game_won, playSound);
            tv_mine_count.setText("YOU WON :)");
            buildGameoverAlert("YOU WON :)").show();
        }
    };

}
