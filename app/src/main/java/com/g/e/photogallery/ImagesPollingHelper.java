package com.g.e.photogallery;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.List;

public class ImagesPollingHelper {

    public  static void CheckForNewImages(Context context, String Tag){

        String query = QueryPreferences.getStoredQuery(context);
        String lastResultId = QueryPreferences.getLastResultId(context);
        List<GalleryItem> items;

        if (query == null)
            items = new FlickrFetchr().fetchRecentPhotos();
        else
            items = new FlickrFetchr().searchPhotos(query);

        if (items.size() == 0) return;

        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)){
            Log.i(Tag, "Got an old result: " + resultId);
        }else {
            Log.i(Tag, "Got a new result: " + resultId);
            sendNotification(context);
        }

        QueryPreferences.setLastResultId(context, resultId);

    }

    private static void sendNotification(Context context) {
        Resources resources = context.getResources();
        Intent intent1 = PhotoGalleryActivity.createIntent(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.notify(0, notification);
    }
}
