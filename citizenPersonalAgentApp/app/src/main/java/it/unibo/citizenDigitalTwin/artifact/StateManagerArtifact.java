package it.unibo.citizenDigitalTwin.artifact;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import cartago.OPERATION;
import cartago.OpFeedbackParam;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.LoginResult;
import it.unibo.citizenDigitalTwin.db.AppDatabase;
import it.unibo.citizenDigitalTwin.db.dao.DataDAO;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

/**
 * Artifact that represent the current state of the user.
 * @obsProperty logged the identifier of the current logged user
 * @obsProperty state the current user state
 * @obsProperty notifications the current user notifications
 */
public class StateManagerArtifact extends JaCaArtifact {

    private static final String TAG = "[StateManagerArtifact]";

    private static final String PROP_LOGGED = "logged";
    private static final String PROP_STATE = "state";
    private static final String MSG_NEW_GENERATED_DATA = "newGeneratedData";
    private static final String MSG_NOT_LOGGED = "loginFailed";

    private DataDAO dbState;

    public void init() {
        final AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        this.dbState = db.dataDao();
        defineObsProperty(PROP_STATE, new State());

        /* RxJava Flowable */
        dbState.getAll().map(State::new).forEach(state -> {
            beginExternalSession();
            updateObsProperty(PROP_STATE, state);
            endExternalSession(true);
        });
    }

    /**
     * Check if the login procedure has been successful.
     * @param result the result of the login
     */
    @OPERATION
    public void checkIfLogged(final LoginResult result){
        if(result.isSuccessful()){
            defineObsProperty(PROP_LOGGED, result.getUri().get());
        } else {
            signal(MSG_NOT_LOGGED, result.getErrorMessage(getApplicationContext()).get());
        }
    }

    /**
     * Update the current state with a list of new data.
     * @param dataList the list of data
     */
    @OPERATION
    public void updateState(final List<Data> dataList, final OpFeedbackParam<List<Data>> validDataRes) {
        final Map<LeafCategory,Data> state = ((State)getObsProperty(PROP_STATE).getValue()).getState();
        final List<Data> validData = dataList.stream()
                .filter(x -> this.isMoreRecentThanStoredData(x, state))
                .collect(Collectors.groupingBy(Data::getLeafCategory,Collectors.toList()))
                .values().stream()
                .map(data -> data.stream().max((a, b) -> Long.compare(a.getDate().getTime(), b.getDate().getTime())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        dbState.insertAll(validData);
        validDataRes.set(validData);
    }

    /**
     * Update the state with a new data.
     * @param newData the new data
     */
    @OPERATION
    public void updateStateFromSingleData(final Data newData){
        if (hasObsProperty(PROP_LOGGED))
            newData.getFeeder().setUri(getObsProperty(PROP_LOGGED).stringValue());
        dbState.insert(newData);
        signal(MSG_NEW_GENERATED_DATA,newData);
    }

    private boolean isMoreRecentThanStoredData(final Data newData, final Map<LeafCategory,Data> state){
        return !state.containsKey(newData.getLeafCategory()) || state.get(newData.getLeafCategory()).getDate().before(newData.getDate());
    }

}
