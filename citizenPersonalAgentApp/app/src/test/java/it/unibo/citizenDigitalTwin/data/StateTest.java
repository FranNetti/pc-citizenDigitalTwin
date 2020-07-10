package it.unibo.citizenDigitalTwin.data;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.unibo.citizenDigitalTwin.data.category.GroupCategory;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.db.entity.data.DataBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StateTest {

    private static final String FEEDER_NAME = "feederTest";
    private static final String DEFAULT_VALUE = "defaultValue";
    private static final String DEFAULT_VALUE_2 = "defaultValue2";
    private static final String DEFAULT_VALUE_3 = "defaultValue3";

    private final Feeder feeder = new Feeder(FEEDER_NAME);
    private final Data data = new DataBuilder()
            .leafCategory(LeafCategory.NAME)
            .feeder(feeder)
            .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, DEFAULT_VALUE)
            .build();
    private final Data data2 = new DataBuilder()
            .leafCategory(LeafCategory.SURNAME)
            .feeder(feeder)
            .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, DEFAULT_VALUE_2)
            .build();
    private final Data data3 = new DataBuilder()
            .leafCategory(LeafCategory.TEMPERATURE)
            .feeder(feeder)
            .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, DEFAULT_VALUE_3)
            .build();

    @Test
    public void isEmptyOnCreation() {
        final State state = new State();
        assertTrue(state.getState().isEmpty());
    }

    @Test
    public void isCreatedWithCorrectData() {
        final State state = new State(Arrays.asList(data, data2, data3));
        final Map<LeafCategory, Data> map = Stream.of(data, data2, data3)
                .collect(Collectors.toMap(Data::getLeafCategory, Function.identity()));

        assertEquals(map, state.getState());
    }

    @Test
    public void addsNewDataCorrectly() {
        final State state = new State();
        final State newState = state.addData(data);

        assertEquals(newState.getData(data.getLeafCategory()).get(), data);
    }

    @Test
    public void addMultipleDataCorrectly() {
        final State state = new State();
        final Map<LeafCategory, Data> map = Stream.of(data, data2, data3)
                .collect(Collectors.toMap(Data::getLeafCategory, Function.identity()));

        final State newState = state.addMultipleData(map);
        assertEquals(map, newState.getState());
    }

    @Test
    public void replaceOldDataCorrectly() {
        final State state = new State();
        final LeafCategory category = data.getLeafCategory();
        final Data newData = new DataBuilder()
                .leafCategory(category)
                .feeder(new Feeder(FEEDER_NAME))
                .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, DEFAULT_VALUE_2)
                .build();
        final State newState = state.addData(data).addData(newData);

        assertEquals(newState.getData(category).get(), newData);
    }

    @Test
    public void returnsCorrectDataBasedOnGroupCategory() {
        final State state = new State();
        final GroupCategory groupCategory = GroupCategory.PERSONAL_DATA;
        final Data data = new DataBuilder()
                .leafCategory(LeafCategory.NAME)
                .feeder(feeder)
                .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, DEFAULT_VALUE_2)
                .build();
        final Data data2 = new DataBuilder()
                .leafCategory(LeafCategory.SURNAME)
                .feeder(feeder)
                .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, DEFAULT_VALUE_2)
                .build();
        final Data data3 = new DataBuilder()
                .leafCategory(LeafCategory.TEMPERATURE)
                .feeder(feeder)
                .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, DEFAULT_VALUE_3)
                .build();

        final State newState = state.addData(data).addData(data2).addData(data3);

        assertEquals(2, newState.getDataFromGroupCategory(groupCategory).size());
        assertTrue(newState.getDataFromGroupCategory(groupCategory).containsAll(Arrays.asList(data, data2)));
    }


}
