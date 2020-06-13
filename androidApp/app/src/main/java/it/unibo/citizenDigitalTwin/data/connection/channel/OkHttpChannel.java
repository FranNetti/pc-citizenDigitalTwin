package it.unibo.citizenDigitalTwin.data.connection.channel;

import android.util.Log;
import android.util.Pair;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import it.unibo.citizenDigitalTwin.data.Observable;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class OkHttpChannel implements HttpChannel {

    private static final String TAG = "HTTP_CHANNEL";
    private static final String WS_PROTOCOL = "ws://";
    private static final String HTTP_PROTOCOL = "http://";
    private static final int CHANNEL_CLOSED = 800;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final String restBaseUrl;
    private final String wsBaseUrl;
    private Headers defaultHeaders;
    private final OkHttpClient client;

    private enum ChannelState {
        CREATED,OPENED,FAILED,NON_EXISTENT
    }

    private final Map<String, Pair<Observable<JSONObject>,Pair<WebSocket, ChannelState>>> subscriptions;
    private final Map<String, List<BiConsumer<Throwable,String>>> failureHandlers;

    public OkHttpChannel(final String baseUrl) {
        this(baseUrl,new HashMap<>());
    }

    public OkHttpChannel(final String baseUrl, final Map<String,String> defaultHeaders) {
        this.restBaseUrl = HTTP_PROTOCOL + baseUrl;
        this.wsBaseUrl = WS_PROTOCOL + baseUrl;
        this.client = new OkHttpClient();
        this.subscriptions = new HashMap<>();
        this.failureHandlers = new HashMap<>();
        this.defaultHeaders = Headers.of(defaultHeaders);
    }

    @Override
    public synchronized void setDefaultHeaders(final Map<Header,String> defaultHeaders) {
        this.defaultHeaders = Headers.of(
                defaultHeaders.entrySet().stream()
                    .collect(Collectors.toMap(e -> e.getKey().getName(), Map.Entry::getValue))
        );
    }

    @Override
    public synchronized void closeChannel(final String resource) {
        if (this.subscriptions.containsKey(resource)) {
            Objects.requireNonNull(this.subscriptions.remove(resource)).second.first.close(CHANNEL_CLOSED,null);
            this.failureHandlers.remove(resource);
        }
    }

    @Override
    public synchronized CompletableFuture<ChannelResponse> patch(final String resource, final JSONObject data) {
        final RequestBody body = RequestBody.create(data.toString(), JSON);
        final Request request = request(resource)
                .patch(body)
                .build();
        final CompletableFuture<ChannelResponse> result = new CompletableFuture<>();
        client.newCall(request).enqueue(callback(result));
        return result;
    }

    @Override
    public synchronized CompletableFuture<ChannelResponse> post(String resource, JSONObject data) {
        final RequestBody body = RequestBody.create(data.toString(), JSON);
        final Request request = request(resource)
                .post(body)
                .build();
        final CompletableFuture<ChannelResponse> result = new CompletableFuture<>();
        client.newCall(request).enqueue(callback(result));
        return result;
    }

    @Override
    public synchronized CompletableFuture<ChannelResponse> get(final String resource) {
        final Request request = request(resource)
                .get()
                .build();
        final CompletableFuture<ChannelResponse> result = new CompletableFuture<>();
        client.newCall(request).enqueue(callback(result));
        return result;
    }

    @Override
    public synchronized CompletableFuture<Boolean> send(final String resource, final JSONObject data) {
        createResourceChannelIfNecessary(resource);
        final CompletableFuture<Boolean> futureResult = new CompletableFuture<>();
        if (subscriptions.containsKey(resource) && subscriptions.get(resource).second.second == ChannelState.OPENED) {
            final boolean result = subscriptions.get(resource).second.first.send(data.toString());
            futureResult.complete(result);
        } else {
            futureResult.complete(false);
        }
        return futureResult;
    }

    @Override
    public synchronized void subscribe(final Object subscriber, final String resource, final Consumer<JSONObject> data, final BiConsumer<Throwable,String> onFailure) {
        createResourceChannelIfNecessary(resource);
        Objects.requireNonNull(subscriptions.get(resource)).first.subscribe(subscriber, data);
        final List<BiConsumer<Throwable,String>> newList = new LinkedList<>();
        newList.add(onFailure);
        failureHandlers.merge(resource, newList,(oldL,newL) -> {
           oldL.addAll(newL);
           return oldL;
        });
    }

    @Override
    public synchronized void unsubscribe(final Object subscriber, final String resource) {
        subscriptions.computeIfPresent(resource, (res,subscription) -> {
            subscription.first.unsubscribe(subscriber);
            return subscription;
        });
    }

    private Request.Builder request() {
        return new Request.Builder().headers(defaultHeaders);
    }

    private Request.Builder request(final String resource) {
        return request().url(restBaseUrl +resource);
    }

    private Request.Builder wsRequest(final String resource) {
        return request().url(wsBaseUrl +resource);
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
                    result.complete(ChannelResponse.successfulResponse(response.code(),new JSONObject()));
                }
            }

        };
    }

    private WebSocketListener webSocketListener(final String resource, final Observable<JSONObject> obs) {
        return new WebSocketListener() {

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                super.onFailure(webSocket, t, response);
                synchronized (OkHttpChannel.this) {
                    Objects.requireNonNull(OkHttpChannel.this.failureHandlers.remove(resource))
                            .forEach(x -> x.accept(t,resource));
                    OkHttpChannel.this.subscriptions.remove(resource);
                    webSocket.cancel();
                }
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                try {
                    obs.set(new JSONObject(text));
                } catch (final JSONException e) {
                    Log.e(TAG,e.toString(),e);
                }
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                synchronized (OkHttpChannel.this) {
                    final Pair<Observable<JSONObject>,Pair<WebSocket, ChannelState>> value =
                            Objects.requireNonNull(OkHttpChannel.this.subscriptions.get(resource));
                    OkHttpChannel.this.subscriptions.put(
                            resource,
                            Pair.create(
                                    value.first,
                                    Pair.create(value.second.first, ChannelState.OPENED)
                            )
                    );
                }
            }
        };
    }

    private ChannelException exception(final String message) {
        return new ChannelException(ChannelResponse.errorResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, message));
    }

    private void createResourceChannelIfNecessary(final String resource) {
        if (!subscriptions.containsKey(resource)) {
            final Request request = wsRequest(resource).build();
            final Observable<JSONObject> obs = new Observable<>();
            final WebSocket ws = client.newWebSocket(request, webSocketListener(resource,obs));
            subscriptions.put(resource,Pair.create(obs,Pair.create(ws, ChannelState.CREATED)));
        }
    }
}
