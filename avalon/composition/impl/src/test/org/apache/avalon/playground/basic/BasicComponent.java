/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
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

package org.apache.avalon.playground.basic;

import java.io.File;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.playground.NullService;

/**
 * This is a minimal demonstration component that implements the
 * <code>BasicService</code> interface and has no dependencies.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class BasicComponent extends AbstractLogEnabled
        implements Contextualizable, Configurable, Initializable, Startable, Disposable, BasicService, NullService
{

    private String m_location;
    private String m_message;
    private File m_home;
    private boolean m_started = false;

    //=======================================================================
    // Contextualizable
    //=======================================================================

    /**
     * Supply of the the component context to the component type.
     * @param context the context value
     */
    public void contextualize( Context context )
    {
        BasicContext c = (BasicContext) context;
        m_location = c.getLocation();
        m_home = c.getWorkingDirectory();
    }

    //=======================================================================
    // Configurable
    //=======================================================================

    /**
     * Supply of the the component configuration to the type.
     * @param config the configuration value
     */
    public void configure( Configuration config )
    {
        getLogger().info( "configure" );
        m_message = config.getChild( "message" ).getValue( null );
    }

    //=======================================================================
    // Initializable
    //=======================================================================

    /**
     * Initialization of the component type by its container.
     */
    public void initialize()
    {
        getLogger().info( "initialize" );
        getLogger().debug( "location: " + m_location );
        getLogger().debug( "home: " + m_home );
        getLogger().debug( "message: " + m_message );
    }

    //=======================================================================
    // Startable
    //=======================================================================

    /**
     * Start the component.
     */
    public void start()
    {
        if( !m_started )
        {
            getLogger().info( "starting" );
            doPrimeObjective();
            m_started = true;
        }
    }

    /**
     * Stop the component.
     */
    public void stop()
    {
        getLogger().info( "stopping" );
    }

    /**
     * Dispose of the component.
     */
    public void dispose()
    {
        getLogger().info( "dispose" );
    }

    //=======================================================================
    // BasicService
    //=======================================================================

    /**
     * Service interface implementation.
     */
    public void doPrimeObjective()
    {
        getLogger().info( m_message + " from '" + m_location + "'." );
    }

}
