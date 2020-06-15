#include "Arduino.h"
#include "Subscription.h"
#include "MsgService.h"

Subscription::Subscription(String subscriber, MsgService* msgService, ResourceId resourceId) {
  this->subscriber = subscriber;
  this->msgService = msgService;
  this->resourceId = resourceId;
}
String Subscription::getSubscriber() {
  return this->subscriber;
}

MsgService* Subscription::getMsgService() {
  return this->msgService;
}

ResourceId Subscription::getResourceId() {
  return this->resourceId;
}
