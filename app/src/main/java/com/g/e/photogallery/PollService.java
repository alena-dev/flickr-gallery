package com.g.e.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PollService extends IntentService{
    private  static final String TAG = "PollService";

//    60 seconds
    private static  final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);

    public static Intent createIntent(Context context){
        return new Intent(context, PollService.class);
    }

    public PollService(){
        super(TAG);
    }

    public static void setServiceAlarm (Context context, boolean isOn){
        Intent intent = PollService.createIntent(context);
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

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(!isNetworkAvailableAndConnected())return;

        String query = QueryPreferences.getStoredQuery(this);
        String lastResultId = QueryPreferences.getLastResultId(this);
        List<GalleryItem> items;

        if(query == null)
            items = new FlickrFetchr().fetchRecentPhotos();
        else
            items = new FlickrFetchr().searchPhotos(query);

        if(items.size() == 0) return;

        String resultId = items.get(0).getId();
        if(resultId.equals(lastResultId))
            Log.i(TAG, "Got an old result: " + resultId);
        else
            Log.i(TAG, "Got a new result: " + resultId);

        QueryPreferences.setLastResultId(this, resultId);
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
