/* 
 * Copyright (C) The Apache Software Foundation. All rights reserved. 
 * 
 * This software is published under the terms of the Apache Software License 
 * version 1.1, a copy of which has been included with this distribution in 
 * the LICENSE file. 
 */
package org.apache.avalon.camelot;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Represents a Registry of names to types.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultRegistry
    implements Registry
{
    protected final HashMap        m_infos       = new HashMap();
    protected final Class          m_infoClass;

    public DefaultRegistry( final Class clazz )
    {
        m_infoClass = clazz;
    }

    public void register( final String name, final Info info )
        throws RegistryException
    {
        if( null != m_infos.get( name ) )
        {
            throw new RegistryException( "Name " + name + " already registered" );
        }
        else
        {
            checkInfo( name, info );
            m_infos.put( name, info );
        }
    }

    public void unregister( final String name )
        throws RegistryException
    {
        if( null == m_infos.remove( name ) )
        {
            throw new RegistryException( "Name " + name + " not registered" );
        }
    }

    public Info getInfo( final String name, final Class clazz )
        throws RegistryException
    {
        final Info info = (Info)m_infos.get( name );

        if( null == info )
        {
            throw new RegistryException( "Name " + name + " not registered" );
        }
        else if( !clazz.isInstance( info ) )
        {
            throw new RegistryException( "Info of type " + info.getClass().getName() +
                                         " not compatable with expected type " + clazz.getName() );
        }
        else
        {
            return info;
        }
    }

    public Iterator getInfoNames()
    {
        return m_infos.keySet().iterator();
    }

    protected void checkInfo( final String name, final Info info )
        throws RegistryException
    {
        if( !m_infoClass.isAssignableFrom( info.getClass() ) )
        {
            throw new RegistryException( "Only Infos of type " + m_infoClass.getName() +
                                         " may be placed in registry." );
        }
    }
}
