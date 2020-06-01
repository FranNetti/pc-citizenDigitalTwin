package it.unibo.citizenDigitalTwin.ui.connect_device;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.device.type.BluetoothDevice;
import it.unibo.citizenDigitalTwin.data.device.type.Device;

class ConnectDeviceAdapter extends RecyclerView.Adapter<ConnectDeviceAdapter.NewDeviceHolder> {

    static class NewDeviceHolder extends RecyclerView.ViewHolder {

        final ConstraintLayout container;
        final ImageView deviceTypeIcon;
        final TextView deviceName;

        NewDeviceHolder(final ConstraintLayout layout) {
            super(layout);
            this.container = layout;
            this.deviceTypeIcon = layout.findViewById(R.id.deviceTypeIcon);
            this.deviceName = layout.findViewById(R.id.deviceNameText);
        }
    }

    interface ConnectDeviceAdapterListener {
        void onDeviceSelected(Device device);
    }

    private final List<Device> deviceList;
    private final Context context;
    private final ConnectDeviceAdapterListener listener;

    ConnectDeviceAdapter(final Context context, final List<Device> devices, final ConnectDeviceAdapterListener listener){
        this.context = context;
        this.deviceList = devices;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewDeviceHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_found_item, parent, false);
        return new NewDeviceHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewDeviceHolder holder, final int position) {
        final Device device = this.deviceList.get(position);
        holder.deviceTypeIcon.setImageDrawable(device.getCommunicationType().getDisplayIcon(context));
        String name = device.getName();
        if(Objects.isNull(name)){
            switch (device.getCommunicationType()){
                case BT:
                    name = ((BluetoothDevice)device).getAndroidBluetoothDevice().getAddress();
                    break;
                default: name = context.getString(R.string.device_name_not_found);
            }
        }
        holder.deviceName.setText(name);
        holder.container.setOnClickListener(ev -> listener.onDeviceSelected(device));
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

}
