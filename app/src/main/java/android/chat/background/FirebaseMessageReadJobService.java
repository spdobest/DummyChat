package android.chat.background;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class FirebaseMessageReadJobService extends JobService {

    private static final String TAG = "FirebaseMessageReadJobS";

    private static final int JOB_ID = 1001;
    private static final long REFRESH_INTERVAL = 5 * 1000; // 5 seconds


    @Override
    public boolean onStartJob(JobParameters jobParameters) {


        //Reschedule the Service before calling job finished
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            scheduleRefreshForNaught(getApplicationContext());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getApplicationContext(), MessageReadingService.class));
        } else {
            startService(new Intent(getApplicationContext(), MessageReadingService.class));
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    public void scheduleJobFirebaseToReadMessage(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context
                .getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName componentName = new ComponentName(context,
                FirebaseMessageReadJobService.class);


        //  86400000     =         24 HOUR
        //  900000       =         15 Minute
        //  60000       =         1 Minute

        JobInfo jobInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                    .setBackoffCriteria(6000, JobInfo.BACKOFF_POLICY_LINEAR)
                //    .setOverrideDeadline(300)
                //    .setMinimumLatency(60000)
                    .setPeriodic(60000)
//                    .setExtras(bundle)
                    .build();
        } else {
            jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                    .setPeriodic(REFRESH_INTERVAL)
//                    .setExtras(bundle)
                    .build();
        }

     /*  JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(60000).setRequiredNetworkType(
                        JobInfo.NETWORK_TYPE_NOT_ROAMING)
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setPersisted(true)
                .setPeriodic(10000)
                .build();*/


        int resultCode = jobScheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled!");
        } else {
            Log.d(TAG, "Job not scheduled");
        }
    }

    public void scheduleRefreshForNaught(Context context) {
        JobScheduler mJobScheduler = (JobScheduler) context
                .getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder mJobBuilder =
                new JobInfo.Builder(123,
                        new ComponentName(context.getPackageName(),
                                MessageReadingService.class.getName()));

        /* For Android N and Upper Versions */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ComponentName componentName = new ComponentName(context,
                    FirebaseMessageReadJobService.class);
            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, componentName)
                    .setMinimumLatency(REFRESH_INTERVAL)
//                    .setExtras(bundle)
                    .build();

            mJobBuilder
                    .setMinimumLatency(60 * 1000) //YOUR_TIME_INTERVAL
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            mJobScheduler.schedule(jobInfo);
        }
    }

}