/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.test.data;

import java.util.Arrays;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;

/**
 * A test component.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.4 $ $Date: 2002/10/02 11:25:56 $
 */
public class Component3
    implements Serviceable
{
    public void service( final ServiceManager manager )
        throws ServiceException
    {
        final Service2[] services =
            (Service2[]) manager.lookup( Service2[].class.getName() );
        System.out.println( "Passed the following services: " +
                            Arrays.asList( services ) );
        if( 3 != services.length )
        {
            final String message =
                "Expected to get 3 services but got " + services.length;
            throw new ServiceException( message );
        }
    }
}
