#include "RequestHandlerTask.h"

RequestHandlerTask::RequestHandlerTask(RequestHolder* request, ResourceManagerTask* resourceManager, int maxNumberOfSubscriptions) {
  this->maxNumberOfSubscriptions = maxNumberOfSubscriptions;
  this->subscriptions = (Subscription **)calloc(maxNumberOfSubscriptions, sizeof(Subscription*));
  this->state = HANDLE_REQUEST;
  this->resourceManager = resourceManager;
  this->request = request;
  this->subscriptionsSize = 0;
}

void RequestHandlerTask::tick() {
  switch(this->state) {
    case HANDLE_REQUEST: this->working(); break;
    default: break;
  }
}

void RequestHandlerTask::working() {
  if (this->request->isMsgAvailable()) {
    Msg* msg = this->request->getMsg();
    Serial.println("[RequestHandler] -> " + msg->getType() + "/" + msg->getResource());
    Serial.flush();
    String resource = msg->getResource();
    if (resource==Resource::TEMPERATURE) {
      this->handleRequest(msg, TEMPERATURE);
    } else if (resource==Resource::HEART_RATE) {
      this->handleRequest(msg, HEART_RATE);
    } else if (resource==Resource::OXYGEN) {
      this->handleRequest(msg, OXYGEN);
    } else {
      this->request->getAnswerMsgService()->sendMsg(this->createMessage(resource, "", false));
    }
  }
  if (this->subscriptionsSize > 0) {
    for (int i = 0; i < this->subscriptionsSize; i++) {
      this->sendResourceValue(this->subscriptions[i]->getMsgService(), this->subscriptions[i]->getResourceId());
    }
  }
}

void RequestHandlerTask::handleRequest(Msg* msg, ResourceId resource) {
  String type = msg->getType();
  if (type==MsgType::GET) {
    if (this->request->isAnswerRequired()) {
      MsgService* answer = this->request->getAnswerMsgService();
      this->sendResourceValue(answer,resource);
    } 
  } else if (type==MsgType::SUBSCRIBE 
          && this->subscriptionsSize < this->maxNumberOfSubscriptions 
          && !this->isAlreadySubscribed(msg->getSender(), resource)
          && this->request->isAnswerRequired()) {
    this->subscriptions[this->subscriptionsSize] = new Subscription(msg->getSender(), this->request->getAnswerMsgService(), resource);
    this->subscriptionsSize++;
  }
}

void RequestHandlerTask::sendResourceValue(MsgService* service, ResourceId resource) {
  if (resourceManager->isResourceAvailable(resource)) {
    String value = resourceManager->getResourceValueAsString(resource);
    service->sendMsg(this->createResponse(resource, value, true));
  } else {
    service->sendMsg(this->createResponse(resource, "", false));
  }
}

String RequestHandlerTask::createResponse(ResourceId resource, String value, bool isPresent) {
  String resourceName;
  switch(resource) {
    case TEMPERATURE: resourceName = Resource::TEMPERATURE; break;
    case HEART_RATE: resourceName = Resource::HEART_RATE; break;
    case OXYGEN: resourceName = Resource::OXYGEN; break;
    default: resourceName = ""; break;
  }
  return this->createMessage(resourceName,value,isPresent);
}

String RequestHandlerTask::createMessage(String resource, String value, bool isPresent) {
  String isPresentText = isPresent ? "true" : "false";
  String response = "{ \"type\": \"" + resource + "\", \"value\": \"" + value + "\", \"isPresent\": " + isPresentText + " }";
  return response;
}

bool RequestHandlerTask::isAlreadySubscribed(String subscriber, ResourceId resource) {
  for (int i = 0; i < this->subscriptionsSize; i++) {
    Subscription* sub = this->subscriptions[i];
    if (sub->getSubscriber() == subscriber && sub->getResourceId() == resource)
      return true;
  }
  return false;
}
