package co.calendar.calendartrack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "dayManager",
    TABLE_DAYS = "days",
    KEY_DAY = "day",
    KEY_MONTH = "month",
    KEY_YEAR = "year",
    KEY_COLOR = "color",
    KEY_LIST = "list",
    KEY_NUM = "num";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_DAYS + "(" + KEY_DAY + " TEXT," + KEY_MONTH + " TEXT," + KEY_YEAR + " TEXT," + KEY_COLOR + " TEXT," + KEY_LIST + " TEXT," + KEY_NUM + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAYS);

        onCreate(db);
    }

    public void createDay(Day day) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_DAY, day.getDay());
        values.put(KEY_MONTH, day.getMonth());
        values.put(KEY_YEAR, day.getYear());
        values.put(KEY_COLOR, day.getColor());
        values.put(KEY_LIST, day.getList());
        values.put(KEY_NUM, day.getCalNum());

        db.insert(TABLE_DAYS, null, values);
        db.close();
    }

    public int updateDay(Day day) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_DAY, day.getDay());
        values.put(KEY_MONTH, day.getMonth());
        values.put(KEY_YEAR, day.getYear());
        values.put(KEY_COLOR, day.getColor());
        values.put(KEY_LIST, day.getList());
        values.put(KEY_NUM, day.getCalNum());

        int rowsAffected = db.update(TABLE_DAYS, values, KEY_DAY + "=? AND " + KEY_MONTH + "=? AND " + KEY_YEAR + "=?", new String[] { day.getDay(), day.getMonth(), day.getYear() });
        db.close();

        return rowsAffected;
    }

    public boolean nameExists(String d, String m, String y) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_DAYS, new String[]{KEY_DAY}, KEY_DAY + "=? AND " + KEY_MONTH + "=? AND " + KEY_YEAR + "=?", new String[]{d, m, y}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        boolean result;
        if (cursor.getCount() < 1) {
            result = false;
        } else {
            result = true;
        }
        db.close();
        cursor.close();
        return result;
    }

    public void deleteDay(Day day) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_DAYS, KEY_DAY + "=? AND " + KEY_MONTH + "=? AND " + KEY_YEAR + "=?", new String[]{day.getDay(), day.getMonth(), day.getYear()});
        db.close();
    }

    public List<Day> getAllDays(String month, String year) {
        List<Day> days = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(TABLE_DAYS,
                new String[] {KEY_DAY, KEY_MONTH, KEY_YEAR, KEY_COLOR, KEY_LIST, KEY_NUM},
                KEY_MONTH + "=? AND " + KEY_YEAR + "=?",
                new String[] { month, year },
                null, null, null, null );

        if (c.moveToFirst()) {
            do {
                days.add(new Day(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), "", c.getString(5)));
            }
            while (c.moveToNext());
        }
        c.close();
        db.close();
        return days;
    }
}