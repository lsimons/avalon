/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.avalon.framework.container;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
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

/**
 * Utility class that makes it easier to transfer
 * a component throught it's lifecycle stages.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.13 $ $Date: 2003/02/11 15:58:40 $
 */
public final class ContainerUtil
{
    /**
     * Private constructor to block instantiation.
     */
    private ContainerUtil()
    {
    }

    /**
     * Run specified object through shutdown lifecycle stages
     * (Stop and Dispose).
     *
     * @param object the object to shutdown
     * @throws Exception if there is a problem stoppping object
     */
    public static void shutdown( final Object object )
        throws Exception
    {
        stop( object );
        dispose( object );
    }

    /**
     * Supply specified object with Logger if it implements the
     * {@link LogEnabled} interface.
     *
     * @param object the object to Start
     * @param logger the logger to enable component with. May be null
     *        in which case the specified object must not implement LogEnabled.
     * @throws IllegalArgumentException if the object is LogEnabled but Logger is null
     */
    public static void enableLogging( final Object object,
                                      final Logger logger )
    {
        if( object instanceof LogEnabled )
        {
            if( null == logger )
            {
                final String message = "logger is null";
                throw new IllegalArgumentException( message );
            }
            ( (LogEnabled)object ).enableLogging( logger );
        }
    }

    /**
     * Supply specified object with a Context object if it implements the
     * {@link Contextualizable} interface.
     *
     * @param object the object to contextualize
     * @param context the context object to use for object.
     *        May be null in which case the specified object must not
     *        implement Contextualizable.
     * @throws ContextException if there is a problem contextualizing object
     * @throws IllegalArgumentException if the object is Contextualizable but
     *         context is null
     */
    public static void contextualize( final Object object,
                                      final Context context )
        throws ContextException
    {
        if( object instanceof Contextualizable )
        {
            if( null == context )
            {
                final String message = "context is null";
                throw new IllegalArgumentException( message );
            }
            ( (Contextualizable)object ).contextualize( context );
        }
    }

    /**
     * Supply specified object with ServiceManager if it implements the
     * {@link Serviceable} interface.
     *
     * @param object the object to service
     * @param serviceManager the serviceManager object to use for object.
     *        May be null in which case the specified object must not
     *        implement Serviceable.
     * @throws ServiceException if there is a problem servicing object
     * @throws IllegalArgumentException if the object is Servicable but
     *         ServiceManager is null
     */
    public static void service( final Object object,
                                final ServiceManager serviceManager )
        throws ServiceException
    {
        if( object instanceof Serviceable )
        {
            if( null == serviceManager )
            {
                final String message = "ServiceManager is null";
                throw new IllegalArgumentException( message );
            }
            ( (Serviceable)object ).service( serviceManager );
        }
    }

    /**
     * Supply specified object with ComponentManager if it implements the
     * {@link Composable} interface.
     *
     * @param object the object to compose
     * @param componentManager the ComponentManager object to use for object.
     *        May be null in which case the specified object must not
     *        implement Composable.
     * @throws ComponentException if there is a problem composing object
     * @deprecated compose() is no longer the preferred method via
     *             which components will be supplied with Components. Please
     *             Use service() from Composable instead.
     * @throws IllegalArgumentException if the object is Composable but
     *         ComponentManager is null
     */
    public static void compose( final Object object,
                                final ComponentManager componentManager )
        throws ComponentException
    {
        if( object instanceof Composable )
        {
            if( null == componentManager )
            {
                final String message = "componentManager is null";
                throw new IllegalArgumentException( message );
            }
            ( (Composable)object ).compose( componentManager );
        }
    }

    /**
     * Configure specified object if it implements the
     * {@link Configurable} interface.
     *
     * @param object the object to Start
     * @param configuration the configuration object to use during
     *        configuration. May be null in which case the specified object
     *        must not implement Configurable
     * @throws ConfigurationException if there is a problem Configuring object,
     *         or the object is Configurable but Configuration is null
     * @throws IllegalArgumentException if the object is Configurable but
     *         Configuration is null
     */
    public static void configure( final Object object,
                                  final Configuration configuration )
        throws ConfigurationException
    {
        if( object instanceof Configurable )
        {
            if( null == configuration )
            {
                final String message = "configuration is null";
                throw new IllegalArgumentException( message );
            }
            ( (Configurable)object ).configure( configuration );
        }
    }

    /**
     * Parameterize specified object if it implements the
     * {@link Parameterizable} interface.
     *
     * @param object the object to Parameterize.
     * @param parameters the parameters object to use during Parameterization.
     *        May be null in which case the specified object must not
     *        implement Parameterizable.
     * @throws ParameterException if there is a problem Parameterizing object
     * @throws IllegalArgumentException if the object is Parameterizable but
     *         parameters is null
     */
    public static void parameterize( final Object object,
                                     final Parameters parameters )
        throws ParameterException
    {
        if( object instanceof Parameterizable )
        {
            if( null == parameters )
            {
                final String message = "parameters is null";
                throw new IllegalArgumentException( message );
            }
            ( (Parameterizable)object ).parameterize( parameters );
        }
    }

    /**
     * Initialize specified object if it implements the
     * {@link Initializable} interface.
     *
     * @param object the object to Initialize
     * @throws Exception if there is a problem Initializing object
     */
    public static void initialize( final Object object )
        throws Exception
    {
        if( object instanceof Initializable )
        {
            ( (Initializable)object ).initialize();
        }
    }

    /**
     * Start specified object if it implements the
     * {@link Startable} interface.
     *
     * @param object the object to Start
     * @throws Exception if there is a problem Starting object
     */
    public static void start( final Object object )
        throws Exception
    {
        if( object instanceof Startable )
        {
            ( (Startable)object ).start();
        }
    }

    /**
     * Stop specified object if it implements the
     * {@link Startable} interface.
     *
     * @param object the object to stop
     * @throws Exception if there is a problem stoppping object
     */
    public static void stop( final Object object )
        throws Exception
    {
        if( object instanceof Startable )
        {
            ( (Startable)object ).stop();
        }
    }

    /**
     * Dispose specified object if it implements the
     * {@link Disposable} interface.
     *
     * @param object the object to dispose
     */
    public static void dispose( final Object object )
    {
        if( object instanceof Disposable )
        {
            ( (Disposable)object ).dispose();
        }
    }
}
