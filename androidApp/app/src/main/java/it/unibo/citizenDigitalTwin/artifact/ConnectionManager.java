package it.unibo.citizenDigitalTwin.artifact;

import java.util.Arrays;
import java.util.List;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationChannel;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;
import it.unibo.citizenDigitalTwin.data.connection.HttpChannel;
import it.unibo.citizenDigitalTwin.data.connection.LoginResult;
import it.unibo.citizenDigitalTwin.db.entity.notification.DataNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.Notification;
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
                            .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, "Stefano")
                            .feeder(fakeFeeder)
                            .build(),
                    new DataBuilder()
                            .leafCategory(LeafCategory.SURNAME)
                            .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, "Righini")
                            .feeder(fakeFeeder)
                            .build()
            ),
            Arrays.asList(
                    new DataBuilder()
                            .leafCategory(LeafCategory.BIRTHDATE)
                            .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, "24-10-1996")
                            .feeder(fakeFeeder)
                            .build()
            ),
            Arrays.asList(
                    new DataBuilder()
                            .leafCategory(LeafCategory.ADDRESS)
                            .addInformation(CommunicationStandard.DEFAULT_VALUE_IDENTIFIER, "Via Luca Ghini 4")
                            .feeder(fakeFeeder)
                            .build()
            )
    );
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
    void extendLogin(final String token, final OpFeedbackParam<Boolean> res) {
        //contatta server autenticazione e attende risposta
        res.set(true); // si è autenticato correttamente
    }

    @OPERATION
    void doLogin(final String username, final String password, final OpFeedbackParam<LoginResult> result){
        result.set(LoginResult.loginSuccessful("youWillPass"));
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
