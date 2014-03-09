package il.ac.shenkar.remember_to_do;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_CLICK = "ACTION_CLICK";
    private static int taskPos = 0;
    private static TaskDAO dao;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,  int[] appWidgetIds) {

        dao = TaskDAO.getInstance(context,false);

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            String taskNum = "";
            String title = "";
            String time= "";
            String location= "";

            // Get the next task details and set the text
            if(!dao.isEmpty()) {
                if (taskPos >= dao.getCount() || dao.getItem(taskPos) == null) {
                    taskPos = 0;
                }
                title =  dao.getItem(taskPos).getTitle();
                time =  dao.getItem(taskPos).getDate();
                location =  dao.getItem(taskPos).getLocation();
                int index = taskPos+1;
                taskNum = "Task " + index + "\\" + dao.getCount();
            }
            else title =   "No Tasks";

            remoteViews.setTextViewText(R.id.title, title);
            if (time.isEmpty())
                remoteViews.setTextViewText(R.id.time, location);
            else {
                remoteViews.setTextViewText(R.id.time, time);
                remoteViews.setTextViewText(R.id.location, location);
            }
            remoteViews.setTextViewText(R.id.taskNum, taskNum);

            // Register an onClickListener
            Intent intent = new Intent(context, MyWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        taskPos++;
    }
}
