package it.unibo.citizenDigitalTwin.data.connection.channel;

import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import it.unibo.citizenDigitalTwin.data.connection.JsonSerializable;
import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;

public interface CommunicationChannel {
    CompletableFuture<ChannelResponse> patch(String resource, JsonSerializable data);
    CompletableFuture<ChannelResponse> get(String resource);
    CompletableFuture<Boolean> send(String resource, JsonSerializable data);
    void subscribe(Object subscriber, String resource, Consumer<JSONObject> data);
    void unsubscribe(Object subscriber, String resource);
}
