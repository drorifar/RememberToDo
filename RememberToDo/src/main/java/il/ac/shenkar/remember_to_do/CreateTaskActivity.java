package il.ac.shenkar.remember_to_do;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class manage the creation of a new task
 */
public class CreateTaskActivity extends ActionBarActivity {

    public final static String EXTRA_LIST = "il.ac.shenkar.totodo.LIST";

    // Date & Time verbs
    Dialog picker = null;
    TimePicker timePicker = null;
    DatePicker datePicker = null;
    Integer hour, minute, month, day, year;
    //boolean flag for set reminder
    boolean isReminder;
    //URL string
    String urlString = "http://mobile1-tasks-dispatcher.herokuapp.com/task/random";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task);

        //create task button listener
        final Button createNewTaskButton = (Button) findViewById(R.id.create_task_button);
        createNewTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTask(view);
            }
        });

        //set reminder button listener
        final Button setReminderButton = (Button) findViewById(R.id.set_reminder_button);
        setReminderButton.setOnClickListener(setReminderButtonListener);

        //get random task from the web
        final Button getRandomButton = (Button) findViewById(R.id.get_random_button);
        getRandomButton.setOnClickListener(getRandomButtonListener);


    }

    /**
     * Showing new dialog for date & time pick
     */
    private final View.OnClickListener setReminderButtonListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            picker = new Dialog(CreateTaskActivity.this);
            picker.setContentView(R.layout.picker_frgm);
            picker.setTitle("Select Date and Time");

            datePicker = (DatePicker)picker.findViewById(R.id.datePicker);
            timePicker = (TimePicker)picker.findViewById(R.id.timePicker);
            timePicker.setIs24HourView(true);
            Button setDateTime = (Button)picker.findViewById(R.id.set_date_time);

            setDateTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isReminder = true;
                    month = datePicker.getMonth();
                    day = datePicker.getDayOfMonth();
                    year = datePicker.getYear();
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                    picker.dismiss();
                }
            });
            picker.show();
        }
    };

    private final View.OnClickListener getRandomButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GetFromWebTask getFromWebTask = new GetFromWebTask();
            getFromWebTask.execute(urlString);
        }
    };

    /**
     * Handling new task creation
     * @param view
     */
    public void createTask(View view) {

        EditText editTextTitle = (EditText) findViewById(R.id.title);
        EditText editTextDesc = (EditText) findViewById(R.id.description);
        //get text from the view
        String title = editTextTitle.getText().toString();
        String description = editTextDesc.getText().toString();

        TaskDAO dao = TaskDAO.getInstance(this);
        String date = getDateTime();
        Long id = System.currentTimeMillis();
        //add task to DB
        dao.addTask( new Task(id, title, description, date));

        Toast.makeText(this, title + " Added", Toast.LENGTH_LONG).show();

        //user set reminder
        if(isReminder){
            setReminder(id, title, description, date);
        }
        finish();
    }

    /**
     * Set Reminder
     * @param id
     * @param title
     * @param description
     * @param date
     */
    public void setReminder(Long id, String title, String description, String date){
        //calculating time for the reminder
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        Long timeToWait;
        timeToWait = calendar.getTimeInMillis();

        List<String> tasksList = new ArrayList<String>();
        tasksList.add(id.toString());
        tasksList.add(title);
        tasksList.add(description);
        tasksList.add(date);


        //AlarmManager for invoke ReminderBroadcastReceiver
        Intent intent = new Intent("il.ac.shenkar.reminder_broadcast");
        intent.putExtra("id", id);
        intent.putStringArrayListExtra(EXTRA_LIST, (ArrayList<String>) tasksList);
        PendingIntent pendingIntent =   PendingIntent.getBroadcast(this, id.intValue(), intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeToWait, pendingIntent);
    }

    /**
     * Gets the current time and converts it to String using DateFormat
     * @return String that represent current time
     */
    public String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd/MM/yyyy, kk:mm:ss", Locale.getDefault());
        Date d = Calendar.getInstance().getTime();
        return dateFormat.format(d);
    }

    public JSONObject getRandomTask(String urls){

        String response = null;
        JSONObject jsonResponse = null;

        try{
            URL url = new URL(urls);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            InputStreamReader inReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inReader);
            StringBuilder responseBuilder = new StringBuilder();

            for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine())
            {
                responseBuilder.append(line);
            }
            response = responseBuilder.toString();

            jsonResponse = new JSONObject(response);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return jsonResponse;
    }

    private class GetFromWebTask extends AsyncTask<String, Integer, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject result =  getRandomTask(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result){
            TextView title = (TextView)findViewById(R.id.title);
            TextView description = (TextView)findViewById(R.id.description);

            String jsonTitle = null;
            String jsonDescription = null;
            try {
                jsonTitle = result.getString("topic");
                jsonDescription = result.getString("description");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            title.setText(jsonTitle);
            description.setText(jsonDescription);
        }
    }


}
