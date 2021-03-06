package com.garygoodellinnovator.goodfridayjourneyapp0;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.WorkerParameters;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

//@RequiresApi(api = Build.VERSION_CODES.O)
public class ReminderWorker extends androidx.work.Worker {

    public Data data;
    public int eventSequence;
    public boolean isSilent;
    public String soundFile = "";

    private String NOTIFICATION_CHANNEL_ID = "remind_channel_id_";
    private int RemindEventID = 33;
    private String notificationTitle = "Remember. ";
    private String notificationText = "Were you there?  ";
    private Bitmap myBitmap = BitmapFactory.decodeResource(
            getApplicationContext().getResources(),
            R.drawable.crucifixion_small_hortus_deliciarum_die_kreuzigung_jesu_christi);

    private Bitmap GetPictureBitmap(Integer eventSequence) {
        return GetBitmap(eventSequence, MyData.pictureArray);
    }
    private Bitmap GetIconBitmap(Integer eventSequence) {
        return GetBitmap(eventSequence, MyData.iconArray);
    }
    private Bitmap GetBitmap(Integer eventSequence, Integer[] fromArray) {
        Bitmap useBitmap = null;
        try {
            BitmapFactory.decodeResource(
                    getApplicationContext().getResources(),
                    fromArray[eventSequence]);
        } catch (Exception ex) {
            Log.e("GetBitmap" + eventSequence, ex.toString());
        } finally {
            if (useBitmap == null) {
                useBitmap = myBitmap;
            }
        }
        return useBitmap;
    }

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        Log.println(Log.ASSERT, "ReminderWork",
                "constructor event#" + eventSequence);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.println(Log.ASSERT, "ReminderWork", "doWork");

        eventSequence = getInputData().getInt("eventSequence", -1);

        isSilent = getInputData().getBoolean("isSilent", false);

        if (isSilent == true) {
            soundFile = "android.resource://"
                    + getApplicationContext().getPackageName()
                    + "/" + R.raw.silent;
        } else {
            soundFile = "android.resource://"
                    + getApplicationContext().getPackageName()
                    + "/" + MyData.soundArray[eventSequence];
        }

        Log.println(Log.ASSERT, "ReminderWork",
                "doWork new event#" + eventSequence + " is silent = " + isSilent);
        try {
            Remind();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }

        return Result.success();
    }

    private void Remind() {
        // Method to trigger an instant notification
        triggerNotification();
    }

    private void triggerNotification() {

        //create an intent to open the event details activity

        Intent intent = new Intent(getApplicationContext(), MainActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Log.println(Log.ASSERT, "Remind", "add index to intent");


        // Add the cardIndex to the intent to trigger the app when notification is clicked
        intent.putExtra("eventIndex", eventSequence);


        Log.println(Log.ASSERT, "Remind", "build backstack");
        // Construct a back stack for cross-task navigation:
        TaskStackBuilder stackBuilder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            stackBuilder = TaskStackBuilder.create(getApplicationContext());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(intent);
        }

        Log.println(Log.ASSERT, "Note",
                "Clicking the alert should goto event #"
                        + intent.getIntExtra("eventIndex",7));

        //put together the PendingIntent

        // legacy versions don't preserve the stack
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 1, intent, FLAG_UPDATE_CURRENT);

        // Jellybean version includes the stack
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            pendingIntent = stackBuilder.getPendingIntent(0, FLAG_UPDATE_CURRENT);
        }

        NotificationManager notificationManager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the old support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(getApplicationContext(), notificationManager);
        }

        //build the notification
        NotificationCompat.Builder builder = buildNotification(
                getApplicationContext(), notificationManager, pendingIntent);

        //trigger the notification
        notificationManager.notify(RemindEventID, builder.build());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    void createNotificationChannel(Context context, NotificationManager notificationManager) {

        // If you submit a new channel with the same name and description,
        // the system will just ignore it as duplicate.
        int eventSequence = getInputData().getInt("eventSequence", 0);

        notificationTitle += MyData.headingArray[eventSequence];
        notificationText += " " + MyData.prayerTitleArray[eventSequence] + " [" + eventSequence + "]";

        NOTIFICATION_CHANNEL_ID += eventSequence;
        String channelName = "Reminder Channel " + eventSequence;

        NotificationChannel mChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID, channelName,
                NotificationManager.IMPORTANCE_HIGH);

        // Configure the notification channel.
        mChannel.setDescription("A channel which shows notifications to remember...");
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
        mChannel.enableVibration(false);
        mChannel.setSound(Uri.parse(soundFile), Notification.AUDIO_ATTRIBUTES_DEFAULT);

        notificationManager.createNotificationChannel(mChannel);

        // Register the channel with the system
        notificationManager.createNotificationChannel(mChannel);
    }

    private NotificationCompat.Builder buildNotification(
            Context applicationContext,
            NotificationManager notificationManager,
            PendingIntent pendingIntent) {

        //build the builder
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_cross_leaning)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)
                        .setColorized(true)
                        .setColor(Color.MAGENTA)
                        //.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                        .setVibrate(new long[]{0})
                        .setSound(Uri.parse(soundFile))
                        .setTicker("Hearty365")
                        .setContentInfo("Info")
                        .setLargeIcon(GetIconBitmap(eventSequence))
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(GetPictureBitmap(eventSequence))
                                .bigLargeIcon(null))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX);

        Log.println(Log.ASSERT, "buildNotification()",
                "event#" + eventSequence + " sound = " + soundFile);

        /*            // Add media control buttons that invoke intents in your media service
        .addAction(R.drawable.ic_prev, "Previous", prevPendingIntent) // #0
                    .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)  // #1
                    .addAction(R.drawable.ic_next, "Next", nextPendingIntent)     // #2
                    // Apply the media style template
                    .setStyle(new android.support.v4.media.app.Notification.MediaStyle()
                            .setShowActionsInCompactView(1 *//* #1: pause button *//*)
                            .setMediaSession(mediaSession.getSessionToken()))*/
        return builder;
    }


//    public static boolean isAppRunning( Context context, String packageName)
//    {
//        //     ActivityManager activityManager = context.GetSystemService(Context.AccountService);
//
//        ActivityManager activityManager  = context.GetSystemService(Context.ActivityService) as ActivityManager;
//        var runningAppProcessInfos = activityManager.RunningAppProcesses;
//        System.Collections.Generic.IList<Android.App.ActivityManager.RunningAppProcessInfo> procInfos = activityManager.RunningAppProcesses ;
//        if (procInfos != null)
//        {
//            foreach (ActivityManager.RunningAppProcessInfo processInfo in procInfos)
//            {
//                if (processInfo.ProcessName.Equals(packageName))
//                {
//                    return true;
//                }
//            }
//
//        }
//        return false;
//    }
}
