package it.unibo.citizenDigitalTwin.artifact;

import java.util.List;

import androidx.room.Room;
import cartago.OPERATION;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.db.AppDatabase;
import it.unibo.citizenDigitalTwin.db.dao.DataDAO;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

/**
 * Artifact that represent the current state of the user
 */
public class StateManager extends JaCaArtifact {

    private static final String PROP_STATE = "state";

    private DataDAO dbState;

    public void init() {

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, AppDatabase.DB_NAME).build();
        this.dbState = db.dataDao();
        defineObsProperty(PROP_STATE, new State());

        /* RxJava Flowable */
        dbState.getAll().forEach(dataList -> {
            beginExternalSession();
            updateObsProperty(PROP_STATE, new State(dataList));
            endExternalSession(true);
        });
    }

    @OPERATION
    void updateState(final List<Data> dataList) {
        dbState.insertAll(dataList);
    }

    @OPERATION
    void updateStateFromSingleData(final Data newData){
        dbState.insert(newData);
    }

}
