#ifndef __CONFIG__
#define __CONFIG__

/*Body Temperature sensor*/

#define BODY_TEMPERATURE_SENSOR_PIN A0

/*Bluetooth Adapter For Smartphone*/

#define SP_BT_TXD_PIN 2
#define SP_BT_RXD_PIN 3
#define SP_BT_STATE_PIN 11
#define SP_MSG_SERVICE_NAME "sp"

/*Serial Adapter For Tests*/

#define TESTS_MSG_SERVICE_NAME "tests"

/*Message Service*/
#define BUFFER_SIZE 5
#define NUMBER_OF_MSG_SERVICES 1

/*Scheduler*/
#define MAX_TASKS 10
#define WORKINGTASK_MIN_PERIOD 50

#endif
