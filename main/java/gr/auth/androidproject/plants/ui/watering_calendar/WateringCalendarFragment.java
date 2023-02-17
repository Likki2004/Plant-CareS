package gr.auth.androidproject.plants.ui.watering_calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import gr.auth.androidproject.plants.R;

public class WateringCalendarFragment extends Fragment {

    private WateringCalendarViewModel wateringCalendarViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        wateringCalendarViewModel =
                new ViewModelProvider(this).get(WateringCalendarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_water_calendar, container, false);
//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        wateringCalendarViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
}