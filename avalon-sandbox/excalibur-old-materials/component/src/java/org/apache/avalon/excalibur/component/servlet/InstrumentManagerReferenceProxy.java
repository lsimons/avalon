/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.component.servlet;

import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrumentable;

/**
 * Reference Proxy to an InstrumentManager
 *
 * @author <a href="mailto:leif@apache.org">Leif Mortenson</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/11/07 05:11:35 $
 * @since 4.2
 */
final class InstrumentManagerReferenceProxy
    extends AbstractReferenceProxy
    implements InstrumentManager
{
    private InstrumentManager m_instrumentManager;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Create a new proxy.
     *
     * @param instrumentManager InstrumentManager being proxied.
     * @param latch Latch wich will be notified when this proxy is finalized.
     * @param name Name of the proxy.
     */
    InstrumentManagerReferenceProxy( InstrumentManager instrumentManager,
                                     AbstractReferenceProxyLatch latch,
                                     String name )
    {
        super( latch, name );
        m_instrumentManager = instrumentManager;
    }

    /*---------------------------------------------------------------
     * InstrumentManager Methods
     *-------------------------------------------------------------*/
    /**
     * Instrumentable to be registered with the instrument manager.  Should be
     *  called whenever an Instrumentable is created.  The '.' character is
     *  used to denote a child Instrumentable and can be used to register the
     *  instrumentable at a specific point in an instrumentable hierarchy.
     *
     * @param instrumentable Instrumentable to register with the InstrumentManager.
     * @param instrumentableName The name to use when registering the Instrumentable.
     *
     * @throws Exception If there were any problems registering the Instrumentable.
     */
    public void registerInstrumentable( Instrumentable instrumentable, String instrumentableName )
        throws Exception
    {
        m_instrumentManager.registerInstrumentable( instrumentable, instrumentableName );
    }
}
