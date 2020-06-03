package it.unibo.citizenDigitalTwin.data.device.communication;

import java.util.function.Consumer;

/**
 * Interfaces for classes that represent a direct communication with a device
 */
public interface DeviceChannel {

    /**
     * Ask for a certain type of data
     * @param reqMessage the message that the channel will send to the device
     */
    void askForData(final String reqMessage);

    /**
     * Subscribe for incoming messages
     * @param subscriber the subscriber interested in new messages
     * @param consumer how the new message will be consumes
     */
    void subscribeToIncomingMessages(final Object subscriber, final Consumer<MsgReceived> consumer);

    /**
     * Unsubscribe from incoming messages
     * @param subscriber the subscriber that now doesn't want to be notified anymore
     */
    void unsubscribeFromIncomingMessages(final Object subscriber);

    /**
     * Start communication with the device
     */
    void start();

}
