package it.unibo.citizenDigitalTwin.data.device.type;

import android.os.Parcel;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import androidx.annotation.Nullable;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public class MockDevice implements Device {

    private static final String MOCK_DEVICE_NAME = "Mock-device";
    public static final String MOCK_DEVICE_TEMPERATURE_SENSOR_DATA_IDENTIFIER = "body/temperature";
    public static final String MOCK_DEVICE_HEART_RATE_SENSOR_DATA_IDENTIFIER = "body/heartrate";

    @Override
    public String getName() {
        return MOCK_DEVICE_NAME;
    }

    @Override
    public List<LeafCategory> getCategories() {
        return Arrays.asList(LeafCategory.TEMPERATURE, LeafCategory.HEART_RATE);
    }

    @Override
    public void setCategories(Collection<LeafCategory> categories) {

    }

    @Override
    public CommunicationType getCommunicationType() {
        return CommunicationType.MOCK;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof MockDevice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Creator<MockDevice> CREATOR = new Creator<MockDevice>() {
        @Override
        public MockDevice createFromParcel(final Parcel in) {
            return new MockDevice();
        }

        @Override
        public MockDevice[] newArray(final int size) {
            return new MockDevice[size];
        }
    };
}
