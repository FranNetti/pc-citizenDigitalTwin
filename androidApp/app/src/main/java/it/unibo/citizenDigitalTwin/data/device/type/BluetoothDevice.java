package it.unibo.citizenDigitalTwin.data.device.type;

import android.bluetooth.BluetoothSocket;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class BluetoothDevice extends AbstractDevice {

    private android.bluetooth.BluetoothDevice device;
    private BluetoothSocket socket;

    public BluetoothDevice(final android.bluetooth.BluetoothDevice device) {
        super(device.getName(), new ArrayList<>());
        this.device = device;
    }

    @Override
    public CommunicationType getCommunicationType() {
        return CommunicationType.BT;
    }

    public android.bluetooth.BluetoothDevice getAndroidBluetoothDevice(){
        return this.device;
    }

    public Optional<BluetoothSocket> getSocket() {
        return Optional.ofNullable(socket);
    }

    public void setSocket(final BluetoothSocket socket) {
        this.socket = socket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BluetoothDevice that = (BluetoothDevice) o;
        return device.equals(that.device);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), device);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(device, flags);
    }

    public static final Creator<BluetoothDevice> CREATOR = new Creator<BluetoothDevice>() {
        @Override
        public BluetoothDevice createFromParcel(final Parcel in) {
            final android.bluetooth.BluetoothDevice device = in.readParcelable(getClass().getClassLoader());
            return new BluetoothDevice(device);
        }

        @Override
        public BluetoothDevice[] newArray(final int size) {
            return new BluetoothDevice[size];
        }
    };
}
