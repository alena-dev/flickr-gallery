package com.g.e.photogallery;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;

public class PollServiceNew extends JobService {
    private PollTask mCurrentTask;

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

    private class PollTask extends AsyncTask <JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... params) {
            JobParameters jobParams = params[0];

            // check new images

            jobFinished(jobParams, false);
            return null;
        }
    }
}
