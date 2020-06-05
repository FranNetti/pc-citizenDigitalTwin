#ifndef __CONFIG__
#define __CONFIG__

/*Body Temperature sensor*/

#define BODY_TEMPERATURE_SENSOR_PIN A0

/* Bluetooth Adapter For Smartphone */

#define SP_BT_TXD_PIN 10
#define SP_BT_RXD_PIN 11
#define SP_BT_STATE_PIN 9
#define SP_MSG_SERVICE_NAME "sp"

/* Serial Adapter For Tests */

#define TESTS_MSG_SERVICE_NAME "tests"

/* Message Service */
#define MSG_BUFFER_SIZE 5
#define NUMBER_OF_MSG_SERVICES 1

/* Resources */
#define HEART_RATE_BUFFER_SIZE 5
#define BODY_TEMPERATURE_BUFFER_SIZE 5
#define OXYGEN_BUFFER_SIZE 5
#define NUMBER_OF_RESOURCES 3

/* Request Handler */
#define MAX_SUBSCRIPTIONS NUMBER_OF_RESOURCES*NUMBER_OF_MSG_SERVICES

/* Scheduler */
#define MAX_TASKS 10
#define TASK_MIN_PERIOD 100
#define COMMUNICATION_MANAGER_TASK_PERIOD 500
#define RESOURCE_MANAGER_TASK_PERIOD 9000
#define REQUEST_HANDLER_TASK_PERIOD 500
#define BT_EVENT_CATCHER_TASK_PERIOD 100

/* MAX30102 */
#define MAX_TIME_TO_BPM 3000 //millisencods

#endif
