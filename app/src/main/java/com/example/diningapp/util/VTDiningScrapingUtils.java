package com.example.diningapp.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * The util class providing vt dinning website scraping functionality
 */
public class VTDiningScrapingUtils {
    /**
     * The helper function reading json file from assets
     * @param context
     * @param fileName the name of json file in assets
     * @return a string of file
     */
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
