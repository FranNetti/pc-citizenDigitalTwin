#ifndef __MSG_SERVICE__
#define __MSG_SERVICE__

#include "Config.h"

class MsgType {
  public:
    static const String GET;
    static const String SUBSCRIBE;
};

class Msg {
  private:
    String type;
    String resource;
    String sender;
    String* data;
    int dataLenght;
  public:
    Msg(String type, String resource, String sender, String* data, int lenght);
    String getType();
    String getResource();
    String getSender();
    String* getData();
    int getDataLenght();
    static void freeMsg(Msg* msg);
};

class MsgService {
  private:
    String queue[BUFFER_SIZE];
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
    virtual void sendMsg(int msg) = 0;
    virtual void sendMsg(String msg) = 0;
    virtual void sendMsg(float msg) = 0;
    virtual bool isConnected() = 0;
    String getName();
    String receiveMsg();
    bool isMsgAvailable();
    void channelEvent();
};

#endif
