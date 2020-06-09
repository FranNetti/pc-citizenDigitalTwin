package it.unibo.citizenDigitalTwin.artifact;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
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
    private static final String MSG_NEW_STATE = "newState";

    private static final String LOGIN_RES = "/login";
    private static final String REFRESH_TOKEN_RES = "/refreshToken";
    private static final String STATE_RES = "/state";
    private static final String CDT_CHANNEL_BASE_PATH = "/citizens";
    private static final String AUTHORIZATION_CHANNEL_BASE_PATH = "/citizens";

    private static final String ID = "id";
    private static final String VALUE = "value";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "token";
    private static final String EXPIRATION_IN_MINUTE = "expirationInMinute";
    private static final String USER = "user";
    private static final String IDENTIFIER = "identifier";
    private static final String UPDATED = "updated";

    private HttpChannel cdtChannel;
    private HttpChannel authorizationChannel;
    private long id = 0L;

    void init(final String cdtUrl, final String authorizationUrl) {
        cdtChannel = new OkHttpChannel(cdtUrl + CDT_CHANNEL_BASE_PATH);
        authorizationChannel = new OkHttpChannel(authorizationUrl + AUTHORIZATION_CHANNEL_BASE_PATH);
    }

    @OPERATION
    public void updateDigitalState(final String citizenId, final Data data) {
        try {
            final JSONObject json = new JSONObject()
                    .put(ID,id++)
                    .put(VALUE,data.toJson());
            cdtChannel.send("/"+citizenId+STATE_RES,json);
            //TODO check result
        } catch (final JSONException e) {
            Log.e(TAG,"Error in send: " + e.getLocalizedMessage());
        }
    }

    @OPERATION
    public void refreshToken() {
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
    public void doLogin(final String username, final String password, final OpFeedbackParam<LoginResult> result, final OpFeedbackParam<Boolean> logged) {
        try {
            final CompletableFuture<ChannelResponse> promise = authorizationChannel
                    .post(LOGIN_RES,new JSONObject().put(EMAIL,username).put(PASSWORD,password))
                    .exceptionally(throwable -> ((ChannelException) throwable).getResponse());
            final ChannelResponse response = promise.get();
            logged.set(checkLoginData(response,result));
        } catch (final JSONException e) {
            Log.e(TAG,"Error in doLogin: " + e.getLocalizedMessage());
            result.set(LoginResult.loginFailed(LoginResult.APPLICATION_ERROR));
            logged.set(false);
        } catch (final InterruptedException | ExecutionException e) {
            Log.e(TAG,"Error in doLogin: " + e.getLocalizedMessage());
            result.set(LoginResult.loginFailed(HttpURLConnection.HTTP_INTERNAL_ERROR));
            logged.set(false);
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

    private boolean checkLoginData(final ChannelResponse response, final OpFeedbackParam<LoginResult> result) {
        try {
            final Optional<Pair<String,Integer>> token = getTokenData(response);
            if (token.isPresent()) {
                defineObsProperty(PROP_TOKEN,token.get().first,token.get().second);
                final JSONObject data = response.getData().get();
                final String citizenId = data.getJSONObject(USER).getString(IDENTIFIER);
                result.set(LoginResult.loginSuccessful(citizenId));
                cdtChannel.subscribe(this,
                        "/"+citizenId+STATE_RES,
                        this::consumeNewData
                );
                return true;
            } else {
                result.set(LoginResult.loginFailed(response.getCode()));
            }
        } catch (final NoSuchElementException | JSONException e) {
            Log.e(TAG,"Error in checkLoginData: " + e.getLocalizedMessage());
            result.set(LoginResult.loginFailed(LoginResult.MALFORMED_RECEIVED_DATA));
        }
        return false;
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

    private void consumeNewData(final JSONObject json) {
        try {
            if (json.has(UPDATED)) {
                final Data data = new Data(json.getJSONObject(UPDATED));
                beginExternalSession();
                signal(MSG_NEW_STATE, Collections.singletonList(data));
                endExternalSession(true);
            } else if (json.has(ID) && json.has(VALUE)) {
                //TODO handle update result
            }
        } catch (final JSONException e) {
            Log.e(TAG,"Error in consumeNewData: " + e.getLocalizedMessage());
        }
    }


}
