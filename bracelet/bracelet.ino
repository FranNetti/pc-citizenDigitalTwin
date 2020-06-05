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
#include "ResourceManagerTask.h"
#include "CommunicationManagerTask.h"
#include "RequestHolder.h"
#include "RequestHandlerTask.h"

/*global variables*/
Scheduler sched;
SerialMsgService* serial;

void setup() {
  serial = new SerialMsgService(TESTS_MSG_SERVICE_NAME);
  BluetoothMsgService* smMsgService = new BluetoothMsgService(SP_MSG_SERVICE_NAME, SP_BT_TXD_PIN, SP_BT_RXD_PIN, SP_BT_STATE_PIN);
  BluetoothEventCatcher* btEventCatcher = new BluetoothEventCatcher(smMsgService);
  btEventCatcher->init(BT_EVENT_CATCHER_TASK_PERIOD);

  BodyThermometer* thermometer = new TMP36TemperatureSensor(BODY_TEMPERATURE_SENSOR_PIN);
  MAX30102PulseOximeterSensor* pulseOximeter = new MAX30102PulseOximeterSensor();
  
  if (!pulseOximeter->init()) {
    smMsgService->sendMsg("[MAX30102] error on init");
    while(1);
  }

  BPMResourceCollector* bpmCollector = new BPMResourceCollector(HEART_RATE_BUFFER_SIZE,pulseOximeter);
  SpO2ResourceCollector* SpO2Collector = new SpO2ResourceCollector(OXYGEN_BUFFER_SIZE,pulseOximeter);
  BodyTemperatureResourceCollector* bodyTemperatureCollector = new BodyTemperatureResourceCollector(BODY_TEMPERATURE_BUFFER_SIZE,thermometer);
  
  ResourceManagerTask* resourceManager = new ResourceManagerTask(NUMBER_OF_RESOURCES);
  resourceManager->init(RESOURCE_MANAGER_TASK_PERIOD);
  resourceManager->putResourceCollector(bpmCollector);
  resourceManager->putResourceCollector(SpO2Collector);
  resourceManager->putResourceCollector(bodyTemperatureCollector);
  
  RequestHolder* requestHolder = new RequestHolder();
  
  CommunicationManagerTask* communicationManager = new CommunicationManagerTask(requestHolder,NUMBER_OF_MSG_SERVICES);
  communicationManager->init(COMMUNICATION_MANAGER_TASK_PERIOD);
  communicationManager->addMsgService(smMsgService);

  RequestHandlerTask* requestHandler = new RequestHandlerTask(requestHolder, resourceManager, MAX_SUBSCRIPTIONS);
  requestHandler->init(REQUEST_HANDLER_TASK_PERIOD);
  
  sched.init(TASK_MIN_PERIOD);
  sched.addTask(resourceManager);
  sched.addTask(communicationManager);
  sched.addTask(requestHandler);
  sched.addTask(btEventCatcher);
  serial->sendMsg("[main] ready");
}

void loop() {
  sched.schedule();
}

void serialEvent() {
  serial->channelEvent();
}
