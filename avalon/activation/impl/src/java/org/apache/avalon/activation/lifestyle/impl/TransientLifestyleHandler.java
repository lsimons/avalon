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

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.ArrayList;

import org.apache.avalon.activation.lifecycle.Factory;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.meta.info.InfoDescriptor;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.7 $ $Date: 2004/01/13 11:41:23 $
 */
public class TransientLifestyleHandler extends AbstractLifestyleHandler implements Disposable
{
    private ArrayList m_list = new ArrayList();

    public TransientLifestyleHandler( Logger logger, Factory factory )
    {
        super( logger, factory );
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
        // TODO: setup a background thread to check queues for 
        // released references and remove them from our list, otherwise we
        // have a memory leak due to accumulation of weak references

        Object instance = newInstance();
        Reference reference = getReference( instance );
        m_list.add( reference );
        return instance;
    }

    /**
     * Release an object
     *
     * @param instance the object to be released
     * @param finalized if TRUE the lifestyle handler cannot reuse the instance
     */
    public void release( Object instance, boolean finalized )
    {
        disposeInstance( instance );
    }

   /**
    * Dispose of the component.
    */
    public synchronized void dispose()
    {
        Reference[] refs = (Reference[]) m_list.toArray( new Reference[0] );
        for( int i=0; i<refs.length; i++ )
        {
            Reference ref = refs[i];
            disposeInstance( refs[i].get() );
        }
        m_list.clear();
    }

   /**
    * Overriding getReference to ensure that we never return a hard 
    * reference for a transient.
    */
    Reference getReference( Object instance )
    {
        final int policy = getFactory().getComponentModel().getCollectionPolicy();
        if( policy == InfoDescriptor.DEMOCRAT )
        {
             return new SoftReference( instance );
        }
        else
        {
             return new WeakReference( instance, getLiberalQueue() );
        }
    }
}
