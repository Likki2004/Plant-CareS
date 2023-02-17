package gr.auth.androidproject.plants.ui.details;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import gr.auth.androidproject.plants.R;
import gr.auth.androidproject.plants.domain.Plant;
import gr.auth.androidproject.plants.ui.PlantFormatter;
import gr.auth.androidproject.plants.ui.HomeDetailsSharedViewModel;

public class DetailsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_details, container, false);
        HomeDetailsSharedViewModel sharedViewModel =
                new ViewModelProvider(requireActivity()).get(HomeDetailsSharedViewModel.class);
        Context context = getContext();

        assert getArguments() != null;
        int position = getArguments().getInt("position");
        Plant current_plant =
                Objects.requireNonNull(sharedViewModel.getPlants(context).getValue()).get(position);
        PlantFormatter plantFormatter = new PlantFormatter(context, current_plant);

        // setting the views to the according values of the selected plant
        TextView name = root.findViewById(R.id.textViewDetails2);
        ImageView plant_image = root.findViewById(R.id.imageViewDetails1);
        TextView next_watering = root.findViewById(R.id.textViewDetails4);
        TextView age = root.findViewById(R.id.textViewDetails6);
        name.setText(plantFormatter.name());
        plant_image.setImageBitmap(plantFormatter.photo());
        next_watering.setText(plantFormatter.timeToNextWatering());
        age.setText(plantFormatter.age());

        // getting the delete button view
        Button delete_button = root.findViewById(R.id.buttonDetails2);

        // getting the just watered button view
        Button just_watered_button = root.findViewById(R.id.buttonDetails1);

        delete_button.setOnClickListener(v ->
                deleteWithConfirmation(
                        context, sharedViewModel, position, delete_button, just_watered_button));

        just_watered_button.setOnClickListener(v -> {
            sharedViewModel.waterPlant(position, context);
            next_watering.setText(plantFormatter.timeToNextWatering());
            Snackbar.make(this.requireView(), R.string.details_just_watered_response,
                    Snackbar.LENGTH_SHORT).show();
            just_watered_button.setEnabled(false);
        });

        return root;
    }

    private void deleteWithConfirmation(Context context, HomeDetailsSharedViewModel sharedViewModel,
                                        int position,
                                        Button delete_button, Button just_watered_button) {
        new AlertDialog.Builder(this.requireActivity())
                .setMessage(R.string.details_delete_plant_dialogue)
                .setPositiveButton(R.string.yes, (d, w) -> {
                    // do the delete
                    sharedViewModel.deletePlant(position, context);
                    Snackbar.make(this.requireView(), R.string.details_delete_success_response,
                            Snackbar.LENGTH_SHORT).show();
                    delete_button.setEnabled(false);
                    just_watered_button.setEnabled(false);
                })
                .setNegativeButton(R.string.no, (d, w) -> { // do not delete
                })
                .show();
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
//        // TODO: Use the ViewModel
//    }

}