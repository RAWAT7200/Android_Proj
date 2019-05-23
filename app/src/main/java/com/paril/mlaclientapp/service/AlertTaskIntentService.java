package com.paril.mlaclientapp.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.paril.mlaclientapp.model.MLATaskDetails;
import com.paril.mlaclientapp.receivers.NotificationPublisher;
import com.paril.mlaclientapp.util.PrefsManager;
import com.paril.mlaclientapp.webservice.Api;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;


public class AlertTaskIntentService extends IntentService {


    public AlertTaskIntentService() {
        super("AlertTaskIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.hasExtra("stop")) {
            cancelNotification("", "", 0);

        } else {
            try {

                MLAGetTaskDetails getTaskDetails = new MLAGetTaskDetails(this);
                getTaskDetails.doInBackground();

            } catch (Exception e) {

            }
        }

    }


    //this class will get the task details and add them to listview item by item
    class MLAGetTaskDetails extends AsyncTask<Void, Void, Void> {
        Context context;

        public MLAGetTaskDetails(Context ctx) {
            context = ctx;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                PrefsManager prefsManager = new PrefsManager(AlertTaskIntentService.this);
                Call<List<MLATaskDetails>> callGetTasks = Api.getClient().getTasksByUser(prefsManager.getStringData("userName"), prefsManager.getStringData("userType"));

                Response<List<MLATaskDetails>> responseTaskDetail = callGetTasks.execute();

                if (responseTaskDetail.isSuccessful() && responseTaskDetail.body() != null) {

                    cancelNotification("", "", 0);
                    for (MLATaskDetails taskDetail : responseTaskDetail.body()
                            ) {

                        if (taskDetail != null && taskDetail.getSchedule_startTime() != null) {


                            try {
                                //parse start time
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                Date date = dateFormat.parse(taskDetail.getSchedule_startTime());
                                long startTimeMs = date.getTime();
                                if (Calendar.getInstance().getTimeInMillis() < (startTimeMs - 10 * 60 * 1000)) {
                                    scheduleNotification(taskDetail.getTopic(), taskDetail.getDescription(), (startTimeMs - 10 * 60 * 1000), taskDetail.getIdTask());
                                }


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private void scheduleNotification(String topic, String desc, long time, int id) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_DESC, desc);

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_TITLE, topic);
        Log.d("sch noti", "noti" + new Date(time).toString() + " id:" + id + "topic" + topic + "desc" + desc);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= 19) {

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    private void cancelNotification(String title, String desc, int id) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_DESC, desc);

        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_TITLE, title);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }


}
