package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;


import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

/**
 * Created by Danny on 7/27/2017.
 */

public class ScheduleUtilities {
    //window start
    private static final int SCHEDULE_INTERVAL_MINUTES = 60;
    //window end time minus start time
    private static final int SYNC_FLEXTIME_SECONDS = 0;
    private static final String NEWS_JOB_TAG = "news_job_tag";

    private static boolean sInitialized;

    synchronized public static void scheduleRefresh(@NonNull final Context context){
        //if schedule already initiated do not continue
        if(sInitialized) return;
        //reference driver and dispatcher for use
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        //sets news job to run recurringly using trigger with execution window set by variables defined above
        Job constraintRefreshJob = dispatcher.newJobBuilder()
                .setService(NewsJob.class)
                .setTag(NEWS_JOB_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(SCHEDULE_INTERVAL_MINUTES,
                        SCHEDULE_INTERVAL_MINUTES + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();
        //schedule job
        dispatcher.schedule(constraintRefreshJob);
        //note that schedule has been initiated so it is not again
        sInitialized = true;

    }

}