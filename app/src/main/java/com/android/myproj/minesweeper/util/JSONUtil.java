package com.android.myproj.minesweeper.util;

import static com.android.myproj.minesweeper.config.JSONKey.FILE_SAVED_DATA;

import android.content.Context;
import android.content.ContextWrapper;

import com.android.myproj.minesweeper.config.JSONKey;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class JSONUtil {

    private static final String CLEAR_DATA = "{ \"exists_saved_data\": false }";

    public static void writeToJSONFile(ContextWrapper contextWrapper, String... contents) throws IOException {
        File file = getFileSavedData(contextWrapper);

        // Create file if it does not exist
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file);
        for (String content : contents) {
            fos.write((content + "\n").getBytes());
        }
        fos.close();
    }

    public static JSONObject readJSONFile(ContextWrapper contextWrapper) {
        File file = getFileSavedData(contextWrapper);

        try {
            // If file does not exist, create one and return empty JSONObject
            if (!file.exists()) {
                file.createNewFile();
                return new JSONObject();
            }

            return new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
        } catch (JSONException | IOException e) {
            return new JSONObject();
        }
    }

    public static void clearSavedData(ContextWrapper contextWrapper) throws IOException {
        File file = getFileSavedData(contextWrapper);

        // Create file if it does not exist
        if (!file.exists()) {
            file.createNewFile();
            return;
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(CLEAR_DATA.getBytes());
        fos.close();
    }

    public static void incrementKeyValue(ContextWrapper contextWrapper, String key, int inc) throws IOException, JSONException {
        File file = getFileSavedData(contextWrapper);

        // Create file if it does not exist
        if (!file.exists()) {
            file.createNewFile();
        }

        JSONObject jsonObject = readJSONFile(contextWrapper);
        jsonObject.put(key, jsonObject.getInt(key) + inc);
    }

    public static boolean existsSavedGame(ContextWrapper contextWrapper) {
        File file = getFileSavedData(contextWrapper);

        try {
            // If file does not exist, create one and return empty JSONObject
            if (!file.exists()) {
                file.createNewFile();
                return false;
            }

            JSONObject jsonObject = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            return jsonObject.getBoolean(JSONKey.KEY_EXISTS_SAVED_GAME);
        } catch (JSONException | IOException e) {
            return false;
        }
    }

    public static void createDefaultStatIfNone(ContextWrapper contextWrapper) throws JSONException, IOException {
        // Create default statistics JSONObject
        JSONObject defaultStat = new JSONObject();
        // Create default stat for each level if there is none
        for (String keySavedStat : JSONKey.KEYS_SAVED_STAT) {
            createDefaultStatIfNone(contextWrapper, keySavedStat);
        }
        // Write JSONObject to save file
        writeToJSONFile(contextWrapper, defaultStat.toString());
    }

    public static void createDefaultStatIfNone(ContextWrapper contextWrapper, String keySavedStat) throws JSONException, IOException {
        // Create default statistics JSONObject
        JSONObject defaultStat = new JSONObject();
        // Create default stat for given level if there is none
        if (!existsSavedStat(contextWrapper, keySavedStat)) {
            defaultStat.put(keySavedStat, true);
            for (String key : getAllKeysForLevel(keySavedStat)) {
                defaultStat.put(key, 0);
            }
        }
        writeToJSONFile(contextWrapper, defaultStat.toString());
    }

    private static boolean existsSavedStat(ContextWrapper contextWrapper, String keySavedStat) {
        File file = getFileSavedData(contextWrapper);

        try {
            // If file does not exist, create one and return empty JSONObject
            if (!file.exists()) {
                file.createNewFile();
                return false;
            }
            JSONObject jsonObject = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));

            return jsonObject.getBoolean(keySavedStat);
        } catch (JSONException | IOException e) {
            return false;
        }
    }

    public static String[] getAllKeysForLevel(String keySavedStat) {
        return switch (keySavedStat) {
            case JSONKey.KEY_EXISTS_SAVED_EASY_STAT -> JSONKey.KEYS_EASY_STAT;
            case JSONKey.KEY_EXISTS_SAVED_INTERMEDIATE_STAT ->  JSONKey.KEYS_INTERMEDIATE_STAT;
            case JSONKey.KEY_EXISTS_SAVED_EXPERT_STAT -> JSONKey.KEYS_EXPERT_STAT;
            default -> throw new RuntimeException("Invalid Saved Stat key");
        };
    }

    private static File getFileSavedData(ContextWrapper contextWrapper) {
        return new File(contextWrapper.getDir(contextWrapper.getFilesDir().getName(), Context.MODE_PRIVATE), FILE_SAVED_DATA);
    }


}
