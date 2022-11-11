package com.example.diningapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.diningapp.util.FoodItem;

import java.util.ArrayList;
import java.util.List;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "dininghalldb";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME      = "fooditem";
    private static final String ID_COL          = "id";
    private static final String NAME_COL        = "name";
    private static final String LABEL_COL       = "label";
    private static final String DESCRIPTION_COL = "description";
    private static final String DINING_HALL_COL = "dininghall";
    private static final String TYPE_COL        = "type";
    private static final String OTHER_INFO_COL  = "otherinfo";
    private static final String AMOUNT_COL      = "amount";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + LABEL_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT,"
                + DINING_HALL_COL + " TEXT,"
                + OTHER_INFO_COL + " TEXT,"
                + TYPE_COL + " TEXT,"
                + AMOUNT_COL + " TEXT)";
        db.execSQL(query);
    }

    public void init(List<FoodItem> menuList) {
        if (getAllFoodItem().size() == 0) {
            for (FoodItem foodItem: menuList) {
                addFoodItem(
                        foodItem.getName(),
                        foodItem.getLabel(),
                        foodItem.getDescription(),
                        foodItem.getAmount(),
                        foodItem.getType(),
                        foodItem.getDiningHall(),
                        foodItem.getOtherInfo()
                   );
            }
        }
    }

    public void addFoodItem (String foodName,  String foodLabel, String foodDescription,
                             String foodAmount, String foodType, String foodDiningHall,
                             String foodOtherInfo
    ) {

        SQLiteDatabase db    = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME_COL,        foodName);
        values.put(LABEL_COL,       foodLabel);
        values.put(DESCRIPTION_COL, foodDescription);
        values.put(AMOUNT_COL,      foodAmount);
        values.put(TYPE_COL,        foodType);
        values.put(DINING_HALL_COL, foodDiningHall);
        values.put(OTHER_INFO_COL,  foodOtherInfo);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * The method retrieve all food item from the database
     * @return
     */
    public ArrayList<FoodItem> getAllFoodItem() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Creating a cursor with query to read data from database.
        Cursor cursorfooditems = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ArrayList<FoodItem> foodItemArrayList = new ArrayList<>();

        if (cursorfooditems.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                foodItemArrayList.add(new
                        FoodItem.FoodItemBuilder()
                            .name(cursorfooditems.getString(1))
                            .label(cursorfooditems.getString(2))
                            .description(cursorfooditems.getString(3))
                            .amount(cursorfooditems.getString(4))
                            .type(cursorfooditems.getString(5))
                            .diningHall(cursorfooditems.getString(6))
                            .otherInfo(cursorfooditems.getString(7))
                            .build()
                        );
            } while (cursorfooditems.moveToNext());
        }
        cursorfooditems.close();
        return foodItemArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}