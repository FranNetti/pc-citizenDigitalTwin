package it.unibo.citizenDigitalTwin.ui.devices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import it.unibo.citizenDigitalTwin.R;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceHolder> {

    static class DeviceHolder extends RecyclerView.ViewHolder {

        final TextView deviceName;
        final TextView deviceCharacteristics;
        final Button deleteBtn;

        DeviceHolder(final ConstraintLayout layout) {
            super(layout);
            this.deviceName = layout.findViewById(R.id.deviceNameText);
            this.deviceCharacteristics = layout.findViewById(R.id.deviceCharacteristicsText);
            this.deleteBtn = layout.findViewById(R.id.disconnectBtn);
        }
    }

    private final List<Device> devices;
    private final Context context;

    DeviceAdapter(final Context context, final List<Device> devices){
        this.devices = devices;
        this.context = context;
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        return new DeviceHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceHolder holder, final int position) {
        final Device device = devices.get(position);
        holder.deviceName.setText(device.getName());
        holder.deviceCharacteristics.setText(
                device.getCategories().stream()
                    .map(x -> x.getDisplayName(context))
                    .reduce((a,b) -> a + ", " + b)
                    .orElse("")
        );
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

}
