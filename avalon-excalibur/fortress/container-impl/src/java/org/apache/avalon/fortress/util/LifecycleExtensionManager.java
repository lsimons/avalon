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
package org.apache.avalon.fortress.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.lifecycle.Accessor;
import org.apache.avalon.lifecycle.Creator;

/**
 * <code>LifecycleExtensionManager</code> class. This class manages a list
 * of extensions objects that are executed on components during the various
 * stages of their lifecycles.
 *
 * <p>
 * It provides methods for adding extension objects to the system,
 * and a method for executing them on a particular component object. The
 * current context is also passed in to the extension objects to facilitate
 * the communication of any global values.
 * </p>
 *
 * <p>
 * Extensions are stored internally in a list. This guarentees that the
 * order in which they are executed matches the order in which they are
 * inserted.
 * </p>
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version CVS $Revision: 1.6 $ $Date: 2003/03/22 12:46:34 $
 */
public class LifecycleExtensionManager
    extends AbstractLogEnabled
{
    public static final String ROLE = LifecycleExtensionManager.class.getName();

    // extensions objects
    private final CachedArrayList m_accessorExtensions = new CachedArrayList();
    private final CachedArrayList m_creatorExtensions = new CachedArrayList();

    /**
     * <code>executeAccessExtensions</code> method, executes all access
     * level extensions on the given component.
     *
     * @param component a <code>Component</code> instance
     * @param context a <code>Context</code> instance
     * @exception Exception if an error occurs
     */
    public void executeAccessExtensions( Object component, Context context )
        throws Exception
    {
        executeExtensions( m_accessorExtensions.toArray(), component, context, ACCESS );
    }

    /**
     * <code>executeReleaseExtensions</code> method, executes all release
     * level extensions on the given component.
     *
     * @param component a <code>Component</code> instance
     * @param context a <code>Context</code> instance
     * @exception Exception if an error occurs
     */
    public void executeReleaseExtensions( Object component, Context context )
        throws Exception
    {
        executeExtensions( m_accessorExtensions.toArray(), component, context, RELEASE );
    }

    /**
     * <code>executeCreationExtensions</code> method, executes all creation
     * level extensions on the given component.
     *
     * @param component a <code>Component</code> instance
     * @param context a <code>Context</code> instance
     * @exception Exception if an error occurs
     */
    public void executeCreationExtensions( Object component, Context context )
        throws Exception
    {
        executeExtensions( m_creatorExtensions.toArray(), component, context, CREATE );
    }

    /**
     * <code>executeDestructionExtensions</code> method, executes all
     * destruction level extensions on the given component.
     *
     * @param component a <code>Component</code> instance
     * @param context a <code>Context</code> instance
     * @exception Exception if an error occurs
     */
    public void executeDestructionExtensions( Object component, Context context )
        throws Exception
    {
        executeExtensions( m_creatorExtensions.toArray(), component, context, DESTROY );
    }

    // The following methods define operations that modify the internal list
    // of extensions. I've refrained from returning the List directly, via a
    // getExtensions() method for the following reasons:
    //
    // 1. Returning List breaks encapsulation, implicitly exposing all of List's
    //    current and future operations to the client
    // 2. List operates with type Object, not LifecycleExtension which means we need
    //    more error handling code if we make it possible for the user to add instances
    //    of any type to the extension lists.
    // 3. Wrapping add/remove methods allow us to add optimizations to improve performance
    //    (eg. to convert the List to an array upon each add/remove, and not upon each
    //    execute operation)
    // 4. The book 'Refactoring' says we shouldn't do it :-)
    //
    // I'm open to suggestions though if there's any better ideas ?

    /**
     * Adds an accessor extension to the manager
     *
     * @param extension a <code>Accessor</code> instance
     */
    public void addAccessorExtension( Accessor extension )
    {
        m_accessorExtensions.add( extension );
    }

    /**
     * Adds a creator extension to the manager
     *
     * @param extension a <code>Creator</code> instance
     */
    public void addCreatorExtension( Creator extension )
    {
        m_creatorExtensions.add( extension );
    }

    /**
     * Inserts an accessor extension at a given index in the manager
     *
     * @param position an <code>int</code> index value
     * @param extension a <code>Accessor</code> instance
     */
    public void insertAccessorExtension( int position, Accessor extension )
    {
        m_accessorExtensions.insert( position, extension );
    }

    /**
     * Inserts a creator extension at a given index in the manager
     *
     * @param position an <code>int</code> index value
     * @param extension a <code>Creator</code> instance
     */
    public void insertCreatorExtension( int position, Creator extension )
    {
        m_accessorExtensions.insert( position, extension );
    }

    /**
     * Removes a particular accessor extension from the manager
     *
     * @param position an <code>int</code> index value
     * @return a <code>Accessor</code> instance
     */
    public Accessor removeAccessorExtension( int position )
    {
        return (Accessor)m_accessorExtensions.remove( position );
    }

    /**
     * Removes a particular creator extension from the manager
     *
     * @param position an <code>int</code> index value
     * @return a <code>Creator</code> instance
     */
    public Creator removeCreatorExtension( int position )
    {
        return (Creator)m_creatorExtensions.remove( position );
    }

    /**
     * Obtain an iterator.
     *
     * @return an <code>Iterator</code> instance
     */
    public Iterator accessorExtensionsIterator()
    {
        return m_accessorExtensions.iterator();
    }

    /**
     * Obtain an iterator.
     *
     * @return an <code>Iterator</code> instance
     */
    public Iterator creatorExtensionsIterator()
    {
        return m_creatorExtensions.iterator();
    }

    /**
     * Find out the total number of accessor extensions registered with this manager
     *
     * @return an <code>int</code> value
     */
    public int accessorExtensionsCount()
    {
        return m_accessorExtensions.size();
    }

    /**
     * Find out the total number of creator extensions registered with this manager
     *
     * @return an <code>int</code> value
     */
    public int creatorExtensionsCount()
    {
        return m_creatorExtensions.size();
    }

    /**
     * Obtain the particular accessor extension at the given index
     *
     * @param index an <code>int</code> index value
     * @return a <code>Accessor</code> instance
     */
    public Accessor getAccessorExtension( int index )
    {
        return (Accessor)m_accessorExtensions.get( index );
    }

    /**
     * Obtain the particular creator extension at the given index
     *
     * @param index an <code>int</code> index value
     * @return a <code>Creator</code> instance
     */
    public Creator getCreatorExtension( int index )
    {
        return (Creator)m_creatorExtensions.get( index );
    }

    /**
     * Clears all accessor extensions registered with this manager
     */
    public void clearAccessorExtensions()
    {
        m_accessorExtensions.clear();
    }

    /**
     * Clears all creator extensions registered with this manager
     */
    public void clearCreatorExtensions()
    {
        m_creatorExtensions.clear();
    }

    // Lifecycle method constants, these are passed to executeExtensions()
    protected static final int ACCESS = 0;
    protected static final int RELEASE = 1;
    protected static final int CREATE = 2;
    protected static final int DESTROY = 3;

    /**
     * <code>executeExtensions</code> method, executes a given array of
     * lifecycle interfaces on a given component.
     *
     * @param component a <code>Component</code> instance
     * @param context   a <code>Context</code> instance
     * @param type      a constant, referencing which phase the
     *                  extensions array adheres to
     *
     * @exception Exception if an error occurs
     */
    protected void executeExtensions( final Object[] extensions,
                                      final Object component,
                                      final Context context,
                                      final int type )
        throws Exception
    {
        switch( type )
        {
            case ACCESS:
                for( int i = 0; i < extensions.length; ++i )
                {
                    ( (Accessor)extensions[ i ] ).access( component, context );
                }
                break;

            case RELEASE:
                for( int i = 0; i < extensions.length; ++i )
                {
                    ( (Accessor)extensions[ i ] ).release( component, context );
                }
                break;

            case CREATE:
                for( int i = 0; i < extensions.length; ++i )
                {
                    ( (Creator)extensions[ i ] ).create( component, context );
                }
                break;

            case DESTROY:
                for( int i = 0; i < extensions.length; ++i )
                {
                    ( (Creator)extensions[ i ] ).destroy( component, context );
                }
                break;

            default:
                if( getLogger().isErrorEnabled() )
                {
                    final String message =
                        "Incorrect extension phase specified: " + type;
                    getLogger().error( message );
                }
        }
    }

    /**
     * <code>CachedArrayList</code> class.
     *
     * <p>
     * This class wraps a synchronized ArrayList to provide an optimized
     * <code>toArray()</code> method that returns an internally cached array,
     * rather than a new array generated per <code>toArray()</code>
     * invocation.
     * </p>
     *
     * <p>
     * Use of the class by the Manager results in <code>toArray()</code>
     * being invoked far more often than any other method. Caching the value
     * <code>toArray</code> normally returns is intended to be a performance
     * optimization.
     * </p>
     *
     * <p>
     * The cached array value is updated upon each write operation to the
     * List.
     * </p>
     *
     * <p>
     * REVISIT(MC): investigate using FastArrayList from collections ?
     * </p>
     */
    private final class CachedArrayList
    {
        // Empty array constant
        private final Object[] EMPTY_ARRAY = new Object[ 0 ];

        // Actual list for storing elements
        private final List m_proxy = Collections.synchronizedList( new ArrayList() );

        // Proxy cache, saves unnecessary conversions from List to Array
        private Object[] m_cache = EMPTY_ARRAY;

        /**
         * Add an object to the list
         *
         * @param object an <code>Object</code> value
         */
        public void add( final Object object )
        {
            m_proxy.add( object );
            m_cache = m_proxy.toArray();
        }

        /**
         * Insert an object into a particular position in the list
         *
         * @param position an <code>int</code> value
         * @param object an <code>Object</code> value
         */
        public void insert( final int position, final Object object )
        {
            m_proxy.add( position, object );
            m_cache = m_proxy.toArray();
        }

        /**
         * Remove an object from the list
         *
         * @param position an <code>int</code> value
         * @return a <code>Object</code> value
         */
        public Object remove( final int position )
        {
            final Object object = m_proxy.remove( position );
            m_cache = m_proxy.toArray();
            return object;
        }

        /**
         * Obtain an iterator
         *
         * @return an <code>Iterator</code> value
         */
        public Iterator iterator()
        {
            return m_proxy.iterator();
        }

        /**
         * Obtain the size of the list
         *
         * @return an <code>int</code> value
         */
        public int size()
        {
            return m_proxy.size();
        }

        /**
         * Access an object that is in the list
         *
         * @param index an <code>int</code> value
         * @return a <code>Object</code> value
         */
        public Object get( final int index )
        {
            return m_proxy.get( index );
        }

        /**
         * Find out the index of an object in the list
         *
         * @param object an <code>Object</code> value
         * @return an <code>int</code> value
         */
        public int indexOf( final Object object )
        {
            return m_proxy.indexOf( object );
        }

        /**
         * Clear the list
         */
        public void clear()
        {
            m_proxy.clear();
            m_cache = EMPTY_ARRAY;
        }

        /**
         * Obtain the list as an array. Subsequents calls to this method
         * will return the same array object, until a write operation is
         * performed on the list.
         *
         * @return an <code>Object[]</code> value
         */
        public Object[] toArray()
        {
            return m_cache;
        }
    }
}
