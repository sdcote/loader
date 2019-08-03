package demo;

import coyote.i13n.Tabs;

public class TabsDemo {

  public static void main(String[] args) {

    // Tabs works by allowing you to place instrumentation in your code and leave it there, even in production.
    // Measurements are only taken when the class  of (or a specifically named) sensor is enabled.

    // Turns on all Application Response Measurement sensors
    Tabs.enableArm(true);
    // Turns on only one named ARM sensor ("Bob")
    Tabs.enableArmClass("Bob");
    Tabs.disableArmClass("Bob");

    // Enable all Gauges sensors
    Tabs.enableGauges(true);
    //Tabs.enableGaugeClass("Bob");
    //Tabs.disableGaugeClass("Bob");

    // Enable all the timers
    Tabs.enableTiming(true);
    Tabs.enableTimer("Bob");
    Tabs.disableTimer("Bob");


  }
}
