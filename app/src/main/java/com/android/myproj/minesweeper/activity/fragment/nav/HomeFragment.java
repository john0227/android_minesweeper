package com.android.myproj.minesweeper.activity.fragment.nav;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.activity.MinesweeperGameActivity;
import com.android.myproj.minesweeper.activity.SettingActivity;
import com.android.myproj.minesweeper.config.JSONKey;
import com.android.myproj.minesweeper.config.Key;
import com.android.myproj.minesweeper.config.ResCode;
import com.android.myproj.minesweeper.game.history.GameHistoryVo;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.shape.MyArc;
import com.android.myproj.minesweeper.util.AlertDialogBuilderUtil;
import com.android.myproj.minesweeper.util.AnimationUtil;
import com.android.myproj.minesweeper.util.HistoryUtil;
import com.android.myproj.minesweeper.util.JSONUtil;
import com.android.myproj.minesweeper.util.LogService;
import com.android.myproj.minesweeper.util.MySharedPreferencesUtil;
import com.android.myproj.minesweeper.util.StatUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private Activity activity;
    private ViewGroup rootLayout;
    
    private ActivityResultLauncher<Intent> resultLauncherGame;
    private ActivityResultLauncher<Intent> resultLauncherSetting;
    private Button btn_setting;

    private int rootLayoutHeight;
    private boolean isAttached;
    private boolean playSound;
    private boolean existsSavedData;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.isAttached = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (this.isAttached) {
            this.activity = getActivity();
        }
        
        try {
            this.rootLayout = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

            init();
            setting();
        } catch (Exception e) {
            LogService.error(this.activity, e.getMessage(), e);
        }
        
        return this.rootLayout;
    }

    private void init() {
        // Set height of rootLayout
        new Handler(Looper.getMainLooper()).postDelayed(() -> this.rootLayoutHeight = this.rootLayout.getHeight(), 5);

        this.btn_setting = this.rootLayout.findViewById(R.id.btn_setting);

        // Set Callback Function
        resultLauncherGame = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                gameResultCallback
        );
        resultLauncherSetting = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                settingResultCallback
        );

        // Retrieve sound setting from previously saved SharedPreferences (true by default)
        this.playSound = MySharedPreferencesUtil.getBoolean(this.activity, Key.PREFERENCES_SOUND, true);
    }

    private void setting() throws JSONException {
        // Add listener to level buttons
        this.rootLayout.findViewById(R.id.btn_easy).setOnClickListener(onLevelButtonClick);
        this.rootLayout.findViewById(R.id.btn_intermediate).setOnClickListener(onLevelButtonClick);
        this.rootLayout.findViewById(R.id.btn_expert).setOnClickListener(onLevelButtonClick);
        this.rootLayout.findViewById(R.id.btn_jumbo).setOnClickListener(onLevelButtonClick);
        this.rootLayout.findViewById(R.id.btn_custom).setOnClickListener(onLevelButtonClick);
        this.rootLayout.findViewById(R.id.btn_resume).setOnClickListener(onLevelButtonClick);

        // Bind PopupMenu to ImageButton
        this.btn_setting.setOnClickListener(onSettingClick);

        // Reposition buttons if necessary
        this.repositionButtons();

        // Add callback for when back button is pressed
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                View view = rootLayout.findViewById(R.id.rootLayout_custom_dialog);
                if (view != null) {
                    onNegativeCustomLevelDialogClick.onClick(rootLayout.findViewById(R.id.btn_custom_neg));
                } else {
                    activity.finish();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void repositionButtons() throws JSONException {
        Button resumeButton = this.rootLayout.findViewById(R.id.btn_resume);
        Button intermediateButton = this.rootLayout.findViewById(R.id.btn_intermediate);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) intermediateButton.getLayoutParams();

        // Retrieve saved JSONObject and check if there is data to restore
        JSONObject savedState = JSONUtil.readJSONFile(this.activity);

        // =================================================================
        // ============ If player does not have an ongoing game ============
        // =================================================================
        if (!JSONUtil.existsSavedGame(this.activity)) {
            LogService.info(this.activity, "====== No SavedData =====");
            this.existsSavedData = false;

            // Make RESUME button invisible
            resumeButton.setBackgroundColor(ContextCompat.getColor(this.activity, android.R.color.transparent));
            resumeButton.setText("");
            resumeButton.setEnabled(false);

            // Set vertical bias to 0.40 for INTERMEDIATE button
            lp.verticalBias = 0.37f;
            intermediateButton.setLayoutParams(lp);

            // Remove progress bar
            this.removeAllViews(
                    this.rootLayout.findViewById(R.id.frameLayout_progress_inside_circle),
                    this.rootLayout.findViewById(R.id.frameLayout_progress_outside_arc),
                    this.rootLayout.findViewById(R.id.frameLayout_progress_outside_circle)
            );
            ((TextView) this.rootLayout.findViewById(R.id.tv_progress)).setText("");
            return;
        }

        // =================================================================
        // ================= If player has an ongoing game =================
        // =================================================================
        LogService.info(this.activity, "====== SavedData exists =====");
        this.existsSavedData = true;
        Level level = Level.restoreLevel(savedState);

        // If there is data to restore, make RESUME button visible
        resumeButton.setBackgroundColor(ContextCompat.getColor(this.activity, R.color.purple_level_1));
        switch (level) {
            case EASY -> resumeButton.setText(R.string.btn_resume_easy);
            case INTERMEDIATE -> resumeButton.setText(R.string.btn_resume_intermediate);
            case EXPERT -> resumeButton.setText(R.string.btn_resume_expert);
            case JUMBO -> resumeButton.setText(R.string.btn_resume_jumbo);
            case CUSTOM -> resumeButton.setText(R.string.btn_resume_custom);
        }
        resumeButton.setEnabled(true);

        // Draw the progress bar
        int uncoveredTiles = level.getRow() * level.getCol()
                - savedState.getInt(JSONKey.KEY_COVERED_TILES) - level.getMines();
        int totalNonMineTiles = level.getRow() * level.getCol() - level.getMines();
        this.drawProgressBar((int) (100.0 * uncoveredTiles / totalNonMineTiles));

        // Set vertical bias for INTERMEDIATE button
        lp.verticalBias = 0.43f;
        intermediateButton.setLayoutParams(lp);
    }

    private void drawProgressBar(int progress) {
        // Retrieve respective FrameLayouts
        FrameLayout frameInside = this.rootLayout.findViewById(R.id.frameLayout_progress_inside_circle);
        FrameLayout frameOutsideArc = this.rootLayout.findViewById(R.id.frameLayout_progress_outside_arc);
        FrameLayout frameOutsideCircle = this.rootLayout.findViewById(R.id.frameLayout_progress_outside_circle);

        // Clear anything inside the FrameLayouts
        this.removeAllViews(frameInside, frameOutsideArc, frameOutsideCircle);

        // Draw Circles and Arcs
        float right = MySharedPreferencesUtil.getFloat(this.activity, Key.PREFERENCES_WIDTH, 131);
        float bottom = MySharedPreferencesUtil.getFloat(this.activity, Key.PREFERENCES_HEIGHT, 131);
        MyArc insideCircle = new MyArc(this.activity, Color.parseColor("#CACFD2"), 360,
                new RectF(20, 20, right - 20, bottom - 20));
        MyArc outsideArc = new MyArc(this.activity, Color.parseColor("#CA3CF0"), 360 * progress / 100F,
                new RectF(0, 0, right, bottom));
        MyArc outsideCircle = new MyArc(this.activity, Color.parseColor("#77D081E5"), 360,
                new RectF(0, 0, right, bottom));

        // Add Shapes to Layouts
        frameOutsideCircle.addView(outsideCircle);
        frameOutsideArc.addView(outsideArc);
        frameInside.addView(insideCircle);

        // Show progress as percentage
        ((TextView) this.rootLayout.findViewById(R.id.tv_progress)).setText("" + progress + "%");
    }

    private void removeAllViews(ViewGroup... viewGroups) {
        for (ViewGroup viewGroup : viewGroups) {
            viewGroup.removeAllViews();
        }
    }

    private void setEnableUI(boolean enableUI) {
        // Set enableUI for Level buttons
        this.rootLayout.findViewById(R.id.btn_resume).setEnabled(enableUI);
        this.rootLayout.findViewById(R.id.btn_resume).setClickable(enableUI);
        this.rootLayout.findViewById(R.id.btn_easy).setEnabled(enableUI);
        this.rootLayout.findViewById(R.id.btn_easy).setClickable(enableUI);
        this.rootLayout.findViewById(R.id.btn_intermediate).setEnabled(enableUI);
        this.rootLayout.findViewById(R.id.btn_intermediate).setClickable(enableUI);
        this.rootLayout.findViewById(R.id.btn_expert).setEnabled(enableUI);
        this.rootLayout.findViewById(R.id.btn_expert).setClickable(enableUI);
        this.rootLayout.findViewById(R.id.btn_jumbo).setEnabled(enableUI);
        this.rootLayout.findViewById(R.id.btn_jumbo).setClickable(enableUI);
        this.rootLayout.findViewById(R.id.btn_custom).setEnabled(enableUI);
        this.rootLayout.findViewById(R.id.btn_custom).setClickable(enableUI);
        // Set enableUI for Settings ImageButton
        btn_setting.setEnabled(enableUI);
        btn_setting.setClickable(enableUI);
    }

    private void showCustomLevelDialog(boolean animate) {
        // Prevent player from pressing any buttons or navigating to a different page
        this.setEnableUI(false);
        MySharedPreferencesUtil.putBoolean(this.activity, Key.PREFERENCES_ENABLE, false);

        LayoutInflater inflater = LayoutInflater.from(this.activity);
        View view = inflater.inflate(R.layout.custom_level_dialog, this.rootLayout, true);
        if (animate) {
            AnimationUtil.fadeIn(view.findViewById(R.id.rootLayout_custom_dialog), 180);
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view1, MotionEvent motionEvent) {
                if (isKeyboardActive()) {
                    hideKeyboard();
                } else {
                    onNegativeCustomLevelDialogClick.onClick(view.findViewById(R.id.btn_custom_neg));
                }
                return false;
            }
        });

        // Set listeners for OK and CANCEL buttons
        view.findViewById(R.id.btn_custom_pos).setOnClickListener(onPositiveCustomLevelDialogClick);
        view.findViewById(R.id.btn_custom_neg).setOnClickListener(onNegativeCustomLevelDialogClick);
    }

    private void showResumeAlertDialog(Runnable toRun) {
        String title = "Resume Game?";
        String message = "You have an ongoing game. Are you sure you want to start a new game?";
        String posText = "Continue";
        String negText = "Go Back";
        DialogInterface.OnClickListener posAction = (dialogInterface, i) -> {
            updateSavedData();
            toRun.run();
        };
        DialogInterface.OnClickListener negAction = (dialogInterface, i) -> {};

        AlertDialogBuilderUtil.buildAlertDialog(
                this.activity, title, message, posText, negText, posAction, negAction, true
        ).show();
    }

    // If this method is called, the player must have started a new game even though there was a saved game
    // i.e. this.existsSavedData == true &&
    private void updateSavedData() {
        try {
            // Clear corresponding saved game data
            JSONUtil.clearSavedGame(this.activity);
            // Updates the Win Rate and Current Win Streak statistics of the game level not resumed by player
            JSONObject savedData = JSONUtil.readJSONFile(this.activity);
            Level savedLevel = Level.restoreLevel(savedData);
            StatUtil.updateWinRate(savedData, savedLevel);
            StatUtil.resetCurrStreak(savedData, savedLevel);
            // Update history
            HistoryUtil.saveGameHistory(savedData, savedLevel, GameHistoryVo.GAME_NOT_RESUMED);
            JSONUtil.writeToJSONFile(this.activity, savedData);
        } catch (JSONException | IOException e) {
            LogService.error(this.activity, e.getMessage(), e);
        }
    }

    private void updateSoundSetting() {
        MySharedPreferencesUtil.putBoolean(this.activity, Key.PREFERENCES_SOUND, this.playSound);
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean isKeyboardActive() {
        int currRootLayoutHeight = this.rootLayout.getHeight();
        return currRootLayoutHeight != this.rootLayoutHeight;
    }

    private final PopupMenu.OnMenuItemClickListener onMenuItemClickListener = menuItem -> {
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menuItem.setActionView(new View(this.activity.getApplicationContext()));

        String selMenu = menuItem.getTitle().toString();
        switch (selMenu) {
            case "Setting" -> resultLauncherSetting.launch(new Intent(this.activity, SettingActivity.class));
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
            default -> LogService.error(this.activity, "Selected menu from SETTING popup menu is invalid: " + selMenu);
        }

        return true;
    };

    public View.OnClickListener onLevelButtonClick = view -> {
        // Save width and height to SharedPreferences
        if (MySharedPreferencesUtil.contains(this.activity, Key.PREFERENCES_WIDTH)) {
            MySharedPreferencesUtil.putFloat(this.activity, Key.PREFERENCES_WIDTH,
                    this.rootLayout.findViewById(R.id.frameLayout_progress_outside_circle).getMeasuredWidth());
        }
        if (MySharedPreferencesUtil.contains(this.activity, Key.PREFERENCES_HEIGHT)) {
            MySharedPreferencesUtil.putFloat(this.activity, Key.PREFERENCES_HEIGHT,
                    this.rootLayout.findViewById(R.id.frameLayout_progress_outside_circle).getMeasuredHeight());
        }

        Intent intent = new Intent(this.activity, MinesweeperGameActivity.class);

        int code;
        switch ((String) view.getTag()) {
            case "RESUME" -> code = Key.RETRIEVE_LEVEL_CODE;
            case "EASY" -> code = Level.EASY.getCode();
            case "INTERMEDIATE" ->  code = Level.INTERMEDIATE.getCode();
            case "EXPERT" -> code = Level.EXPERT.getCode();
            case "JUMBO" -> code = Level.JUMBO.getCode();
            case "CUSTOM" -> {
                this.showCustomLevelDialog(true);
                return;
            }
            default -> throw new RuntimeException();
        }

        Runnable launchGame = () -> {
            // Pass Level code
            intent.putExtra(Key.LEVEL_KEY, code);
            resultLauncherGame.launch(intent);
        };

        // If there is saved data, but player did not choose resume, alert the player
        if (this.existsSavedData && code != Key.RETRIEVE_LEVEL_CODE) {
            this.showResumeAlertDialog(launchGame);
        } else {
            launchGame.run();
        }
    };

    private final View.OnClickListener onSettingClick = view -> {
        // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(this.activity, btn_setting);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.home_menu_setting, popupMenu.getMenu());
        popupMenu.getMenu().getItem(1).setChecked(playSound);
        popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);

        // Showing the popup menu
        popupMenu.show();
    };

    private final View.OnClickListener onPositiveCustomLevelDialogClick = view -> {
        DialogInterface.OnClickListener reshowCustomDialog = (dialogInterface, i) -> {
            this.rootLayout.removeView(this.rootLayout.findViewById(R.id.rootLayout_custom_dialog));
            this.showCustomLevelDialog(false);
        };

        int rows, cols, mines;
        try {
            rows = Integer.parseInt(((EditText) this.rootLayout.findViewById(R.id.et_row_custom)).getText().toString());
            cols = Integer.parseInt(((EditText) this.rootLayout.findViewById(R.id.et_column_custom)).getText().toString());
            mines = Integer.parseInt(((EditText) this.rootLayout.findViewById(R.id.et_mines_custom)).getText().toString());
        } catch (NumberFormatException nfe) {
            this.hideKeyboard();
            AlertDialogBuilderUtil.buildNonCancelableAlertDialog(
                    this.activity,
                    "Invalid number",
                    "Please enter valid numbers",
                    "OK",
                    reshowCustomDialog
            ).show();
            return;
        }

        boolean areAllPositive = rows > 0 && cols > 0 && mines > 0;
        boolean isValidRow = rows >= 10 && rows <= 50;
        boolean isValidCol = cols >= 10 && cols <= 50;
        // Only allow mines up to 70% of the total numbers of tiles
        boolean isValidMine = mines <= rows * cols * 0.7;

        if (areAllPositive && isValidRow && isValidCol && isValidMine) {
            // Remove Custom Level dialog
            this.rootLayout.removeView(this.rootLayout.findViewById(R.id.rootLayout_custom_dialog));

            // Allow player to press buttons and navigate to different pages
            this.setEnableUI(true);
            MySharedPreferencesUtil.putBoolean(this.activity, Key.PREFERENCES_ENABLE, true);

            // Hide keyboard
            this.hideKeyboard();

            // Launch game activity
            Intent intent = new Intent(this.activity, MinesweeperGameActivity.class);
            Runnable launchGame = () -> {
                // Set Custom Level dimensions and launch game activity
                long animationDelay = mines <= 30 ? 50 : mines <= 100 ? 20 : 10;
                Level.CUSTOM.setValues(cols, rows, mines, animationDelay);
                // Pass Level code
                intent.putExtra(Key.LEVEL_KEY, Level.CUSTOM.getCode());
                resultLauncherGame.launch(intent);
            };

            // If there is saved data, alert the player
            if (this.existsSavedData) {
                this.showResumeAlertDialog(launchGame);
            } else {
                launchGame.run();
            }
        } else {
            if (!areAllPositive) {
                this.hideKeyboard();
                AlertDialogBuilderUtil.buildNonCancelableAlertDialog(
                        this.activity,
                        "Values must be positive",
                        "Please enter positive numbers only",
                        "OK",
                        reshowCustomDialog
                ).show();
            } else if (!isValidRow) {
                this.hideKeyboard();
                AlertDialogBuilderUtil.buildNonCancelableAlertDialog(
                        this.activity,
                        "Invalid number of rows",
                        "Please enter a number between 10 and 50",
                        "OK",
                        reshowCustomDialog
                ).show();
            } else if (!isValidCol) {
                this.hideKeyboard();
                AlertDialogBuilderUtil.buildNonCancelableAlertDialog(
                        this.activity,
                        "Invalid number of columns",
                        "Please enter a number between 10 and 50",
                        "OK",
                        reshowCustomDialog
                ).show();
            } else {  // same as else if (!isValidMine)
                this.hideKeyboard();
                AlertDialogBuilderUtil.buildNonCancelableAlertDialog(
                        this.activity,
                        "Large number of mines",
                        "Please enter a smaller number",
                        "OK",
                        reshowCustomDialog
                ).show();
            }
        }
    };

    private final View.OnClickListener onNegativeCustomLevelDialogClick = view -> {
        AnimationUtil.fadeOut(this.rootLayout.findViewById(R.id.rootLayout_custom_dialog), this.rootLayout, 130);

        // Allow player to press buttons and navigate to different pages
        this.setEnableUI(true);
        MySharedPreferencesUtil.putBoolean(this.activity, Key.PREFERENCES_ENABLE, true);

        // Hide keyboard
        this.hideKeyboard();
    };

    private final ActivityResultCallback<ActivityResult> gameResultCallback = result -> {
        LogService.info(this.activity, "Returned from game to Home Screen");
        try {
            setting();
        } catch (JSONException je) {
            LogService.error(this.activity, je.getMessage(), je);
        }
    };

    private final ActivityResultCallback<ActivityResult> settingResultCallback = result -> {
        LogService.info(this.activity, "Returned from setting to Home Screen");
        switch (result.getResultCode()) {
            case ResCode.SETTING_ALL_CHANGED, ResCode.SETTING_SOUND_CHANGED ->
                    playSound = MySharedPreferencesUtil.getBoolean(
                            this.activity,
                            Key.PREFERENCES_SOUND,
                            true
                    );
            default -> {}
        }
    };
    
}
