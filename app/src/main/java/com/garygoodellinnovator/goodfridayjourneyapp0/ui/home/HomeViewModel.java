package com.garygoodellinnovator.goodfridayjourneyapp0.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mHeading;
    private MutableLiveData<Integer> mTotalPrayerTimeMode;
    private MutableLiveData<Integer> mPrayerMode;
    private MutableLiveData<String> mStartTime;
    private MutableLiveData<Boolean> mSilentAlerts;

    int defaultToTimePeriod = 0;
    int defaultToHalfHour = 2;
    String defaultToNow = "Now";

    public HomeViewModel() {
        mHeading = new MutableLiveData<>();
        mHeading.setValue("Start a Good Friday devotion...");
        mPrayerMode = new MutableLiveData<>();
        mPrayerMode.setValue(defaultToTimePeriod);
        mTotalPrayerTimeMode = new MutableLiveData<>();
        mTotalPrayerTimeMode.setValue(defaultToHalfHour);
        mStartTime = new MutableLiveData<>();
        mStartTime.setValue(defaultToNow);
        mSilentAlerts = new MutableLiveData<>();
        mSilentAlerts.setValue(false);
    }

    public LiveData<String> getHeading() {
        return mHeading;
    }
    public LiveData<Integer> getTotalPrayerTime() {
        return mTotalPrayerTimeMode;
    }
    public LiveData<String> getStartTime() {
        return mStartTime;
    }

    public LiveData<Integer> getPrayerMode() {
        return mPrayerMode;
    }
    public LiveData<Boolean> getSilentAlerts() { return mSilentAlerts; }
}