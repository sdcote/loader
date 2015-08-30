/*
 * $Id: ScheduledJob.java,v 1.3 2004/04/16 12:18:44 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.loader.thread;

import java.util.Calendar;
import java.util.Date;

import coyote.commons.IDescribable;
import coyote.commons.INamable;


/**
 * Class ScheduledJob
 *
 * @todo Add support for IScheduledJobListeners to be notified when jobs are completed, and to provide reference to exceptions that may have occurred during execution
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.3 $
 */
public class ScheduledJob extends ThreadJob implements INamable, IDescribable {

  /** The name of this job for easy reporting */
  protected String name = null;

  /** The description of this Scheduled Job */
  protected String description = null;

  /** When we are supposed to start running */
  protected long executionTime = 0;

  /** When we are supposed to stop running */
  protected long expirationTime = 0;

  /** How long between executions we should wait */
  protected long executionInterval = 0;

  /** How many times we are allowed to execute */
  protected long executionLimit = 0;

  /** How many times we have executed */
  protected long executionCount = 0;

  /** The next job that we should run when we finish */
  protected ScheduledJob chainedJob = null;

  /** Used to implement a doubly-linked list in the Scheduler */
  protected ScheduledJob nextJob = null;

  /** Used to implement a doubly-linked list in the Scheduler */
  protected ScheduledJob previousJob = null;

  /** Indicates this job has been cancelled */
  protected volatile boolean cancelled = false;

  /** Indicates this job is to be repeated */
  protected volatile boolean repeatable = false;

  /** Indicates this job is enabled to be run */
  protected volatile boolean enabled = true;

  /** The Scheduler managing the timing of our execution */
  protected Scheduler scheduler = null;




  /**
   * Constructor ScheduledJob
   */
  public ScheduledJob() {}




  /**
   * Constructor ScheduledJob
   *
   * @param task
   */
  public ScheduledJob( Runnable task ) {
    super.work = task;
  }




  /**
   * Method getDescription
   *
   * @return
   */
  public String getDescription() {
    return description;
  }




  /**
   * Method setDescription
   *
   * @param description
   */
  public void setDescription( String description ) {
    this.description = description;
  }




  /**
   * Method getName
   *
   * @return
   */
  public String getName() {
    return name;
  }




  /**
   * Method setName
   *
   * @param name
   */
  public void setName( String name ) {
    this.name = name;
  }




  /**
   * Method getExecutionInterval
   *
   * @return
   */
  public long getExecutionInterval() {
    return executionInterval;
  }




  /**
   * Method setExecutionInterval
   *
   * @param executionInterval
   */
  public void setExecutionInterval( long executionInterval ) {
    this.executionInterval = executionInterval;
  }




  /**
   * Method getExecutionLimit
   *
   * @return
   */
  public long getExecutionLimit() {
    return executionLimit;
  }




  /**
   * Method setExecutionLimit
   *
   * @param executionLimit
   */
  public void setExecutionLimit( long executionLimit ) {
    this.executionLimit = executionLimit;
  }




  /**
   * Method getExecutionTime
   *
   * @return
   */
  public long getExecutionTime() {
    return executionTime;
  }




  /**
   * Method setExecutionTime
   *
   * @param date
   */
  public void setExecutionTime( Date date ) {
    if ( date != null ) {
      executionTime = date.getTime();
    }
  }




  /**
   * Method setExecutionTime
   *
   * @param cal
   */
  public void setExecutionTime( Calendar cal ) {
    if ( cal != null ) {
      executionTime = cal.getTime().getTime();
    }
  }




  /**
   * Method setExecutionTime
   *
   * @param millis
   */
  public void setExecutionTime( long millis ) {
    executionTime = millis;
  }




  /**
   * Method getExpirationTime
   *
   * @return
   */
  public long getExpirationTime() {
    return expirationTime;
  }




  /**
   * Method setExpirationTime
   *
   * @param date
   */
  public void setExpirationTime( Date date ) {
    if ( date != null ) {
      expirationTime = date.getTime();
    }
  }




  /**
   * Method setExpirationTime
   *
   * @param cal
   */
  public void setExpirationTime( Calendar cal ) {
    if ( cal != null ) {
      expirationTime = cal.getTime().getTime();
    }
  }




  /**
   * Method setExpirationTime
   *
   * @param millis
   */
  public void setExpirationTime( long millis ) {
    expirationTime = millis;
  }




  /**
   * Returns whether or not this job has been running longer than it is
   * supposed.
   *
   * @return True if the expiration time has elapsed, false if the expiration
   *         time has not passed ot if no expiration time has been set.
   */
  public boolean isExpired() {
    if ( ( expirationTime > 0 ) && ( System.currentTimeMillis() - this.started_time > expirationTime ) ) {
      return true;
    }

    return false;
  }




  /**
   * Method isCancelled
   *
   * @return
   */
  public boolean isCancelled() {
    return cancelled;
  }




  /**
   * Method setCancelled
   *
   * @param cancelled
   */
  public void setCancelled( boolean cancelled ) {
    this.cancelled = cancelled;
  }




  /**
   * Method getNextJob
   *
   * @return
   */
  public ScheduledJob getNextJob() {
    return nextJob;
  }




  /**
   * Method setNextJob
   *
   * @param nextJob
   */
  public void setNextJob( ScheduledJob nextJob ) {
    this.nextJob = nextJob;
  }




  /**
   * Method getPreviousJob
   *
   * @return
   */
  public ScheduledJob getPreviousJob() {
    return previousJob;
  }




  /**
   * Method setPreviousJob
   *
   * @param previousJob
   */
  public void setPreviousJob( ScheduledJob previousJob ) {
    this.previousJob = previousJob;
  }




  /**
   * Method getChainedJob
   *
   * @return
   */
  public ScheduledJob getChainedJob() {
    return chainedJob;
  }




  /**
   * Method setChainedJob
   *
   * @param chainedJob
   */
  public void setChainedJob( ScheduledJob chainedJob ) {
    this.chainedJob = chainedJob;
  }




  /**
   * Method getExecutionCount
   *
   * @return
   */
  public long getExecutionCount() {
    return executionCount;
  }




  /**
   * Method incrementExecutionCount
   */
  public void incrementExecutionCount() {
    this.executionCount++;
  }




  /**
   * Method isRepeatable
   *
   * @return
   */
  public boolean isRepeatable() {
    return repeatable;
  }




  /**
   * Method setRepeatable
   *
   * @param repeatable
   */
  public void setRepeatable( boolean repeatable ) {
    this.repeatable = repeatable;
  }




  /**
   * Method isEnabled
   *
   * @return
   */
  public boolean isEnabled() {
    return enabled;
  }




  /**
   * Method setEnabled
   *
   * @param enabled
   */
  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
  }




  /**
   * Method reschedule
   */
  public void reschedule() {
    if ( scheduler != null ) {
      scheduler.reschedule( this );
    }
  }




  /**
   * Method getScheduler
   *
   * @return
   */
  public Scheduler getScheduler() {
    return scheduler;
  }




  /**
   * Method setScheduler
   *
   * @param scheduler
   */
  public void setScheduler( Scheduler scheduler ) {
    this.scheduler = scheduler;
  }

}