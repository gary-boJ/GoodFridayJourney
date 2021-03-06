package com.garygoodellinnovator.goodfridayjourneyapp0.ui.home;

import android.app.Application;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.garygoodellinnovator.goodfridayjourneyapp0.MainActivity;
import com.garygoodellinnovator.goodfridayjourneyapp0.MyData;
import com.garygoodellinnovator.goodfridayjourneyapp0.R;
import com.garygoodellinnovator.goodfridayjourneyapp0.ReminderWorker;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static java.lang.Thread.*;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    EditText etStartTime;
    ImageButton ibTimePick;
    MaterialButton mbStart;
    private int totalPrayerTimeMode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container,
                             Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView tvHeading = root.findViewById(R.id.text_home);
        homeViewModel.getHeading().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tvHeading.setText(s);
            }
        });

        final RadioGroup rgPrayerMode = root.findViewById(R.id.radioGroupTop);
        homeViewModel.getPrayerMode().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer i) {
                rgPrayerMode.getChildAt(i).setSelected(true);
            }
        });
        final RadioButton rbByTime = root.findViewById(R.id.radioForATime);
        final RadioButton rbByLocation = root.findViewById(R.id.radioAroundAnArea);
        rbByLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(getView(), "This feature is not yet implemented\r\nLook for it in the next update!",
                        Snackbar.LENGTH_LONG).show();
                rbByTime.setChecked(true);
            }
        });

        final RadioGroup rgTotalPrayerTimeMode = root.findViewById(R.id.radioGroup1);
        homeViewModel.getTotalPrayerTime().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer i) {
                rgTotalPrayerTimeMode.getChildAt(i).setSelected(true);
            }
        });

        final CheckBox cbSilentAlerts = root.findViewById(R.id.cbSilentAlerts);
        homeViewModel.getSilentAlerts().observe(this,  new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean b) {
                cbSilentAlerts.setChecked(b);
            }
        });

        etStartTime = root.findViewById(R.id.editTextStartTime);
        homeViewModel.getStartTime().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                etStartTime.setText(s);
            }
        });

        ibTimePick = root.findViewById(R.id.ibTimePick);
        ibTimePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startTime = etStartTime.getText().toString();
                showTimePicker(startTime, v.getContext());
            }
        });

        mbStart = root.findViewById(R.id.mbStart);
        mbStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Reminders are being scheduled...",
                        Snackbar.LENGTH_LONG).show();

                ClearOldReminders();

                totalPrayerTimeMode = rgTotalPrayerTimeMode.getCheckedRadioButtonId();
                Log.println(Log.INFO, "*","Previously scheduled alarms have been cancelled."
                + " [new mode=" + totalPrayerTimeMode + "]");

                long priorMinFromStart = -1;
                long minutesFromNowToStart = 0;
                int[] eventIdList = MyData.remindersDay;
                long timeDivisor = 1;

                if (totalPrayerTimeMode == R.id.radioManual) { //manual
                    // No notifications -- Just jump to he first event card
                    Log.println(Log.INFO, "*","manual mode");
                    Snackbar.make(v, "...All reminders have been cancelled.",
                            Snackbar.LENGTH_LONG).show();

                    // Jump to the start of the journey
                    int eventIndex = 0;
                    Log.println(Log.ASSERT, "hardcode Event Index",
                            "eventIndex(0)=" + eventIndex);
                    int nextDestination = (eventIndex == -1) ? R.id.nav_home : R.id.nav_slideshow;
                    MainActivity.navController.navigate(nextDestination);

                } else {
                    // Schedule automatic alert notifications
                    if (totalPrayerTimeMode == R.id.radio240) {  //24 hour
                        Log.println(Log.INFO, "*","24 hour");
                        eventIdList = MyData.remindersDay;
                        timeDivisor = 1;
                    } else if (totalPrayerTimeMode == R.id.radio010) {  //1 hour
                        Log.println(Log.INFO, "*","1 hour");
                        eventIdList = MyData.remindersHour;
                        timeDivisor = 24;
                    } else if (totalPrayerTimeMode == R.id.radio005) {  //half hour
                        Log.println(Log.INFO, "*","1/2 hour");
                        eventIdList = MyData.remindersHalfHour;
                        timeDivisor = 58;
                    }
                    for (int eventSequence :eventIdList) {

                        long minFromStart = MyData.MinutesFromStart(eventSequence) / timeDivisor;

                        if (minFromStart <= priorMinFromStart) {
                            minFromStart = priorMinFromStart + 2;
                        }
                        priorMinFromStart = minFromStart;

                        long minutesFromNowToReminder = (minutesFromNowToStart + minFromStart);
                        ScheduleReminder(minutesFromNowToReminder, eventSequence, cbSilentAlerts.isChecked());
                    }
                    Snackbar.make(v, "...All " + ((cbSilentAlerts.isChecked())?"silent ":"") + "reminders are scheduled.",
                            Snackbar.LENGTH_LONG).show();
                }
            }
        });

        return root;
    }

    private void ClearOldReminders() {
        WorkManager.getInstance(getContext()).cancelAllWork();
    }

    private void ScheduleReminder(Long afterMinutes, final Integer eventSequence, Boolean isSilent) {

        TimeUnit timeUnit = TimeUnit.MINUTES;
        Long duration = afterMinutes;
        if (afterMinutes == 0L) {
            timeUnit = TimeUnit.SECONDS;
            duration = 10L;
        }

        Constraints constraints = new Constraints.Builder()
                .setRequiresDeviceIdle(true)
                .setRequiresCharging(false)
                .build();

        Data data = new Data.Builder()
                .putInt("eventSequence", eventSequence)
                .putBoolean("isSilent", isSilent)
                .build();

        Log.println(Log.INFO, "*",
                "Reminder #" + eventSequence
                        + " will alert in " + duration + " " + timeUnit.name());

        OneTimeWorkRequest remindWorkRequest = new OneTimeWorkRequest
                .Builder(ReminderWorker.class)
                .setInitialDelay(duration, timeUnit)
                .setInputData(data)
                .build();
        WorkManager.getInstance(getContext()).enqueue(remindWorkRequest);

        WorkManager.getInstance(getContext())
                .getWorkInfoByIdLiveData(remindWorkRequest.getId())
                .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(@Nullable WorkInfo workInfo) {

                    //Log.println(Log.INFO, "Reminder", "Reminder #" + eventSequence + " " + workInfo.getId() + " work state = " + workInfo.getState().toString());
                    //Toast.makeText(getContext(),
                    //    "Reminder " + eventSequence + "  " + workInfo.getState().toString(),
                    //    Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void showTimePicker(String startTime, Context context) {
        final String dateFormatString = getDateFormatPattern();
        String patternString;
        int hourTemp;
        final int hour;
        final int min;
        if (dateFormatString == "HH:mm") {
            patternString = "#+#:##";
        } else {
            patternString = "#+#:## [a|p]m";
        }
        if (Pattern.compile(patternString).matcher(startTime).matches() == false) {
            startTime = getCurrentTime();
        }
        hourTemp = Integer.parseInt(startTime.split(":")[0]);
        if (hourTemp <= 12 && startTime.toLowerCase().endsWith("pm")) {
            hourTemp += 12;
        }
        hour = hourTemp;
        min = Integer.parseInt(startTime.split(":")[1]);


        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (hourOfDay != hour || minute != min) {
                    String minuteString = "00" + minute;
                    minuteString = minuteString.substring(minuteString.length() - 2);
                    String hourStringPadded = "00" + hourOfDay;
                    hourStringPadded = hourStringPadded.substring(hourStringPadded.length() - 2);

                    if (dateFormatString == "HH:mm") {
                        etStartTime.setText(hourStringPadded + ":" + minuteString);
                    } else {
                        etStartTime.setText("" + (hourOfDay % 12) + ":" + minuteString
                                + (((hourOfDay / 12) < 1) ? " am" : " pm"));
                    }
                }
            }
        }, hour, min, (getDateFormatPattern() == "HH:mm"));
        timePickerDialog.show();
    }

    public static String getCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat(getDateFormatPattern());
        Date time = Calendar.getInstance().getTime();
        return dateFormat.format(time);
    }

    private static String getDateFormatPattern() {
        String dateFormatPattern;
        int currentTimezoneOffset = TimeZone.getDefault().getRawOffset();
        if (currentTimezoneOffset >= 5 && currentTimezoneOffset <= 8) {
            dateFormatPattern = "hh:mm a";
        } else {
            dateFormatPattern = "HH:mm";
        }
        //SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatPattern;
    }
}