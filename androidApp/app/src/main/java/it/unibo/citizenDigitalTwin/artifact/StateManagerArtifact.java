package it.unibo.citizenDigitalTwin.artifact;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import cartago.OPERATION;
import cartago.ObsProperty;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.category.GroupCategory;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.LoginResult;
import it.unibo.citizenDigitalTwin.db.dao.NotificationDAO;
import it.unibo.citizenDigitalTwin.db.entity.notification.DataNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.Notification;
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
    private static final String PROP_NOTIFICATIONS = "notifications";
    private static final String MSG_NEW_GENERATED_DATA = "newGeneratedData";
    private static final String MSG_NOT_LOGGED = "loginFailed";
    private static final String FIRST_RUN = "first_run";
    private static final String SHARED_PREFERENCES_ID = "it.unibo.cdt.stateManager";

    private DataDAO dbState;
    private NotificationDAO dbNotifications;
    private boolean isFirstRun;

    public void init() {
        final AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        this.dbState = db.dataDao();
        this.dbNotifications = db.notificationDAO();
        defineObsProperty(PROP_STATE, new State());
        defineObsProperty(PROP_NOTIFICATIONS, new ArrayList<Notification>());
        isFirstRun = isFirstRun();

        /* RxJava Flowable */
        dbState.getAll().map(State::new).forEach(state -> {
            beginExternalSession();
            updateObsProperty(PROP_STATE, state);
            endExternalSession(true);
        });

        dbNotifications.getAllMessageNotifications().forEach(notifications -> {
            updatePropNotification(notifications, MessageNotification.class);
        });

        dbNotifications.getAllDataNotifications().forEach(notifications -> {
            updatePropNotification(notifications, DataNotification.class);
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
    public void updateState(final List<Data> dataList) {
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
    }

    /**
     * Generate new notifications from the given list of data.
     * @param dataList the list of data
     */
    @OPERATION
    public void createNotifications(final List<Data> dataList){
        final String myUri = hasObsProperty(PROP_LOGGED) ?
                getObsProperty(PROP_LOGGED).stringValue() : "";
        final Map<LeafCategory,Data> state = ((State)getObsProperty(PROP_STATE).getValue()).getState();
        final List<DataNotification> notifications = dataList.stream()
                .filter(x -> x.getLeafCategory().getGroupCategory() != GroupCategory.PERSONAL_DATA || !isFirstRun)
                .filter(x -> this.isMoreRecentThanStoredData(x, state))
                .filter(x -> x.getFeeder().isResource() && !x.getFeeder().getUri().equals(myUri))
                .collect(Collectors.groupingBy(x -> x.getFeeder().getUri(), Collectors.toSet()))
                .entrySet().stream()
                .map(x -> new DataNotification(x.getKey(), x.getValue().stream().map(Data::getLeafCategory).collect(Collectors.toList())))
                .collect(Collectors.toList());
        dbNotifications.insertDataNotifications(notifications);
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

    /**
     * Set the given notifications as read.
     * @param notifications the notification
     */
    @OPERATION
    public void setNotificationsRead(final List<Notification> notifications){
        final List<MessageNotification> msgNotifications = new ArrayList<>();
        final List<DataNotification> dataNotifications = new ArrayList<>();
        notifications.forEach(x -> {
            x.setRead(true);
            switch (x.getType()){
                case DATA: dataNotifications.add((DataNotification)x); break;
                case MESSAGE: msgNotifications.add((MessageNotification)x); break;
                default: Log.e(TAG, "Unhandled notification in updateNotificationsStatus: " + x.getType());
            }
        });
        if(!msgNotifications.isEmpty()){
            dbNotifications.updateMessageNotifications(msgNotifications);
        }
        if(!dataNotifications.isEmpty()){
            dbNotifications.updateDataNotifications(dataNotifications);
        }
    }

    private void updatePropNotification(final List<? extends Notification> notifications, final Class<?> notClass){
        beginExternalSession();
        ObsProperty prop = getObsProperty(PROP_NOTIFICATIONS);
        final List<Notification> savedNotifications = (List<Notification>)prop.getValue();
        final List<Notification> notificationToDelete = savedNotifications.stream()
                .filter(x -> x.getClass().isAssignableFrom(notClass))
                .collect(Collectors.toList());
        savedNotifications.removeAll(notificationToDelete);
        savedNotifications.addAll(notifications);
        savedNotifications.sort((a,b) -> a.getDate().compareTo(b.getDate()));
        prop.updateValue(savedNotifications);
        endExternalSession(true);
    }

    private boolean isFirstRun() {
        final boolean isFirstRun = readSharedPreference(SHARED_PREFERENCES_ID, FIRST_RUN, true);
        if (isFirstRun) {
            writeSharedPreference(SHARED_PREFERENCES_ID, FIRST_RUN, false);
        }
        return isFirstRun;
    }

    private boolean isMoreRecentThanStoredData(final Data newData, final Map<LeafCategory,Data> state){
        return !state.containsKey(newData.getLeafCategory()) || state.get(newData.getLeafCategory()).getDate().before(newData.getDate());
    }

}
