package com.paril.mlaclientapp.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.paril.mlaclientapp.R;
import com.paril.mlaclientapp.ui.activity.MLALoginActivity;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    public static String NOTIFICATION_TITLE = "notification_title";

    public static String NOTIFICATION_DESC= "notification_desc";
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive","show notii"+intent.getStringExtra(NOTIFICATION_TITLE));

        Log.d("onReceive","show notii"+intent.getStringExtra(NOTIFICATION_DESC));
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(context, MLALoginActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MLALoginActivity
        .class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(
               id ,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle(context.getString(R.string.app_name))
                .setContentText("Your class is going to start.Please visit the application and start the task.").setAutoCancel(true)
                .setSound(alarmSound).setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);

    }
}
