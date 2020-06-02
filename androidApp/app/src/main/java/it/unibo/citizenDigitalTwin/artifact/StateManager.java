package it.unibo.citizenDigitalTwin.artifact;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import cartago.OPERATION;
import cartago.ObsProperty;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.db.entity.data.DataBuilder;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

/**
 * Artifact that represent the current state of the user
 */
public class StateManager extends JaCaArtifact {

    private static final String PROP_STATE = "state";

    public void init() {
        State state = new State();
        state = state.addData(LeafCategory.NAME, new DataBuilder()
                .dataCategory(LeafCategory.NAME)
                .value("Francesco")
                .feeder(new Feeder("", "Francesco Grandinetti"))
                .build());
        defineObsProperty(PROP_STATE, state);
    }

    @OPERATION
    void updateState(final List<Data> dataList) {
        final ObsProperty stateProp = getObsProperty(PROP_STATE);
        State state = (State)stateProp.getValue();

        final Map<LeafCategory,Data> newData = dataList.stream()
                .collect(Collectors.toMap(Data::getLeafCategory, Function.identity()));

        state = state.addMultipleData(newData);
        stateProp.updateValue(state);
    }

}
