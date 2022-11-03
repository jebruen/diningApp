package com.example.diningapp.database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.diningapp.util.FoodItem;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "dininghalldb";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_NAME = "fooditem";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for our course name column
    private static final String NAME_COL = "name";

    // below variable id for our course duration column.
    private static final String LABEL_COL = "label";

    // below variable for our course description column.
    private static final String DESCRIPTION_COL = "description";

    // below variable for our course description column.
    private static final String DINING_HALL_COL = "dininghall";
    private static final String TYPE_COL = "type";

    private static final String OTHER_INFO_COL = "otherinfo";
    // below variable is for our course tracks column.
    private static final String AMOUNT_COL = "amount";


    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + LABEL_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT,"
                + DINING_HALL_COL + " TEXT,"
                + OTHER_INFO_COL + " TEXT,"
                + TYPE_COL + " TEXT,"
                + AMOUNT_COL + " TEXT)";

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addFoodItem
    (String foodName,
     String foodLabel,
     String foodDescription,
     String foodAmount,
     String foodType,
     String foodDiningHall,
     String foodOtherInfo
    ) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();

        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
        values.put(NAME_COL, foodName);
        values.put(LABEL_COL, foodLabel);
        values.put(DESCRIPTION_COL, foodDescription);
        values.put(AMOUNT_COL, foodAmount);
        values.put(TYPE_COL, foodType);
        values.put(DINING_HALL_COL, foodDiningHall);
        values.put(OTHER_INFO_COL, foodOtherInfo);

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    // we have created a new method for reading all the courses.
    public ArrayList<FoodItem> getAllFoodItem() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<FoodItem> foodItemArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorCourses.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                foodItemArrayList.add(new
                        FoodItem.FoodItemBuilder()
                        .name(cursorCourses.getString(1))
                        .label(cursorCourses.getString(2))
                        .type(cursorCourses.getString(3))
                        .description(cursorCourses.getString(4))
                        .diningHall(cursorCourses.getString(5))
                        .otherInfo(cursorCourses.getString(6))
                        .amount(cursorCourses.getString(7))
                                .build()
                        );
            } while (cursorCourses.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCourses.close();
        return foodItemArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
