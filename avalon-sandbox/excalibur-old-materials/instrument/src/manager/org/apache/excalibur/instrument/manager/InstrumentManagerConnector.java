/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.manager;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;

/**
 * Interface for classes which can be registered as Connectors for
 *  InstrumentManagers.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/04 10:33:33 $
 * @since 4.1
 */
public interface InstrumentManagerConnector
    extends Configurable, Startable
{
    /**
     * Set the InstrumentManager to which the Connecter will provide
     *  access.  This method is called before the new connector is
     *  configured or started.
     */
    void setInstrumentManager( DefaultInstrumentManager manager );
}
