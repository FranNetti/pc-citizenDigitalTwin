#include "CommunicationManagerTask.h"

CommunicationManagerTask::CommunicationManagerTask(RequestHolder* request, int numberOfMsgServices) {
  this->request = request;
  this->msgServices = (MsgService **)calloc(numberOfMsgServices, sizeof(MsgService*));
  this->numberOfMsgServices = numberOfMsgServices;
  this->freeSlot=0;
  this->state = WORKING;
  this->serviceToCheck = 0;
  this->incompleteMsg = NULL;
  this->incompleteMsgService = NULL;
}

bool CommunicationManagerTask::addMsgService(MsgService* msgService) {
  if (this->freeSlot < this->numberOfMsgServices) {
    this->msgServices[this->freeSlot] = msgService;
    return true;
  }
  return false;
}

void CommunicationManagerTask::tick() {
  switch(this->state) {
    case WORKING: this->working(); break;
    case HANDLE_PARTIAL_MESSAGE: this->handlePartialMessage(); break;
    default: break;
  }
}

void CommunicationManagerTask::working() {
  this->request->removeRequest();
  MsgService* service = this->msgServices[this->serviceToCheck];
  int size;
  if (this->freeSlot < this->numberOfMsgServices)
    size = this->freeSlot + 1;
  else
    size = this->numberOfMsgServices;
  this->serviceToCheck = (this->serviceToCheck + 1) % size;
  if (service != NULL) {
    if (service->isMsgAvailable()) {
      String msg = service->receiveMsg();
      int index = msg.indexOf('/');
      if (index != -1 && msg.length() > (index+1)) {
        String type = msg.substring(0,index);
        String resource = msg.substring(index+1);
        Msg* message = new Msg(type, resource, service->getName());
        Serial.println("[CommunicationManager] msg: " + message->getType() + "/" + message->getResource());
        Serial.flush();
        this->request->setRequest(message,service);
      }
    }
  }
}

void CommunicationManagerTask::handlePartialMessage() {
  if (this->completeMsg(this->incompleteMsg, this->incompleteMsgService)) {
    this->request->setRequest(this->incompleteMsg,this->incompleteMsgService);
    this->incompleteMsg = NULL;
    this->incompleteMsgService = NULL;
    this->state = WORKING;
  }
}

bool CommunicationManagerTask::completeMsg(Msg* msg, MsgService* service) {
  if (msg != NULL && service != NULL && service->isMsgAvailable()) {
    String* data = msg->getData();
    int dataLenght = msg->getDataLenght();
    int i = 0;
    while(i < dataLenght && service->isMsgAvailable()) {
      if (data[i] == "")
        data[i] = service->receiveMsg();
      i++;
    }
    if (i == dataLenght)
      return true;
  }
  return false;
}
