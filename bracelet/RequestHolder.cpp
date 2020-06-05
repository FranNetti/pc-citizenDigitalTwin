#include "Arduino.h"
#include "RequestHolder.h"
#include "MsgService.h"

RequestHolder::RequestHolder() {
  this->msg = NULL;
  this->answerMsgService = NULL;
}

void RequestHolder::setRequest(Msg* msg) {
  this->removeRequest();
  this->msg = msg;
}

void RequestHolder::setRequest(Msg* msg, MsgService* msgService) {
  this->removeRequest();
  this->msg = msg;
  this->answerMsgService = msgService;
}

void RequestHolder::removeRequest() {
  if (this->msg != NULL)
    Msg::freeMsg(this->msg);
  this->msg = NULL;
  this->answerMsgService = NULL;
}

Msg* RequestHolder::getMsg() {
  return this->msg;
}

bool RequestHolder::isMsgAvailable() {
  return this->msg != NULL;
}

MsgService* RequestHolder::getAnswerMsgService() {
  return this->answerMsgService;
}

bool RequestHolder::isAnswerRequired() {
  return this->answerMsgService != NULL;
}
