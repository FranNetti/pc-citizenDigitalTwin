#ifndef __CONFIG__
#define __CONFIG__

/*Body Temperature sensor*/

/**
 * Pin to which the body temperature sensor is connected.
 */
#define BODY_TEMPERATURE_SENSOR_PIN A0

/* Bluetooth Adapter For Smartphone */

/**
 * Pin to which the TDX channel of the Bluetooth Adapter is connected.
 */
#define SP_BT_TXD_PIN 10
/**
 * Pin to which the RDX channel of the Bluetooth Adapter is connected.
 */
#define SP_BT_RXD_PIN 11
/**
 * Pin to which the state of the Bluetooth Adapter is connected.
 */
#define SP_BT_STATE_PIN 9
/**
 * Name of the msg service to be connected to the user smartphone.
 */
#define SP_MSG_SERVICE_NAME "sp"

/* Serial Adapter For Tests */

#define TESTS_MSG_SERVICE_NAME "tests"

/* Message Service */
/**
 * Max number of messages that can be buffered in the msg service.
 */
#define MSG_BUFFER_SIZE 5
/**
 * Number of external_agents/message_services allowed.
 */
#define NUMBER_OF_MSG_SERVICES 1

/* Resources */
/**
 * Max number of values that can be buffered in the for the heart rate (BPM) computing.
 */
#define HEART_RATE_BUFFER_SIZE 5
/**
 * Max number of values that can be buffered in the for the body temperature computing.
 */
#define BODY_TEMPERATURE_BUFFER_SIZE 5
/**
 * Max number of values that can be buffered in the for the SpO2 computing.
 */
#define OXYGEN_BUFFER_SIZE 5
/**
 * Number of resources currently collectable by the system.
 */
#define NUMBER_OF_RESOURCES 3

/* Request Handler */
/**
 * Max number of subscriptions that can be handled.
 * The number can be obtained multiplying the number of collectable resources with the number of external agents allowed.
 */
#define MAX_SUBSCRIPTIONS NUMBER_OF_RESOURCES*NUMBER_OF_MSG_SERVICES

/* Scheduler */
/**
 * Max number of tasks that can be handled by the Scheduler.
 */
#define MAX_TASKS 10
/**
 * Highest common denominator between the tasks periods.
 */
#define TASK_MIN_PERIOD 100
/**
 * Interval time between each Communication Manager Task execution.
 */
#define COMMUNICATION_MANAGER_TASK_PERIOD 500
/**
 * Interval time between each Resource Manager Task execution.
 */
#define RESOURCE_MANAGER_TASK_PERIOD 9000
/**
 * Interval time between each Request Handler Task execution.
 */
#define REQUEST_HANDLER_TASK_PERIOD 500
/**
 * Interval time between each Bluetooth Event Catcher execution.
 */
#define BT_EVENT_CATCHER_TASK_PERIOD 100

/* MAX30102 */
/**
 * Max time given, in milliseconds, for the BPM sampling.
 */
#define MAX_TIME_TO_BPM 3000 //millisencods

#endif
