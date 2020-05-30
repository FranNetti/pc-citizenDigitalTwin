#ifndef __SUBSCRIPTION__
#define __SUBSCRIPTION__

#include "Arduino.h"
#include "MsgService.h"

class Subscription {
private:
  String subscriber;
  MsgService* msgService;
  uint8_t resourceId;
public:
  Subscription(String subscriber, MsgService* msgService, uint8_t resourceId);
  String getSubscriber();
  MsgService* getMsgService();
  uint8_t getResourceId();
};

#endif
