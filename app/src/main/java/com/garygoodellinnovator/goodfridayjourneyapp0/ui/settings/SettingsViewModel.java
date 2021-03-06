package com.garygoodellinnovator.goodfridayjourneyapp0.ui.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SettingsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("(Additional setings will be added in future versions.  Currently all options are available on the home screen.)");
    }

    public LiveData<String> getText() {
        return mText;
    }
}