#ifndef __RESOURCE_MANAGER_TASK__
#define __RESOURCE_MANAGER_TASK__

#include "Arduino.h"
#include "Task.h"
#include "Models.h"
#include "BodyHeartRateMonitor.h"
#include "BodyOximeter.h"
#include "BodyThermometer.h"
#include "DataStructures.h"

class StringResourceCollector {
public:
  virtual ResourceId getResourceId() = 0;
  virtual bool isValueAvailable() = 0;
  virtual String getValueAsString() = 0;
  virtual void work() = 0;
};

template<typename X>
class ResourceCollector: public StringResourceCollector {
public:
  virtual X getValue() = 0;
};

template <typename X>
class AbstractResourceCollector: public ResourceCollector<X> {
private:
  ResourceId resourceId;
  bool isAvailable;
protected:
  X value;
  AbstractResourceCollector(ResourceId resourceId, int valuesSize);
  CircularArray<X>* values;
  void setValueAvailable(bool value);
  void addValue(X value);
public:
  ResourceId getResourceId();
  X getValue();
  bool isValueAvailable();
};

class IntResourceCollector: public AbstractResourceCollector<int> {
protected:
  int getValuesAvg();
public:
  IntResourceCollector(ResourceId resourceId, int valuesSize): AbstractResourceCollector<int>(resourceId, valuesSize){}
  String getValueAsString();
};

class FloatResourceCollector: public AbstractResourceCollector<float> {
protected:
  float getValuesAvg();
public:
  FloatResourceCollector(ResourceId resourceId, int valuesSize): AbstractResourceCollector<float>(resourceId, valuesSize){}
  String getValueAsString();
};


class BPMResourceCollector: public IntResourceCollector {
private:
  BodyHeartRateMonitor* bodyHeartRateMonitor;
public:
  BPMResourceCollector(int valuesSize, BodyHeartRateMonitor* bodyHeartRateMonitor);
  void work();
};

class SpO2ResourceCollector: public IntResourceCollector {
private:
  BodyOximeter* bodyOximeter;
public:
  SpO2ResourceCollector(int valuesSize, BodyOximeter* bodyOximeter);
  void work();
};

class BodyTemperatureResourceCollector: public FloatResourceCollector {
private:
  BodyThermometer* bodyThermometer;
public:
  BodyTemperatureResourceCollector(int valuesSize, BodyThermometer* bodyThermometer);
  void work();
};

class ResourceManagerTask: public AbstractTask {
private:
  StringResourceCollector** resources;
  int numberOfResources;
public:
  ResourceManagerTask(int numberOfResources);
  void putResourceCollector(StringResourceCollector* resourceCollector);
  void tick();
  bool isResourceAvailable(ResourceId id);
  String getResourceValueAsString(ResourceId id);
};

#endif
