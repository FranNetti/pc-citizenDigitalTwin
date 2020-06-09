package it.unibo.citizenDigitalTwin.artifact;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import cartago.OpFeedbackParam;
import it.unibo.citizenDigitalTwin.data.category.LeafCategory;
import it.unibo.citizenDigitalTwin.data.connection.CommunicationStandard;
import it.unibo.citizenDigitalTwin.data.connection.channel.ChannelException;
import it.unibo.citizenDigitalTwin.data.connection.channel.HttpChannel;
import it.unibo.citizenDigitalTwin.data.connection.channel.OkHttpChannel;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.LoginResult;
import it.unibo.citizenDigitalTwin.db.entity.notification.DataNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.MessageNotification;
import it.unibo.citizenDigitalTwin.db.entity.notification.Notification;
import it.unibo.citizenDigitalTwin.db.entity.Feeder;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.citizenDigitalTwin.db.entity.data.DataBuilder;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;
import it.unibo.citizenDigitalTwin.data.connection.channel.HttpChannel.Header;

import static it.unibo.citizenDigitalTwin.data.connection.channel.HttpChannel.Header.*;

/**
 * Artifact that handles the communication with the backend
 */
public class ConnectionManager extends JaCaArtifact {

    private static final long WAIT_TIME = 5000;
    private static final double TOKEN_TTL_REDUCTION_FACTOR = 0.8;
    private static final String TAG = "[ConnectionManager]";

    private static final String PROP_TOKEN = "token";
    private static final String MSG_REFRESH_TOKEN_FAILED = "refreshTokenFailed";

    private static final String LOGIN_RES = "/citizens/login";
    private static final String REFRESH_TOKEN_RES = "/citizens/refreshToken";

    private static final String ID = "id";
    private static final String VALUE = "value";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "token";
    private static final String EXPIRATION_IN_MINUTE = "expirationInMinute";
    private static final String USER = "user";
    private static final String IDENTIFIER = "identifier";

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

    private HttpChannel cdtChannel;
    private HttpChannel authorizationChannel;
    private long id = 0L;

    void init(final String cdtUrl, final String authorizationUrl) {
        execInternalOp("generateData");
        cdtChannel = new OkHttpChannel(cdtUrl);
        authorizationChannel = new OkHttpChannel(authorizationUrl);
    }

    @OPERATION
    void send(final Data data) {
        try {
            final JSONObject json = new JSONObject()
                    .put(ID,id++).put(VALUE,data.toJson());
            cdtChannel.send("ciccio",json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OPERATION
    void refreshToken() {
        try {
            final CompletableFuture<ChannelResponse> promise = authorizationChannel
                    .post(REFRESH_TOKEN_RES,new JSONObject())
                    .exceptionally(throwable -> ((ChannelException) throwable).getResponse());
            checkRefreshTokenData(promise.get());
        } catch (final InterruptedException | ExecutionException e) {
            Log.e(TAG,"Error in refreshToken: " + e.getLocalizedMessage());
            signal(MSG_REFRESH_TOKEN_FAILED);
        }
    }

    @OPERATION
    void doLogin(final String username, final String password, final OpFeedbackParam<LoginResult> result, final OpFeedbackParam<Boolean> logged) {
        /*try {
            final CompletableFuture<ChannelResponse> promise = authorizationChannel
                    .post(LOGIN_RES,new JSONObject().put(EMAIL,username).put(PASSWORD,password))
                    .exceptionally(throwable -> ((ChannelException) throwable).getResponse());
            final ChannelResponse response = promise.get();
            checkLoginData(response,result);
        } catch (final JSONException e) {
            Log.e(TAG,"Error in doLogin: " + e.getLocalizedMessage());
            result.set(LoginResult.loginFailed(LoginResult.APPLICATION_ERROR));
        } catch (final InterruptedException | ExecutionException e) {
            Log.e(TAG,"Error in doLogin: " + e.getLocalizedMessage());
            result.set(LoginResult.loginFailed(HttpURLConnection.HTTP_INTERNAL_ERROR));
        }*/
        result.set(LoginResult.loginSuccessful("pantofole"));
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

    private void checkRefreshTokenData(final ChannelResponse response) {
        try {
            final Optional<Pair<String,Integer>> token = getTokenData(response);
            if (token.isPresent()) {
                updateObsProperty(PROP_TOKEN,token.get().first,token.get().second);
            } else {
                signal(MSG_REFRESH_TOKEN_FAILED);
            }
        } catch (final NoSuchElementException | JSONException e) {
            Log.e(TAG,"Error in checkRefreshTokenData: " + e.getLocalizedMessage());
            signal(MSG_REFRESH_TOKEN_FAILED);
        }
    }

    private void checkLoginData(final ChannelResponse response, final OpFeedbackParam<LoginResult> result) {
        try {
            final Optional<Pair<String,Integer>> token = getTokenData(response);
            if (token.isPresent()) {
                defineObsProperty(PROP_TOKEN,token.get().first,token.get().second);
                final JSONObject data = response.getData().get();
                result.set(LoginResult.loginSuccessful(data.getJSONObject(USER).getString(IDENTIFIER)));
            } else {
                result.set(LoginResult.loginFailed(response.getCode()));
            }
        } catch (final NoSuchElementException | JSONException e) {
            Log.e(TAG,"Error in checkLoginData: " + e.getLocalizedMessage());
            result.set(LoginResult.loginFailed(LoginResult.MALFORMED_RECEIVED_DATA));
        }
    }

    private Optional<Pair<String,Integer>> getTokenData(final ChannelResponse response) throws JSONException, NoSuchElementException {
        if (response.getCode() == LoginResult.LOGIN_SUCCESS) {
            final JSONObject data = response.getData().get();
            final int ttl = (int)(data.getInt(EXPIRATION_IN_MINUTE) * 60 * 1000 * TOKEN_TTL_REDUCTION_FACTOR);
            final String token = data.getString(TOKEN);
            final Map<Header,String> headers = new HashMap<>();
            headers.put(AUTHORIZATION,token);
            cdtChannel.setDefaultHeaders(headers);
            authorizationChannel.setDefaultHeaders(headers);
            return Optional.of(Pair.create(token,ttl));
        }
        return Optional.empty();
    }


}
