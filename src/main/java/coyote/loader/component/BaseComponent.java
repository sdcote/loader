/*
 * Copyright Stephan D. Cote' 2008 - All rights reserved.
 */
package coyote.loader.component;

import coyote.dataframe.DataFrame;



/**
 * The BaseComponent class models a starting point for components.
 * 
 * @author Stephan D. Cote' - Enterprise Architecture
 */
public class BaseComponent implements Component
{
  public static final String UNKNOWN = "Unknown";




  /**
   * @see net.smartforge.oam.Component#getApplicationId()
   */
  public String getApplicationId()
  {
    return BaseComponent.UNKNOWN;
  }




  /**
   * @see net.smartforge.oam.Component#getCategory()
   */
  public String getCategory()
  {
    return BaseComponent.UNKNOWN;
  }




  /**
   * @see net.smartforge.oam.Component#getDescription()
   */
  public String getDescription()
  {
    return BaseComponent.UNKNOWN;
  }




  /**
   * @see net.smartforge.oam.Component#getId()
   */
  public String getId()
  {
    return BaseComponent.UNKNOWN;
  }




  /**
   * @see net.smartforge.oam.Component#getProfile()
   */
  public DataFrame getProfile()
  {
    return null;
  }




  /**
   * @see net.smartforge.oam.Component#getStatus()
   */
  public DataFrame getStatus()
  {
    return null;
  }




  /**
   * @see net.smartforge.oam.Component#getSystemId()
   */
  public String getSystemId()
  {
    return BaseComponent.UNKNOWN;
  }




  /**
   * @see net.smartforge.oam.Component#getName()
   */
  public String getName()
  {
    return BaseComponent.UNKNOWN;
  }




  /**
   * @see net.smartforge.oam.Component#shutdown(net.smartforge.Message)
   */
  public void shutdown( final DataFrame command )
  {
  }

}
