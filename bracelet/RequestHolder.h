#ifndef __REQUEST_HOLDER__
#define __REQUEST_HOLDER__

#include "Arduino.h"
#include "MsgService.h"

class RequestHolder {
private:
  Msg* msg;
  MsgService* answerMsgService;
public:
  RequestHolder();
  void setRequest(Msg* msg);
  void setRequest(Msg* msg, MsgService* msgService);
  void removeRequest();
  Msg* getMsg();
  bool isMsgAvailable();
  MsgService* getAnswerMsgService();
  bool isAnswerRequired();
};

#endif
