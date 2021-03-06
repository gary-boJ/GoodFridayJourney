package com.garygoodellinnovator.goodfridayjourneyapp0;

import android.app.Notification;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PlaySoundWorker extends Worker {

    MediaPlayer mp = new MediaPlayer();
    Context mContext;

    public PlaySoundWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        mContext = context;
        mp = MediaPlayer.create(context, R.raw.abandon);;

        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                Log.println(Log.ASSERT, "doWork","onPrepared: mp.start();");
                player.start();
                Log.println(Log.ASSERT, "doWork","after start");
            }
        });
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.println(Log.ASSERT, "doWork","here");

        int eventSequence = getInputData().getInt("eventSequence", -1);

        Log.println(Log.ASSERT, "doWork","eventIndex=" + eventSequence);

        String result = "-";
        try {
            Log.println(Log.ASSERT, "doWork","here1");
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();
            }
            Log.println(Log.ASSERT, "doWork","here2");

            mp = MediaPlayer.create(mContext, MyData.soundArray[eventSequence]);
            mp.setDataSource("android.resource://"
                    + getApplicationContext().getPackageName()
                    + "/" + MyData.soundArray[eventSequence]);

            Log.println(Log.ASSERT, "doWork","here3 - "
            + "android.resource://"
                    + getApplicationContext().getPackageName()
                    + "/" + MyData.soundArray[eventSequence]);

            result = "done";
        } catch(Exception e) {
            e.printStackTrace();
            Log.println(Log.ASSERT, "doWork","error: " + e.getMessage());
            result = e.getMessage();
        }

        //...set the output, and we're done!
        Data output = new Data.Builder()
                .putString("Result", result)
                .build();
        return Result.success(output);
    }
}
