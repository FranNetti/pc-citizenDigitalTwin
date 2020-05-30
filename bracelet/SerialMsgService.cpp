#include "Arduino.h"
#include "SerialMsgService.h"


SerialMsgService::SerialMsgService(String name): MsgService(name) {
  Serial.begin(9600);
  while(!Serial) {}
}

void SerialMsgService::sendMsg(String msg){
  Serial.println(msg);
  Serial.flush();  
}

void SerialMsgService::sendMsg(float msg){
  Serial.println(msg);
  Serial.flush();  
}

void SerialMsgService::sendMsg(int msg){
  Serial.println(msg);
  Serial.flush();
}

bool SerialMsgService::isChannelAvailable() {
  return Serial.available();
}

bool SerialMsgService::isConnected() {
  return true;
}

char SerialMsgService::read() {
  return (char) Serial.read();
}

/*called automatically when serial has an avent*/
