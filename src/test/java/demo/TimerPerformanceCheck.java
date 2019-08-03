package demo;

import coyote.i13n.NullTimer;
import coyote.i13n.Tabs;
import coyote.i13n.Timer;

import java.text.NumberFormat;


/**
 *
 */
public class TimerPerformanceCheck {
  private static final long LIMIT = 10000000;


  private static long runNullTimerTest() {
    Tabs.enableTiming(false);

    long started = System.currentTimeMillis();

    for (int x = 0; x < LIMIT; x++) {
      Timer mon = Tabs.startTimer("NullTimer");
      mon.stop();
    }
    long elapsed = System.currentTimeMillis() - started;

    Tabs.enableTiming(true);

    return elapsed;
  }


  private static long runNullTimerStartStop() {
    Timer mon = new NullTimer();
    long started = System.currentTimeMillis();

    for (int x = 0; x < LIMIT; x++) {
      mon.start();
      mon.stop();
    }
    return System.currentTimeMillis() - started;
  }


  private static long runTimingTimerTest() {
    Tabs.enableTiming(true);

    long started = System.currentTimeMillis();

    for (int x = 0; x < LIMIT; x++) {
      Timer mon = Tabs.startTimer("ArgosTimer");
      mon.stop();
    }
    return System.currentTimeMillis() - started;
  }


  private static long runTimingTimerStartStop() {
    Tabs.enableTiming(true);

    long started = System.currentTimeMillis();

    Timer mon = new NullTimer();
    for (int x = 0; x < LIMIT; x++) {
      mon.start();
      mon.stop();
    }
    return System.currentTimeMillis() - started;
  }


  /**
   * @param args
   */
  public static void main(String[] args) {

    long elapsed = runNullTimerTest();
    System.out.print("Null factory test: " + ((float) elapsed / LIMIT) + " ms/c Avg");
    System.out.println(" (" + NumberFormat.getInstance().format((1000 / ((float) elapsed / LIMIT))) + " cps)");

    elapsed = runNullTimerStartStop();
    System.out.print("Null Start-Stop test: " + ((float) elapsed / LIMIT) + " ms/c Avg");
    System.out.println(" (" + NumberFormat.getInstance().format((1000 / ((float) elapsed / LIMIT))) + " cps)");

    elapsed = runTimingTimerTest();
    System.out.print("Timing Timer test: " + ((float) elapsed / LIMIT) + " ms/c Avg");
    System.out.println(" (" + NumberFormat.getInstance().format((1000 / ((float) elapsed / LIMIT))) + " cps)");

    elapsed = runTimingTimerStartStop();
    System.out.print("Timing Timer Start-Stop test: " + ((float) elapsed / LIMIT) + " ms/c Avg");
    System.out.println(" (" + NumberFormat.getInstance().format((1000 / ((float) elapsed / LIMIT))) + " cps)");

  }

}
