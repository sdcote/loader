/*
 * Copyright (c) 2006 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and implementation
 */
package coyote.i13n;

/**
 * The NullGauge class models a gauge that does nothing.
 */
public class NullGauge implements Gauge
{

  protected String name = null;




  public NullGauge( final String name )
  {
    this.name = name;
  }




  /**
   * @see coyote.i13n.Gauge#getAvgValuePerSecond()
   */
  public float getAvgValuePerSecond()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.Gauge#getElapsedSeconds()
   */
  public float getElapsedSeconds()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.Gauge#getLastValuePerSecond()
   */
  public long getLastValuePerSecond()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.Gauge#getMaxValuePerSecond()
   */
  public long getMaxValuePerSecond()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.Gauge#getMinuteTotal()
   */
  public long getMinuteTotal()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.Gauge#getMinValuePerSecond()
   */
  public long getMinValuePerSecond()
  {
    return 0;
  }




  /**
   * @see  coyote.i13n.Gauge#getName()
   */
  public String getName()
  {
    return name;
  }




  /**
   * @see coyote.i13n.Gauge#getTotal()
   */
  public long getTotal()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.Gauge#getValuePerMinute()
   */
  public float getValuePerMinute()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.Gauge#getValuePerSecond()
   */
  public float getValuePerSecond()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.Gauge#reset()
   */
  public void reset()
  {
  }




  /**
   * <p>Last profiler metric: 0.000443 ms per call - 2,257,336cps</p>
   * 
   * @see coyote.i13n.Gauge#update(long)
   */
  public void update( final long val )
  {
  }

}
