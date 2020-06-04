package it.unibo.citizenDigitalTwin.artifact;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import it.unibo.citizenDigitalTwin.data.State;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.db.dao.NotificationDAO;
import it.unibo.citizenDigitalTwin.db.entity.notification.DataNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.Notification;
import it.unibo.citizenDigitalTwin.db.AppDatabase;
import it.unibo.citizenDigitalTwin.db.dao.DataDAO;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;

/**
 * Artifact that represent the current state of the user
 */
public class StateManager extends JaCaArtifact {

    private static final String TAG = "[StateManager]";
    private static final String PROP_STATE = "state";
    private static final String PROP_NOTIFICATIONS = "notifications";

    private DataDAO dbState;
    private NotificationDAO dbNotifications;

    private final List<Notification> notifications = Arrays.asList(
            new DataNotification("Pippo e Minnie", Arrays.asList(LeafCategory.NAME)),
            new MessageNotification("Cicciolina", "Vienimi a prendere fustacchione"),
            new DataNotification("Paperino", Arrays.asList(LeafCategory.BIRTHDATE)),
            new MessageNotification("Charles Leclerc", "Corriamo insieme!!!"),
            new DataNotification("Pluto", Arrays.asList(LeafCategory.ADDRESS)),
            new MessageNotification("Stefano Righini", "Vieni a recuperare i prodotti della mia terra"),
            new DataNotification("Topolino", Arrays.asList(LeafCategory.SURNAME)),
            new MessageNotification("Dottor Filippone", "Hai il Covid-19 coglione")
    );

    public void init() {
        final AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        this.dbState = db.dataDao();
        this.dbNotifications = db.notificationDAO();
        defineObsProperty(PROP_STATE, new State());
        defineObsProperty(PROP_NOTIFICATIONS, new ArrayList<Notification>());

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
    public void addNotifications(final List<Notification> notifications){

    }

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
        prop.updateValue(savedNotifications);
        endExternalSession(true);
    }

}
