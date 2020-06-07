package it.unibo.citizenDigitalTwin.data.connection.channel;

import it.unibo.citizenDigitalTwin.data.connection.channel.response.ChannelResponse;

public class ChannelException extends Exception {
    private final ChannelResponse response;

    public ChannelException(final ChannelResponse response) {
        this.response = response;
    }

    public ChannelResponse getResponse() {
        return response;
    }
}
