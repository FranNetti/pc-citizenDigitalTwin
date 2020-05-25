package it.unibo.citizenDigitalTwin.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView userName = root.findViewById(R.id.userName);

        final RecyclerView listView = root.findViewById(R.id.homeRecyclerView);
        final GridLayoutManager linearLayoutManager = new GridLayoutManager(this.getContext(), 2);
        listView.setLayoutManager(linearLayoutManager);
        listView.setNestedScrollingEnabled(false);

        listView.setAdapter(new DataCategoryAdapter(getContext()));


        userName.setText("Francesco");

        return root;
    }
}
