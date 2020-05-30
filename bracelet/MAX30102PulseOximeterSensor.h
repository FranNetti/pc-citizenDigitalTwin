#ifndef __MAX30102_PULSE_OXIMETER_SENSOR__
#define __MAX30102_PULSE_OXIMETER_SENSOR__

#include "BodyOximeter.h"
#include "BodyHeartRateMonitor.h"

class MAX30102PulseOximeterSensor: public BodyOximeter, public BodyHeartRateMonitor {

public:
  MAX30102PulseOximeterSensor();
  int getSpO2();
  int getBPM();
};

#endif
