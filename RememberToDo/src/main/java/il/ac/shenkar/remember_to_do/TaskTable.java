package il.ac.shenkar.remember_to_do;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class represent table of tasks in the DB
 * it's manage database creation
 * @author Dror Afargan & Ran Nahmijas
 */
public class TaskTable {
    // Task Table name
    public static final String TABLE_TASKS = "tasks";

    // Task Table Columns names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_PRIORITY = "isPriority";

    private static final  String CREATE_TASKS_TABLE = " CREATE TABLE " + TABLE_TASKS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TITLE + " TEXT,"
                + COLUMN_DATE + " TEXT,"  + COLUMN_LOCATION + " TEXT,"
                + COLUMN_NOTES + " TEXT," + COLUMN_PRIORITY + " TEXT"
                + ")";


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

