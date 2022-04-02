package com.android.myproj.minesweeper.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.myproj.minesweeper.R;
import com.android.myproj.minesweeper.game.logic.Level;
import com.android.myproj.minesweeper.util.JSONKey;
import com.android.myproj.minesweeper.util.JSONUtil;
import com.android.myproj.minesweeper.util.Key;
import com.android.myproj.minesweeper.util.LogService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> resultLauncher;
    private ImageButton ibtn_setting;

    private boolean playSound;
    private boolean existsSavedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_main);

            LogService.info(this, "====== MainActivity created ======");
            init();
            setting();
        } catch (Exception e) {
            LogService.error(this, e.getMessage(), e);
        }
    }

    private void init() {
        this.ibtn_setting = findViewById(R.id.ibtn_setting);

        // Set Callback Function
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                activityResultCallback
        );

        // Retrieve sound setting from previously saved SharedPreferences (true by default)
        SharedPreferences myPref = getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        this.playSound = myPref.getBoolean(Key.PREFERENCES_SOUND, true);
    }

    private void setting() throws JSONException {
        // Create saved data for statistics if there are none
        try {
            JSONUtil.createDefaultStatIfNone(this);
        } catch (JSONException | IOException e) {
            LogService.error(this, "Was unable to create default saved data for statistics", e);
        }

        // Bind PopupMenu to ImageButton
        this.ibtn_setting.setOnClickListener(onSettingClick);

        Button resumeButton = findViewById(R.id.btn_resume);
        Button intermediateButton = findViewById(R.id.btn_intermediate);
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) intermediateButton.getLayoutParams();

        // Retrieve saved JSONObject and check if there is data to restore
        JSONObject savedState = JSONUtil.readJSONFile(this);
        if (!JSONUtil.existsSavedGame(this)) {
            LogService.info(this, "====== No SavedData =====");
            this.existsSavedData = false;

            // Make RESUME button invisible
            resumeButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            resumeButton.setText("");
            resumeButton.setEnabled(false);

            // Set vertical bias to 0.50 for INTERMEDIATE button
            lp.verticalBias = 0.50f;
            intermediateButton.setLayoutParams(lp);

            // Remove progress bar
            this.removeAllViews(
                    findViewById(R.id.frameLayout_progress_inside_circle),
                    findViewById(R.id.frameLayout_progress_outside_arc),
                    findViewById(R.id.frameLayout_progress_outside_circle)
            );
            ((TextView) findViewById(R.id.tv_progress)).setText("");
            return;
        }

        LogService.info(this, "====== SavedData exists =====");
        this.existsSavedData = true;
        Level level = Level.getLevelFromCode(savedState.getInt(JSONKey.KEY_LEVEL));

        // If there is data to restore, make RESUME button visible
        resumeButton.setBackgroundResource(android.R.drawable.btn_default);
        switch (level) {
            case EASY -> resumeButton.setText(R.string.btn_resume_easy);
            case INTERMEDIATE -> resumeButton.setText(R.string.btn_resume_intermediate);
            case EXPERT -> resumeButton.setText(R.string.btn_resume_expert);
        }
        resumeButton.setEnabled(true);

        // Draw the progress bar
        int uncoveredTiles = level.getRow() * level.getCol()
                - savedState.getInt(JSONKey.KEY_COVERED_TILES) - level.getMines();
        int totalNonMineTiles = level.getRow() * level.getCol() - level.getMines();
        this.drawProgressBar((int) (100.0 * uncoveredTiles / totalNonMineTiles));

        // Set vertical bias for INTERMEDIATE button
        lp.verticalBias = 0.56f;
        intermediateButton.setLayoutParams(lp);
    }

    private void drawProgressBar(int progress) {
        // Retrieve respective FrameLayouts
        FrameLayout frameInside = findViewById(R.id.frameLayout_progress_inside_circle);
        FrameLayout frameOutsideArc = findViewById(R.id.frameLayout_progress_outside_arc);
        FrameLayout frameOutsideCircle = findViewById(R.id.frameLayout_progress_outside_circle);

        // Clear anything inside the FrameLayouts
        this.removeAllViews(frameInside, frameOutsideArc, frameOutsideCircle);

        // Draw Circles and Arcs
        SharedPreferences myPref = getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        float right = myPref.getFloat(Key.PREFERENCES_WIDTH, 131);
        float bottom = myPref.getFloat(Key.PREFERENCES_HEIGHT, 131);
        MyArc insideCircle = new MyArc(this, Color.parseColor("#CACFD2"), 360,
                new RectF(20, 20, right - 20, bottom - 20));
        MyArc outsideArc = new MyArc(this, Color.parseColor("#CA3CF0"), 360 * progress / 100F,
                new RectF(0, 0, right, bottom));
        MyArc outsideCircle = new MyArc(this, Color.parseColor("#77D081E5"), 360,
                new RectF(0, 0, right, bottom));

        // Add Shapes to Layouts
        frameOutsideCircle.addView(outsideCircle);
        frameOutsideArc.addView(outsideArc);
        frameInside.addView(insideCircle);

        // Show progress as percentage
        ((TextView) findViewById(R.id.tv_progress)).setText("" + progress + "%");
    }

    private void removeAllViews(ViewGroup... viewGroups) {
        for (ViewGroup viewGroup : viewGroups) {
            viewGroup.removeAllViews();
        }
    }

    public void onClick(View view) {
        // Save width and height to SharedPreferences
        SharedPreferences myPref = getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor myPrefEditor = myPref.edit();
        if (myPref.contains(Key.PREFERENCES_WIDTH)) {
            myPrefEditor.putFloat(Key.PREFERENCES_WIDTH, findViewById(R.id.frameLayout_progress_outside_circle).getMeasuredWidth());
        }
        if (myPref.contains(Key.PREFERENCES_HEIGHT)) {
            myPrefEditor.putFloat(Key.PREFERENCES_HEIGHT, findViewById(R.id.frameLayout_progress_outside_circle).getMeasuredHeight());
        }
        myPrefEditor.apply();

        Intent intent = new Intent(this, MinesweeperGameActivity.class);

        int code;
        switch ((String) view.getTag()) {
            case "RESUME" -> code = Key.RETRIEVE_LEVEL_CODE;
            case "EASY" -> code = Level.EASY.getCode();
            case "INTERMEDIATE" ->  code = Level.INTERMEDIATE.getCode();
            case "EXPERT" -> code = Level.EXPERT.getCode();
            default -> throw new RuntimeException();
        }

        Runnable launchGame = () -> {
            // TODO : Remove this if statement
            if (code != Key.RETRIEVE_LEVEL_CODE) {
                try {
                    JSONUtil.clearSavedData(this);
                } catch (IOException ioe) {
                    LogService.error(MainActivity.this, ioe.getMessage(), ioe);
                }
            }
            // Pass Level code
            intent.putExtra(Key.LEVEL_KEY, code);
            resultLauncher.launch(intent);
        };

        // If there is saved data, but player did not choose resume, alert the player
        if (existsSavedData && code != Key.RETRIEVE_LEVEL_CODE) {
            this.showAlertDialog(launchGame);
        } else {
            launchGame.run();
        }
    }

    private void showAlertDialog(Runnable toRun) {
        // =================================================================================
        // =============== Building AlertDialog ============================================
        // =================================================================================
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("You have an ongoing game. Are you sure you want to start a new game?");

        builder.setPositiveButton("Continue", (dialogInterface, i) -> toRun.run());
        builder.setNegativeButton("Go Back", (dialogInterface, i) -> {});

        // Alert Dialog 이외의 공간을 터치 했을때 Alert 팝업이 안사라지도록 함
        // 무조건 +ve, -ve, neutral 버튼을 눌러야 Alert 팝업이 사라짐
        builder.setCancelable(false);
        // =================================================================================
        // =================================================================================
        // =================================================================================

        builder.show();
    }

    private final View.OnClickListener onSettingClick = view -> {
         // Initializing the popup menu and giving the reference as current context
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, ibtn_setting);

        // Inflating popup menu from popup_menu.xml file
        popupMenu.getMenuInflater().inflate(R.menu.main_menu_setting, popupMenu.getMenu());
        popupMenu.getMenu().getItem(1).setChecked(playSound);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                menuItem.setActionView(new View(getApplicationContext()));

                String selMenu = menuItem.getTitle().toString();
                if (selMenu.equals("Statistics")) {
                    startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
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
                            MainActivity.this,
                            "Selected menu from SETTING popup menu is invalid: " + selMenu
                    );
                }
                return true;
            }
        });
        // Showing the popup menu
        popupMenu.show();
    };

    private void updateSoundSetting() {
        SharedPreferences myPref = getSharedPreferences(Key.PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor myPrefEditor = myPref.edit();
        myPrefEditor.putBoolean(Key.PREFERENCES_SOUND, this.playSound);
        myPrefEditor.apply();
    }

    private final ActivityResultCallback<ActivityResult> activityResultCallback = result -> {
        LogService.info(MainActivity.this, "Returned from game to Main Screen");
        try {
            setting();
        } catch (JSONException je) {
            LogService.error(MainActivity.this, je.getMessage(), je);
        }
    };

    private static class MyArc extends View {

        private final int color;
        private final float sweepAngle;
        private final Paint paint;
        private final RectF rectF;

        public MyArc(Context context, @ColorInt int color, float sweepAngle, RectF rectF) {
            super(context);
            this.color = color;
            this.sweepAngle = sweepAngle == 0 ? 1 : sweepAngle;
            this.rectF = rectF;
            paint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(color);

            canvas.drawArc(rectF, -90, this.sweepAngle, true, paint);
        }

    }

}
