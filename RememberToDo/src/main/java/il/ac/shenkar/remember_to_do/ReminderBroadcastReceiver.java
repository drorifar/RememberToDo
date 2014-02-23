package il.ac.shenkar.remember_to_do;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * This class manage notification sending
 */
public class ReminderBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //Receiving data from CreateTaskActivity
        ArrayList<String> arrayListExtra = intent.getStringArrayListExtra(CreateTaskActivity.EXTRA_LIST);

        Intent taskListIntent = new Intent(context, TaskListActivity.class);
        Long taskId = intent.getLongExtra("id", 0);
        PendingIntent pIntent = PendingIntent.getActivity(context, taskId.intValue(), taskListIntent, 0);

        // Build notification
        Notification n = new Notification.Builder(context)
                .setContentTitle("Reminder from ToToDo")
                .setContentText("The Task: " + arrayListExtra.get(1))
                .setSmallIcon(R.drawable.ic_board)
                .setContentIntent(pIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Hide the notification after its selected
        n.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
        n.defaults |= Notification.DEFAULT_SOUND;
        n.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(taskId.intValue(), n);
    }
}
