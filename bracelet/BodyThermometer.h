#ifndef __BODY_THERMOMETER__
#define __BODY_THERMOMETER__

/**
 * Component to retrieve the body temperature.
 */
class BodyThermometer {
public:
  /**
   * Retrieve the body temperature.
   * @return the body temperature
   */
  virtual float getTemperature() = 0;
};


#endif
