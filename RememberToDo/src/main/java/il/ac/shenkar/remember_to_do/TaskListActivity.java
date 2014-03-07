package il.ac.shenkar.remember_to_do;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ActionMode;
import android.support.v7.app.ActionBarActivity;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

/**
 * the main_list_view activity - the tasks list view
 */
public class TaskListActivity extends ActionBarActivity {

    private Context context;
    TaskListBaseAdapter adapter = null;
    private ActionMode mActionMode;
    private TaskDAO dao;
    int selectedItemPosition;
    private ShareActionProvider mShareActionProvider;

    public TaskListActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_list_view);

        context = this;

        dao = TaskDAO.getInstance(this);

        ListView listView = (ListView) findViewById(R.id.listV_main);
        listView.setEmptyView(findViewById(R.id.emptyView));
        adapter = new TaskListBaseAdapter(this);
        listView.setAdapter(adapter);


        final ImageView addNewTaskButton = (ImageView) findViewById(R.id.add_task_button);
        addNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask(view);
            }
        });

        SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener
                (listView, new SwipeDismissListViewTouchListener.OnDismissCallback() {
                    public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions)
                        {
                            Toast.makeText(getBaseContext(), "delete", Toast.LENGTH_SHORT).show();
                            dao.deleteTask(dao.getItem(position));
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
            TaskDAO dao = TaskDAO.getInstance(context);
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
                    dao.deleteTask(dao.getItem(selectedItemPosition));
                    Toast.makeText(getBaseContext(), "deleted", Toast.LENGTH_SHORT).show();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
