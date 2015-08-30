/*
 * $Id: ThreadPool.java,v 1.5 2005/04/12 17:05:48 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.loader.thread;

import java.io.InterruptedIOException;
import java.util.HashSet;
import java.util.Iterator;

import coyote.dataframe.DataFrameException;
import coyote.loader.cfg.Config;
import coyote.loader.log.Log;


/**
 * A pool of worker threads.
 *
 * <p>This class can be specialized to handle a variety of jobs, but is designed
 * to handle objects that extend net.bralyn.util.thread.ThreadJob. The reason for this
 * is that ThreadJob will respond to suspend(), resume() and shutdown() without
 * using deprecated methods of the java.lang.Thread class.</p>
 *
 * <p>Avoids the expense of thread creation by pooling threads after their run
 * methods exit for reuse.</p>
 *
 * <p>If the maximum pool size is reached, jobs wait for a free thread. By
 * default there is no maximum pool size.  Idle threads timeout and terminate
 * until the minimum number of threads are running.</p>
 *
 * <p>This implementation uses the run(ThreadJob) method to place jobs in a
 * queue, which is read by the getJob(timeout) method. Derived implementations
 * may specialize getJob(timeout) to obtain jobs from other sources without
 * queing overheads.</p>
 *
 * TODO Rethink "job_wait_time: specifically all threads and and below the
 *       minimum should have a job_wait_time of 0 while the rest should have a
 *       time-out so they can shutdown if necessary.
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.5 $
 */
public class ThreadPool {
  private int maximum_workers = 128;
  private int minimum_workers = 2;
  private int shutdown_wait = 1000;

  /**
   * How many milliseconds we wait / block to get a job from the queue
   * Default=0=forever
   */
  private int job_wait_time = 0;

  /** This is the name of this thread pool */
  private String pool_name;

  /** This is the unique identifier for thread workers */
  private int thread_identifier = 0;

  /** Flag indication if we are running or not */
  private volatile boolean active = false;

  /** This is the blocking job gueue we used to feed the workers */
  private BlockingQueue jobqueue;

  /** This holds the references to threads that are idle */
  private HashSet idle_set = new HashSet();

  /** This holds the references to threads that are participating in the pool */
  private HashSet worker_set;

  /** The time when we start idling due to inactivity */
  protected long idle_timeout = 30000;

  /** The DataCapsule we use to store and output our configuration */
  protected Config configuration = new Config();

  /** Counter of threadpools */
  private static long poolcount = 0;

  /** Field JOBWAIT_TAG */
  public static final String JOBWAIT_TAG = "ThreadJobWait";

  /** Field STOPWAIT_TAG */
  public static final String STOPWAIT_TAG = "ThreadStopWait";

  /** Field MINWORKERS_TAG */
  public static final String MINWORKERS_TAG = "ThreadMinWorkers";

  /** Field MAXWORKERS_TAG */
  public static final String MAXWORKERS_TAG = "ThreadMaxWorkers";

  private static final long THREAD = Log.getCode( "THREAD" );




  /**
   *
   */
  public ThreadPool() {
    configuration.setName( "ThreadService" );

    pool_name = "ThreadPool" + poolcount++;
  }




  /**
   * Constructor
   *
   * @param name Pool name
   */
  public ThreadPool( String name ) {
    this();

    pool_name = name;
  }




  /**
   * Constructor using a DataCapsule to determine it's configuration
   *
   * @param config
   * @throws IllegalArgumentException if the configuration is invalid
   */
  public ThreadPool( Config config ) throws IllegalArgumentException {
    this();

    config( config );
  }




  /**
   * Configure the threadpool with the given data capsule
   *
   * @param cfg
   *
   * @throws IllegalArgumentException
   */
  public void config( Config cfg ) throws IllegalArgumentException {
    if ( cfg.getName() == null ) {
      throw new IllegalArgumentException( "Configuration capsule did not contain a name" );
    }

    setName( cfg.getName() );

    // Set the time a job should wait to get a job from the job queue
    if ( cfg.contains( JOBWAIT_TAG ) ) {
      try {
        setJobWaitTime( cfg.getAsInt( JOBWAIT_TAG ) );
      } catch ( DataFrameException e ) {
      }
    }

    // Set the time the threadpool will wait for all the threads to stop on
    // shutdown
    if ( cfg.contains( STOPWAIT_TAG ) ) {
      try {
        setStopWaitTime( cfg.getAsInt( STOPWAIT_TAG ) );
      } catch ( DataFrameException e ) {
      }
    }

    // Set the maximum number of workers to have active at any one time
    if ( cfg.contains( MAXWORKERS_TAG ) ) {
      try {
        setMaxThreadCount( cfg.getAsInt( MAXWORKERS_TAG ) );
      } catch ( DataFrameException e ) {
      }
    }

    // Set the minimum number of workers to have active at any one time
    if ( cfg.contains( MINWORKERS_TAG ) ) {
      try {
        setMinThreadCount( cfg.getAsInt( MINWORKERS_TAG ) );
      } catch ( DataFrameException e ) {
      }
    }
  }




  /**
   * @return the name of the pool.
   */
  public String getName() {
    return pool_name;
  }




  /**
   * Sets the name of the pool.
   *
   * @param name
   */
  public void setName( String name ) {
    pool_name = name;

    configuration.setName( name );
  }




  /**
   * Is the pool running jobs?
   *
   * @return True if start() has been called.
   */
  public boolean isStarted() {
    return isRunning() && ( worker_set != null );
  }




  /**
   * Sets the running flag to the given boolean argument.
   *
   * @param flag
   */
  private void setRunning( boolean flag ) {
    synchronized( pool_name ) {
      active = flag;
    }
  }




  /**
   * @return whether or not the current thread is running.
   */
  public boolean isRunning() {
    synchronized( pool_name ) {
      return active;
    }
  }




  /**
   * Get the number of threads in the pool.
   *
   * @return Number of threads
   */
  public int getThreadCount() {
    if ( worker_set == null ) {
      return 0;
    }

    return worker_set.size();
  }




  /**
   * Get the minimum number of threads.
   *
   * @return minimum number of threads.
   */
  public int getMinWorkerCount() {
    return minimum_workers;
  }




  /**
   * Set the minimum number of workers for this threadpool.
   *
   * @param min
   */
  public void setMinThreadCount( int min ) {
    configuration.put( MINWORKERS_TAG, new Integer( min ).toString() );
    minimum_workers = min;
  }




  /**
   * Get the number of milliseconds of inactivity before starting to idle.
   *
   * @return the number of milliseconds
   */
  public long getIdleTimeout() {
    return idle_timeout;
  }




  /**
   * Set the number of milliseconds of inactivity before starting to idle.
   *
   * @param millis
   */
  public void setIdleTimeout( long millis ) {
    idle_timeout = millis;

    synchronized( worker_set ) {
      Object[] worker = worker_set.toArray();

      for ( int i = 0; i < worker.length; i++ ) {
        ThreadWorker tworker = (ThreadWorker)worker[i];
        tworker.setIdleTimeout( millis );
      }
    }
  }




  /**
   * Get the maximum number of workers for this threadpool.
   *
   * @return <code>int</code> representing the maximum allowed threads for
   *         this thread pool
   */
  public int getMaxThreadCount() {
    return maximum_workers;
  }




  /**
   * Set the maximum number of workers for this threadpool.
   *
   * @param max
   */
  public void setMaxThreadCount( int max ) {
    configuration.put( MAXWORKERS_TAG, new Integer( max ).toString() );
    maximum_workers = max;
  }




  /**
   * Get the job-wait time in milliseconds.
   *
   * <p>This returns the number of milliseconds a threadworker will wait when
   * trying to get a job from the job queue. A value of zero indicates the
   * ThreadWorker will block indefinitely, or until it is interrupted as in a
   * shutdown situation. If a ThreadWorker cannot receive a job in
   * getJobWaitTime() milliseconds, it will probably be destroyed. </p>
   *
   * <p>The reasoning behind this is that since the ThreadWorker waited for the
   * entire wait time, it is probably not needed and is wasting CPU time.</p>
   *
   * @return Maximum wait time in milliseconds a ThreadWorker waits for a job
   *         from the job queue.
   */
  public int getJobWaitTime() {
    return job_wait_time;
  }




  /**
   * Set the job-wait time in milliseconds.
   *
   * <p>This sets the number of milliseconds a threadworker will wait when
   * trying to get a job from the job queue. A value of zero indicates the
   * ThreadWorker will block indefinitely, or until it is interrupted as in a
   * shutdown situation. If a ThreadWorker cannot receive a job in
   * getJobWaitTime() milliseconds, it will probably be destroyed.</p>
   *
   * <p>The reasoning behind this is that since the ThreadWorker waited for the
   * entire wait time, it is probably not needed and is wasting CPU time.</p>
   *
   * @param millis
   */
  public void setJobWaitTime( int millis ) {
    configuration.put( JOBWAIT_TAG, new Integer( millis ).toString() );

    job_wait_time = millis;
  }




  /**
   * Get the stop (shutdown) thread wait time.
   *
   * <p>This returns the number of milliseconds the threadpool will wait for
   * all the ThreadWorker to terminate before clearing out the pool.</p>
   *
   * @return Maximum wait time in milliseconds a ThreadPool waits for a job to
   *         complete after requesting a shutdown.
   */
  public int getStopWaitTime() {
    return shutdown_wait;
  }




  /**
   * Set the maximum number of milliseconds to wait for all thread jobs to
   * terminate before clearing out the thread references.
   *
   * <p>The stop wait time is the amount of time the thread pool will wait for
   * all the thread jobs to complete processing after issuing a shutdown
   * request to the thread. If a thread is still alive after this time, an
   * error will be sent to standard error and the references will be
   * cleared.</p>
   *
   * @param millis Maximum wait time in milliseconds a ThreadPool waits for a
   *          job to complete after requesting a shutdown.
   */
  public void setStopWaitTime( int millis ) {
    configuration.put( STOPWAIT_TAG, new Integer( millis ).toString() );
    shutdown_wait = millis;
  }




  /**
   * @return the number of jobs in the pool.
   */
  public int size() {
    return jobqueue.size();
  }




  /**
   * @return the number of open slots in the pool.
   */
  public int space() {
    return jobqueue.space();
  }




  /**
   * Start the ThreadPool.
   *
   * Construct the minimum number of threads.
   */
  synchronized public void start() {
    if ( isRunning() ) {
      Log.append( THREAD, "'" + pool_name + "' already started" );

      return;
    }

    Log.append( THREAD, "ThreadPool.start() Starting '" + pool_name + "' pool" );

    // Create a job queue with enough space for all our workers to have 5
    // queued jobs each
    jobqueue = new BlockingQueue( maximum_workers * 5 );

    // Create a set large enough to hold the maximum number of workers/2 +5
    worker_set = new HashSet( maximum_workers + maximum_workers / 2 + 5 );

    // Start the threads
    for ( int i = 0; i < minimum_workers; i++ ) {
      newWorker();
    }

    // Give everything a chance to start
    Thread.yield();

    // Indicate we are open for business
    setRunning( true );
    Log.append( THREAD, "ThreadPool.start() returning with " + getThreadCount() + " workers running" );
  }




  /**
   * Check the load in the job queue and start another thread if necessary.
   *
   * <p>This method is used by those who want to make sure that the pool is
   * running at capacity. Sometimes the maximum thread count will be raised and
   * the object that raised the max thread count wants to make sure we honor
   * that new capacity.</p>
   *
   * @return The number of jobs currently waiting to be serviced by the queue.
   */
  public int checkLoad() {
    // if the queue is not empty AND there is work to do AND there are no idle
    // workers to do it...
    if ( ( jobqueue.size() > 0 ) && ( worker_set.size() < maximum_workers ) && ( idle_set.size() == 0 ) ) {
      // ...create a new worker for this pool
      newWorker();
    }

    return jobqueue.size();
  }




  /**
   * Stop the ThreadPool.
   *
   * <p>All threads are interrupted and if they do not terminate after a short
   * delay, they are stopped.</p>
   */
  synchronized public void stop() {
    if ( Log.isLogging( THREAD ) ) {
      Log.append( THREAD, "Stopping '" + pool_name + "' thread pool" );
    }

    if ( worker_set == null ) {
      return;
    }

    setRunning( false );

    // Shutdown the workers
    Object[] worker = worker_set.toArray();

    for ( int i = 0; i < worker.length; i++ ) {
      ThreadWorker tworker = (ThreadWorker)worker[i];
      tworker.shutdown();
      tworker.join( 50 );
    }

    // wait a while for all threads to die
    Thread.yield();

    try {
      long end_wait = System.currentTimeMillis() + shutdown_wait;

      while ( ( worker_set.size() > 0 ) && ( end_wait > System.currentTimeMillis() ) ) {
        Thread.yield();
      }

      // Warn about any still running
      if ( worker_set.size() > 0 ) {
        worker = worker_set.toArray();

        for ( int i = 0; i < worker.length; i++ ) {
          ThreadWorker tworker = (ThreadWorker)worker[i];

          if ( tworker.current_thread.isAlive() ) {
            tworker.shutdown();
            Thread.yield();

            if ( tworker.current_thread.isAlive() ) {
              if ( Log.isLogging( THREAD ) ) {
                Log.append( THREAD, "Can't stop \"" + tworker.getThread().getName() + "\"" );
              }
            }
          }
        }
      }
    }
    finally {
      worker_set.clear();

      worker_set = null;
    }
  }




  /**
   * Start a new ThreadWorker.
   */
  private synchronized void newWorker() {
    try {
      ThreadWorker worker = new ThreadWorker( pool_name + ".wkr." + ( thread_identifier++ ) );
    } catch ( Exception e ) {
      if ( Log.isLogging( THREAD ) ) {
        Log.append( THREAD, "ThreadPool.newWorker() exception: " + e.toString() );
      }
    }
  }




  /**
   * Join the ThreadPool.
   *
   * Wait for all threads to complete.
   * @exception java.lang.InterruptedException
   */
  final public void join() throws java.lang.InterruptedException {
    while ( ( worker_set != null ) && ( worker_set.size() > 0 ) ) {
      ThreadWorker worker = null;

      synchronized( this ) {
        Iterator iter = worker_set.iterator();

        while ( iter.hasNext() ) {
          worker = (ThreadWorker)iter.next();
        }
      }

      if ( worker != null ) {
        worker.getThread().join();
      }
    }
  }




  /**
   * Get a job.
   *
   * <p>This method is called by the ThreadWorkers to get jobs. The call blocks
   * until a job is available.</p>
   *
   * <p>The default implementation removes jobs from the BlockingQueue used by
   * the run() method. Derived implementations of ThreadPool may specialize this
   * method to obtain jobs from other sources.</p>
   *
   * @param millis The timout to wait for a job.
   *
   * @return Job or null if no job available after timeout.
   *
   * @exception InterruptedException
   * @exception InterruptedIOException
   */
  protected ThreadJob getJob( int millis ) throws InterruptedException, InterruptedIOException {
    return (ThreadJob)jobqueue.get( millis );
  }




  /**
   * Handle the job by placing it in the pool (hope it doesn't get too wet).
   *
   * <p>The job is passed via a BlockingQueue with the same capacity as the
   * ThreadPool.</p>
   *
   * <p>If the ThreadPool is not running when this method is called, then this
   * method starts the pool running.</p>
   *
   * @param job A ThreadJob object to run.
   *
   * @throws InterruptedException
   */
  public void handle( ThreadJob job ) throws InterruptedException {
    // If we are not running, we better get started
    if ( !isRunning() ) {
      start();
    }

    if ( ( getThreadCount() == 0 ) && ( getMaxThreadCount() > 0 ) ) {
      newWorker();
    }

    // if there is no job to do...
    if ( job == null ) {
      // whine and complain
      Log.warn( "ThreadPool.handle() received a null job" );
    } else {
      // otherwise, dunk it in the pool
      jobqueue.put( job );
    }
  }




  /**
   * Place a runnable object inside a ThreadJob and place the ThreadJob in the
   * pool.
   *
   * @param runnable - the runnable object
   */
  public void run( Runnable runnable ) {
    ThreadJob job = new ThreadJob( runnable );

    try {
      handle( job );
    } catch ( InterruptedException ie ) {}
  }




  /**
   * Get the set of ThreadWorkers as an array of objects.
   *
   * @return An array of references to the threadworkers
   */
  Object[] getThreadWorkers() {
    if ( worker_set != null ) {
      return worker_set.toArray();
    }

    return new Object[0];
  }

  /**
   * Pool worker class.
   *
   * <p>This class represents a thread of execution managed by a ThreadPool. It
   * has the ability to handle ThreadJobs. Not Runnable, only ThreadJobs. Those
   * Runnable implementations have no way to let them know that they have to
   * stop running or suspend processing for a while. Rather inflexible if you
   * ask me.</p>
   *
   * <p>ThreadJob can do all that quite nicely in a very thread-safe way,
   * because we wrote it to be very thread-safe, and all our profiling tools and
   * lint-checkers told us so <code>:P</code>.</p>
   *
   * <p>It was written as an inner-class for compactness and because we changed
   * our design, like, 5 times during development, and left it that way. (Why do
   * I keep saying &quot;we&quot;? I'm the only one to blame for this mess!</p>
   */
  class ThreadWorker extends ThreadJob {

    /** Reference to our current ThreadJob */
    private volatile ThreadJob current_job = null;

    /** The number of times this thread has run */
    private int runs = 0;




    /**
     * Constructor
     *
     * @param name
     */
    public ThreadWorker( String name ) {
      current_thread = new Thread( this );

      current_thread.setName( name );
      current_thread.start();
    }




    /**
     * Request this object to shutdown.
     */
    public void shutdown() {
      synchronized( mutex ) {
        this.shutdown = true;

        if ( current_thread != null ) {
          // If there is a job running in our current thread tell it to shutdown
          if ( current_job != null ) {
            // make sure it does not try to restart
            current_job.restart = false;
            current_job.shutdown = true;

            // inform it to shutdown
            current_job.shutdown();
          }

          // Interrupt the current thread of execution
          current_thread.interrupt();

          // Make sure suspended threads are resumed so they can shutdown
          mutex.notifyAll();
        }
      }
    }




    /**
     *
     */
    public void initialize() {
      super.initialize();
      worker_set.add( this );

      if ( Log.isLogging( THREAD ) ) {
        Log.append( THREAD, "[O] " + current_thread.getName() + " is initialized." );
      }
    }




    /**
     * Final resource clean-up before exiting or restarting.
     *
     * <p>Normally this routine is called as the main run() loop exits. This
     * routine is also called when a restart() is requested. So design your
     * termination procedure with care.</p>
     */
    public void terminate() {
      super.terminate();

      if ( worker_set != null ) {
        synchronized( worker_set ) {
          worker_set.remove( this );
        }
      }

      if ( Log.isLogging( Log.DEBUG_EVENTS ) ) {
        // Had to put this in because we were getting a null pointer exception
        // on some occasions Figure out why!
        if ( ( current_thread != null ) && ( worker_set != null ) && ( idle_set != null ) ) {
          if ( Log.isLogging( THREAD ) ) {
            Log.append( THREAD, "[X] " + current_thread.getName() + " is terminated normally after running " + runs + " jobs. - Remaining workers=" + worker_set.size() + "  of which " + idle_set.size() + " are idle." );
          }
        }
      }
    }




    /**
     * Try to get a job from the job pool and then try to run it.
     *
     * <p>First this method tries to get a job from the pool queue (pool cue?)
     * and if it times-out before it could get a job, it calls shutdown on
     * itself and terminates normally if the number of current workers is
     * greater than the minimum.</p>
     *
     * <p>If it gets a job, then it runs that job. Duh!</p>
     */
    public void doWork() {
      ThreadJob job = null;

      // Try to get a job from the queue
      try {
        // set this thread in the idle/waiting set
        synchronized( idle_set ) {
          idle_set.add( current_thread );
        }

        // Call getJob(timeout) and wait for a job to arrive
        job = getJob( job_wait_time );

        // If no job, it means we timed out before a job arrived in the queue
        if ( ( job == null ) && isRunning() ) {
          if ( Log.isLogging( THREAD ) ) {
            Log.append( THREAD, "Total workers=" + worker_set.size() + "  Minimum=" + minimum_workers + "  Idle workers=" + idle_set.size() + " jobqueue=" + jobqueue.size() + "/" + jobqueue.capacity() + " jobs waiting" );
          }

          if ( ( worker_set.size() > minimum_workers ) && ( idle_set.size() > 0 ) ) {
            // Kill thread if it is in excess of the minimum.
            if ( Log.isLogging( THREAD ) ) {
              Log.append( THREAD, "Idle death: " + current_thread.getName() );
            }

            this.shutdown();
          }
        }
      } catch ( InterruptedException e ) {
        // Log.append(THREAD,"\""+current_thread.getName()+"\" ThreadWorker.doWork() exception "+e.toString()+" shutdown="+isShutdown());
      } catch ( InterruptedIOException e ) {
        if ( Log.isLogging( THREAD ) ) {
          Log.append( THREAD, "\"" + current_thread.getName() + "\" ThreadWorker.doWork() exception " + e.toString() + " shutdown=" + isShutdown() );
        }
      } catch ( Exception e ) {
        Log.append( THREAD, "\"" + current_thread.getName() + "\" ThreadWorker.doWork() exception " + e.toString() + "-" + e.getMessage() );
      }
      finally {
        synchronized( idle_set ) {
          idle_set.remove( current_thread );

          // If not more threads accepting - start one
          if ( ( idle_set.size() == 0 ) && isRunning() && ( job != null ) && ( worker_set.size() < maximum_workers ) ) {
            newWorker();
          }

        } // sync

      } // End of trying to get a job from the queue

      // handle the ThreadJob
      if ( !isShutdown() && ( job != null ) ) {
        try {
          // Tag thread if debugging
          Log.append( THREAD, this.getThread().getName() + " handling " + job + " - total runs=" + runs );

          // If we got a job to do...
          if ( job != null ) {
            // Let the other methods know about it
            synchronized( mutex ) {
              current_job = job;
            }

            // It all comes down to this: "Run the current job in this thread"
            current_job.run();

            // Now that we are done, remove the job from the global reference
            synchronized( current_job ) {
              current_job = null;
            }

            // Increment the run counter
            runs++;
          }
        } catch ( Exception e ) {
          java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
          e.printStackTrace( new java.io.PrintWriter( out, true ) );
          Log.error( "\"" + current_thread.getName() + "\" ThreadWorker.run() exception during run() call " + e.toString() + ":" + e.getMessage() + System.getProperty( "line.separator" ) + out.toString() );
        }
        finally {
          job = null;
        }

      } // end of handling the threadjob

    } // End of doWork()

  } // End of inner class

}