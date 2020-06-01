package it.unibo.citizenDigitalTwin.ui.connect_device;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Objects;

import it.unibo.citizenDigitalTwin.R;
import it.unibo.citizenDigitalTwin.data.device.type.Device;

public class ConnectDeviceDialog extends DialogFragment {

    private static final String DEVICE = "device";

    public static ConnectDeviceDialog getInstance(final Device device){
        final ConnectDeviceDialog fragment = new ConnectDeviceDialog();
        final Bundle bundle = new Bundle();
        if(Objects.nonNull(device)){
            bundle.putParcelable(DEVICE, device);
        }
        fragment.setArguments(bundle);
        return fragment;
    }

    interface ConnectDeviceDialogListener {
        void onPositiveButtonClick(Device device, String model);
        void onNegativeButtonClick();
    }

    private Device device;
    private ConnectDeviceDialogListener listener;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            this.device = getArguments().getParcelable(DEVICE);
        }
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        try {
            listener = (ConnectDeviceDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement ConnectDeviceDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.add_device_dialog, null);
        final EditText deviceModel = view.findViewById(R.id.deviceModelEditText);

        builder.setView(view)
                .setTitle(R.string.add_device_title)
                .setPositiveButton(R.string.add_device_positive, null)
                .setNegativeButton(R.string.add_device_negative, (d,i) -> listener.onNegativeButtonClick());

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialog1 -> {
            final Button button = ((AlertDialog) dialog1).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                final String deviceModelText = deviceModel.getText().toString();
                if(deviceModelText.isEmpty() || deviceModelText.trim().isEmpty()){
                    deviceModel.setError(getString(R.string.add_device_error_text));
                    deviceModel.requestFocus();
                } else {
                    listener.onPositiveButtonClick(device, deviceModelText);
                    dismiss();
                }
            });
        });

        return dialog;
    }
}
