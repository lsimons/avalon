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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * The ThreadSafeComponentHandler to make sure components are initialized
 * and destroyed correctly.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.7 $ $Date: 2003/10/01 12:30:00 $
 * @since 4.0
 */
public final class PerThreadComponentHandler
    extends AbstractComponentHandler
{
    private ThreadLocalComponent m_instance;
    private List m_instances;

    public void initialize()
        throws Exception
    {
        super.initialize();
        m_instance = new ThreadLocalComponent( this );
        m_instances = Collections.synchronizedList(new LinkedList());
    }

    /**
     * Get a reference of the desired Component
     */
    protected Object doGet()
        throws Exception
    {
        final Object instance = m_instance.get();
        if ( null == instance )
        {
            throw new IllegalStateException( "Instance is unavailable" );
        }

        return instance;
    }

    protected void doDispose()
    {
        Iterator it = m_instances.iterator();
        while (it.hasNext())
        {
            disposeComponent( it.next() );
            it.remove();
        }
        m_instance = null;
        m_instances = null;
    }

    private static final class ThreadLocalComponent
        extends ThreadLocal
    {
        private final PerThreadComponentHandler m_handler;

        protected ThreadLocalComponent( final PerThreadComponentHandler handler )
        {
            m_handler = handler;
        }

        protected Object initialValue()
        {
            try
            {
                Object component = m_handler.newComponent();
                m_handler.m_instances.add(component);
                return component;
            }
            catch ( final Exception e )
            {
                return null;
            }
        }
    }
}
