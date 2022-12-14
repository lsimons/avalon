/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.components.manager;

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.i18n.ResourceManager;
import org.apache.avalon.excalibur.i18n.Resources;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.interfaces.ManagerException;
import org.apache.avalon.phoenix.interfaces.SystemManager;

/**
 * This is abstract implementation of SystemManager.
 *
 * @author Peter Donald
 */
public abstract class AbstractSystemManager
    extends AbstractLogEnabled
    implements SystemManager, Initializable, Disposable
{
    private static final Resources REZ =
        ResourceManager.getPackageResources( AbstractSystemManager.class );

    private final Map m_entries = new HashMap();

    private SubContext m_subContext;

    public void initialize()
        throws Exception
    {
        m_subContext = new SubContext( this, null, null );
    }

    public void dispose()
    {
        m_subContext = null;
    }

    /**
     * @see SystemManager#register(String, Object, Class[])
     */
    public synchronized void register( final String name,
                                       final Object object,
                                       final Class[] interfaces )
        throws ManagerException, IllegalArgumentException
    {
        if( null == interfaces )
        {
            final String message =
                REZ.getString( "manager.error.interfaces.null", name );
            throw new IllegalArgumentException( message );
        }
        verifyInterfaces( object, interfaces );

        doRegister( name, object, interfaces );
    }

    /**
     * @see SystemManager#register(String, Object)
     */
    public synchronized void register( final String name,
                                       final Object object )
        throws ManagerException, IllegalArgumentException
    {
        doRegister( name, object, null );
    }

    /**
     * @see SystemManager#unregister(String)
     */
    public synchronized void unregister( final String name )
        throws ManagerException
    {
        final ManagedEntry entry = (ManagedEntry)m_entries.remove( name );
        if( null == entry )
        {
            final String message =
                REZ.getString( "manager.error.unregister.noentry", name );
            throw new ManagerException( message );
        }

        unexport( name, entry.getExportedObject() );
    }

    /**
     * Returns the subcontext of the specified name.  If it does not exist it
     * is created.
     *
     * @throws ManagerException if context cannot be created or retrieved
     * @return  the subcontext with the specified name
     */
    public SystemManager getSubContext( final String parent, final String type )
        throws ManagerException
    {
        return m_subContext.getSubContext( parent, type );
    }

    /**
     * Export the object to the particular management medium using
     * the supplied object and interfaces.
     * This needs to be implemented by subclasses.
     *
     * @param name the name of object
     * @param object the object
     * @param interfaces the interfaces
     * @return the exported object
     * @throws ManagerException if an error occurs
     */
    protected abstract Object export( String name, Object object, Class[] interfaces )
        throws ManagerException;

    /**
     * Stop the exported object from being managed.
     *
     * @param name the name of object
     * @param exportedObject the object return by export
     * @throws ManagerException if an error occurs
     */
    protected abstract void unexport( String name, Object exportedObject )
        throws ManagerException;

    /**
     * Verfify that name is well formed.
     *
     * @param name the name
     * @param object the object so named
     */
    protected void verifyName( final String name,
                               final Object object )
        throws ManagerException
    {
    }

    /**
     * Verify that an interface conforms to the requirements of management medium.
     *
     * @param clazz the interface class
     * @throws ManagerException if verification fails
     */
    protected abstract void verifyInterface( Class clazz )
        throws ManagerException;

    /**
     * Verify that object implements interfaces and interfaces are of "acceptable form".
     * "Acceptable form" is determined by specific management policy.
     *
     * @param object the object
     * @param interfaces the array of interfaces to check
     * @throws ManagerException if an error occurs
     */
    private void verifyInterfaces( final Object object, final Class[] interfaces )
        throws ManagerException
    {
        for( int i = 0; i < interfaces.length; i++ )
        {
            final Class clazz = interfaces[ i ];

            if( !clazz.isInterface() )
            {
                final String message =
                    REZ.getString( "manager.error.verify.notinterface", clazz.getName() );
                throw new ManagerException( message );
            }

            if( !clazz.isInstance( object ) )
            {
                final String message =
                    REZ.getString( "manager.error.verify.notinstance", clazz.getName() );
                throw new ManagerException( message );
            }

            verifyInterface( clazz );
        }
    }

    /**
     * Helper method to help check before an objects registration.
     * Verifies name and object are not null and verifies no entry exists using name.
     *
     * @param name the name of object
     * @param object the object to be registered
     * @throws ManagerException if name already exists
     * @throws IllegalArgumentException if name or object is null
     */
    private void checkRegister( final String name, final Object object )
        throws ManagerException, IllegalArgumentException
    {
        if( null == object )
        {
            throw new NullPointerException( "object" );
        }

        if( null == name )
        {
            throw new NullPointerException( "name" );
        }

        verifyName( name, object );

        if( null != m_entries.get( name ) )
        {
            final String message = REZ.getString( "manager.error.register.exists", name );
            throw new ManagerException( message );
        }
    }

    /**
     * Utility method that actually does the registration.
     *
     * @param name the name to register under
     * @param object the object
     * @param interfaces the interfaces (may be null)
     * @throws ManagerException if error occurs
     */
    private void doRegister( final String name,
                             final Object object,
                             final Class[] interfaces )
        throws ManagerException
    {
        checkRegister( name, object );

        final Object exportedObject = export( name, object, interfaces );
        final ManagedEntry entry =
            new ManagedEntry( object, interfaces, exportedObject );
        m_entries.put( name, entry );
    }
}
