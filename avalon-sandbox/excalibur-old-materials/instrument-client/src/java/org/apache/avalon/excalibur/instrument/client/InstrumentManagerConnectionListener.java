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
 * @version CVS $Revision: 1.2 $ $Date: 2002/03/30 01:30:49 $
 * @since 4.1
 */
interface InstrumentManagerConnectionListener
{
    /**
     * Called when the connection is opened.  May be called more than once if 
     *  the connection to the InstrumentManager is reopened.
     *
     * @param connection Connection which was opened.
     */
    void opened( InstrumentManagerConnection connection );
    
    /**
     * Called when the connection is closed.  May be called more than once if 
     *  the connection to the InstrumentManager is reopened.
     *
     * @param connection Connection which was closed.
     */
    void closed( InstrumentManagerConnection connection );
    
    /**
     * Called when the connection is deleted.  All references should be removed.
     *
     * @param connection Connection which was deleted.
     */
    void deleted( InstrumentManagerConnection connection );
}

