package demo;

import coyote.i13n.ArmTransaction;
import coyote.i13n.Tabs;

import java.text.NumberFormat;


/**
 *
 */
public class ArmPerformanceCheck {
  private static final long LIMIT = 1000000;


  private static long runNullArmTest() {
    Tabs.enableArm(false);

    long started = System.currentTimeMillis();

    for (int x = 0; x < LIMIT; x++) {
      ArmTransaction arm = Tabs.startArm("NullArm");
      arm.stop();
    }
    long elapsed = System.currentTimeMillis() - started;

    Tabs.enableArm(true);

    return elapsed;
  }


  private static long runTimingArmTest() {
    Tabs.enableTiming(true);

    long started = System.currentTimeMillis();

    for (int x = 0; x < LIMIT; x++) {
      ArmTransaction arm = Tabs.startArm("TestArm");
      arm.stop();
    }
    return System.currentTimeMillis() - started;
  }


  /**
   * @param args
   */
  public static void main(String[] args) {

    long elapsed = runNullArmTest();
    System.out.print("Null factory test: " + ((float) elapsed / LIMIT) + " ms/c Avg");
    System.out.println(" (" + NumberFormat.getInstance().format((1000 / ((float) elapsed / LIMIT))) + " cps)");

    elapsed = runTimingArmTest();
    System.out.print("Timing ARM test: " + ((float) elapsed / LIMIT) + " ms/c Avg");
    System.out.println(" (" + NumberFormat.getInstance().format((1000 / ((float) elapsed / LIMIT))) + " cps)");

  }

}
