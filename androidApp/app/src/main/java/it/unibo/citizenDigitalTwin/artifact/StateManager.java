package it.unibo.citizenDigitalTwin.artifact;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Room;
import cartago.OPERATION;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.notification.Notification;
import it.unibo.citizenDigitalTwin.db.AppDatabase;
import it.unibo.citizenDigitalTwin.db.dao.DataDAO;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

/**
 * Artifact that represent the current state of the user
 */
public class StateManager extends JaCaArtifact {

    private static final String PROP_STATE = "state";
    private static final String PROP_NOTIFICATIONS = "notifications";

    private DataDAO dbState;

    public void init() {
        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, AppDatabase.DB_NAME).build();
        this.dbState = db.dataDao();
        defineObsProperty(PROP_STATE, new State());
        defineObsProperty(PROP_NOTIFICATIONS, new ArrayList<Notification>());

        /* RxJava Flowable */
        dbState.getAll().map(State::new).forEach(state -> {
            beginExternalSession();
            updateObsProperty(PROP_STATE, state);
            endExternalSession(true);
        });
    }

    @OPERATION
    public void updateState(final List<Data> dataList) {
        dbState.insertAll(dataList);
    }

    @OPERATION
    public void updateStateFromSingleData(final Data newData){
        //TODO set user uri in the feeder
        dbState.insert(newData);
    }

    @OPERATION
    public void updateNotifications(final List<Notification> notifications){

    }

}
