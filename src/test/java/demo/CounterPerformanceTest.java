package demo;

import coyote.i13n.Counter;
import coyote.i13n.Tabs;

/**
 *
 */
public class CounterPerformanceTest {

  /**
   *
   */
  public CounterPerformanceTest() {
    super();
    // TODO Auto-generated constructor stub
  }


  /**
   * Run a 10 second test.
   *
   * @return the actual elapsed time.
   */
  private static long runTest() {
    long started = System.currentTimeMillis();
    long end = started + 10000;
    while (System.currentTimeMillis() <= end) {
      Tabs.increment("DemoCounter");
    }
    return System.currentTimeMillis() - started;
  }


  public static void main(String[] args) {
    // Initialize the Tabs instance
    String argusId = Tabs.getId();

    // 2,322,454.52 calls per second on a Pentium 3 JVM 1.4

    System.out.println("Initialized - starting test...");

    // don't include counter creation in the measures
    Counter counter = Tabs.getCounter("DemoCounter");

    long totalElapsed = 0;
    long totalCount = 0;

    int runs = 10;
    for (int x = 0; x < runs; x++) {
      totalElapsed += runTest();
      totalCount += counter.getValue();
      counter.reset();
    }

    System.out.println("Throughput = " + ((((float) totalCount / (float) totalElapsed) * 10000) / runs) + " calls per second");
  }
}