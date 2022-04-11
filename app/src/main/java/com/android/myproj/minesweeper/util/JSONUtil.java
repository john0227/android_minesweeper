package com.android.myproj.minesweeper.util;

import static com.android.myproj.minesweeper.config.JSONKey.FILE_SAVED_DATA;

import android.content.Context;
import android.content.ContextWrapper;

import com.android.myproj.minesweeper.config.JSONKey;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONUtil {

    public static void writeToJSONFile(ContextWrapper contextWrapper, JSONObject dataToSave) throws IOException {
        File file = getFileSavedData(contextWrapper);

        // Create file if it does not exist
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(dataToSave.toString().getBytes());
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

    public static Object readKeyFromFile(ContextWrapper contextWrapper, String key) {
        JSONObject savedStat = readJSONFile(contextWrapper);
        Object valueRead;
        try {
            valueRead = savedStat.get(key);
        } catch (JSONException je) {
            valueRead = null;
        }
        return valueRead;
    }

    public static List<Object> readKeysFromFile(ContextWrapper contextWrapper, String... keys) {
        JSONObject savedStat = readJSONFile(contextWrapper);
        List<Object> valuesRead = new ArrayList<>();
        for (String key : keys) {
            try {
                valuesRead.add(savedStat.get(key));
            } catch (JSONException je) {
                valuesRead.add(null);
            }
        }
        return valuesRead;
    }

    public static void clearSavedGame(ContextWrapper contextWrapper) throws JSONException, IOException {
        JSONObject savedGame = readJSONFile(contextWrapper);
        savedGame.put(JSONKey.KEY_EXISTS_SAVED_GAME, false);
        writeToJSONFile(contextWrapper, savedGame);
    }

    public static boolean existsSavedGame(ContextWrapper contextWrapper) {
        try {
            JSONObject jsonObject = readJSONFile(contextWrapper);
            return jsonObject.getBoolean(JSONKey.KEY_EXISTS_SAVED_GAME);
        } catch (JSONException e) {
            return false;
        }
    }

    public static void createDefaultStatIfNone(ContextWrapper contextWrapper) throws JSONException, IOException {
        // Create default stat for each level if there is none
        for (String keySavedStat : JSONKey.KEYS_SAVED_STAT) {
            createDefaultStatIfNone(contextWrapper, keySavedStat);
        }
    }

    public static void createDefaultStatIfNone(ContextWrapper contextWrapper, String keySavedStat) throws JSONException, IOException {
        // Create default statistics JSONObject
        JSONObject defaultStat = readJSONFile(contextWrapper);
        // Create default stat for given level if there is none
        if (!existsSavedStat(contextWrapper, keySavedStat)) {
            defaultStat.put(keySavedStat, true);
            for (String key : getAllKeysForLevel(keySavedStat)) {
                defaultStat.put(key, 0);
            }
            writeToJSONFile(contextWrapper, defaultStat);
        }
    }

    public static void createDefaultStat(ContextWrapper contextWrapper, String keySavedStat) throws JSONException, IOException {
        // Create default statistics JSONObject
        JSONObject defaultStat = readJSONFile(contextWrapper);
        // Create default stat for given level
        defaultStat.put(keySavedStat, true);
        for (String key : getAllKeysForLevel(keySavedStat)) {
            defaultStat.put(key, 0);
        }
        writeToJSONFile(contextWrapper, defaultStat);
    }

    public static boolean existsSavedStat(ContextWrapper contextWrapper, String keySavedStat) {
        try {
            JSONObject jsonObject = readJSONFile(contextWrapper);
            return jsonObject.getBoolean(keySavedStat);
        } catch (JSONException e) {
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

    public static boolean existsSavedHistory(ContextWrapper contextWrapper) {
        boolean existsSavedHistory = true;
        for (String keySavedHistory : JSONKey.KEYS_SAVED_HISTORY) {
            existsSavedHistory = existsSavedHistory && existsSavedHistory(contextWrapper, keySavedHistory);
        }
        return existsSavedHistory;
    }

    public static boolean existsSavedHistory(ContextWrapper contextWrapper, String keySavedHistory) {
        try {
            JSONObject jsonObject = readJSONFile(contextWrapper);
            return jsonObject.getBoolean(keySavedHistory);
        } catch (JSONException e) {
            return false;
        }
    }

    public static void createDefaultHistoryIfNone(ContextWrapper contextWrapper) throws JSONException, IOException {
        // Create default history for each level if there is none
        for (String keySavedHistory : JSONKey.KEYS_SAVED_HISTORY) {
            createDefaultHistoryIfNone(contextWrapper, keySavedHistory);
        }
    }

    public static void createDefaultHistoryIfNone(ContextWrapper contextWrapper, String keySavedHistory)
            throws JSONException, IOException {
        // Create default history for given level if there is none
        if (!existsSavedHistory(contextWrapper, keySavedHistory)) {
            // Create default statistics JSONObject
            JSONObject defaultStat = readJSONFile(contextWrapper);
            defaultStat.put(keySavedHistory, true);
            defaultStat.put(getSavedHistoryArrayKey(keySavedHistory), new JSONArray());
            writeToJSONFile(contextWrapper, defaultStat);
        }
    }

    private static String getSavedHistoryArrayKey(String keySavedHistory) {
        return switch (keySavedHistory) {
            case JSONKey.KEY_EXISTS_EASY_SAVED_HISTORY -> JSONKey.KEY_EASY_SAVED_HISTORY_ARRAY;
            case JSONKey.KEY_EXISTS_INTERMEDIATE_SAVED_HISTORY -> JSONKey.KEY_INTERMEDIATE_SAVED_HISTORY_ARRAY;
            case JSONKey.KEY_EXISTS_EXPERT_SAVED_HISTORY -> JSONKey.KEY_EXPERT_SAVED_HISTORY_ARRAY;
            default -> throw new RuntimeException("Invalid JSONKey for saved history");
        };
    }

    private static File getFileSavedData(ContextWrapper contextWrapper) {
        return new File(contextWrapper.getDir(contextWrapper.getFilesDir().getName(), Context.MODE_PRIVATE), FILE_SAVED_DATA);
    }

}
