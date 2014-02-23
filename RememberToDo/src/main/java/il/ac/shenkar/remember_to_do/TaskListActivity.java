package il.ac.shenkar.remember_to_do;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.ListView;

public class TaskListActivity extends ActionBarActivity {

    private Context context;
    TaskListBaseAdapter adapter = null;

    public TaskListActivity() {}


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = this;

        TaskDAO dao = TaskDAO.getInstance(this);

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
    }

    @Override
    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public void addTask(View view) {
        startActivity(new Intent(context, CreateTaskActivity.class));
    }

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
