#include "Arduino.h"
#include "TMP36TemperatureSensor.h"

TMP36TemperatureSensor::TMP36TemperatureSensor(int pin) {
  this->pin = pin;
}

float TMP36TemperatureSensor::getTemperature() {
  int sensVal = analogRead(pin);
  float voltage = (sensVal/1024.0) * 5.0;
  float temperature = (voltage - .5) * 100;
  return temperature;
}
