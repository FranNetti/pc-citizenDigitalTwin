#ifndef __MSG_SERVICE__
#define __MSG_SERVICE__

#include "Arduino.h"
#include "Config.h"

class MsgType {
  public:
    static const String GET;
    static const String SUBSCRIBE;
};

/**
 * Represents a messagge received from an external agent.
 */
class Msg {
  private:
    String type;
    String resource;
    String sender;
    String* data;
    int dataLenght;
  public:
    Msg(String type, String resource, String sender, String* data, int lenght);
    Msg(String type, String resource, String sender);
    /**
     * Retrieve the type of the message.
     * @return the type of the message
     */
    String getType();
    /**
     * Retrieve the resource of the message.
     * @return the resource of the message
     */
    String getResource();
    /**
     * Retrieve the sender of the message.
     * @return the sender of the message
     */
    String getSender();
    /**
     * Retrieve the payloads of the message.
     * @return the payloads of the message
     */
    String* getData();
    /**
     * Retrieve the number of available payloads.
     * @return the number of available payloads
     */
    int getDataLenght();
    /**
     * Allow to delete a message.
     */
    static void freeMsg(Msg* msg);
};

class MsgService {
  private:
    String queue[MSG_BUFFER_SIZE];
    int head;
    int tail;
    String name;
    void saveMsg(String msg);
    void resetBuffer();
  protected:
    MsgService(String name);
    virtual bool isChannelAvailable() = 0;
    virtual char read() = 0;
  public:
    /**
     * Send an int messagge into the channel.
     */
    virtual void sendMsg(int msg) = 0;
    /**
     * Send a String messagge into the channel.
     */
    virtual void sendMsg(String msg) = 0;
    /**
     * Send a float messagge into the channel.
     */
    virtual void sendMsg(float msg) = 0;
    /**
     * Verify if someone is connected to the channel.
     * @return true if someone is connected to the channel
     */
    virtual bool isConnected() = 0;
    /**
     * Get the name of the channel.
     * @return the name of the channel
     */
    String getName();
    /**
     * Get oldest received message.
     * @return the oldest received message otherwise NULL.
     */
    String receiveMsg();
    /**
     * Check if there is an available message.
     * @return true if there is an available message otherwise false
     */
    bool isMsgAvailable();
    /**
     * Check if there are new data from the channel, in the afferamative case collects the new data and create a new message.
     */
    void channelEvent();
};

#endif
