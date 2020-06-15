#ifndef __BLUETOOTH_EVENT_CATCHER_TASK__
#define __BLUETOOTH_EVENT_CATCHER_TASK__

#include "Task.h"
#include "BluetoothMsgService.h"

class BluetoothEventCatcher: public AbstractTask {
private:
  BluetoothMsgService* bluetoothMsgService;
public:
  BluetoothEventCatcher(BluetoothMsgService* bluetoothMsgService);
  void tick();
};

#endif
