/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.excalibur.logger;

import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;

/**
 * Log4JLoggerManager implementation.  This is the interface used to get instances of
 * a Logger for your system.  This manager does not set up the categories--it
 * leaves that as an excercise for Log4J's construction.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @author <a href="http://cvs.apache.org/~atagunov">Anton Tagunov</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/10/02 19:18:43 $
 * @since 4.1
 */
public class Log4JLoggerManager extends AbstractLoggerManager
    implements LoggerManager, LogEnabled
{
    /** The hierarchy private to Log4JManager */
    private final LoggerRepository m_hierarchy;

    /**
     * Creates a new <code>DefaultLog4JManager</code>. It will use a new <code>Hierarchy</code>.
     */
    public Log4JLoggerManager()
    {
        this( LogManager.getLoggerRepository() );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> with an existing <code>Hierarchy</code>.
     */
    public Log4JLoggerManager( final LoggerRepository hierarchy )
    {
        this( (String) null, hierarchy, (String) null, (Logger) null, (Logger) null );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> using
     * specified logger name as root logger.
     */
    public Log4JLoggerManager( final String prefix )
    {
        this( prefix, (LoggerRepository) null, (String) null, (Logger) null, (Logger) null );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> with an existing <code>Hierarchy</code> using
     * specified logger name as root logger.
     */
    public Log4JLoggerManager( final String prefix,
                               final LoggerRepository hierarchy )
    {
        this( prefix, hierarchy, (String) null, (Logger) null, (Logger) null );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> using
     * specified logger name as root logger.
     */
    public Log4JLoggerManager( final String prefix, final String switchToCategory )
    {
        this( prefix, (LoggerRepository) null, switchToCategory, (Logger) null, (Logger) null );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> with an existing <code>Hierarchy</code> using
     * specified logger name as root logger.
     */
    public Log4JLoggerManager( final String prefix,
                               final LoggerRepository hierarchy,
                               final String switchToCategory )
    {
        this( prefix, hierarchy, switchToCategory, (Logger) null, (Logger) null );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> with an existing <code>Hierarchy</code> using
     * specified logger name as root logger.
     */
    public Log4JLoggerManager( final String prefix,
                               final LoggerRepository hierarchy,
                               final Logger defaultLogger )
    {
        this( prefix, hierarchy, (String) null, defaultLogger, defaultLogger );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code> with an existing <code>Hierarchy</code> using
     * specified logger name as root logger.
     */
    public Log4JLoggerManager( final String prefix,
                               final LoggerRepository hierarchy,
                               final Logger defaultLogger,
                               final Logger logger )
    {
        this( prefix, hierarchy, (String) null, defaultLogger, logger );
    }

    /**
     * Creates a new <code>DefaultLog4JManager</code>.
     * @param prefix to prepend to every category name on 
     *         <code>getLoggerForCategory()</code>
     * @param hierarchy a Log4J LoggerRepository to run with
     * @param switchToCategory if this parameter is not null
     *         after <code>start()</code>
     *         <code>LogKitLoggerManager</code> will start
     *         to log its own debug and error messages to
     *         a logger obtained via
     *         <code>this.getLoggerForCategory( switchToCategory )</code>.
     *         Note that prefix will be prepended to
     *         the value of <code>switchToCategory</code> also.
     * @param defaultLogger the logger to override the default
     *         logger configured by Log4J; probably should be
     *         null to allow users set up whatever logger they
     *         like as the root logger via Log4J configuration
     * @param logger the logger to log our own initialization
     *         messages (currently we have none) and to log
     *         errors (currently this functionality is not used
     *         either)
     */
    public Log4JLoggerManager( final String prefix,
                               final LoggerRepository hierarchy,
                               final String switchToCategory,
                               final Logger defaultLogger,
                               final Logger logger )
    {
        super( prefix, switchToCategory, defaultLogger );

        if ( hierarchy == null )
        {
            // is this an analog of new Hierarchy() or an
            // analog of Hierarchy.getDefaultHierarchy()?
            // we should have an analog of new Hierarchy() here
            // I guess - Anton Tagunov
            m_hierarchy = LogManager.getLoggerRepository();
        }
        else
        {
            m_hierarchy = hierarchy;
        }

        if ( logger != null )
        {
            this.enableLogging( logger );
        }
    }

    /* Actaully create the Logger */
    protected Logger doGetLoggerForCategory( final String fullCategoryName )
    {
        return new Log4JLogger( m_hierarchy.getLogger( fullCategoryName ) );
    }
}
