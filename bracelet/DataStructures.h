#ifndef __DATA_STRUCTURES__
#define __DATA_STRUCTURES__

template<typename X>
class CircularArray {
private:
  X* values;
  int length;
  int head;
  int tail;
public:
  CircularArray(int size) {
    this->length = size;
    this->values = (X *)calloc(size, sizeof(X));
    this->clear();
  }
  void clear() {
    this->head = -1;
    this->tail = -1;
  }
  int add(X value) {
    this->tail = (this->tail + 1) % this->length;
    this->values[this->tail] = value;
    if (this->tail == this->head || this->head == -1) {
      this->head = (this->head + 1) % this->length;
    }
    return this->size() - 1;
  }
  X get(int index) {
    int pos = (index + this->head) % this->length;
    return this->values[pos];
  }
  X pop() {
    X value = this->values[this->head];
    this->head = (this->head + 1) % this->length;
    if (this->head > this->tail) {
      this->clear();
    }
    return value;
  }
  bool isEmpty() {
    return this->head < 0;
  }
  int size() {
    if (this->head < 0)
      return 0;
    int size = this->tail - this->head + 1;
    if (this->tail < this-> head)
      size += this->length;
    return size;
  }
};


#endif
