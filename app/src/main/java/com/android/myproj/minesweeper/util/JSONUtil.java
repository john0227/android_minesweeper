package com.android.myproj.minesweeper.util;

import static com.android.myproj.minesweeper.config.JSONKey.FILE_NAME;

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
        File directory = contextWrapper.getDir(contextWrapper.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, FILE_NAME);

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
        File directory = contextWrapper.getDir(contextWrapper.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, FILE_NAME);

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
        File directory = contextWrapper.getDir(contextWrapper.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, FILE_NAME);

        // Create file if it does not exist
        if (!file.exists()) {
            file.createNewFile();
            return;
        }

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(CLEAR_DATA.getBytes());
        fos.close();
    }

    public static boolean existsSavedData(ContextWrapper contextWrapper) {
        File directory = contextWrapper.getDir(contextWrapper.getFilesDir().getName(), Context.MODE_PRIVATE);
        File file =  new File(directory, FILE_NAME);

        try {
            // If file does not exist, create one and return empty JSONObject
            if (!file.exists()) {
                file.createNewFile();
                return false;
            }

            JSONObject jsonObject = new JSONObject(FileUtils.readFileToString(file, "UTF-8"));
            return jsonObject.getBoolean(JSONKey.KEY_EXISTS_SAVED_DATA);
        } catch (JSONException | IOException e) {
            return false;
        }
    }

}
