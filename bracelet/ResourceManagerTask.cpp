#include "Arduino.h"
#include "ResourceManagerTask.h"
#include "Models.h"
#include "DataStructures.h"

template<typename X>
AbstractResourceCollector<X>::AbstractResourceCollector(ResourceId resourceId, int valuesSize) {
  this->resourceId = resourceId;
  this->values = new CircularArray<X>(valuesSize);
  this->isAvailable = false;
}

template<typename X>
ResourceId AbstractResourceCollector<X>::getResourceId() {
  return this->resourceId;
}

template<typename X>
X AbstractResourceCollector<X>::getValue() {
  return this->value;
}

template<typename X>
void AbstractResourceCollector<X>::setValueAvailable(bool value) {
  this->isAvailable = value;
}

template<typename X>
bool AbstractResourceCollector<X>::isValueAvailable() {
  return this->isAvailable;
}

template<typename X>
void AbstractResourceCollector<X>::addValue(X value) {
  this->values->add(value);
}

int IntResourceCollector::getValuesAvg() {
  int size = this->values->size();
  if (this->values->isEmpty())
    return 0;
  int sum = 0;
  for (int i = 0; i < size; i++) {
    sum += this->values->get(i);
  }
  return sum / size;
}

String IntResourceCollector::getValueAsString() {
  return String(this->value);
}

float FloatResourceCollector::getValuesAvg() {
  int size = this->values->size();
  if (this->values->isEmpty())
    return 0;
  float sum = 0;
  for (int i = 0; i < size; i++) {
    sum += this->values->get(i);
  }
  return sum / size;
}

String FloatResourceCollector::getValueAsString() {
  return String(this->value);
}

BPMResourceCollector::BPMResourceCollector(int valuesSize, BodyHeartRateMonitor* bodyHeartRateMonitor): IntResourceCollector(HEART_RATE,valuesSize) {
  this->bodyHeartRateMonitor = bodyHeartRateMonitor;
}

void BPMResourceCollector::work() {
  int beatsPerMinute = this->bodyHeartRateMonitor->getBPM();
  if (beatsPerMinute < 255 && beatsPerMinute > 20) {
    this->addValue(beatsPerMinute);
    this->value = this->getValuesAvg();
    this->setValueAvailable(true);
  } else {
    this->setValueAvailable(false);
  }
}

SpO2ResourceCollector::SpO2ResourceCollector(int valuesSize, BodyOximeter* bodyOximeter): IntResourceCollector(OXYGEN,valuesSize) {
  this->bodyOximeter = bodyOximeter;
}

void SpO2ResourceCollector::work() {
  int spo2 = this->bodyOximeter->getSpO2();
  if (spo2 > 0) {
    this->values->add(spo2);
    this->value = this->getValuesAvg();
    this->setValueAvailable(true);
  } else {
    this->setValueAvailable(false);
  }
}

BodyTemperatureResourceCollector::BodyTemperatureResourceCollector(int valuesSize, BodyThermometer* bodyThermometer): FloatResourceCollector(TEMPERATURE,valuesSize) {
  this->bodyThermometer = bodyThermometer; 
}

void BodyTemperatureResourceCollector::work() {
  this->values->add(this->bodyThermometer->getTemperature());
  this->value = this->getValuesAvg();
  this->setValueAvailable(true);
}

ResourceManagerTask::ResourceManagerTask(int numberOfResources) {
  this->resources = (StringResourceCollector **)calloc(numberOfResources, sizeof(StringResourceCollector*));
  this->numberOfResources = numberOfResources;
}

void ResourceManagerTask::putResourceCollector(StringResourceCollector* resourceCollector) {
  this->resources[resourceCollector->getResourceId()] = resourceCollector;
}


void ResourceManagerTask::tick() {
  for (int i = 0; i < this->numberOfResources; i++) {
    if (this->resources[i] != NULL)
      this->resources[i]->work();
  }
}

bool ResourceManagerTask::isResourceAvailable(ResourceId id) {
  if (this->resources[id] == NULL)
    return false;
  return this->resources[id]->isValueAvailable();
}

String ResourceManagerTask::getResourceValueAsString(ResourceId id) {
  if (this->resources[id] == NULL)
    return "";
  return this->resources[id]->getValueAsString();
}
