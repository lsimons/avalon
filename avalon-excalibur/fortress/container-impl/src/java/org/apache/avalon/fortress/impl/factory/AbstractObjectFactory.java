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
package org.apache.avalon.fortress.impl.factory;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.activity.Suspendable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.Recomposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Reconfigurable;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.context.Recontextualizable;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Reparameterizable;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.mpool.ObjectFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * AbstractObjectFactory does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public abstract class AbstractObjectFactory implements ObjectFactory, Instrumentable
{
    /**
     * The list interfaces that will not be proxied.
     */
    private static final Class[] INVALID_INTERFACES = new Class[]
    {
        Loggable.class,
        LogEnabled.class,
        Contextualizable.class,
        Recontextualizable.class,
        Composable.class,
        Recomposable.class,
        Serviceable.class,
        Configurable.class,
        Reconfigurable.class,
        Parameterizable.class,
        Reparameterizable.class,
        Initializable.class,
        Startable.class,
        Suspendable.class,
        Disposable.class,
        Serializable.class
    };

    /**
     * The {@link ObjectFactory ObjectFactory} proper
     * we delegate all calls to.
     */
    protected final ObjectFactory m_delegateFactory;

    public AbstractObjectFactory( final ObjectFactory objectFactory )
    {
        if ( null == objectFactory )
        {
            throw new NullPointerException( "objectFactory" );
        }

        m_delegateFactory = objectFactory;
    }

    /**
     * @see ObjectFactory#newInstance()
     */
    public abstract Object newInstance() throws Exception;

    /**
     * @see ObjectFactory#getCreatedClass()
     */
    public final Class getCreatedClass()
    {
        return m_delegateFactory.getCreatedClass();
    }

    /**
     * @see ObjectFactory#dispose(Object)
     */
    public abstract void dispose( Object object ) throws Exception;

    /* (non-Javadoc)
     * @see org.apache.excalibur.instrument.Instrumentable#setInstrumentableName(java.lang.String)
     */
    public final void setInstrumentableName( final String name )
    {
        if ( m_delegateFactory instanceof Instrumentable )
        {
            ( (Instrumentable) m_delegateFactory ).setInstrumentableName( name );
        }
    }

    /* (non-Javadoc)
     * @see org.apache.excalibur.instrument.Instrumentable#getInstrumentableName()
     */
    public final String getInstrumentableName()
    {
        if ( m_delegateFactory instanceof Instrumentable )
        {
            return ( (Instrumentable) m_delegateFactory ).getInstrumentableName();
        }

        return "";
    }

    /* (non-Javadoc)
     * @see org.apache.excalibur.instrument.Instrumentable#getInstruments()
     */
    public final Instrument[] getInstruments()
    {
        if ( m_delegateFactory instanceof Instrumentable )
        {
            return ( (Instrumentable) m_delegateFactory ).getInstruments();
        }

        return new Instrument[]{};
    }

    /* (non-Javadoc)
     * @see org.apache.excalibur.instrument.Instrumentable#getChildInstrumentables()
     */
    public final Instrumentable[] getChildInstrumentables()
    {
        if ( m_delegateFactory instanceof Instrumentable )
        {
            return ( (Instrumentable) m_delegateFactory ).getChildInstrumentables();
        }

        return new Instrumentable[]{};
    }

    /**
     * Get a list of interfaces to proxy by scanning through
     * all interfaces a class implements and skipping invalid interfaces
     * (as defined in {@link #INVALID_INTERFACES}).
     *
     * @param clazz the class
     * @return the list of interfaces to proxy
     */
    protected static Class[] guessWorkInterfaces( final Class clazz )
    {
        final HashSet workInterfaces = new HashSet();
        
        // Get *all* interfaces
        guessWorkInterfaces( clazz, workInterfaces );

        // Make sure we have Component in there.
        workInterfaces.add( Component.class );
        
        // Remove the invalid ones.
        for ( int j = 0; j < INVALID_INTERFACES.length; j++ )
        {
            workInterfaces.remove(INVALID_INTERFACES[j]);
        }
        
        return (Class[]) workInterfaces.toArray( new Class[workInterfaces.size()] );
    }

    /**
     * Get a list of interfaces to proxy by scanning through
     * all interfaces a class implements.
     *
     * @param clazz           the class
     * @param workInterfaces  the set of current work interfaces
     */
    private static void guessWorkInterfaces( final Class clazz,
                                             final Set workInterfaces )
    {
        if ( null != clazz )
        {
            final Class[] interfaces = clazz.getInterfaces();

            for ( int i = 0; i < interfaces.length; i++ )
            {
                workInterfaces.add( interfaces[i] );
            }

            guessWorkInterfaces( clazz.getSuperclass(), workInterfaces );
        }
    }
}
