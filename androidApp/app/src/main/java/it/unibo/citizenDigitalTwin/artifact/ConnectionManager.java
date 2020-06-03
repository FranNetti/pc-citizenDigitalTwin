package it.unibo.citizenDigitalTwin.artifact;

import java.util.Arrays;
import java.util.List;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.db.entity.data.DataBuilder;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

/**
 * Artifact that handles the communication with the backend
 */
public class ConnectionManager extends JaCaArtifact {

    private static final long WAIT_TIME = 5000;
    private final Feeder fakeFeeder = new Feeder("/stefano", "Stefano Righini");
    private List<List<Data>> fakeStates = Arrays.asList(
            Arrays.asList(
                    new DataBuilder()
                            .leafCategory(LeafCategory.NAME)
                            .addInformation("value", "Stefano")
                            .feeder(fakeFeeder)
                            .build(),
                    new DataBuilder()
                            .leafCategory(LeafCategory.SURNAME)
                            .addInformation("value", "Righini")
                            .feeder(fakeFeeder)
                            .build()
            ),
            Arrays.asList(
                    new DataBuilder()
                            .leafCategory(LeafCategory.BIRTHDATE)
                            .addInformation("value", "24-10-1996")
                            .feeder(fakeFeeder)
                            .build()
            ),
            Arrays.asList(
                    new DataBuilder()
                            .leafCategory(LeafCategory.ADDRESS)
                            .addInformation("value", "Via Luca Ghini 4")
                            .feeder(fakeFeeder)
                            .build()
            )
    );

    void init() {
        execInternalOp("generateData");
    }

    @OPERATION
    void send(final List<Data> state) {
        state.forEach(data -> {
            //System.out.println("{data_category: " + data.getLeafCategory() + " | value: " + data.getValue() + "}");
        });
    }

    @OPERATION
    void authenticate(final String username, final String password, final OpFeedbackParam<Boolean> res) {
        //contatta server autenticazione e attende risposta
        res.set(true); // si è autenticato correttamente
    }

    @INTERNAL_OPERATION
    void generateData() {
        final int length = fakeStates.size();
        for (int i = 0; i < length; i++) {
            await_time(WAIT_TIME);
            final List<Data> state = fakeStates.get(i);
            signal("newState",state);
        }
    }


}
