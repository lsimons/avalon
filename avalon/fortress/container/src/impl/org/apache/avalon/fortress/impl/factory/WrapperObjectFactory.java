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

import org.apache.excalibur.mpool.ObjectFactory;
import org.apache.avalon.fortress.impl.factory.BCELWrapperGenerator;
import org.apache.avalon.fortress.impl.factory.WrapperClass;

/**
 * An object factory that delegates all calls to another object factory and
 * wraps the returned object into another object that exposes only the wrapped
 * object's work interface(s).
 *
 * @author <a href="mailto:olaf.bergner@gmx.de">Olaf Bergner</a>
 * @version CVS $ Revision: 1.1 $
 */
public final class WrapperObjectFactory extends AbstractObjectFactory
{

    /**
     * The {@link BCELWrapperGenerator} to use for creating the wrapper.
     */
    private final BCELWrapperGenerator m_wrapperGenerator;

    /**
     * Creates a {@link WrapperObjectFactory} with the specified
     * {@link org.apache.excalibur.mpool.ObjectFactory ObjectFactory} as the
     * object factory to delegate all calls for new instances to.
     *
     * @param  objectFactory The {@link org.apache.excalibur.mpool.ObjectFactory
     *                     ObjectFactory} to sue when creating new instances
     * @throws IllegalArgumentException If <code>objFactory</code> is
     * 									 <code>null</code>
     */
    public WrapperObjectFactory( final ObjectFactory objectFactory )
        throws IllegalArgumentException
    {
        super( objectFactory );
        m_wrapperGenerator = new BCELWrapperGenerator();
    }

    /**
     * @see org.apache.excalibur.mpool.ObjectFactory#newInstance()
     */
    public Object newInstance() throws Exception
    {
        final Object obj = m_delegateFactory.newInstance();

        final Class wrappedClass =
            m_wrapperGenerator.createWrapper( obj.getClass() );

        return wrappedClass.getConstructor(
            new Class[]{obj.getClass()} ).newInstance(
                new Object[]{obj} );
    }

    /**
     * @see org.apache.excalibur.mpool.ObjectFactory#dispose(java.lang.Object)
     */
    public void dispose( final Object object ) throws Exception
    {
        if ( object == null )
        {
            final String error = "Supplied argument is <null>";
            throw new IllegalArgumentException( error );
        }
        if ( !( object instanceof WrapperClass ) )
        {
            final String error =
                "Supplied argument is no instance of \""
                + WrapperClass.class.getName()
                + "\"";
            throw new IllegalArgumentException( error );
        }

        final Object target = ( (WrapperClass) object ).getWrappedObject();

        m_delegateFactory.dispose( target );
    }
}