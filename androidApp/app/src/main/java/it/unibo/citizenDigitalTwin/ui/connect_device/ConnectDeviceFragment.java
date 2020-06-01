package it.unibo.citizenDigitalTwin.ui.connect_device;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.artifact.MainUI;
import it.unibo.citizenDigitalTwin.data.device.type.Device;
import it.unibo.citizenDigitalTwin.ui.util.FragmentWithId;

public class ConnectDeviceFragment extends FragmentWithId implements ConnectDeviceAdapter.ConnectDeviceAdapterListener,
        ConnectDeviceDialog.ConnectDeviceDialogListener {

    private static final String FRAGMENT_ID = "CONNECT_DEVICE";
    private static final String ARTIFACT = "artifact";
    private static final String DIALOG_TAG = "connectDeviceDialog";

    public static ConnectDeviceFragment getInstance(final MainUI.MainUIMediator artifact){
        final ConnectDeviceFragment fragment = new ConnectDeviceFragment();
        final Bundle bundle = new Bundle();
        if(Objects.nonNull(artifact)){
            bundle.putParcelable(ARTIFACT, artifact);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    private List<Device> pairedDevices = new ArrayList<>();
    private List<Device> discoveredDevices = new ArrayList<>();
    private MainUI.MainUIMediator artifact;

    private ConnectDeviceAdapter pairedAdapter;
    private ConnectDeviceAdapter discoveredAdapter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Objects.nonNull(getArguments())){
            artifact = getArguments().getParcelable(ARTIFACT);
        }
    }

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_connect_device, container, false);

        final RecyclerView discoveredDevicesRV = root.findViewById(R.id.discoveredDevicesRecyclerView);
        final RecyclerView pairedDevicesRV = root.findViewById(R.id.pairedDevicesRecyclerView);

        final LinearLayoutManager linearLayoutManagerD = new LinearLayoutManager(this.getContext());
        final LinearLayoutManager linearLayoutManagerP = new LinearLayoutManager(this.getContext());
        final DividerItemDecoration dividerItemDecorationD = new DividerItemDecoration(
                discoveredDevicesRV.getContext(),
                linearLayoutManagerD.getOrientation()
        );
        final DividerItemDecoration dividerItemDecorationP = new DividerItemDecoration(
                pairedDevicesRV.getContext(),
                linearLayoutManagerP.getOrientation()
        );

        discoveredDevicesRV.addItemDecoration(dividerItemDecorationD);
        discoveredDevicesRV.setLayoutManager(linearLayoutManagerD);
        pairedDevicesRV.addItemDecoration(dividerItemDecorationP);
        pairedDevicesRV.setLayoutManager(linearLayoutManagerP);

        pairedAdapter = new ConnectDeviceAdapter(getContext(), this.pairedDevices, this);
        discoveredAdapter = new ConnectDeviceAdapter(getContext(), this.discoveredDevices, this);

        pairedDevicesRV.setAdapter(pairedAdapter);
        discoveredDevicesRV.setAdapter(discoveredAdapter);

        return root;
    }

    @Override
    public String getFragmentId() {
        return FRAGMENT_ID;
    }

    @Override
    public void onDeviceSelected(final Device device) {
        final DialogFragment fragment = ConnectDeviceDialog.getInstance(device);
        fragment.show(ConnectDeviceFragment.this.getChildFragmentManager(), DIALOG_TAG);
    }

    @Override
    public void onPositiveButtonClick(final Device device, final String model) {
        if(Objects.nonNull(artifact)){
            artifact.connectToDevice(device, model);
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onNegativeButtonClick() {
        if(Objects.nonNull(artifact)){
            artifact.newSubView(this);
        }
    }

    public void updateDiscoveredDevices(final List<Device> devices){
        this.discoveredDevices.clear();
        this.discoveredDevices.addAll(devices);
        this.discoveredAdapter.notifyDataSetChanged();
    }

    public void updatePairedDevices(final List<Device> devices){
        this.pairedDevices.clear();
        this.pairedDevices.addAll(devices);
        this.pairedAdapter.notifyDataSetChanged();
    }
}
