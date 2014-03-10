package il.ac.shenkar.remember_to_do;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * This class manage the creation of a new task
 */
public class CreateTaskActivity extends ActionBarActivity {

    public final static String EXTRA_LIST = "il.ac.shenkar.totodo.LIST";

    //onActivityResult Consts
    private static final int CAMERA_PICTURE_REQUEST = 1;
    private static final int GALLERY_PICTURE_REQUEST = 2;
    private static final int VR_REQUEST = 3;
    private static final int LOCATION_ACTIVITY_REQUEST = 4;

    boolean isEdit = false;
    int taskPosition;
    long taskId;

    // Date & Time verbs
    Dialog picker = null;
    TimePicker timePicker = null;
    DatePicker datePicker = null;
    Calendar calendar;
    String stringTimeReminder = "";

    //location verb
    String location = "";

    //boolean flag for set reminder
    boolean isReminder;

    //add notes verb
    String notes = "";;

    //is priority flag
    boolean isPriority = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_view);

        //lock the screen in portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Intent receiveDataIntent = getIntent();
        Bundle receivedDataBundle = receiveDataIntent.getExtras();

        //checks if its a new task or existing task
        if (receivedDataBundle != null)  {
            isEdit = true;
        }

        //set reminder button listener
        final ImageView setReminderButton = (ImageView) findViewById(R.id.add_clock_reminder);
        setReminderButton.setOnClickListener(setReminderButtonListener);

        //set image button listener
        final ImageView setImageButton = (ImageView) findViewById(R.id.add_notes_button);
        setImageButton.setOnClickListener(setNotesButtonListener);

        //set location button listener
        final ImageView setLocationButton = (ImageView) findViewById(R.id.location_reminder_button);
        setLocationButton.setOnClickListener(setLocationButtonListener);

        //set voice button listener
        final ImageView setVoiceButton = (ImageView) findViewById(R.id.add_voice_button);
        setVoiceButton.setOnClickListener(setSpeechButtonListener);

        //set priority button listener
        final ImageView setPriorityButton = (ImageView) findViewById(R.id.priority_button);
        setPriorityButton.setOnClickListener(setPriorityButtonListener);

        //if it is an existing task set its details
        if (isEdit){
            setExistTaskDetail(receivedDataBundle);
        }

    }

    /**
     * Showing new dialog for location
     */
    private final View.OnClickListener setLocationButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Bundle taskDetailsBundle = new Bundle();
            taskDetailsBundle.putString("location", location);
            Intent passDataIntent=(new Intent(CreateTaskActivity.this, LocationActivity.class));
            passDataIntent.putExtras(taskDetailsBundle);
            //start edit selected list item activity
            startActivityForResult(passDataIntent, LOCATION_ACTIVITY_REQUEST);
        }
    };

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
            Button clearDateTime = (Button)picker.findViewById(R.id.clear);

            setDateTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isReminder = true;
                    calendar = Calendar.getInstance();
                    calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
                            timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                    stringTimeReminder = Utilities.getDateTime(calendar);
                    picker.dismiss();
                    final ImageView setReminderButton = (ImageView) findViewById(R.id.add_clock_reminder);
                    setReminderButton.setImageResource(R.drawable.ic_alarm);
                    final TextView setReminderTxt = (TextView) findViewById(R.id.clock_reminder_txt);
                    setReminderTxt.setText(stringTimeReminder);
                }
            });
            clearDateTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isReminder = false;
                    stringTimeReminder = "";
                    picker.dismiss();
                    final ImageView setReminderButton = (ImageView) findViewById(R.id.add_clock_reminder);
                    setReminderButton.setImageResource(R.drawable.ic_alarm_opa);
                    final TextView setReminderTxt = (TextView) findViewById(R.id.clock_reminder_txt);
                    setReminderTxt.setText(stringTimeReminder);
                }
            });
            picker.show();
        }
    };

    /**
     * Showing new dialog for add notes
     */
    private final View.OnClickListener setNotesButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Notes_Dialog();
        }
    };

    /**
     * Showing new dialog for add pic
     */
    private final View.OnClickListener setSpeechButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            //find out whether speech recognition is supported
            PackageManager packManager = getPackageManager();
            List<ResolveInfo> intActivities = packManager.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
            if (intActivities.size() != 0) {
                listenToSpeech();
            }
        }
    };

    /**
     * change the priority
     */
    private final View.OnClickListener setPriorityButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            isPriority = !isPriority;
            final ImageView priorityButton = (ImageView) findViewById(R.id.priority_button);
            if (isPriority)
            {
                priorityButton.setImageResource(R.drawable.ic_important);
            }
            else priorityButton.setImageResource(R.drawable.ic_important_opa);
        }
    };

    /**
     * set existing task  details
     * @param receivedDataBundle - the bundle send from the last activity - include the task position
     */
    private void setExistTaskDetail(Bundle receivedDataBundle) {

        final TaskDAO dao = TaskDAO.getInstance(this, false);
        taskPosition = receivedDataBundle.getInt("position");
        //get the existing task by its position
        Task existTask = dao.getItem(taskPosition);

        final EditText titleText = (EditText) findViewById(R.id.title);
        titleText.setText(existTask.getTitle());

        if (existTask.getDate()== null || existTask.getDate().isEmpty()) {
            final ImageView setReminderButton = (ImageView) findViewById(R.id.add_clock_reminder);
            setReminderButton.setImageResource(R.drawable.ic_alarm_opa);
        }
        else {
            final ImageView setReminderButton = (ImageView) findViewById(R.id.add_clock_reminder);
            setReminderButton.setImageResource(R.drawable.ic_alarm);
            final TextView setReminderTxt = (TextView) findViewById(R.id.clock_reminder_txt);
            setReminderTxt.setText(existTask.getDate());
            stringTimeReminder = existTask.getDate();
        }

        if (existTask.getLocation()== null || existTask.getLocation().isEmpty()) {
            final ImageView setLocationButton = (ImageView) findViewById(R.id.location_reminder_button);
            setLocationButton.setImageResource(R.drawable.ic_location_opa);
        }
        else {
            location = existTask.getLocation();
            final ImageView setLocationButton = (ImageView) findViewById(R.id.location_reminder_button);
            setLocationButton.setImageResource(R.drawable.ic_location);
            final TextView setLocationTxt = (TextView) findViewById(R.id.location_reminder_txt);
            setLocationTxt.setText(location);
        }

        if(existTask.isPriority()) {
            final ImageView PriorityButton = (ImageView) findViewById(R.id.priority_button);
            PriorityButton.setImageResource(R.drawable.ic_important);
            isPriority = true;
        }

        if (existTask.getNotes()!= null && !existTask.getNotes().isEmpty()) {
            final ImageView NotesButton = (ImageView) findViewById(R.id.add_notes_button);
            NotesButton.setImageResource(R.drawable.ic_note);
            notes = existTask.getNotes();
        }

        taskId = receivedDataBundle.getLong("id");
    }

    /**
     * Handling new task creation
     * @param view
     */
    public void createTask(View view) {
        TaskDAO dao = TaskDAO.getInstance(this, false);

        //get text from the view
        EditText editTextTitle = (EditText) findViewById(R.id.title);
        String title = editTextTitle.getText().toString();

        Long id = System.currentTimeMillis();

        //add task to DB
        dao.addTask( new Task(id, title, stringTimeReminder,location,  isPriority, notes));

        Toast.makeText(this, title + " Added", Toast.LENGTH_LONG).show();

        //user set reminder
        if(isReminder){
            setReminder(id, title, stringTimeReminder);
        }
        finish();
    }

    /**
     * update existing task - when push "update" on the action bar
     */
    private void updateTask() {
        TaskDAO dao = TaskDAO.getInstance(this, false);

        //get title from the view
        EditText editTextTitle = (EditText) findViewById(R.id.title);
        String title = "";
        if (editTextTitle.getText() != null)
            title = editTextTitle.getText().toString();

        Long id = System.currentTimeMillis();

        Task updatedTask = new Task(id, title, stringTimeReminder,location, isPriority, notes);
        updatedTask.setId(taskId);

        //update task in DB
        dao.updateTask(updatedTask, taskPosition);
        Toast.makeText(this, title + " updated", Toast.LENGTH_LONG).show();

        //user set reminder
        if(isReminder){
            setReminder(id, title, stringTimeReminder);
        }
        finish();
    }


    /**
     * onActivityResult function
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == VR_REQUEST ) {
                //store the returned word list as an ArrayList
                ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (suggestedWords != null && !suggestedWords.isEmpty())
                {
                    final EditText titleText = (EditText) findViewById(R.id.title);
                    titleText.setText(suggestedWords.get(0));
                }
            }
            else if (requestCode == LOCATION_ACTIVITY_REQUEST ) {
                //store the returned word list as an ArrayList
                Bundle bundle = data.getExtras();
                if (bundle != null && bundle.containsKey("location"))
                    location = bundle.getString("location");
                final ImageView setLocationButton = (ImageView) findViewById(R.id.location_reminder_button);
                final TextView setLocationTxt = (TextView) findViewById(R.id.location_reminder_txt);
                if (location != null && !location.isEmpty()){
                    setLocationButton.setImageResource(R.drawable.ic_location);
                    setLocationTxt.setText(location);
                }
                else {
                    setLocationButton.setImageResource(R.drawable.ic_location_opa);
                    setLocationTxt.setText("");
                }
            }
        }
    }

    /**
     * open the image picker dialog
     */
    public void Notes_Dialog() {
        picker = new Dialog(CreateTaskActivity.this);
        picker.setContentView(R.layout.description_layout);
        picker.setTitle("Enter Task Description");

        final EditText description = (EditText)picker.findViewById(R.id.description);
        description.setText(notes);

        final Button setDescription = (Button)picker.findViewById(R.id.set_descriptiom);
        final Button clearDescription = (Button)picker.findViewById(R.id.clear);

        setDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notes = description.getText().toString();
                picker.dismiss();
                final ImageView setNotesButton = (ImageView) findViewById(R.id.add_notes_button);
                if (notes != null && !notes.isEmpty()){
                    setNotesButton.setImageResource(R.drawable.ic_note);
                }
                else {
                    setNotesButton.setImageResource(R.drawable.ic_note_opa);
                }
            }
        });
        clearDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notes = "";
                picker.dismiss();
                final ImageView setNotesButton = (ImageView) findViewById(R.id.add_notes_button);
                setNotesButton.setImageResource(R.drawable.ic_note_opa);
            }
        });
        picker.show();
    }

    /**
     * Instruct the app to listen for user speech input
     */
    private void listenToSpeech() {

        //start the speech recognition intent passing required data
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //indicate package
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        //message to display while listening
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a task!");
        //set speech model
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //specify number of results to retrieve
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);

        //start listening
        startActivityForResult(listenIntent, VR_REQUEST);
    }


    /**
     * Set he Time Reminder
     * @param id
     * @param title
     * @param date
     */
    public void setReminder(Long id, String title,  String date){
        //calculating time for the reminder
        long timeToWait = calendar.getTimeInMillis();

        List<String> tasksList = new ArrayList<String>();
        tasksList.add(id.toString());
        tasksList.add(title);
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
     * the option menu onCreate method
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        if (isEdit)
        {
            setTitle(R.string.title_update);
            inflater.inflate(R.menu.edit_menu, menu);
        }
        else
        {
            inflater.inflate(R.menu.add_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * the option menu onOptionsItemSelected method
     * @param item- the menu item that was selected
     * @return - true if correct
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_createTask:
                createTask(getCurrentFocus());
                return true;
            case R.id.action_editTask:
                updateTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}