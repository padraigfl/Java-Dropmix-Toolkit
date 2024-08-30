package util;

public class Counter {
  private int counter;
  public Counter(int startNumber) {
    this.counter = startNumber;
  }
  public void iterate() {
    this.counter++;
  }
  public void iterate(int num) {
    this.counter += num;
  }
  public int getCounter() {
    return this.counter;
  }
}
