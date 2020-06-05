#ifndef __REQUEST_HANDLER_TASK__
#define __REQUEST_HANDLER_TASK__

#include "Task.h"
#include "ResourceManagerTask.h"
#include "MsgService.h"
#include "RequestHolder.h"
#include "Subscription.h"

typedef enum { HANDLE_REQUEST } RequestHandlerState;

class RequestHandlerTask: public AbstractTask {
private:
  RequestHandlerState state;
  Subscription** subscriptions;
  ResourceManagerTask* resourceManager;
  RequestHolder* request;
  int maxNumberOfSubscriptions;
  int subscriptionsSize;
  void working();
  void handleRequest(Msg* msg, ResourceId resource);
  void sendResourceValue(MsgService* servive, ResourceId resource);
  String createResponse(ResourceId resource, String value, bool isPresent);
  String createMessage(String resource, String value, bool isPresent);
  bool isAlreadySubscribed(String sender, ResourceId resource);
public:
  RequestHandlerTask(RequestHolder* request, ResourceManagerTask* resourceManager, int maxNumberOfSubscriptions);
  void tick();
};

#endif
