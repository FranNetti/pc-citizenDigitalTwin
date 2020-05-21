package it.unibo.citizenDigitalTwin.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final RecyclerView listView = root.findViewById(R.id.homeRecyclerView);
        listView.setHasFixedSize(true);
        listView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        final List<String> ciccio = Arrays.asList(
                "Dati anagrafici",
                "Dati sulla posizione",
                "Dati sulla salute",
                "Licenze acquisite",
                "Dati legali"
        );

        listView.setAdapter(new DataCategoryAdapter(ciccio));


        return root;
    }
}
