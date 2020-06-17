#ifndef __BLUETOOTH_EVENT_CATCHER_TASK__
#define __BLUETOOTH_EVENT_CATCHER_TASK__

#include "Task.h"
#include "BluetoothMsgService.h"

/**
 * Task to catch Bluetooth events of a BluetoothMsgService.
 */
class BluetoothEventCatcher: public AbstractTask {
private:
  BluetoothMsgService* bluetoothMsgService;
public:
  BluetoothEventCatcher(BluetoothMsgService* bluetoothMsgService);
  void tick();
};

#endif
