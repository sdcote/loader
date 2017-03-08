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
 * The NullArm class models an ARM transaction that performs no work.
 */
public class NullArm implements ArmTransaction
{
  protected static final long ARM_EVENTS = Log.getCode( ArmTransaction.LOG_CATEGORY );

  /** The master timer that accumulates our data. */
  protected ArmMaster _master = null;

  /** The parent of this transaction. */
  protected ArmTransaction _parent = null;

  protected String _crid = null;
  protected String _name = null;

  protected short _status = ArmTransaction.NEW;




  public NullArm( final ArmMaster master, final String name, final String crid )
  {
    _master = master;
    _crid = crid;

    if( ( name == null ) && ( _master != null ) )
    {
      _name = _master._name;
    }
    else
    {
      _name = name;
    }
  }




  public NullArm( final ArmMaster master, final String name, final String crid, final ArmTransaction parent )
  {
    _master = master;
    _crid = crid;
    _parent = parent;
    if( ( name == null ) && ( _master != null ) )
    {
      _name = _master._name;
    }
    else
    {
      _name = name;
    }
  }




  /**
   * @see net.coyote.i13n.NullArm#decrement(java.lang.String, long)
   */
  public long decrease( final String name, final long value )
  {
    return 0;
  }




  /**
   * @see net.coyote.i13n.NullArm#decrement(java.lang.String)
   */
  public long decrement( final String name )
  {
    return 0;
  }




  /**
   * @see net.coyote.i13n.NullArm#destroy()
   */
  public void destroy()
  {
  }




  /**
   * @see coyote.i13n.ArmTransaction#getCRID()
   */
  public String getCRID()
  {
    return _crid;
  }




  /**
   * @see coyote.i13n.ArmTransaction#getMaster()
   */
  public ArmMaster getMaster()
  {
    return _master;
  }




  /**
   * @see coyote.i13n.ArmTransaction#getName()
   */
  public String getName()
  {
    return _name;
  }




  /**
   * @see coyote.i13n.ArmTransaction#getOverheadTime()
   */
  public long getOverheadTime()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.ArmTransaction#getStartTime()
   */
  public long getStartTime()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.ArmTransaction#getStatus()
   */
  public short getStatus()
  {
    return _status;
  }




  /**
   * @see coyote.i13n.ArmTransaction#getStopTime()
   */
  public long getStopTime()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.ArmTransaction#getTotalTime()
   */
  public long getTotalTime()
  {
    return 0;
  }




  /**
   * @see coyote.i13n.ArmTransaction#getWaitTime()
   */
  public long getWaitTime()
  {
    return 0;
  }




  /**
   * @see net.coyote.i13n.NullArm#increase(java.lang.String, long)
   */
  public long increase( final String name, final long value )
  {
    return 0;
  }




  /**
   * @see net.coyote.i13n.NullArm#increment(java.lang.String)
   */
  public long increment( final String name )
  {
    return 0;
  }




  /**
   * @see coyote.i13n.ArmTransaction#setCRID(java.lang.String)
   */
  public void setCRID( final String crid )
  {
    _crid = crid;
  }




  /**
   * @see net.coyote.i13n.NullArm#start()
   */
  public void start()
  {
  }




  /**
   * Create a child ARM.
   * @param name
   * @param crid the coorelation identifier to use for this arm.
   * @return
   */
  public ArmTransaction startArm( final String name )
  {
    return new NullArm( null, null, null );
  }




  public ArmTransaction startArm( final String name, final String crid )
  {
    return new NullArm( null, null, null );
  }




  /**
   * @see net.coyote.i13n.NullArm#stop()
   */
  public long stop()
  {
    return 0;
  }




  /**
   * @see net.coyote.i13n.NullArm#stop(short)
   */
  public long stop( final short status )
  {
    return 0;
  }




  /**
   * Return the string representation of the timer.
   * 
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    if( _master != null )
    {
      return _master.toString();
    }

    return "NullARM";
  }




  /**
   * @see net.coyote.i13n.NullArm#update(java.lang.String, java.lang.Object)
   */
  public void update( final String name, final Object value )
  {
  }

}
