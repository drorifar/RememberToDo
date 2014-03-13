package il.ac.shenkar.remember_to_do;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * This class manage the alarm to invoke the ReminderBroadcastReceiver
 * @author Dror Afargan & Ran Nahmijas
 */

public class ReminderAlarmManager {

    public final static String EXTRA_LIST = "il.ac.shenkar.totodo.LIST";

    private Context context;

    public ReminderAlarmManager(Context context){
        this.context = context;
    }

    /**
     *
     * set the alarm for the ic_task_board
     */
    public void setAlarm(Task taskForAlarm, Calendar cal){
        //calculating time for the reminder
        long timeToWait = cal.getTimeInMillis();

        List<String> tasksList = new ArrayList<String>();
        Long id = taskForAlarm.getId();
        tasksList.add(String.valueOf(taskForAlarm.getId()));
        tasksList.add(taskForAlarm.getTitle());
        tasksList.add(taskForAlarm.getDate());

        //AlarmManager for invoke ReminderBroadcastReceiver
        Intent intent = new Intent("il.ac.shenkar.reminder_broadcast");
        intent.putExtra("id", id);
        intent.putStringArrayListExtra(EXTRA_LIST, (ArrayList<String>) tasksList);
        PendingIntent pendingIntent =   PendingIntent.getBroadcast(context, id.intValue(), intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeToWait , pendingIntent);
    }

    /**
     * cancel the alarm of the ic_task_board
     *
     */
    public void cancelAlarm(Task taskToCancel)
    {
        Long id = taskToCancel.getId();

        //AlarmManager for invoke ReminderBroadcastReceiver
        Intent intent = new Intent("il.ac.shenkar.reminder_broadcast");
        PendingIntent.getBroadcast(context, id.intValue(), intent, 0).cancel();

    }

}
