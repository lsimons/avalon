/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.excalibur.datasource;

import java.sql.SQLException;


/**
 * Exception that is thrown when there are no more Connection objects available
 * in the pool.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2001/11/02 18:57:11 $
 * @since 4.1
 */
public class NoAvailableConnectionException extends SQLException {

    public NoAvailableConnectionException()
    {
       super();
    }

    public NoAvailableConnectionException( String message )
    {
       super( message );
    }

    public NoAvailableConnectionException( String message, String SQLState )
    {
       super( message, SQLState );
    }

    public NoAvailableConnectionException( String message, String SQLState, int vendorCode )
    {
       super( message, SQLState, vendorCode );
    }
}