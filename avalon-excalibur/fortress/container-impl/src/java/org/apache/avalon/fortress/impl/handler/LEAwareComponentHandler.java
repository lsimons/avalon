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

import org.apache.avalon.fortress.util.LifecycleExtensionManager;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;

/**
 * A ComponentHandler that delegates to underlying handler but also
 * calls relevent Lifecycle Extension handlers at the right time.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.11 $ $Date: 2003/03/22 12:46:33 $
 */
public class LEAwareComponentHandler
    implements ComponentHandler, Disposable
{
    private final ComponentHandler m_componentHandler;
    private final LifecycleExtensionManager m_extManager;
    private final Context m_context;

    /**
     * Creation of a new handler.
     * @param componentHandler the handler
     * @param extManager the extension manager
     * @param context the context
     */
    public LEAwareComponentHandler( final ComponentHandler componentHandler,
                                    final LifecycleExtensionManager extManager,
                                    final Context context )
    {
        if( null == componentHandler )
        {
            throw new NullPointerException( "componentHandler" );
        }
        if( null == extManager )
        {
            throw new NullPointerException( "extManager" );
        }
        if( null == context )
        {
            throw new NullPointerException( "context" );
        }

        m_componentHandler = componentHandler;
        m_extManager = extManager;
        m_context = context;
    }

    /**
     * Return the component's class that this handler is trying to create.
     * Used for deubug information.
     *
     * @return the <code>Class</code> object for the component
     */
    public Class getComponentClass()
    {
        return m_componentHandler.getComponentClass();
    }

    /**
     * Prepare the handler.
     * @exception Exception if a handler preparation error occurs
     */
    public void prepareHandler()
        throws Exception
    {
        m_componentHandler.prepareHandler();
    }

    /**
     * Retrieve the object and execute access extensions.
     *
     * @return the object
     * @throws Exception if unable to aquire object
     */
    public Object get() throws Exception
    {
        final Object object = m_componentHandler.get();
        m_extManager.executeAccessExtensions( object, m_context );
        return object;
    }

    /**
     * Return component and execute Release extensions.
     *
     * @param component the component
     */
    public void put( final Object component )
    {
        try
        {
            m_extManager.executeReleaseExtensions( component, m_context );
        }
        catch( Exception e )
        {
            // REVISIT(MC): we need to log this somewhere
        }
        m_componentHandler.put( component );
    }

    /**
     * Disposal of the handler.
     */
    public void dispose()
    {
        ContainerUtil.dispose( m_componentHandler );
    }
}
