#ifndef __TASK__
#define __TASK__

class Task {
public:
  virtual void init(int period) = 0;
  virtual void tick() = 0;
  virtual bool updateAndCheckTime(int basePeriod) = 0;
  virtual int getPeriod() = 0;
};

class AbstractTask: public Task {
private:
  int myPeriod;
  int timeElapsed;
  
public:
  virtual void init(int period){
    myPeriod = period;  
    timeElapsed = 0;
  }

  virtual void tick() = 0;

  bool updateAndCheckTime(int basePeriod){
    timeElapsed += basePeriod;
    if (timeElapsed >= myPeriod){
      timeElapsed = 0;
      return true;
    } else {
      return false; 
    }
  }

  virtual int getPeriod() {
    return myPeriod;  
  }
  
};

#endif
