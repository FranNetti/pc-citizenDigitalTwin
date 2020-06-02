package it.unibo.citizenDigitalTwin.data.device.communication;

import java.util.function.Consumer;

public interface DeviceChannel {

    void askForData(final String reqMessage);

    void subscribeToIncomingMessages(final Object subscriber, final Consumer<MsgReceived> consumer);

    void unsubscribeFromIncomingMessages(final Object subscriber);

}
