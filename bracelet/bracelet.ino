/**
 * Bracelet:
 * Body Heart Rate Monitor
 * Body Oximeter
 * Body Temperature
 */

#include "Config.h"
#include "TMP36TemperatureSensor.h"
#include "BluetoothMsgService.h"
#include "SerialMsgService.h"
#include "Scheduler.h"
#include "BluetoothEventCatcher.h"
#include "MAX30102PulseOximeterSensor.h"
#include "SerialMsgService.h"

/*global variables*/
Scheduler sched;
SerialMsgService* serial = new SerialMsgService(TESTS_MSG_SERVICE_NAME);

/*test*/

void setup() {
  BodyThermometer* thermometer = new TMP36TemperatureSensor(BODY_TEMPERATURE_SENSOR_PIN);
  BluetoothMsgService* smMsgService = new BluetoothMsgService(SP_MSG_SERVICE_NAME, SP_BT_TXD_PIN, SP_BT_RXD_PIN, SP_BT_STATE_PIN);
  Task* btEventCatcher = new BluetoothEventCatcher(smMsgService);
  MAX30102PulseOximeterSensor* pulseOximeter = new MAX30102PulseOximeterSensor();
}

void loop() {
  sched.schedule();
}

void serialEvent() {
  serial->channelEvent();
}
