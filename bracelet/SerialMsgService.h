#ifndef __SERIAL_MSG_SERVICE__
#define __SERIAL_MSG_SERVICE__

#include "Arduino.h"
#include "MsgService.h"

class SerialMsgService: public MsgService {

protected:
  char read();
public: 
  void sendMsg(String msg);
  void sendMsg(float msg);
  void sendMsg(int msg);
  SerialMsgService(String name);
  bool isChannelAvailable();
  bool isConnected();
};
#endif
