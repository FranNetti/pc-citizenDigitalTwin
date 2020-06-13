package it.unibo.citizenDigitalTwin.artifact;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import cartago.OPERATION;
import cartago.OpFeedbackParam;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import it.unibo.citizenDigitalTwin.data.connection.channel.ChannelException;
import it.unibo.citizenDigitalTwin.data.connection.channel.HttpChannel;
import it.unibo.citizenDigitalTwin.data.connection.channel.OkHttpChannel;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.LoginResult;
import it.unibo.citizenDigitalTwin.db.AppDatabase;
import it.unibo.citizenDigitalTwin.db.dao.PendingUpdateDAO;
import it.unibo.citizenDigitalTwin.db.entity.PendingUpdate;
import it.unibo.citizenDigitalTwin.db.entity.data.Data;
import it.unibo.pslab.jaca_android.core.JaCaArtifact;
import it.unibo.citizenDigitalTwin.data.connection.channel.HttpChannel.Header;

import static it.unibo.citizenDigitalTwin.data.connection.channel.HttpChannel.Header.*;

/**
 * Artifact that handles the communication with the backend
 */
public class ConnectionManager extends JaCaArtifact {

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
    private static final String DATA = "data";
    private static final String STATE_REGEX = "\\/[a-z|A-Z|0-9]+\\/state";

    private HttpChannel cdtChannel;
    private HttpChannel authorizationChannel;
    private Long id = 0L;
    private final Map<Long,JSONArray> pendingUpdates = new HashMap<>();
    private PendingUpdateDAO pendingUpdatesDb;
    private boolean hasToResendPendingUpdates;

    void init(final String cdtUrl, final String authorizationUrl) {
        cdtChannel = new OkHttpChannel(cdtUrl + CDT_CHANNEL_BASE_PATH);
        authorizationChannel = new OkHttpChannel(authorizationUrl);
        pendingUpdatesDb = AppDatabase.getInstance(getApplicationContext()).pendingUpdateDAO();
        hasToResendPendingUpdates = true;
        pendingUpdatesDb.getAll()
                .flatMap(x -> Flowable.create(emitter -> {
                        x.forEach(emitter::onNext);
                        emitter.onComplete();
                    }, BackpressureStrategy.BUFFER)
                )
                .collectInto(new HashMap<Long,JSONArray>(),(map,obj) -> {
                    final PendingUpdate update = (PendingUpdate)obj;
                    map.put(update.getId(),update.getData());
                })
                .subscribe(map -> {
                    synchronized (pendingUpdates) {
                        pendingUpdates.clear();
                        pendingUpdates.putAll(map);
                    }
                },error -> Log.e(TAG,"Error in init: " + error.getLocalizedMessage()));
    }

    @OPERATION
    public void getDigitalState(final String citizenId) {
        final String resource = stateResource(citizenId);
        cdtChannel.get(resource)
                .exceptionally(throwable -> ((ChannelException) throwable).getResponse())
                .thenAcceptAsync(response -> {
                    Log.d(TAG,"State response code: " + response.getCode());
                    Log.d(TAG,"Is response successful: " + response.isSuccessful());
                    if (response.isSuccessful()) {
                        response.getData().ifPresent(this::handleNewState);
                    }
                });
    }

    @OPERATION
    public void onClosing(final String citizenId) {
        hasToResendPendingUpdates = false;
        cdtChannel.closeChannel(stateResource(citizenId));
    }

    @OPERATION
    public void updateDigitalState(final String citizenId, final Data data) {
        try {
            final JSONArray jsonValue = new JSONArray().put(data.toJson());
            final String resource = stateResource(citizenId);
            send(resource,jsonValue);
        } catch (final Exception e) {
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
                final String resource = stateResource(citizenId);
                resendAllPendingUpdates(resource);
                cdtChannel.subscribe(this,
                        resource,
                        this::consumeNewData,
                        this::onChannelFailure
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
            headers.put(AUTHORIZATION,BEARER_TOKEN.getName() + token);
            cdtChannel.setDefaultHeaders(headers);
            authorizationChannel.setDefaultHeaders(headers);
            return Optional.of(Pair.create(token,ttl));
        }
        return Optional.empty();
    }

    private void consumeNewData(final JSONObject json) {
        try {
            Log.d(TAG,"Message received: " + json);
            if (json.has(UPDATED)) {
                final Data data = new Data(json.getJSONObject(UPDATED));
                beginExternalSession();
                signal(MSG_NEW_STATE, Collections.singletonList(data));
                endExternalSession(true);
            } else if (json.has(ID) && json.has(VALUE)) {
                synchronized (pendingUpdates) {
                    pendingUpdatesDb.delete(json.getLong(ID));
                }
            }
        } catch (final JSONException | IllegalArgumentException e) {
            Log.e(TAG,"Error in consumeNewData: " + e.getLocalizedMessage());
        }
    }

    private void onChannelFailure(final Throwable t, final String resource) {
        if (resource.matches(STATE_REGEX)) {
            cdtChannel.subscribe(this,
                    resource,
                    this::consumeNewData,
                    this::onChannelFailure
            );
            resendAllPendingUpdates(resource);
        }
    }

    private void resendAllPendingUpdates(final String resource) {
        if (hasToResendPendingUpdates) {
            final List<JSONArray> toSend;
            synchronized (pendingUpdates) {
                toSend = new ArrayList<>(pendingUpdates.values());
                pendingUpdatesDb.clear();
            }
            toSend.forEach(data -> send(resource,data));
        }
    }

    private void send(final String resource, final JSONArray jsonValue) {
        try {
            final JSONObject json = new JSONObject();
            synchronized (pendingUpdates) {
                json.put(ID,id).put(VALUE,jsonValue);
                pendingUpdatesDb.insert(new PendingUpdate(id++,jsonValue));
            }
            final CompletableFuture<Boolean> result = cdtChannel.send(resource,json);
            if (!result.get()) {
                synchronized (pendingUpdates) {
                    pendingUpdatesDb.delete(json.getLong(ID));
                }
                send(resource,jsonValue);
            }
        } catch (final Exception e) {
            Log.e(TAG,"Error in send: " + e.getLocalizedMessage());
        }
    }

    private String stateResource(final String citizenId) {
        return "/"+citizenId+STATE_RES;
    }

    private void handleNewState(final JSONObject json) {
        try {
            Log.d(TAG,"State received: " + json);
            if (json.has(DATA)) {
                final JSONArray stateData = json.getJSONArray(DATA);
                final List<Data> state = new ArrayList<>();
                for (int i = 0; i < stateData.length(); i++) {
                    try {
                        state.add(new Data(stateData.getJSONObject(i)));
                    } catch (final IllegalArgumentException e) {
                        Log.e(TAG,"Error in handleNewState: " + e.getLocalizedMessage());
                    }
                }
                beginExternalSession();
                signal(MSG_NEW_STATE, state);
                endExternalSession(true);
            }
        } catch (final JSONException e) {
            Log.e(TAG,"Error in handleNewState: " + e.getLocalizedMessage());
        }
    }


}
