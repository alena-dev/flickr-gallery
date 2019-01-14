package com.g.e.photogallery;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;

@TargetApi(21)
public class PollServiceNew extends JobService {
    private  static final String TAG = "PollServiceNew";
    private PollTask mCurrentTask;
    private static final int JOB_ID = 1;

    @Override
    public boolean onStartJob(JobParameters params) {
        mCurrentTask = new PollTask();
        mCurrentTask.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if(mCurrentTask != null) mCurrentTask.cancel(true);

        return true;
    }

    public static boolean isPollingStarted(Context context) {
        JobScheduler scheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        boolean hasBeenScheduled = false;
        for (JobInfo jobInfo : scheduler.getAllPendingJobs()){
            if (jobInfo.getId() == JOB_ID){
                hasBeenScheduled = true;
                break;
            }
        }

        return hasBeenScheduled;
    }

    public static void setPollingState(Context context, boolean isPollingOn) {
        JobScheduler scheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        if(isPollingOn){
            JobInfo jobInfo = new JobInfo.Builder(
                    JOB_ID, new ComponentName(context, PollServiceNew.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setPeriodic(1000*60*15)
                    .setPersisted(true)
                    .build();
            scheduler.schedule(jobInfo);
        } else {
            scheduler.cancel(JOB_ID);
        }

    }

    private class PollTask extends AsyncTask <JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... params) {
            JobParameters jobParams = params[0];

            ImagesPollingHelper.CheckForNewImages(PollServiceNew.this, TAG);

            jobFinished(jobParams, false);
            return null;
        }
    }
}
