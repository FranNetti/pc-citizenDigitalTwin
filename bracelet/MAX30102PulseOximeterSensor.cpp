#include "Arduino.h"
#include "MAX30102PulseOximeterSensor.h"
#include "Config.h"

MAX30102PulseOximeterSensor::MAX30102PulseOximeterSensor() {
}

bool MAX30102PulseOximeterSensor::init() {
  if (!this->particleSensor.begin(Wire, I2C_SPEED_FAST)) {
    return false;
  }
  particleSensor.setup(); //Configure sensor with default settings
  particleSensor.setPulseAmplitudeRed(0x0A); //Turn Red LED to low to indicate sensor is running
  particleSensor.setPulseAmplitudeGreen(0); //Turn off Green LED
  this->bufferLength = 100;
  return true;
}

int MAX30102PulseOximeterSensor::getBPM() {
  int firstBeat = 0;
  float beatsPerMinute = -1;
  uint8_t sensed = 0;
  unsigned long start = millis();
  while (millis() - start < MAX_TIME_TO_BPM && sensed < 2) {
    long irValue = particleSensor.getIR();
    if (checkForBeat(irValue)) {
      sensed++;
      if (sensed == 1) {
        firstBeat = millis();
      } else {
        long delta = millis() - firstBeat;
        beatsPerMinute = 60 / (delta / 1000.0);
      }
    }
  }
  return (int)beatsPerMinute;
}

int MAX30102PulseOximeterSensor::getSpO2() {
  int32_t spo2; //SPO2 value
  int8_t validSPO2; //indicator to show if the SPO2 calculation is valid
  int32_t heartRate; //heart rate value
  int8_t validHeartRate; //indicator to show if the heart rate calculation is valid

  for (byte i = 0; i < this->bufferLength; i++) {
    while (particleSensor.available() == false) //do we have new data?
      particleSensor.check(); //Check the sensor for new data

    //digitalWrite(readLED, !digitalRead(readLED)); //Blink onboard LED with every data read

    redBuffer[i] = particleSensor.getRed();
    irBuffer[i] = particleSensor.getIR();
    particleSensor.nextSample(); //We're finished with this sample so move to next sample
  }

  maxim_heart_rate_and_oxygen_saturation(this->irBuffer, this->bufferLength, this->redBuffer, &spo2, &validSPO2, &heartRate, &validHeartRate);
  if (validSPO2)
    return spo2;
  else
    return -1;
}
