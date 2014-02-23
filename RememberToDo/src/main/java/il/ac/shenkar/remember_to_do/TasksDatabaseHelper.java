package il.ac.shenkar.remember_to_do;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * This class extends SQLiteOpenHelper
 * It's manage database creation and version management.
 */
public class TasksDatabaseHelper extends SQLiteOpenHelper {
    // All Static variables
    // Database Name
    private static final String DATABASE_NAME = "taskstable.db";
    // Database Version
    private static final int DATABASE_VERSION = 1;

    //C'tor
    public TasksDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create Table
    public void onCreate(SQLiteDatabase database) {
        TaskTable.onCreate(database);
    }

    //Upgrading Table
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        TaskTable.onUpgrade(database, oldVersion, newVersion);

    }

}
