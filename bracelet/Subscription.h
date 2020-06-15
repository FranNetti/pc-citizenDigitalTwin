#ifndef __SUBSCRIPTION__
#define __SUBSCRIPTION__

#include "Arduino.h"
#include "MsgService.h"
#include "Models.h"

class Subscription {
private:
  String subscriber;
  MsgService* msgService;
  ResourceId resourceId;
public:
  Subscription(String subscriber, MsgService* msgService, ResourceId resourceId);
  String getSubscriber();
  MsgService* getMsgService();
  ResourceId getResourceId();
};

#endif
