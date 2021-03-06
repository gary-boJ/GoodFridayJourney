package com.garygoodellinnovator.goodfridayjourneyapp0;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.Data;
import androidx.work.DelegatingWorkerFactory;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerFactory;

import android.view.Menu;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    public static NavController navController;
    public static int eventIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.println(Log.ASSERT, "onClick",
                        "eventIndex=" + (++eventIndex));

                updateEventIndex(getIntent(), true);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_settings, R.id.nav_help)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController,
                mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.println(Log.ASSERT, "onStart",
                "old eventIndex=" + eventIndex);

        eventIndex = this.getIntent().getIntExtra("eventIndex", -1);

        Log.println(Log.ASSERT, "onStart",
                "eventIndex(from Extra)=" + eventIndex);

        updateEventIndex(this.getIntent(), false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        Log.println(Log.ASSERT, "onNewIntent",
                "old eventIndex=" + eventIndex);

        eventIndex = intent.getIntExtra("eventIndex", -1);

        Log.println(Log.ASSERT, "onNewIntent",
                "eventIndex(from Extra)=" + eventIndex);

        updateEventIndex(intent, false);
    }

    private void updateEventIndex(Intent intent, boolean playSound) {

        Log.println(Log.ASSERT, "updateEventIndex",
                "eventIndex=" + eventIndex + "  playSound=" + playSound);

        int nextDestination = (eventIndex == -1) ? R.id.nav_home : R.id.nav_slideshow;

        // Play the sound
        if (playSound == true) {
            Data data = new Data.Builder()
                    .putInt("eventSequence", eventIndex)
                    .build();
            OneTimeWorkRequest playSoundWorkRequest = new OneTimeWorkRequest
                    .Builder(PlaySoundWorker.class)
                    .setInitialDelay(0L, TimeUnit.SECONDS)
                    .setInputData(data)
                    .build();
            WorkManager.getInstance(getApplicationContext()).enqueue(playSoundWorkRequest);

            WorkManager.getInstance(getApplicationContext())
                    .getWorkInfoByIdLiveData(playSoundWorkRequest.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(@Nullable WorkInfo workInfo) {

                            Log.println(Log.INFO, "playSoundWork", "work in progress" + " " + workInfo.getId() + " work state = " + workInfo.getState().toString());
                            //Toast.makeText(getContext(),
                            //    "Reminder " + eventSequence + "  " + workInfo.getState().toString(),
                            //    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        navController.navigate(nextDestination);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
