/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * Default LogTargetFactoryManager implementation.  It populates the LogTargetFactoryManager
 * from a configuration file.
 *
 * @author <a href="mailto:giacomo@apache.org">Giacomo Pati</a>
 * @version CVS $Revision: 1.4 $ $Date: 2002/11/26 07:44:45 $
 * @since 4.0
 */
public class DefaultLogTargetFactoryManager
    extends AbstractLogEnabled
    implements LogTargetFactoryManager, Contextualizable, Configurable
{
    /** Map for name to logger mapping */
    private final Map m_factories = new HashMap();

    /** The context object */
    private Context m_context;

    /**
     * The classloader to use to load target factorys.
     */
    private ClassLoader m_classLoader;

    /**
     * Retrieves a LogTargetFactory from a name. Usually
     * the factory name refers to a element name. If
     * this LogTargetFactoryManager does not have the match a null
     * will be returned.
     *
     * @param factoryName The name of a configured LogTargetFactory.
     * @return the LogTargetFactory or null if none is found.
     */
    public final LogTargetFactory getLogTargetFactory( final String factoryName )
    {
        return (LogTargetFactory)m_factories.get( factoryName );
    }

    /**
     * Reads a context object.
     *
     * @param context The context object.
     * @throws ContextException if the context is malformed
     */
    public final void contextualize( final Context context )
        throws ContextException
    {
        m_context = context;
        try
        {
            m_classLoader = (ClassLoader)m_context.get( "classloader" );
        }
        catch( ContextException ce )
        {
        }
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
        final Configuration[] confs = configuration.getChildren( "factory" );
        for( int i = 0; i < confs.length; i++ )
        {
            final String factoryClass = confs[ i ].getAttribute( "class" );
            final String factoryType = confs[ i ].getAttribute( "type" );

            //FIXME(GP): Is this the right way to load a class or
            //           should the ContextClassLoader be used?
            final LogTargetFactory logTargetFactory;
            try
            {
                Class clazz = null;

                //First lets try the supplied ClassLoader
                if( null != m_classLoader )
                {
                    try
                    {
                        clazz = m_classLoader.loadClass( factoryClass );
                    }
                    catch( final ClassNotFoundException cnfe )
                    {
                    }
                }

                //Next lets try the context ClassLoader
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if( null == clazz && null != classLoader )
                {
                    try
                    {
                        clazz = classLoader.loadClass( factoryClass );
                    }
                    catch( final ClassNotFoundException cnfe )
                    {
                    }
                }

                //Okay now lets try classLoader this class was loaded from
                if( null == clazz )
                {
                    clazz = getClass().getClassLoader().loadClass( factoryClass );
                }

                logTargetFactory = (LogTargetFactory)clazz.newInstance();
            }
            catch( final ClassNotFoundException cnfe )
            {
                final String message =
                    "cannot find LogTargetFactory class " + factoryClass;
                throw new ConfigurationException( message, cnfe );
            }
            catch( final InstantiationException ie )
            {
                final String message =
                    "cannot instantiate LogTargetFactory class " + factoryClass;
                throw new ConfigurationException( message, ie );
            }
            catch( final IllegalAccessException iae )
            {
                final String message =
                    "cannot access LogTargetFactory class " + factoryClass;
                throw new ConfigurationException( message, iae );
            }

            ContainerUtil.enableLogging( logTargetFactory, getLogger() );
            try
            {
                ContainerUtil.contextualize( logTargetFactory, m_context );
            }
            catch( final ContextException ce )
            {
                final String message =
                    "cannot contextualize LogTargetFactory " + factoryClass;
                throw new ConfigurationException( message, ce );
            }
            ContainerUtil.configure( logTargetFactory, confs[ i ] );

            if( logTargetFactory instanceof LogTargetFactoryManageable )
            {
                ( (LogTargetFactoryManageable)logTargetFactory ).setLogTargetFactoryManager( this );
            }

            if( getLogger().isDebugEnabled() )
            {
                final String message =
                    "added new LogTargetFactory of type " + factoryType;
                getLogger().debug( message );
            }
            m_factories.put( factoryType, logTargetFactory );
        }
    }
}
