/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.format;

import org.apache.log.Formatter;
import org.apache.log.LogEvent;
import java.util.Date;

/**
 * Basic XML formatter that writes out a basic XML-ified log event.
 *
 * Note that this formatter assumes that the category and context
 * values will produce strings that do not need to be escaped in XML.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class XMLFormatter
    implements Formatter
{
    private static final String EOL       = System.getProperty( "line.separator", "\n" );

    //Booleans indicating whether or not we 
    //print out a particular field
    private boolean m_printTime           = true;
    private boolean m_printRelativeTime   = false;
    private boolean m_printPriority       = true;
    private boolean m_printCategory       = true;
    private boolean m_printContext        = true;
    private boolean m_printMessage        = true;
    private boolean m_printException      = true;

    private boolean m_printNumericTime    = true;

    /**
     * Print out time field to log.
     *
     * @param printTime true to print time, false otherwise
     */
    public void setPrintTime( final boolean printTime )
    {
        m_printTime = printTime;
    }

    /**
     * Print out relativeTime field to log.
     *
     * @param printRelativeTime true to print relativeTime, false otherwise
     */
    public void setPrintRelativeTime( final boolean printRelativeTime )
    {
        m_printRelativeTime = printRelativeTime;
    }

    /**
     * Print out priority field to log.
     *
     * @param printPriority true to print priority, false otherwise
     */
    public void setPrintPriority( final boolean printPriority )
    {
        m_printPriority = printPriority;
    }

    /**
     * Print out category field to log.
     *
     * @param printCategory true to print category, false otherwise
     */
    public void setPrintCategory( final boolean printCategory )
    {
        m_printCategory = printCategory;
    }

    /**
     * Print out context field to log.
     *
     * @param printContext true to print context, false otherwise
     */
    public void setPrintContext( final boolean printContext )
    {
        m_printContext = printContext;
    }

    /**
     * Print out message field to log.
     *
     * @param printMessage true to print message, false otherwise
     */
    public void setPrintMessage( final boolean printMessage )
    {
        m_printMessage = printMessage;
    }

    /**
     * Print out exception field to log.
     *
     * @param printException true to print exception, false otherwise
     */
    public void setPrintException( final boolean printException )
    {
        m_printException = printException;
    }

    /**
     * Format log event into string.
     *
     * @param event the event
     * @return the formatted string
     */
    public String format( final LogEvent event )
    {
        final StringBuffer sb = new StringBuffer( 400 );

        sb.append( "<log-entry>" );
        sb.append( EOL );
        
        if( m_printTime )
        {
            sb.append( "  <time>" );
            
            if( m_printNumericTime )
            {
                sb.append( event.getTime() );
            }
            else
            {
                sb.append( new Date( event.getTime() ) );
            }

            sb.append( "</time>" );
            sb.append( EOL );
        }

        if( m_printRelativeTime )
        {
            sb.append( "  <relative-time>" );
            sb.append( event.getRelativeTime() );
            sb.append( "</relative-time>" );
            sb.append( EOL );
        }

        if( m_printPriority )
        {
            sb.append( "  <priority>" );
            sb.append( event.getPriority().getName() );
            sb.append( "</priority>" );
            sb.append( EOL );
        }

        if( m_printCategory )
        {
            sb.append( "  <category>" );
            sb.append( event.getCategory() );
            sb.append( "</category>" );
            sb.append( EOL );
        }

        if( m_printContext )
        {
            sb.append( "  <context-stack>" );
            sb.append( event.getContextStack() );
            sb.append( "</context-stack>" );
            sb.append( EOL );
        }

        if( m_printMessage && null != event.getMessage() )
        {
            sb.append( "  <message><![CDATA[" );
            sb.append( event.getMessage() );
            sb.append( "]]></message>" );
            sb.append( EOL );
        }

        if( m_printException && null != event.getThrowable() )
        {
            sb.append( "  <exception><![CDATA[" );
            //sb.append( event.getThrowable() );
            sb.append( "]]></exception>" );
            sb.append( EOL );
        }

        sb.append( "</log-entry>" );
        sb.append( EOL );

        return sb.toString();
    }
}
