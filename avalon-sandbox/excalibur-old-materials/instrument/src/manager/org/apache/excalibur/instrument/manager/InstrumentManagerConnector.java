/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

/**
 * Interface for classes which can be registered as Connectors for
 *  InstrumentManagers.
 *
 * The InstrumentManager is smart about handling connectors which implement
 *  the LogEnabled, Configurable, Initializable, Startable and Disposable
 *  interfaces.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/08/06 12:58:26 $
 * @since 4.1
 */
public interface InstrumentManagerConnector
{
    /**
     * Set the InstrumentManager to which the Connecter will provide
     *  access.  This method is called before the new connector is
     *  configured or started.
     */
    void setInstrumentManager( DefaultInstrumentManager manager );
}
