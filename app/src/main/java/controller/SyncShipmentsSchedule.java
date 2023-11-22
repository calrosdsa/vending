package controller;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.tcn.sdk.springdemo.domain.util.FileLogger;

public class SyncShipmentsSchedule extends JobService {


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("DEBUG_APP_JOB","START JOB");
//        FileLogger.logError("onStartJob","start job");
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
