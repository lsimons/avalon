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

import java.lang.reflect.Constructor;

/**
 * ProxyManager is used to abstract away the plumbing for the underlying
 * proxy generator.  Each proxy solution has to implement the ObjectFactory interface,
 * that way we can keep a soft dependency on things like BCEL.
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public final class ProxyManager
{
    private static final String BCEL_CLASS = "org.apache.bcel.classfile.JavaClass";
    private static final String BCEL_WRAPPER = "org.apache.avalon.fortress.impl.factory.WrapperObjectFactory";
    private static final String PROXY_WRAPPER = "org.apache.avalon.fortress.impl.factory.ProxyObjectFactory";
    private static final boolean m_isBCELPresent;
    private final boolean m_useBCELPreference;
    private Class m_factoryClass;

    static
    {
        boolean bcelPresent = false;
        try
        {
            Thread.currentThread().getContextClassLoader().loadClass( BCEL_CLASS );
            bcelPresent = true;
        }
        catch ( ClassNotFoundException cfne )
        {
            //ignore because we already have the proper value
        }

        m_isBCELPresent = bcelPresent;
    }

    public ProxyManager()
    {
        this(false);
    }

    public ProxyManager(final boolean useBCEL)
    {
        m_useBCELPreference = useBCEL;
    }

    public ObjectFactory getWrappedObjectFactory(final ObjectFactory source) throws Exception
    {
        if ( null == m_factoryClass )
        {
            final ClassLoader loader = source.getClass().getClassLoader();

            if (m_useBCELPreference && m_isBCELPresent)
            {
                m_factoryClass = loader.loadClass(BCEL_WRAPPER);
            }
            else
            {
                m_factoryClass = loader.loadClass(PROXY_WRAPPER);
            }
        }

        final Constructor constr = m_factoryClass.getConstructor(new Class[] {ObjectFactory.class});
        return (ObjectFactory) constr.newInstance(new Object[] {source});
    }
}
