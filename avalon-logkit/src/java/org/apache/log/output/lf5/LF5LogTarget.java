/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.output.lf5;

import org.apache.log.*;
import org.apache.log.format.Formatter;
import org.apache.log.format.PatternFormatter;

import org.apache.log4j.lf5.viewer.LogBrokerMonitor;

/**
 * A {@link LogTarget} that displays log events using the
 * <a href="http://jakarta.apache.org/log4j/docs/lf5/overview.html">LogFactor5</a>
 * Swing GUI.
 *
 * @author <a href="sylvain@apache.org">Sylvain Wallez</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/01/23 09:08:45 $
 */
public class LF5LogTarget implements LogTarget
{
    /** Common monitor */
    static private LogBrokerMonitor c_defaultLogMonitor;
    
    /** Default context map formatter */
    static private Formatter        c_defaultContextFormatter = new PatternFormatter("");
    
    /** Monitor for this LogTarget */
    private LogBrokerMonitor        m_monitor;
    
    /** Format for context maps */
    private Formatter               m_contextFormatter = c_defaultContextFormatter;
    
    /**
     * Create a <code>LogFactorLogTarget</code> on a given <code>LogBrokerMonitor</code>.
     */
    public LF5LogTarget( final LogBrokerMonitor monitor )
    {
        m_monitor = monitor;
    }
    
    /**
     * Create <code>LogFactorLogTarget</code> on the default <code>LogBrokerMonitor</code>.
     */
    public LF5LogTarget()
    {
        // Creation of m_monitor is deferred up to the first call to processEvent().
        // This allows the Swing window to pop up only if this target is actually used.
    }
    
    /**
     * Sets the {@link Formatter} that will be used to produce the "NDC" (nested diagnostic
     * context) text on the GUI.
     */
    public void setNDCFormatter( Formatter formatter )
    {
    	m_contextFormatter = formatter;
    }
    
    /**
     * Get the default <code>LogBrokerMonitor</code> instance.
     */
    public synchronized static LogBrokerMonitor getDefaultMonitor()
    {
        if( null == c_defaultLogMonitor )
        {
            c_defaultLogMonitor = new LogBrokerMonitor( LogKitLogRecord.LOGKIT_LOGLEVELS );
            c_defaultLogMonitor.setFontSize( 12 );
            c_defaultLogMonitor.show();
        }
        
        return c_defaultLogMonitor;
    }        
    
    /**
     * Process a log event.
     */
    public void processEvent( final LogEvent event )
    {
        if ( null == m_monitor )
        {
            m_monitor = getDefaultMonitor();
        }

        m_monitor.addMessage( new LogKitLogRecord( event, m_contextFormatter ) );
    }
}