package android.chat.background;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FirebaseMessageReadJobService extends JobService {

    private static final String TAG = "FirebaseMessageReadJobS";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        startService(new Intent(getApplicationContext(),MessageReadingService.class));
        return true;
    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public void scheduleJobFirebaseToReadMessage(Context context){
        JobScheduler jobScheduler = (JobScheduler)context
                .getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(context,
                FirebaseMessageReadJobService.class);


        //  86400000     =         24 HOUR
        //  900000       =         15 Minute
        //  60000       =         1 Minute

        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(60000).setRequiredNetworkType(
                        JobInfo.NETWORK_TYPE_NOT_ROAMING)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setPersisted(true)
                .setPeriodic(10000)
                .build();

        jobScheduler.schedule(jobInfo);

        int resultCode = jobScheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled!");
        } else {
            Log.d(TAG, "Job not scheduled");
        }

    }
} 