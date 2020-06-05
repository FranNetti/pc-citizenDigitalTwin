#ifndef __COMMUNICATION_MANAGER_TASK__
#define __COMMUNICATION_MANAGER_TASK__

#include "Task.h"
#include "ResourceManagerTask.h"
#include "MsgService.h"
#include "RequestHolder.h"

typedef enum { WORKING, HANDLE_PARTIAL_MESSAGE } CommunicationState;

class CommunicationManagerTask: public AbstractTask {
private:
  CommunicationState state;
  MsgService** msgServices;
  Msg* incompleteMsg;
  MsgService* incompleteMsgService;
  int serviceToCheck;
  RequestHolder* request;
  int numberOfMsgServices;
  int freeSlot;
  void working();
  void handlePartialMessage();
  bool completeMsg(Msg* msg, MsgService* service);
public:
  CommunicationManagerTask(RequestHolder* request, int numberOfMsgServices);
  bool addMsgService(MsgService* msgService);
  void tick();
};

#endif
