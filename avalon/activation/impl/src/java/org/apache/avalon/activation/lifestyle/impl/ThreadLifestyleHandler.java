/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.activation.lifestyle.impl;

import org.apache.avalon.activation.lifecycle.LifecycleRuntimeException;
import org.apache.avalon.activation.lifecycle.Factory;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.activity.Disposable;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.4 $ $Date: 2003/10/19 06:12:58 $
 */
public class ThreadLifestyleHandler extends AbstractLifestyleHandler implements Disposable
{
    /**
     * Internal utility class to hold the thread local instance.
     */
    private static final class ThreadLocalHolder extends ThreadLocal
    {
        private final Factory m_factory;

        protected ThreadLocalHolder( Factory factory )
        {
            m_factory = factory;
        }

        //
        // TODO: the current implementation is hard coded to CONSERVATIVE
        // collection policy - we need to update the ThreadLocalHolder so 
        // it regenerates the value relative to DEMOCRAT or LIBERAL policies
        // (but I just need to check docs on thread local state access
        // semantics)
        //

        protected Object initialValue()
        {
            try
            {
                return m_factory.newInstance();
            }
            catch( Exception e )
            {
                final String error = 
                  "Unable to establish thread local variable.";
                throw new LifecycleRuntimeException( error );
            }
        }
    }

    private final Factory m_factory;

    private ThreadLocalHolder m_local;

    public ThreadLifestyleHandler( Logger logger, Factory factory )
    {
        super( logger, factory );
        m_factory = factory;
    }

    /**
     * Resolve a object to a value relative to a supplied set of 
     * interface classes.
     *
     * @return the resolved object
     * @throws Exception if an error occurs
     */
    public Object resolve() throws Exception
    {
        if( m_local == null )
        {
            m_local = new ThreadLocalHolder( m_factory );
        }
        return m_local.get();
    }

    /**
     * Release an object.  The abstract implementation does nothing,
     *
     * @param instance the object to be reclaimed
     */
    public void release( Object instance, boolean finalized )
    {
        if( finalized )
        {
            final String error =
              "Not possible because the thread local varliable is holding a hard reference.";
            throw new IllegalStateException( error );
        }
        else
        {
            // don't release because this is a sharable reference
        }
    }

   /**
    * Dispose of the component.
    */
    public void dispose()
    {
        if( m_local != null )
        {
            m_factory.destroy( m_local.get() );
        }
        m_local = null;
    }

}
