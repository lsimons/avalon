/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.instrument.client;

/**
 *
 * @author <a href="mailto:leif@silveregg.co.jp">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/03/26 11:32:24 $
 * @since 4.1
 */
interface InstrumentManagerConnectionListener
{
    /**
     * Called when the connection is opened.  May be called more than once if 
     *  the connection to the InstrumentManager is reopened.
     *
     * @param host Host of the connection.
     * @param host Port of the connection.
     */
    void opened( String host, int port );
    
    /**
     * Called when the connection is closed.  May be called more than once if 
     *  the connection to the InstrumentManager is reopened.
     *
     * @param host Host of the connection.
     * @param host Port of the connection.
     */
    void closed( String host, int port );
    
    /**
     * Called when the connection is disposed.  All references should be removed.
     *
     * @param host Host of the connection.
     * @param host Port of the connection.
     */
    void disposed( String host, int port );
}

