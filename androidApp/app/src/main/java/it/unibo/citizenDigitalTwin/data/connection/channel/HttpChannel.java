package it.unibo.citizenDigitalTwin.data.connection.channel;

import android.util.Pair;

import org.json.JSONObject;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;

public interface HttpChannel {

    enum Header {

        AUTHORIZATION("Authorization");

        private final String name;

        Header(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    void setDefaultHeaders(Map<Header,String> defaultHeaders);
    void closeChannel(String resource);
    CompletableFuture<ChannelResponse> patch(String resource, JSONObject data);
    CompletableFuture<ChannelResponse> post(String resource, JSONObject data);
    CompletableFuture<ChannelResponse> get(String resource);
    CompletableFuture<Boolean> send(String resource, JSONObject data);
    void subscribe(Object subscriber, String resource, Consumer<JSONObject> data);
    void unsubscribe(Object subscriber, String resource);
}
