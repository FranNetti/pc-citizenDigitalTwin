#include "Arduino.h"
#include "BluetoothEventCatcher.h"

BluetoothEventCatcher::BluetoothEventCatcher(BluetoothMsgService* bluetoothMsgService) {
  this->bluetoothMsgService = bluetoothMsgService;
}

void BluetoothEventCatcher::tick(){
  if (bluetoothMsgService->isChannelAvailable()) {
    bluetoothMsgService->channelEvent();
  }
}
