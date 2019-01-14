package com.g.e.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

public class PollServiceOld extends IntentService{
    private  static final String TAG = "PollServiceOld";

//    15 min
    private static  final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(15);

    public static Intent createIntent(Context context){
        return new Intent(context, PollServiceOld.class);
    }

    public PollServiceOld(){
        super(TAG);
    }

    public static void setServiceAlarm (Context context, boolean isOn){
        Intent intent = PollServiceOld.createIntent(context);
        PendingIntent pendingIntent = PendingIntent
                .getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context){
        Intent intent = PollServiceOld.createIntent(context);
        PendingIntent pendingIntent = PendingIntent
                .getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!isNetworkAvailableAndConnected()) return;

        ImagesPollingHelper.CheckForNewImages(this, TAG);
    }

    private boolean isNetworkAvailableAndConnected(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = connectivityManager.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                connectivityManager.getActiveNetworkInfo().isConnected();

        return isNetworkConnected;
    }
}
