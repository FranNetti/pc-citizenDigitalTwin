#ifndef __SUBSCRIPTION__
#define __SUBSCRIPTION__

#include "Arduino.h"
#include "MsgService.h"
#include "Models.h"

/**
 * Represent the subscription of an external agent to a specific respurce.
 */
class Subscription {
private:
  String subscriber;
  MsgService* msgService;
  ResourceId resourceId;
public:
  Subscription(String subscriber, MsgService* msgService, ResourceId resourceId);
  /**
   * Get the subscriber name.
   * @return the subscriber name
   */
  String getSubscriber();
  /**
   * Get the message service to be used to communicate with the subscriber.
   * @return the message service to be used to communicate with the subscriber
   */
  MsgService* getMsgService();
  /**
   * Get the resource id.
   * @return the resource id
   */
  ResourceId getResourceId();
};

#endif
