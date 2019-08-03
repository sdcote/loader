package demo;

import coyote.i13n.AppEvent;
import coyote.i13n.EventList;

/**
 *
 */
public class EventListStepper {

  public static void main(String[] args) {
    EventList list = new EventList();
    EventList.setMaxEvents(2);

    AppEvent alert0 = list.createEvent("Zero");
    AppEvent alert1 = list.createEvent("One");
    AppEvent alert2 = list.createEvent("Two");
    AppEvent alert3 = list.createEvent("Three");

    System.out.println(alert0);
    System.out.println(alert1);
    System.out.println(alert2);
    System.out.println(alert3);
    System.out.println();

    System.out.println("First = " + list.getFirst());
    System.out.println("Last = " + list.getLast());

  }
}
