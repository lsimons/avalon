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

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * This logger manager is a wrapper around all other "real" logger managers.
 * The idea is to have one single configuration file where you can
 * define, which logger manager (Log4J, LogKit etc.) you want to use, so
 * you don't have to hard-code this.
 * 
 * FIXME: This is only a start, it's neither tested not run
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.2 $ $Date: 2003/03/22 11:29:12 $
 */

public final class DefaultLoggerManager 
    implements LoggerManager, 
                ThreadSafe, 
                LogEnabled, 
                Contextualizable, 
                Configurable, 
                Serviceable,
                Disposable
{
    /** The used LoggerManager */
    private LoggerManager m_loggermanager;

    /** The context object */
    private Context m_context;

    /** The logger used to log output from the logger manager. */
    private Logger m_logger;

    /** The prefix */
    private String m_prefix;
    
    /** The service manager */
    private ServiceManager m_manager;
    
    /** Do we have to dispose the manager */
    private boolean m_disposeManager = false;
    
    /**
     * Creates a new <code>DefaultLoggerManager</code>. .
     */
    public DefaultLoggerManager()
    {
    }

    /**
     * Creates a new <code>DefaultLoggerManager</code>. .
     */
    public DefaultLoggerManager(String prefix)
    {
        m_prefix = prefix;
    }

    /**
     * Provide a logger.
     *
     * @param logger the logger
     **/
    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    /**
     * Return the Logger for the specified category.
     */
    public final Logger getLoggerForCategory( final String categoryName )
    {
        return m_loggermanager.getLoggerForCategory( categoryName );
    }

    /**
     * Return the default Logger.  This is basically the same
     * as getting the Logger for the "" category.
     */
    public final Logger getDefaultLogger()
    {
        return m_loggermanager.getDefaultLogger();
    }

    /**
     * Reads a context object that will be supplied to the logger manager.
     *
     * @param context The context object.
     * @throws ContextException if the context is malformed
     */
    public final void contextualize( final Context context )
        throws ContextException
    {
        m_context = context;
    }

    /**
     * Reads a configuration object and creates the category mapping.
     *
     * @param configuration  The configuration object.
     * @throws ConfigurationException if the configuration is malformed
     */
    public final void configure( final Configuration configuration )
        throws ConfigurationException
    {
        // first we test for the class name to use
        final String className = configuration.getAttribute("manager-class", null);
        
        if ( null != className ) 
        {
            // is a prefix available?
            final String prefix = configuration.getAttribute("prefix", m_prefix);
        
            // create logger manager
            try {
                if ( null == prefix )
                {
                    m_loggermanager = (LoggerManager)Class.forName( className ).newInstance();
                }
                else
                {
                    m_loggermanager = (LoggerManager)Class.forName( className )
                                                          .getConstructor(new Class[] {String.class})
                                                          .newInstance(new Object[] {prefix});
                }
            } 
            catch (Exception e) 
            {
                throw new ConfigurationException("Unable to create new logger manager for class " + className, e);
            }
        
            // now test for some lifecycle interfaces
            if ( m_loggermanager instanceof LogEnabled ) 
            {
                ((LogEnabled)m_loggermanager).enableLogging( m_logger);
            }
        
            if ( m_loggermanager instanceof Contextualizable)
            {
                try 
                {
                    ((Contextualizable)m_loggermanager).contextualize( m_context );
                } 
                catch (ContextException ce)
                {
                    throw new ConfigurationException("Unable to contextualize new logger manager.", ce);
                }
            }
        
            if ( m_loggermanager instanceof Configurable )
            {
                ((Configurable)m_loggermanager).configure(configuration.getChildren()[0]);
            }
            else if ( m_loggermanager instanceof Parameterizable ) 
            {
                try 
                {
                    ((Parameterizable)m_loggermanager).parameterize(Parameters.fromConfiguration(configuration.getChildren()[0]));
                } 
                catch (ParameterException pe) 
                {
                    throw new ConfigurationException("Unable to parameterize new logger manager.", pe);
                }
            }
        } 
        else
        {
            // now test for role name
            final String roleName = configuration.getAttribute("manager-role", null);
            if ( null == roleName ) 
            {
                throw new ConfigurationException("The LoggerManager needs either a manager-role or a manager-class");
            }
            
            try {
                m_loggermanager = (LoggerManager)m_manager.lookup( roleName );
                m_disposeManager = true;
            } catch (ServiceException e) {
                throw new ConfigurationException("Unable to lookup logger manager with role " + roleName);
            }
        }
    }

    public void service(ServiceManager manager) 
        throws ServiceException 
    {
        m_manager = manager;
    }

    public void dispose() {
        if ( m_disposeManager && null != m_manager)
        {
            m_manager.release( m_loggermanager );
        }
        m_manager = null;
        m_loggermanager = null;
        m_disposeManager = false;
    }

}
