/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log;

/**
 * Class holding constants for logging priorities.
 * 
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class Priority
{
    /**
     * DEBUG indicates a message that is used during debugging stage of 
     * project.
     */
    public final static Priority DEBUG = new Priority( "DEBUG", 5 );

    /**
     * INFO indicates a message that is secondary information.
     */
    public final static Priority INFO = new Priority( "INFO", 10 );

    /**
     * WARN indicates a message that is gives a warning of a 
     * potential conflict.
     */
    public final static Priority WARN = new Priority( "WARN", 15 );

    /**
     * ERROR indicates a message that describes a non fatal error.
     */
    public final static Priority ERROR = new Priority( "ERROR", 20 );
    
    /**
     * FATAL_ERROR indicates a message that describes a error 
     * that will terminate the appliation.
     */         
    public final static Priority FATAL_ERROR = new Priority( "FATAL_ERROR", 25 );
   
    private final String        m_name;
    private final int           m_priority;

    /**
     * Retrieve a Priority object for the name parameter.
     *
     * @param priority the priority name
     * @return the Priority for name
     */
    public static Priority getPriorityForName( final String priority )
    {
        if( Priority.DEBUG.getName().equals( priority ) ) return Priority.DEBUG;
        else if( Priority.INFO.getName().equals( priority ) ) return Priority.INFO;
        else if( Priority.WARN.getName().equals( priority ) ) return Priority.WARN;
        else if( Priority.ERROR.getName().equals( priority ) ) return Priority.ERROR;
        else if( Priority.FATAL_ERROR.getName().equals( priority ) ) return Priority.FATAL_ERROR;
        else return Priority.DEBUG;
    }   
 
    private Priority( final String name, final int priority ) 
    {
        m_name = name;
        m_priority = priority;
    }
    
    public String toString()
    {
        return "Priority[" + getName() + "/" + getValue() + "]";
    }
    
    public int getValue() 
    {
        return m_priority;
    }
    
    public String getName() 
    {
        return m_name;
    }
    
    public boolean isGreater( final Priority level ) 
    {
        return m_priority > level.getValue();
    }
    
    public boolean isLower( final Priority level ) 
    {
        return m_priority < level.getValue();
    }
    
    public boolean isLowerOrEqual( final Priority level ) 
    {
        return m_priority <= level.getValue();
    }        
}
