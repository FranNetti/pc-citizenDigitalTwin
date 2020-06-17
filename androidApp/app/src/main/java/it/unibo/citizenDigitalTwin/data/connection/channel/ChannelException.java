package it.unibo.citizenDigitalTwin.data.connection.channel;

import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;

/**
 * Class that represent an exception from the channel.
 */
public class ChannelException extends Exception {
    private final ChannelResponse response;

    public ChannelException(final ChannelResponse response) {
        this.response = response;
    }

    /**
     * Get the channel response.
     * @return the channel response
     */
    public ChannelResponse getResponse() {
        return response;
    }
}
