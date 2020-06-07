package it.unibo.citizenDigitalTwin.data.connection.channel;

import android.util.Log;
import android.util.Pair;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import it.unibo.citizenDigitalTwin.data.Observable;
import it.unibo.citizenDigitalTwin.data.connection.JsonSerializable;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class HttpChannel implements CommunicationChannel {

    private static final String TAG = "HTTP_CHANNEL";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String baseUrl;
    private final OkHttpClient client;
    private final Map<String, Pair<Observable<JSONObject>,WebSocket>> subscriptions;

    public HttpChannel(final String baseUrl) {
        this.baseUrl = "http://" + baseUrl;
        this.client = new OkHttpClient();
        this.subscriptions = new HashMap<>();
    }

    @Override
    public CompletableFuture<ChannelResponse> patch(final String resource, final JsonSerializable data) {
        final RequestBody body = RequestBody.create(data.toJson().toString(), JSON);
        final Request request = request(resource)
                .patch(body)
                .build();
        final CompletableFuture<ChannelResponse> result = new CompletableFuture<>();
        client.newCall(request).enqueue(callback(result));
        return result;
    }

    @Override
    public CompletableFuture<ChannelResponse> get(final String resource) {
        final Request request = request(resource)
                .get()
                .build();
        final CompletableFuture<ChannelResponse> result = new CompletableFuture<>();
        client.newCall(request).enqueue(callback(result));
        return result;
    }

    @Override
    public CompletableFuture<Boolean> send(String resource, JsonSerializable data) {
        createResourceChannelIfNecessary(resource);
        final CompletableFuture<Boolean> futureResult = new CompletableFuture<>();
        final boolean result = Objects.requireNonNull(subscriptions.get(resource)).second.send(data.toJson().toString());
        futureResult.complete(result);
        return futureResult;
    }

    @Override
    public void subscribe(final Object subscriber, final String resource, final Consumer<JSONObject> data) {
        createResourceChannelIfNecessary(resource);
        Objects.requireNonNull(subscriptions.get(resource)).first.subscribe(subscriber, data);
    }

    @Override
    public void unsubscribe(final Object subscriber, final String resource) {
        subscriptions.computeIfPresent(resource, (res,subscription) -> {
            subscription.first.unsubscribe(subscriber);
            return subscription;
        });
    }

    private Request.Builder request(final String resource) {
        return new Request.Builder().url(baseUrl+resource);
    }

    private Callback callback(final CompletableFuture<ChannelResponse> result) {
        return new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                result.completeExceptionally(exception(e.toString()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    final JSONObject dataResult = new JSONObject(response.body().string());
                    result.complete(ChannelResponse.successfulResponse(response.code(),dataResult));
                } catch (final JSONException | NullPointerException e) {
                    result.completeExceptionally(exception(e.toString()));
                }
            }

        };
    }

    private WebSocketListener webSocketListener(final Observable<JSONObject> obs) {
        return new WebSocketListener() {
            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                try {
                    obs.set(new JSONObject(text));
                } catch (final JSONException e) {
                    Log.e(TAG,e.toString(),e);
                }
            }
        };
    }

    private ChannelException exception(final String message) {
        return new ChannelException(ChannelResponse.errorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, message));
    }

    private void createResourceChannelIfNecessary(final String resource) {
        if (!subscriptions.containsKey(resource)) {
            final Request request = request(resource).get().build();
            final Observable<JSONObject> obs = new Observable<>();
            final WebSocket ws = client.newWebSocket(request, webSocketListener(obs));
            subscriptions.put(resource,Pair.create(obs,ws));
        }
    }
}
