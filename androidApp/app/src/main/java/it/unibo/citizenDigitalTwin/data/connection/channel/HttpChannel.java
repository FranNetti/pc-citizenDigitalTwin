package it.unibo.citizenDigitalTwin.data.connection.channel;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;

/**
 * Interface for classes that want to represent an HTTP channel.
 */
public interface HttpChannel {

    /**
     * An enum for the HTTP Header.
     */
    enum Header {

        AUTHORIZATION("Authorization"),
        BEARER_TOKEN("Bearer ");

        private final String name;

        Header(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Set the given headers.
     * @param defaultHeaders the headers.
     */
    void setDefaultHeaders(Map<Header,String> defaultHeaders);

    /**
     * Close the channel of the given resource.
     * @param resource the resource you want to close the channel to
     */
    void closeChannel(String resource);

    /**
     * Do an HTTP patch operation.
     * @param resource the resource you want to do the call to
     * @param data the data to send
     * @return a future that contains the response from the server
     */
    CompletableFuture<ChannelResponse> patch(String resource, JSONObject data);

    /**
     * Do an HTTP post operation.
     * @param resource the resource you want to do the call to
     * @param data the data to send
     * @return a future that contains the response from the server
     */
    CompletableFuture<ChannelResponse> post(String resource, JSONObject data);

    /**
     * Do an HTTP get operation.
     * @param resource the resource you want to do the call to
     * @return a future that contains the response from the server
     */
    CompletableFuture<ChannelResponse> get(String resource);

    /**
     * Send a data to the given resource.
     * @param resource the resource you want to do the call to
     * @param data the data to send
     * @return a future that contains the response from the server
     */
    CompletableFuture<Boolean> send(String resource, JSONObject data);

    /**
     * Subscribe to incoming messages from the given resource.
     * @param subscriber the entity that wants to subscribe to the new messages
     * @param resource the resource you are interested in
     * @param onNewData a consumer that will be called each time a new data is received from the channel
     * @param onFailure a consumer that will be called if an error occurs
     */
    void subscribe(Object subscriber, String resource, Consumer<JSONObject> onNewData, BiConsumer<Throwable,String> onFailure);

    /**
     * Unsubscribe from incoming messages from the given resource.
     * @param subscriber the entity you want to unsubscribe
     * @param resource the resource you don't want to be notified anymore
     */
    void unsubscribe(Object subscriber, String resource);
}
