package il.ac.shenkar.remember_to_do;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class represent table of tasks in the DB
 * t's manage database creation
 */
public class TaskTable {
    // Task Table name
    public static final String TABLE_TASKS = "tasks";
    // Task Table Columns names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";

    // Database creation SQL statement
    private static final String CREATE_TASKS_TABLE = "CREATE TABLE "
            + TABLE_TASKS
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_DATE + " DATETIME"
            + ");";


    // Create Table
    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TASKS_TABLE);
    }

    //Upgrading Table
    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.i(TABLE_TASKS, "Upgrading il.ac.shenkar.ToToDo from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(database);
    }
}

