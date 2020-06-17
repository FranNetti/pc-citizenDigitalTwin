#ifndef __MODELS__
#define __MODELS__

#include "Config.h"

/**
 * Simple static class to give to the Resources some static properties.
 * In this case are defined teh resource names/identifiers.
 */
class Resource {
  public:
    static const String TEMPERATURE;
    static const String HEART_RATE;
    static const String OXYGEN;
};

/**
 * Represents the available Resources.
 */
typedef enum {
  TEMPERATURE,
  HEART_RATE,
  OXYGEN
} ResourceId;

#endif
