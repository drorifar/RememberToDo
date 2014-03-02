package il.ac.shenkar.remember_to_do;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


/**
 * This class manage the creation of a new task
 */
public class CreateTaskActivity extends ActionBarActivity {

    public final static String EXTRA_LIST = "il.ac.shenkar.totodo.LIST";

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

    String location ="";

    //boolean flag for set reminder
    boolean isReminder;

    //add pic verb
    Uri selectedImageUri;
    String  selectedPath;
    boolean isPicFromCam = false;

    //is priority flag
    boolean isPriority = false;

    //URL string
    String urlString = "http://mobile1-tasks-dispatcher.herokuapp.com/task/random";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_task_view);

        Intent receiveDataIntent = getIntent();
        Bundle receivedDataBundle = receiveDataIntent.getExtras();
        if (receivedDataBundle != null)  {
            isEdit = true;

        }
        //set reminder button listener
        final ImageView setReminderButton = (ImageView) findViewById(R.id.add_clock_reminder);
        setReminderButton.setOnClickListener(setReminderButtonListener);

        //set image button listener
        final ImageView setImageButton = (ImageView) findViewById(R.id.add_image_button);
        setImageButton.setOnClickListener(setImageButtonListener);

        //set location button listener
        final ImageView setLocationButton = (ImageView) findViewById(R.id.location_reminder_button);
        setLocationButton.setOnClickListener(setLocationButtonListener);

        //set voice button listener
        final ImageView setVoiceButton = (ImageView) findViewById(R.id.add_voice_button);
        setVoiceButton.setOnClickListener(setSpeechButtonListener);

        //set priority button listener
        final ImageView setPriorityButton = (ImageView) findViewById(R.id.priority_button);
        setPriorityButton.setOnClickListener(setPriorityButtonListener);

        if (isEdit)
        {
            setExistTaskDetail(receivedDataBundle);
        }

    }

    private void setExistTaskDetail(Bundle receivedDataBundle) {

        final TaskDAO dao = TaskDAO.getInstance(this);
        taskPosition = receivedDataBundle.getInt("position");
        Task existTask = dao.getItem(taskPosition);

        final EditText titleText = (EditText) findViewById(R.id.title);
        titleText.setText(existTask.getTitle());

        if (existTask.getDate()== null || existTask.getDate().isEmpty()) {
            final ImageView setReminderButton = (ImageView) findViewById(R.id.add_clock_reminder);
            setReminderButton.setImageResource(R.drawable.alarmclock_gray);
        }
        else
        {
            final ImageView setReminderButton = (ImageView) findViewById(R.id.add_clock_reminder);
            setReminderButton.setImageResource(R.drawable.alarm_clock);
            final TextView setReminderTxt = (TextView) findViewById(R.id.clock_reminder_txt);
            setReminderTxt.setText(existTask.getDate());
            stringTimeReminder = existTask.getDate();
        }

        if (existTask.getLocation()== null || existTask.getLocation().isEmpty()) {
            final ImageView setLocationButton = (ImageView) findViewById(R.id.location_reminder_button);
            setLocationButton.setImageResource(R.drawable.map_gray);
        }
        else
        {
            location = existTask.getLocation();
            final ImageView setLocationButton = (ImageView) findViewById(R.id.location_reminder_button);
            setLocationButton.setImageResource(R.drawable.map);
            final TextView setLocationTxt = (TextView) findViewById(R.id.location_reminder_txt);
            setLocationTxt.setText(location);
        }

        if(existTask.isPriority())
        {
            final ImageView PriorityButton = (ImageView) findViewById(R.id.priority_button);
            PriorityButton.setImageResource(R.drawable.exclamation_mark);
            isPriority = true;
        }

        if (existTask.getImageUri()!= null) {
            final ImageView preview = (ImageView) findViewById(R.id.add_image_button);;
            preview.setImageURI(existTask.getImageUri());
            selectedPath = existTask.getImagePath();
            selectedImageUri = existTask.getImageUri();
        }
        taskId = receivedDataBundle.getLong("id");
    }

    private void editTask(View view) {
        TaskDAO dao = TaskDAO.getInstance(this);
        //get text from the view
        EditText editTextTitle = (EditText) findViewById(R.id.title);
        String title = editTextTitle.getText().toString();


        Long id = System.currentTimeMillis();

        Task updatedTask = new Task(id, title, stringTimeReminder,location, selectedImageUri, isPriority, selectedPath, isPicFromCam);
        updatedTask.setId(taskId);

        //update task to DB
        dao.updateTask(updatedTask, taskPosition);
        Toast.makeText(this, title + " updated", Toast.LENGTH_LONG).show();

        //user set reminder
        if(isReminder){
            setReminder(id, title, stringTimeReminder);
        }

        finish();
    }

    /**
     * Showing new dialog for location
     */
    private final View.OnClickListener setLocationButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(CreateTaskActivity.this, LocationActivity.class), LOCATION_ACTIVITY_REQUEST);
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
                    stringTimeReminder = getDateTime(calendar);
                    picker.dismiss();
                    final ImageView setReminderButton = (ImageView) findViewById(R.id.add_clock_reminder);
                    setReminderButton.setImageResource(R.drawable.alarm_clock);
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
                    setReminderButton.setImageResource(R.drawable.alarmclock_gray);
                    final TextView setReminderTxt = (TextView) findViewById(R.id.clock_reminder_txt);
                    setReminderTxt.setText(stringTimeReminder);
                }
            });
            picker.show();
        }
    };

    /**
     * Showing new dialog for add pic
     */
    private final View.OnClickListener setImageButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Image_Picker_Dialog();
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
                priorityButton.setImageResource(R.drawable.exclamation_mark);
            }
            else priorityButton.setImageResource(R.drawable.exclamation_mark_gray);
        }
    };

    protected static Uri createUriFromPhotoIntentForHtcDesireHD( Activity activity, Intent intent, Uri uri ) {
        FileOutputStream fos = null;
        try {
            Bitmap bitmap = (Bitmap) intent.getExtras().get( "data" );
            File outputDir = activity.getCacheDir();
            File outputFile = File.createTempFile( "Photo-", ".jpg", outputDir );
            fos = new FileOutputStream( outputFile );
            bitmap.compress( Bitmap.CompressFormat.JPEG, 90, fos );
            uri = Uri.fromFile( outputFile );
        } catch ( IOException e ) {
        } finally {
            try {
                if ( fos != null ) {
                    fos.close();
                }
            } catch ( IOException e ) {
            }
        }
        return uri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        final ImageView preview = (ImageView) findViewById(R.id.add_image_button);

        if (resultCode == RESULT_OK) { 
            if(data.getData() != null){
                selectedImageUri = data.getData();
            }

            if (requestCode == CAMERA_PICTURE_REQUEST) {
                if ( selectedImageUri == null && data.getExtras() != null &&  data.getExtras().get( "data" ) instanceof Bitmap ) {
                    selectedImageUri = createUriFromPhotoIntentForHtcDesireHD( this, data, selectedImageUri );
                    isPicFromCam = true;
                    preview.setImageURI(selectedImageUri);
                }
                else {
                    selectedPath = getPath(selectedImageUri);
                    preview.setImageURI(selectedImageUri);
                    Log.d("selectedPath : " ,selectedPath);
                }
            }

            else if (requestCode == GALLERY_PICTURE_REQUEST)  {
                selectedPath = getPath(selectedImageUri);
                preview.setImageURI(selectedImageUri);
                Log.d("selectedPath : " ,selectedPath);
            }

            else if (requestCode == VR_REQUEST ) {
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
                location = bundle.getString("location");
                final ImageView setLocationButton = (ImageView) findViewById(R.id.location_reminder_button);
                setLocationButton.setImageResource(R.drawable.map);
                final TextView setLocationTxt = (TextView) findViewById(R.id.location_reminder_txt);
                setLocationTxt.setText(location);
            }

        }
    }


    public String getPath(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public void Image_Picker_Dialog()
    {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Pictures Option");
        myAlertDialog.setMessage("Select Picture Mode");

        myAlertDialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                Intent pictureActionIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pictureActionIntent, GALLERY_PICTURE_REQUEST);
            }
        });

        myAlertDialog.setNegativeButton("Camera", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                Intent pictureActionIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(pictureActionIntent, CAMERA_PICTURE_REQUEST);
            }
        });
        myAlertDialog.show();
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
     * Handling new task creation
     * @param view
     */
    public void createTask(View view) {
        TaskDAO dao = TaskDAO.getInstance(this);

        //get text from the view
        EditText editTextTitle = (EditText) findViewById(R.id.title);
        String title = editTextTitle.getText().toString();


        Long id = System.currentTimeMillis();

        //add task to DB
        dao.addTask( new Task(id, title, stringTimeReminder,location, selectedImageUri, isPriority, selectedPath, isPicFromCam));

        Toast.makeText(this, title + " Added", Toast.LENGTH_LONG).show();

        //user set reminder
        if(isReminder){
           setReminder(id, title, stringTimeReminder);
        }
        finish();
    }

    /**
     * Set Reminder
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
     * Gets the current time and converts it to String using DateFormat
     * @return String that represent current time
     */
    public String getDateTime(Calendar cal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd/MM/yyyy, kk:mm:ss", Locale.getDefault());
        Date d = cal.getTime();
        return dateFormat.format(d);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        if (isEdit)
               inflater.inflate(R.menu.edit_menu, menu);
        else inflater.inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_createTask:
                createTask(getCurrentFocus());
                return true;
            case R.id.action_editTask:
                editTask(getCurrentFocus());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
