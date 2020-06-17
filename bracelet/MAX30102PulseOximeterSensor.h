#ifndef __MAX30102_PULSE_OXIMETER_SENSOR__
#define __MAX30102_PULSE_OXIMETER_SENSOR__

#include "BodyOximeter.h"
#include "BodyHeartRateMonitor.h"
#include <Wire.h>
#include "MAX30105.h"
#include "heartRate.h"
#include "spo2_algorithm.h"

/**
 * Encapsulate the technology and the logic used to work with the MAX30102 sensor.
 */
class MAX30102PulseOximeterSensor: public BodyOximeter, public BodyHeartRateMonitor {
private:
  MAX30105 particleSensor;
  uint32_t irBuffer[100]; //infrared LED sensor data
  uint32_t redBuffer[100];  //red LED sensor data
  uint16_t bufferLength;
public:
  MAX30102PulseOximeterSensor();
  bool init();
  int getSpO2();
  int getBPM();
};

#endif
