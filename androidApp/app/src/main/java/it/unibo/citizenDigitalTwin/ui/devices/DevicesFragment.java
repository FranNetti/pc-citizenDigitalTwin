package it.unibo.citizenDigitalTwin.ui.devices;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.artifact.MainUI;
import it.unibo.citizenDigitalTwin.data.device.type.Device;
import it.unibo.citizenDigitalTwin.ui.connect_device.ConnectDeviceFragment;
import it.unibo.citizenDigitalTwin.ui.util.FragmentWithId;

public class DevicesFragment extends FragmentWithId implements DeviceAdapter.DeviceAdapterListener {

    private static final String FRAGMENT_ID = "DEVICES";
    private static final String ARTIFACT = "artifact";

    public static DevicesFragment getInstance(final MainUI.MainUIMediator artifact){
        final DevicesFragment fragment = new DevicesFragment();
        final Bundle bundle = new Bundle();
        if(Objects.nonNull(artifact)){
            bundle.putParcelable(ARTIFACT, artifact);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    private DeviceAdapter deviceAdapter;
    private NestedScrollView deviceView;
    private TextView emptyDevices;

    private List<Device> devices = new ArrayList<>();
    private MainUI.MainUIMediator artifact;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Objects.nonNull(getArguments())){
            artifact = getArguments().getParcelable(ARTIFACT);
        }
    }

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_devices, container, false);

        emptyDevices = root.findViewById(R.id.emptyDevices);
        deviceView = root.findViewById(R.id.devicesScrollView);

        final RecyclerView listView = root.findViewById(R.id.devicesRecyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getContext());
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                linearLayoutManager.getOrientation());

        listView.addItemDecoration(dividerItemDecoration);
        listView.setLayoutManager(linearLayoutManager);
        listView.setNestedScrollingEnabled(false);

        deviceAdapter = new DeviceAdapter(getContext(), devices, this);
        listView.setAdapter(deviceAdapter);

        /* handle plus button click */
        final FloatingActionButton addDeviceBtn = root.findViewById(R.id.addDeviceBtn);
        addDeviceBtn.setOnClickListener(ev -> addDeviceButtonClicked());

        if(Objects.nonNull(artifact)){
            artifact.newSubView(this);
        }

        return root;
    }

    @Override
    public String getFragmentId() {
        return FRAGMENT_ID;
    }

    @Override
    public void onDisconnectButtonClick(final Device device) {
        if(Objects.nonNull(artifact)) {
            artifact.disconnectFromDevice(device);
        }
    }

    public void updateConnectedDevices(final List<Device> devices){
        this.devices.clear();
        this.devices.addAll(devices);
        deviceAdapter.notifyDataSetChanged();
        if(devices.size() > 0){
            showDevices();
        } else {
            hideDevices();
        }
    }

    public void connectionFailed(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.connection_failed)
                .setPositiveButton(android.R.string.ok, null);
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addDeviceButtonClicked(){
        final FragmentManager fragmentManager = getFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final ConnectDeviceFragment fragment = ConnectDeviceFragment.getInstance(artifact);
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        if(Objects.nonNull(artifact)){
            artifact.newSubView(fragment);
        }
    }

    private void hideDevices(){
        setRightVisualization(false);
    }

    private void showDevices(){
        setRightVisualization(true);
    }

    private void setRightVisualization(final boolean visibleNotifications){
        emptyDevices.setVisibility(visibleNotifications ? View.GONE : View.VISIBLE);
        deviceView.setVisibility(visibleNotifications ? View.VISIBLE : View.GONE);
    }
}
