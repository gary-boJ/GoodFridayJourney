package com.garygoodellinnovator.goodfridayjourneyapp0.ui.help;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HelpViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HelpViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Welcome to the prototype version of the Good Friday Journey App.\r\n" +
                "\r\n" +
                "From the home screen, you can start a Good Friday devotional journey " +
                "through the events of Good Friday, starting with the disciples gathering" +
                "for the Last Supper:\r\n" +
                "> For a given time period -- phone notifications will alert you automtically!\r\n" +
                "> Manually -- You can advance through the events of Good Friday at your own pace.\r\n" +
                "\r\n" +
                "(> Location based notifications will be added in a future release.)" +
                "\r\n" +
                "To start your devotional journey, simply pick your option and click\r\n" +
                "on (Start this Devotion)." +
                "\r\n" +
                "Important! -- To cancel scheduled notifications, just pick the Manual option" +
                " and click on (Start this Devotion) again.  Prior scheduled alerts will be cleared.\r\n" +
                "\r\n" +
                "\r\n" +
                "Settings:\r\n" +
                "   You can choose to exclude audible \"icons\" for silent notifications.\r\n" +
                "\r\n" +
                "" +
                "");
    }

    public LiveData<String> getText() {
        return mText;
    }
}