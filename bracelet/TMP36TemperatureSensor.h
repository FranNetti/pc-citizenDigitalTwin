#ifndef __TMP36_TEMPERATURE_SENSOR__
#define __TMP36_TEMPERATURE_SENSOR__

#include "BodyThermometer.h"

class TMP36TemperatureSensor: public BodyThermometer {

public:
  TMP36TemperatureSensor(int pin);
  float getTemperature();
private:
  int pin;
};

#endif
