package gr.auth.androidproject.plants.ui.add_new;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddNewViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public AddNewViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}