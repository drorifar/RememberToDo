package il.ac.shenkar.remember_to_do;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Administrator on 02/03/14.
 */
public class EditTaskActivity extends ActionBarActivity {

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_view);

        //create task button listener
        final Button createNewTaskButton = (Button) findViewById(R.id.create_task_button);
        createNewTaskButton.setText("Update");
        createNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTask(view);
            }
        });

        Intent receiveDataIntent = getIntent();
        Bundle receivedDataBundle = receiveDataIntent.getExtras();
        String receivedName = receivedDataBundle.getString("name");
        String receivedDescription = receivedDataBundle.getString("description");
        int taskPosition = receivedDataBundle.getInt("position");
        long taskId = receivedDataBundle.getLong("id");

    }

    private void editTask(View view) {

    }


}
