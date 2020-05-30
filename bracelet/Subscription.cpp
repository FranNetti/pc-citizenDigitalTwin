#include "Arduino.h"
#include "Subscription.h"
#include "MsgService.h"

Subscription::Subscription(String subscriber, MsgService* msgService, uint8_t resourceId) {
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

uint8_t Subscription::getResourceId() {
  return this->resourceId;
}
