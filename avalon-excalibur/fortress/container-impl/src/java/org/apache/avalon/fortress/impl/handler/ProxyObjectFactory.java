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
package org.apache.avalon.fortress.impl.handler;

import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.mpool.ObjectFactory;

/**
 * An ObjectFactory that delegates to another ObjectFactory
 * and proxies results of that factory.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.8 $ $Date: 2003/04/18 20:02:29 $
 */
public final class ProxyObjectFactory
    implements ObjectFactory, Instrumentable
{
    /**
     * The underlying object factory that this factory proxies.
     */
    private final ObjectFactory m_objectFactory;

    /**
     * Create factory that delegates to specified factory.
     *
     * @param objectFactory the factory to delegate to
     * @exception NullPointerException if the supplied object factory is null
     */
    public ProxyObjectFactory( final ObjectFactory objectFactory ) throws NullPointerException
    {
        if ( null == objectFactory )
        {
            throw new NullPointerException( "objectFactory" );
        }

        m_objectFactory = objectFactory;
    }

    /**
     * Create a new instance from delegated factory and proxy it.
     *
     * @return the proxied object
     * @throws Exception if unable to create new instance
     */
    public Object newInstance()
        throws Exception
    {
        final Object object = m_objectFactory.newInstance();
        return ProxyHelper.createProxy( object );
    }

    /**
     * Return the class created by factory.
     *
     * @return the class created by factory.
     */
    public Class getCreatedClass()
    {
        return m_objectFactory.getCreatedClass();
    }

    /**
     * Dispose of objects created by this factory.
     * Involves deproxying object and delegating to real ObjectFactory.
     *
     * @param object the proxied object
     * @throws Exception if unable to dispose of object
     */
    public void dispose( final Object object )
        throws Exception
    {
        final Object target = ProxyHelper.getObject( object );
        m_objectFactory.dispose( target );
    }

    /* (non-Javadoc)
     * @see org.apache.excalibur.instrument.Instrumentable#setInstrumentableName(java.lang.String)
     */
    public void setInstrumentableName( final String name )
    {
        if ( m_objectFactory instanceof Instrumentable )
        {
            ( (Instrumentable) m_objectFactory ).setInstrumentableName( name );
        }
    }

    /* (non-Javadoc)
     * @see org.apache.excalibur.instrument.Instrumentable#getInstrumentableName()
     */
    public String getInstrumentableName()
    {
        if ( m_objectFactory instanceof Instrumentable )
        {
            return ( (Instrumentable) m_objectFactory ).getInstrumentableName();
        }

        return "";
    }

    /* (non-Javadoc)
     * @see org.apache.excalibur.instrument.Instrumentable#getInstruments()
     */
    public Instrument[] getInstruments()
    {
        if ( m_objectFactory instanceof Instrumentable )
        {
            return ( (Instrumentable) m_objectFactory ).getInstruments();
        }

        return new Instrument[]{};
    }

    /* (non-Javadoc)
     * @see org.apache.excalibur.instrument.Instrumentable#getChildInstrumentables()
     */
    public Instrumentable[] getChildInstrumentables()
    {
        if ( m_objectFactory instanceof Instrumentable )
        {
            return ( (Instrumentable) m_objectFactory ).getChildInstrumentables();
        }

        return new Instrumentable[]{};
    }
}
