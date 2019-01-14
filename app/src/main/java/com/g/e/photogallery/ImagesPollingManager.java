package com.g.e.photogallery;

import android.content.Context;
import android.os.Build;

public class ImagesPollingManager {

    public static boolean isPollingOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return PollServiceOld.isServiceAlarmOn(context);
        } else {
            return PollServiceNew.isPollingStarted(context);
        }
    }

    public static void togglePolling(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            PollServiceOld.setServiceAlarm(context);
        } else {
            PollServiceNew.setPollingState(context);
        }
    }
}
