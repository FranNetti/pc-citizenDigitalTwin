package it.unibo.citizenDigitalTwin.data.device.type;

import android.os.Parcel;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public class AbstractDeviceTest {

    private static class TestDevice extends AbstractDevice {

        TestDevice(final String name){
            super(name);
        }

        @Override
        public CommunicationType getCommunicationType() {
            return CommunicationType.MOCK;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }
    }

    private static final String TEST_NAME = "testName";

    @Test
    public void hasCorrectName() {
        final TestDevice testDevice = new TestDevice(TEST_NAME);
        assertEquals(TEST_NAME, testDevice.getName());
    }

    @Test
    public void hasEmptyCategories() {
        final TestDevice testDevice = new TestDevice(TEST_NAME);
        assertTrue(testDevice.getCategories().isEmpty());
    }

    @Test
    public void setCategoriesCorrectly() {
        final TestDevice testDevice = new TestDevice(TEST_NAME);
        final List<LeafCategory> categories = Arrays.asList(LeafCategory.NAME, LeafCategory.SURNAME);
        testDevice.setCategories(categories);
        assertEquals(categories, testDevice.getCategories());
    }

}
