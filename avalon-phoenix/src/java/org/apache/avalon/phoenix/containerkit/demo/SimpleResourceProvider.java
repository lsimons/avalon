/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.containerkit.demo;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.containerkit.factory.ComponentFactory;
import org.apache.avalon.phoenix.containerkit.kernel.AbstractServiceKernel;
import org.apache.avalon.phoenix.containerkit.kernel.ComponentEntry;
import org.apache.avalon.phoenix.containerkit.lifecycle.impl.AbstractResourceProvider;
import org.apache.avalon.phoenix.containerkit.metadata.ComponentMetaData;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.manager.NoopInstrumentManager;

/**
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.2 $ $Date: 2003/03/17 03:42:20 $
 */
public class SimpleResourceProvider
    extends AbstractResourceProvider
{
    private final AbstractServiceKernel m_serviceKernel;

    public SimpleResourceProvider( final AbstractServiceKernel serviceKernel,
                                   final ComponentFactory factory )
    {
        super( factory );
        m_serviceKernel = serviceKernel;
    }

    protected ComponentMetaData getMetaData( final Object entry )
    {
        return ( (ComponentEntry)entry ).getProfile().getMetaData();
    }

    protected Object getService( final String name,
                                 final Object entry )
    {
        return m_serviceKernel.getComponent( name );
    }

    protected Object getContextValue( final String name,
                                      final Object entry )
    {
        //Should return classloaders that are available
        return null;
    }

    public Logger createLogger( final Object entry )
        throws Exception
    {
        final ComponentMetaData component = getMetaData( entry );
        return getLogger().getChildLogger( component.getName() );
    }

    public InstrumentManager createInstrumentManager( Object entry )
        throws Exception
    {
        return new NoopInstrumentManager();
    }

    public String createInstrumentableName( Object entry )
        throws Exception
    {
        final ComponentMetaData component = getMetaData( entry );
        return component.getName();
    }
}
