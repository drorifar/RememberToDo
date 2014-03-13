package il.ac.shenkar.remember_to_do;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 *
 * This class is the main_list_view activity - the tasks list view
 * @author Dror Afargan & Ran Nahmijas
 */
public class TaskListActivity extends ActionBarActivity {

    private Context context;
    TaskListBaseAdapter adapter = null;
    private ActionMode mActionMode;

    private TaskDAO dao;
    int selectedItemPosition;
    private ShareActionProvider mShareActionProvider;
    String userName;

    private ReminderAlarmManager alarmManager;
    public enum CloudOptionsEnum {UPLOAD, DOWNLOAD}

    public TaskListActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_view);

        alarmManager = new ReminderAlarmManager(this);

        //initializing the parse cloud
        ParseObject.registerSubclass(TaskForCloud.class);
        Parse.initialize(this, "ADShhrgdbLs87kgP0vUrI9mQfwRnJ2RoNpSUevOB", "Q1fl5Hzx9dtxoO76c8IQt29VnRnzMs5hlyYHD8v3");

        context = this;

        dao = TaskDAO.getInstance(this, false);

        //list view
        ListView listView = (ListView) findViewById(R.id.listV_main);
        listView.setEmptyView(findViewById(R.id.emptyView));
        adapter = new TaskListBaseAdapter(this);
        listView.setAdapter(adapter);

        //add ic_task_board button listener
        final ImageView addNewTaskButton = (ImageView) findViewById(R.id.add_task_button);
        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask(view);
            }
        });

        //deleting ic_task_board from the view by swipe
        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener
                (listView, new SwipeDismissListViewTouchListener.OnDismissCallback() {
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions)
                        {
                            Task taskToDelete = dao.getItem(position);
                            dao.deleteTask(taskToDelete);
                            alarmManager.cancelAlarm(taskToDelete);
                            Toast.makeText(getBaseContext(), taskToDelete.getTitle() + " delete", Toast.LENGTH_SHORT).show();

                            adapter.notifyDataSetChanged();
                        }
                    }
                });
        listView.setOnTouchListener(touchListener);
        listView.setOnScrollListener(touchListener.makeScrollListener());

        // short click on one row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
                selectedItemPosition = position;
                editTask(position);
            }
        });

     // long click on one row
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onItemLongClick(AdapterView<?> p, View view, int pos, long id) {
                selectedItemPosition = pos;
                if (mActionMode != null) {
                    return false;
                }
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = startActionMode( mActionModeCallback );
                view.setSelected(true);
                return true;
            }
        });
    }

    /**
     * edit ic_task_board handling
     * @param position
     */
    public void editTask (int position){
        Bundle taskDetailsBundle = new Bundle();
        taskDetailsBundle.putInt("position",position);
        taskDetailsBundle.putLong("id",dao.getItem(position).getId());
        Intent passDataIntent=new Intent(getApplicationContext(),CreateTaskActivity.class);
        passDataIntent.putExtras(taskDetailsBundle);
        //start edit selected list item activity
        startActivity(passDataIntent);
    }

    @Override
    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    /**
     * add new ic_task_board, start CreateTaskActivity
     * @param view
     */
    public void addTask(View view) {
        startActivity(new Intent(context, CreateTaskActivity.class));
    }


    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.task_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_share:
                    //Getting the action provider associated with the menu item whose id is share
                    mShareActionProvider = (ShareActionProvider) item.getActionProvider();
                    //Setting a share intent
                    mShareActionProvider.setShareIntent(createShareIntent());
                    //mode.finish(); // Action picked, so close the CAB
                    return true;

                case R.id.action_delete:
                    //delete the list item in the selected position
                    Task taskToDelete = dao.getItem(selectedItemPosition);
                    dao.deleteTask(taskToDelete);
                    alarmManager.cancelAlarm(taskToDelete);
                    Toast.makeText(getBaseContext(), taskToDelete.getTitle() + " deleted", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    mode.finish(); // Action picked, so close the CAB
                    return true;

                case R.id.action_edit: //edit the list item in the selected position
                    editTask(selectedItemPosition);
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        /** Returns a share intent */
        private Intent createShareIntent(){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "FunToDo Share");
            intent.putExtra(Intent.EXTRA_TEXT, "\n" + dao.getItem(selectedItemPosition).getTitle());
            return intent;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_addTask:
                addTask(getCurrentFocus());
                return true;
            case R.id.action_priority:
                dao.getAllTasks(true);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.action_show_all:
                dao.getAllTasks(false);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.action_upload_to_cloud:
                userDialog(CloudOptionsEnum.UPLOAD);
                return true;
            case R.id.action_download_from_cloud:
                userDialog(CloudOptionsEnum.DOWNLOAD);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * open the image picker dialog
     */
    public void userDialog(final CloudOptionsEnum cloudOption) {
        final Dialog picker = new Dialog(TaskListActivity.this);
        picker.setContentView(R.layout.user_details_layout);
        picker.setTitle("Enter User Details");

        final EditText user_name = (EditText)picker.findViewById(R.id.user_name);
        user_name.setText(userName);

        final EditText password = (EditText)picker.findViewById(R.id.password);

        final Button setUserName = (Button)picker.findViewById(R.id.set);

        setUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = user_name.getText().toString();
                String pass = password.getText().toString();
                picker.dismiss();
                if (userName != null || !userName.isEmpty()) {
                    if (cloudOption == CloudOptionsEnum.UPLOAD){
                        uploadToCloud(userName, pass);
                    }
                    else downloadFromCloud(userName, pass);
                }
            }
        });
        picker.show();
    }

    /**
     * upload all the user tasks to the cloud
     */
    private void uploadToCloud(String userName, String password) {
        // delete all the user current tasks before uploading
        deleteAllFromCloud(userName, password);
        //get all the tasks from DB
        dao.getAllTasks(false);

        if (!dao.isEmpty()){
            for (int i = 0; i < dao.getCount(); i++) {
                if (dao.getItem(i) != null){
                    TaskForCloud todoItem = new TaskForCloud(dao.getItem(i), userName, password);
                    todoItem.saveInBackground();
                }
            }
        }
        Toast.makeText(getBaseContext(), "Save On Cloud", Toast.LENGTH_SHORT).show();
    }

    /**
     * download all the user tasks from the cloud
     */
    private void downloadFromCloud(String userName, String password) {
        // Define the class we would like to query
        ParseQuery<TaskForCloud> query = ParseQuery.getQuery(TaskForCloud.class);
        // Define our query conditions
        query.whereEqualTo("user_name", userName);
        query.whereEqualTo("password", password);
        // Execute the find asynchronously
        query.findInBackground(new FindCallback<TaskForCloud>() {
            public void done(List<TaskForCloud> itemList, ParseException e) {
                if (e == null && itemList != null && itemList.size() > 0) {
                    // Access the array of results
                    dao.deleteAllTasks();
                    for (TaskForCloud taskFromCloud : itemList){
                        Task newTask = new Task(taskFromCloud);
                        dao.addTask(newTask);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getBaseContext(),"User Has No Tasks", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * delete all the user tasks from the cloud
     */
    private void deleteAllFromCloud(String userName, String password){
        // Define the class we would like to query
        ParseQuery<TaskForCloud> query = ParseQuery.getQuery(TaskForCloud.class);
        // Define our query conditions
        query.whereEqualTo("user_name", userName);
        query.whereEqualTo("password", password);
        // Execute the find asynchronously
        query.findInBackground(new FindCallback<TaskForCloud>() {
            public void done(List<TaskForCloud> itemList, ParseException e) {
                if (e == null && itemList != null && itemList.size() > 0) {
                    // Access the array of results
                    for (TaskForCloud taskFromCloud : itemList){
                        taskFromCloud.deleteInBackground();
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

}
