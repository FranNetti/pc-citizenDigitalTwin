#ifndef __MODELS__
#define __MODELS__

#include "Config.h"

class Resource {
  public:
    static const String TEMPERATURE;
    static const String HEART_RATE;
    static const String OXYGEN;
};

enum ResourceId {
  TEMPERATURE,
  HEART_RATE,
  OXYGEN
};

#endif
