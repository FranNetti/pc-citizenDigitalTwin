#ifndef __TMP36_TEMPERATURE_SENSOR__
#define __TMP36_TEMPERATURE_SENSOR__

#include "BodyThermometer.h"

/**
 * Encapsulate the technology and the logic used to work with the TMP36 sensor.
 */
class TMP36TemperatureSensor: public BodyThermometer {

public:
  TMP36TemperatureSensor(int pin);
  float getTemperature();
private:
  int pin;
};

#endif
