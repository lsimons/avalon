/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.httpproxy;

import java.io.IOException;
import java.io.InputStream;
import org.apache.log.Logger;

/**
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class HttpGetRequestWrapper 
    extends HttpRequestWrapper
{
    protected HttpGetRequestWrapper( final Logger logger, final String rq ) 
        throws IOException
    {
        super( logger, rq );
    }

    protected HttpGetRequestWrapper( final Logger logger, final InputStream is ) 
        throws IOException
    {
        super( logger );

        String wholeBuffer = "GET";
        byte[] bytes = new byte[ SEGLEN ];
        int bytesRead = is.read( bytes );

        do 
        {
            wholeBuffer += new String( bytes, 0, bytesRead );

            if( !wholeBuffer.endsWith( EOF ) )
            {
                bytesRead = is.read( bytes );
            }
        } 
        while( !wholeBuffer.endsWith( EOF ) );

        super.setRequest( wholeBuffer );
    }
}
