package it.unibo.citizenDigitalTwin.ui.devices;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.view_model.MainActivityViewModel;
import it.unibo.citizenDigitalTwin.view_model.DevicesViewModel;

public class DevicesFragment extends Fragment {

    private DevicesViewModel devicesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        devicesViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class).devices;
        final View root = inflater.inflate(R.layout.fragment_devices, container, false);

        final TextView emptyDevices = root.findViewById(R.id.emptyDevices);

        final RecyclerView listView = root.findViewById(R.id.devicesRecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                linearLayoutManager.getOrientation());

        listView.addItemDecoration(dividerItemDecoration);
        listView.setLayoutManager(linearLayoutManager);
        listView.setNestedScrollingEnabled(false);

        final List<Device> ciccio = Arrays.asList(
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE)),
                new Device("Braccialetto", Arrays.asList(LeafCategory.BLOOD_OXIGEN, LeafCategory.HEART_RATE)),
                new Device("Cardiofrequenziometro", Arrays.asList(LeafCategory.HEART_RATE)),
                new Device("Termometro", Arrays.asList(LeafCategory.TEMPERATURE))
        );

        listView.setAdapter(new DeviceAdapter(getContext(), ciccio));

        return root;
    }
}
