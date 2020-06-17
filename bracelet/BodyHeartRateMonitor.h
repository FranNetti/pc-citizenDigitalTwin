#ifndef __BODY_HEART_RATE_MONITOR__
#define __BODY_HEART_RATE_MONITOR__

/**
 * Component to retrieve the BPM value.
 */
class BodyHeartRateMonitor {
public:
  /**
   * Retrieve the BPM value.
   * @return the BPM value
   */
  virtual int getBPM() = 0;
};


#endif
