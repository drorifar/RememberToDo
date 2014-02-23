package il.ac.shenkar.remember_to_do;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implement the interface ITaskDAO.
 * It's designed in DAO design pattern
 */
public class TaskDAO implements ITaskDAO  {

    //instance for the Singleton implementation
    private static TaskDAO instance = null;
    //Tasks list
    private List<Task> taskList;
    private Context context;

    //Database fields
    private SQLiteDatabase database;
    private TasksDatabaseHelper dbHelper;

    //Singleton C'tor
    private TaskDAO(Context context){
        this.context = context;

        dbHelper = new TasksDatabaseHelper(context);

        taskList = new ArrayList<Task>();
        taskList = getAllTasks();
    }

    /**
     * Singleton getInstance()
     * @param context
     * @return instance
     */
    public synchronized static TaskDAO getInstance(Context context){
        if(instance == null)
        {
            instance = new TaskDAO(context);
        }
        return  instance;
    }

    /**
     * accepts new TaskDetails object and adding him into the DB
     * @param taskObj (task)
     */
    @Override
    public void addTask(Task taskObj) {
        //add new task object to Tasks list
        taskList.add(taskObj);

        //add to Database
        database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskTable.COLUMN_TITLE, taskObj.getTitle());
        values.put(TaskTable.COLUMN_DESCRIPTION, taskObj.getDescription());
        values.put(TaskTable.COLUMN_DATE, taskObj.getDate());

        //insert into DB
        long id = database.insert(TaskTable.TABLE_TASKS, null, values);

        database.close();
        System.out.println("----------  " + taskObj.getId() + " \" " + taskObj.getTitle() + " \"" + " ADDED   ----------");
    }

    /**
     * Accepts TaskDetails object and removes this task from the DB
     * @param taskObj (task)
     */
    @Override
    public void deleteTask(Task taskObj) {
        //add new task object to Tasks list
        taskList.remove(taskObj);

        database = dbHelper.getWritableDatabase();
        //delete from DB
        database.delete(TaskTable.TABLE_TASKS, TaskTable.COLUMN_ID + "=?",
                new String[]{String.valueOf(taskObj.getId())});

        database.close();
        System.out.println("----------  " + taskObj.getId() + " \" " + taskObj.getTitle() + " \"" + " DELETED   ----------");
    }


    /**
     *
     *  @return ArrayList<TaskDetails> of all tasks
     */
    @Override
    public ArrayList<Task> getAllTasks() {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TaskTable.TABLE_TASKS;

        database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Task taskDetails = new Task();
                taskDetails.setId(Long.parseLong(cursor.getString(0)));
                taskDetails.setTitle(cursor.getString(1));
                taskDetails.setDescription(cursor.getString(2));
                taskDetails.setDate(cursor.getString(3));

                // Adding contact to list
                taskList.add(taskDetails);
            } while (cursor.moveToNext());
        }

        database.close();
        // return contact list
        return (ArrayList<Task>) taskList;
    }

    //Returns the number of elements in the list
    public int getCount(){
        return taskList.size();
    }

    //Returns whether this list contains no elements.
    public boolean isEmpty(){
        return taskList.isEmpty();
    }

    //get items from new to old
    public Task getItem(int i){
        return taskList.get(taskList.size()-i-1);
    }
}
