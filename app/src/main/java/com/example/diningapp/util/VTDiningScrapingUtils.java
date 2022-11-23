package com.example.diningapp.util;

import android.content.Context;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * The util class providing vt dinning website scraping functionality
 */
public class VTDiningScrapingUtils {

    public static List<DiningHallHour> readingHourFromCsvFile (String fileName, String[] header) throws IOException {
        Reader in = new FileReader(fileName + ".csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(header).parse(in);
        List<DiningHallHour> list = new ArrayList<>();
        for (CSVRecord record : records) {
            list.add(
                DiningHallHour.DiningHallHourBuilder.aDiningHallHour()
                        .date( record.get("Date"))
                        .diningHall(record.get("Dining Hall"))
                        .hours( record.get("Hours"))
                        .build()
            );
        }

        return list;
    }

    public static List<FoodItem> readingMenuFromCsvFile(String fileName, String[] header) throws IOException {
        Reader in = new FileReader(fileName + ".csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(header).parse(in);
        List<FoodItem> list = new ArrayList<>();
        for (CSVRecord record : records) {
            list.add(
                    FoodItem.FoodItemBuilder.aFoodItem()
                            .name(record.get("Food Item Name"))
                            .diningHall(record.get("Dinning Hall"))
                            .type(record.get("Type"))
                            .amount(record.get("Amount"))
                            .description(record.get("Description"))
                            .label(record.get("Label"))
                            .otherInfo(record.get("Other Info"))
                            .build()
            );
        }

        return list;
    }

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
