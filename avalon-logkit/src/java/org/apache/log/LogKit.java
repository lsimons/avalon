/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

import java.io.File;
import java.lang.reflect.*;
import java.util.*;
import org.apache.log.output.*;
import org.apache.log.output.DefaultOutputLogTarget;
/**
 * The LogKit provides the access to static methods to
 * manipulate the logging sub-system
 *
 *  @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class LogKit
{
    protected static final ThreadLocal     c_context                = new ThreadLocal();
    protected static final Hashtable       c_loggers                = new Hashtable();
    protected static final Hashtable       c_categories             = new Hashtable();
    protected static final Hashtable       c_logTargets;
    protected static Priority.Enum         c_priority               = Priority.DEBUG;
    protected static LogTarget             c_defaultLogTarget;

    static
    {
        c_logTargets = new Hashtable();
        c_defaultLogTarget = new DefaultOutputLogTarget();
        c_logTargets.put( "default", c_defaultLogTarget );
    }

    /*
      //Need to add this in at LogManager level rather than here ....
      public static Logger createLogger(String name, URL destination, Priority.Enum priority)
      {

      // create the new category. If it already exist return the cached one.
      Category category = createCategory(name, priority );
      // create the new target. if already exist uses the chached one.
      // the target name is destination based so if a caller ask for a
      // log name but aiming to the same destination, the same target is used.
      LogTarget logTarget = createLogTarget("" + destination, destination);
      LogTarget logTargets[] = new LogTarget[] { logTarget };
      LogKit.createLogger( category, logTargets );
      return LogKit.getLoggerFor(name);
      }

      private static LogTarget createLogTarget(String name, URL destination) {

      LogTarget logTarget = getLogTarget(name);
      if (logTarget == null) {
      String protocol = destination.getProtocol();
      String logTargetClassName = null;

      // bad hack here... need to read from Properties?
      if (protocol.equals("file")) {
      logTarget = new FileOutputLogTarget();
      } else if (protocol.equals("null")) {
      logTarget = new NullOutputLogTarget();
      } else {
      logTarget = new GenericOutputLogTarget();
//                logTarget = c_defaultLogTarget;
}
//            if (protocol.equals("file")) {
//                logTargetClassName = "FileOutputLogTarget";
//            } else if (protocol.equals("null")) {
//                logTargetClassName = "NullOutputLogTarget";
//            }

//            if (logTargetClassName != null) {
//                try {
//                    logTarget = (LogTarget) Class.forName( "org.apache.log.output." + logTargetClassName ).newInstance();
//                } catch(Exception e ) {
//                    logTarget = c_defaultLogTarget;
//                }
//                logTarget.setDestination(destination);
//            } else {
//                logTarget = c_defaultLogTarget;
//            }/
logTarget.setDestination(destination);
LogKit.addLogTarget( name, logTarget );
}
return logTarget;
}
    */

    /**
     * Add a named log target to global list.
     *
     * @param name the name of target
     * @param target the target
     */
    public static void addLogTarget( final String name, final LogTarget target )
    {
        if( name.equals("default") ) c_defaultLogTarget = target;
        c_logTargets.put( name, target );
    }

    /**
     * Retrieve named log target if it exists.
     *
     * @param name the name of log target
     * @return the LogTarget
     */
    public static LogTarget getLogTarget( final String name )
    {
        return (LogTarget)c_logTargets.get( name );
    }

    /**
     * Create or update named category and retrieve it.
     *
     * @param categoryName name of category
     * @param priority the priority of categroy
     * @return the catgeory
     */
    public static Category createCategory( final String categoryName,
                                           final Priority.Enum priority )
    {
        Category category = (Category)c_categories.get( categoryName );

        if( null == category )
        {
            category = new Category( categoryName );
            c_categories.put( categoryName, category );
        }

        category.setPriority( priority );

        return category;
    }

    /**
     * Create or update instance of logger.
     *
     * @param category the category that logger logs about
     * @return the Logger
     */
    public static Logger createLogger( final Category category )
    {
        return createLogger( category, null );
    }

    /**
     * Create or update instance of logger.
     *
     * @param logTargets[] the list of log targets logger is to output to.
     * @param category the category that logger logs about
     * @return the Logger
     */
    public static Logger createLogger( final Category category, final LogTarget logTargets[] )
    {
        final String categoryName = category.getName();
        Logger logger = (Logger)c_loggers.get( categoryName );

        if( null == logger )
        {
            final int index = categoryName.lastIndexOf( Category.SEPARATOR );

            Logger parent = null;

            if( -1 != index )
            {
                final String parentName = categoryName.substring( 0, index );
                parent = getLoggerFor( parentName );
            }

            logger = new Logger( category, logTargets, parent );
            c_loggers.put( categoryName, logger );
        }
        else
        {
            if( null != logTargets )
            {
                logger.setLogTargets( logTargets );
            }
        }

        return logger;
    }

    /**
     * Get the Current ContextStack.
     * This returns a ContextStack associated with current thread. If the
     * thread doesn't have a ContextStack associated with it then a new
     * ContextStack is created with the name of thread as base context.
     *
     * @return the current ContextStack
     */
    public static ContextStack getCurrentContext()
    {
        ContextStack context = (ContextStack)c_context.get();

        if( null == context )
        {
            context = new ContextStack();
            context.push( Thread.currentThread().getName() );
            c_context.set( context );
        }

        return context;
    }

    /**
     * Retrieve the default log target.
     *
     * @return the default LogTarget
     */
    public static LogTarget getDefaultLogTarget()
    {
        return c_defaultLogTarget;
    }

    /**
     * Return VM global priority.
     *
     * @return the priority
     */
    public static Priority.Enum getGlobalPriority()
    {
        return c_priority;
    }

    /**
     * Retrieve a logger for named category.
     *
     * @param category the context
     * @return the Logger
     */
    public static Logger getLoggerFor( final String category )
    {
        synchronized( c_loggers )
        {
            Logger logger = (Logger)c_loggers.get( category );
            if( null == logger )
            {
                logger = createLogger( createCategory( category, Priority.DEBUG ) );
            }
            return logger;
        }
    }

    /**
     * Retrieve a Priority.Enum value for the String Priority level.
     *
     * @param priority the priority
     * @return the descriptive string
     */
    public static Priority.Enum getPriorityForName( final String priority )
    {
        if( Priority.DEBUG.getName().equals( priority ) ) return Priority.DEBUG;
        else if( Priority.INFO.getName().equals( priority ) ) return Priority.INFO;
        else if( Priority.WARN.getName().equals( priority ) ) return Priority.WARN;
        else if( Priority.ERROR.getName().equals( priority ) ) return Priority.ERROR;
        else if( Priority.FATAL_ERROR.getName().equals( priority ) ) return Priority.FATAL_ERROR;
        else return Priority.DEBUG;
    }

    /**
     * Loga an error message and exception to stderr.
     */
    public static void log( final String message, final Throwable t )
    {
        System.err.println( "Error: " + message );
        t.printStackTrace();
    }

    /**
     * Logs an error message to stderr.
     */
    public static void log( final String message )
    {
        System.err.println( "Error: " + message );
    }

    /**
     * Sets the default LogTarget for the default logger.
     */
    public static void setDefaultLogTarget( final LogTarget defaultLogTarget )
    {
        addLogTarget( "default", defaultLogTarget );
        c_defaultLogTarget = defaultLogTarget;
    }

    /**
     * Set the global priority for this virtual machine.  Nothing below
     * this level will be logged when using this LogKit.
     */
    public static void setGlobalPriority( final Priority.Enum priority )
    {
        c_priority = priority;
    }

    /**
     * Constructor hidden to prevent instantiation of this static object
     */
    private LogKit()
    {
    }
}
