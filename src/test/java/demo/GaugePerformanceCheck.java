/*
 * $Id:$
 *
 * Infrastructure Systems Project: Stephan D. Cote' - Enterprise Architecture
 */
package demo;

import coyote.i13n.Tabs;

import java.text.NumberFormat;


/**
 * Basic performance tests for gauges
 */
public class GaugePerformanceCheck {
  private static final long LIMIT = 10000000;


  private static long runNullGaugeTest() {
    Tabs.enableGauges(false);

    long started = System.currentTimeMillis();

    for (int x = 0; x < LIMIT; x++) {
      Tabs.getGauge("NullGauge").update(1);
    }
    long elapsed = System.currentTimeMillis() - started;

    Tabs.enableGauges(true);

    return elapsed;
  }


  private static long runEnabledGaugeTest() {
    Tabs.enableGauges(true);

    long started = System.currentTimeMillis();

    for (int x = 0; x < LIMIT; x++) {
      Tabs.getGauge("GaugeTest").update(1);
    }
    return System.currentTimeMillis() - started;
  }


  /**
   * @param args
   */
  public static void main(String[] args) {

    long elapsed = runNullGaugeTest();
    System.out.print("Null gauge test: " + ((float) elapsed / LIMIT) + " ms/c Avg");
    System.out.println(" (" + NumberFormat.getInstance().format((1000 / ((float) elapsed / LIMIT))) + " cps)");

    elapsed = runEnabledGaugeTest();
    System.out.print("Timing gauge test: " + ((float) elapsed / LIMIT) + " ms/c Avg");
    System.out.println(" (" + NumberFormat.getInstance().format((1000 / ((float) elapsed / LIMIT))) + " cps)");

  }

}
