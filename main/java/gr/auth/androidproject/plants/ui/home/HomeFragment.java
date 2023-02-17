package gr.auth.androidproject.plants.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import gr.auth.androidproject.plants.R;
import gr.auth.androidproject.plants.domain.Plant;
import gr.auth.androidproject.plants.ui.HomeDetailsSharedViewModel;

public class HomeFragment extends Fragment {

    FloatingActionButton fab;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter<RecyclerAdapter.ViewHolder> adapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        HomeDetailsSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity())
                .get(HomeDetailsSharedViewModel.class);

        Context context = getContext();

        recyclerView = root.findViewById(R.id.recyclerView);

        //Set the layout of the items in the RecyclerView
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        List<Plant> plants = sharedViewModel.getPlants(context).getValue();
        //Set my Adapter for the RecyclerView
        adapter = new RecyclerAdapter(context, plants);
        recyclerView.setAdapter(adapter);

//        sharedViewModel.getPlants(context).
//                observe(getViewLifecycleOwner(), (Observer) o -> {
//
//                });


        // setting the floating action button to go to add new page when pressed
        fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(
                v -> Navigation.findNavController(v).navigate(R.id.action_nav_home_to_nav_add_new)
        );


        return root;
    }

}