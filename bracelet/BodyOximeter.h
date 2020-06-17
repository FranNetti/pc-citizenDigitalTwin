#ifndef __BODY_OXIMETER__
#define __BODY_OXIMETER__

/**
 * Component to retrieve the SpO2 percentage value.
 */
class BodyOximeter {
public:
  /**
   * Retrieve the SpO2 percentage value.
   * @return the SpO2 percentage value
   */
  virtual int getSpO2() = 0;
};


#endif
