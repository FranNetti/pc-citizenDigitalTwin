package it.unibo.citizenDigitalTwin.data.device.sensor;

import java.util.Random;

import it.unibo.citizenDigitalTwin.data.category.LeafCategory;

public class MockTemperatureSensor implements Sensor<Double> {

    private static final double MAX_TEMP = 39.5;
    private static final double MIN_TEMP = 34.5;

    @Override
    public Double getData() {
        return (new Random()).nextDouble() * (MAX_TEMP - MIN_TEMP) + MIN_TEMP;
    }

    @Override
    public LeafCategory getLeafCategory() {
        return LeafCategory.TEMPERATURE;
    }

}
