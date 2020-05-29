package it.unibo.citizenDigitalTwin.ui.devices;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;

public class DevicesFragment extends Fragment {

    private static final String DEVICES = "devices";

    public static DevicesFragment getInstance(final List<Device> devices){
        final DevicesFragment fragment = new DevicesFragment();
        final Bundle bundle = new Bundle();
        if(Objects.nonNull(devices)){
            bundle.putSerializable(DEVICES, new ArrayList<>(devices));
        } else {
            bundle.putSerializable(DEVICES, new ArrayList<>());
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    private List<Device> devices;
    private DeviceAdapter deviceAdapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Objects.nonNull(getArguments())){
            devices = (List<Device>)getArguments().getSerializable(DEVICES);
        }
    }

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_devices, container, false);

        final TextView emptyDevices = root.findViewById(R.id.emptyDevices);

        final RecyclerView listView = root.findViewById(R.id.devicesRecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                linearLayoutManager.getOrientation());

        listView.addItemDecoration(dividerItemDecoration);
        listView.setLayoutManager(linearLayoutManager);
        listView.setNestedScrollingEnabled(false);

        deviceAdapter = new DeviceAdapter(getContext(), devices);
        listView.setAdapter(deviceAdapter);

        return root;
    }

    public void newDevice(final Device device){
        this.devices.add(device);
        deviceAdapter.notifyDataSetChanged();
    }

    public void removedDevice(final Device device){
        this.devices.remove(device);
        deviceAdapter.notifyDataSetChanged();
    }

}
