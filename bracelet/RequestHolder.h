#ifndef __REQUEST_HOLDER__
#define __REQUEST_HOLDER__

#include "Arduino.h"
#include "MsgService.h"

/**
 * Contain a request.
 * Updated by the CommunicationManager.
 * Read by the RequestHandler
 */
class RequestHolder {
private:
  Msg* msg;
  MsgService* answerMsgService;
public:
  RequestHolder();
  /**
   * Set a new request.
   * @param msg the message of the request
   */
  void setRequest(Msg* msg);
  /**
   * Set a new request.
   * @param msg the message of the request
   * @param msgService the message service to be used for a response
   */
  void setRequest(Msg* msg, MsgService* msgService);
  /**
   * Remove the request.
   */
  void removeRequest();
  /**
   * Get the request message.
   * @return the message of the request
   */
  Msg* getMsg();
  /**
   * Check if the request has a message.
   * @return true if the request has a message otherwise false
   */
  bool isMsgAvailable();
  /**
   * Get the request message service to be used for a response.
   * @return the request message service to be used for a response
   */
  MsgService* getAnswerMsgService();
  /**
   * Check if an answer is requiered for the request.
   * @return true if an answer is requiered for the request otherwise false
   */
  bool isAnswerRequired();
};

#endif
