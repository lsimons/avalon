/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

/**
 * Interface holding constants for logging priorities.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Priority
{
    /**
     * DEBUG indicates a message that is used during debugging stage of 
     * project.
     */
    Enum DEBUG = new Enum( "DEBUG", 5 );

    /**
     * INFO indicates a message that is secondary information.
     */
    Enum INFO = new Enum( "INFO", 10 );

    /**
     * WARN indicates a message that is gives a warning of a 
     * potential conflict.
     */
    Enum WARN = new Enum( "WARN", 15 );

    /**
     * ERROR indicates a message that describes a non fatal error.
     */
    Enum ERROR = new Enum( "ERROR", 20 );
    
    /**
     * FATAL_ERROR indicates a message that describes a error 
     * that will terminate the appliation.
     */         
    Enum FATAL_ERROR = new Enum( "FATAL_ERROR", 20 );
    
    public final class Enum 
    {
        protected final String        m_name;
        protected final int           m_priority;

        Enum( final String name, final int priority ) 
        {
            m_name = name;
            m_priority = priority;
        }

        public int getPriority() 
        {
            return m_priority;
        }

        public String getName() 
        {
            return m_name;
        }

        public boolean isGreater( final Enum level ) 
        {
            return m_priority > level.getPriority();
        }
        
        public boolean isLower( final Enum level ) 
        {
            return m_priority < level.getPriority();
        }

        public boolean isLowerOrEqual( final Enum level ) 
        {
            return m_priority <= level.getPriority();
        }        
    }
}
