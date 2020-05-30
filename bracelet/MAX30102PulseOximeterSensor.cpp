#include "Arduino.h"
#include "MAX30102PulseOximeterSensor.h"

MAX30102PulseOximeterSensor::MAX30102PulseOximeterSensor() {
}

int MAX30102PulseOximeterSensor::getBPM() {
  return 60;
}

int MAX30102PulseOximeterSensor::getSpO2() {
  return 90;
}
