package org.apache.avalon.cornerstone.threads.tutorial;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Disposable;

import org.apache.avalon.excalibur.thread.ThreadControl;
import org.apache.avalon.excalibur.thread.ThreadPool;
import org.apache.avalon.cornerstone.services.threads.ThreadManager;

/**
 * ThreadConsumer is a example of a component that uses the ThreadManager
 * servicde to aquired a thread pool.
 *
 * @author Stephen McConnell
 */
public class ThreadConsumer extends AbstractLogEnabled implements
Serviceable, Initializable, Disposable
{
   /**
    * The service manager fro which serrvices are aquired and released.
    */
    private ServiceManager m_manager;

   /**
    * The cornerstone thread manager.
    */
    private ThreadManager m_threads;

   /**
    * A thread pool aquired from the thread manager.
    */
    private ThreadPool m_pool;

   /**
    * A thread control return from the launching of a new thread.
    */
    private ThreadControl m_control;


   /**
    * Servicing of the component by the container during which the 
    * component aquires the ThreadManager service.
    * 
    * @param manager the thread manager
    * @exception ServiceException if the thread manager service is 
    *   unresolvable
    */
    public void service( ServiceManager manager ) throws ServiceException
    {
        m_manager = manager;
        getLogger().info( "aquiring cornerstone threads service" );
        m_threads = (ThreadManager) m_manager.lookup( "threads" );
        getLogger().info( "thread manager aquired: " + m_threads );
    }

   /**
    * Initialization of the component by the container during which we 
    * establish a child thread by passing a runnable object to the thread pool.
    * @exception Exception if an initialization stage error occurs
    */
    public void initialize() throws Exception
    {
        getLogger().info( "initialization" );

        //
        // get the default thread pool
        //

        m_pool = m_threads.getDefaultThreadPool();

        //
        // create a runnable object to run
        //
 
        Counter counter = new Counter();
        counter.enableLogging( getLogger().getChildLogger( "counter" ) );

        //
        // start a thread and get the thread control reference
        //

        m_control = m_pool.execute( counter );
    }

   /**
    * Disposal of the component during which he handle the closue of the 
    * child thread we have establshed during the initialization stage.
    */
    public void dispose()
    {
        getLogger().info( "disposal" );

        if( !m_control.isFinished() )
        {
            //
            // interrupt the child 
            //

            getLogger().info( "disposal invoked while child thread active" );
            m_control.interrupt();

            //
            // Using m_control.join() locks things up - why?  Using a 
            // wait for finished state instead.
            // 

            while( !m_control.isFinished() )
            {
                getLogger().info( "waiting for child" );
                try
                {
                    Thread.sleep( 1000 );
                }
                catch( InterruptedException ie )
                {
                    // ignore it
                }
            }
        }

        //
        // check for errors
        //

        if( m_control.getThrowable() != null )
        {
            getLogger().warn( 
              "thread terminated with exception condition", 
               m_control.getThrowable() );
        }

        if( m_pool != null )
        {
            if( m_pool instanceof Disposable )
            {
                ((Disposable)m_pool).dispose();
            }
            m_pool = null;
        }

        m_manager.release( m_threads );
        m_control = null;
        m_threads = null;
        m_manager = null;
        getLogger().info( "disposal complete" );
    }
}

