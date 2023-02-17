package gr.auth.androidproject.plants.ui.watering_calendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WateringCalendarViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public WateringCalendarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}