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
public class HttpPostRequestWrapper extends HttpRequestWrapper {

    protected HttpPostRequestWrapper( final Logger logger, final String rq ) 
        throws IOException
    {
        super( logger, rq );
    }

    protected HttpPostRequestWrapper( final Logger logger, final InputStream is )
        throws IOException
    {
        super( logger );

        String wholeBuffer = "POST";

        is.read( new byte[1] ); // T from post

        byte[] bytes = new byte[SEGLEN];
        int bytesRead = is.read( bytes );
        int contLen;
        int lenAfterContent;

        do 
        {
            wholeBuffer += new String( bytes, 0, bytesRead );

            String tmpRqst = wholeBuffer.toUpperCase();

            contLen = getContentLength( tmpRqst );
            lenAfterContent = getLengthAfterContent( tmpRqst );

            if( -1 == wholeBuffer.indexOf( EOF ) ) 
            {
                bytesRead = is.read( bytes );
            }
        } 
        while( ( lenAfterContent < contLen ) && 
               ( -1 == wholeBuffer.indexOf( EOF ) ) );

        super.setRequest( wholeBuffer );
    }

    private int getContentLength( final String rqst ) 
    {
        int firstDigit = rqst.indexOf("CONTENT-LENGTH:") + 16;

        if( -1 != firstDigit )
        {
            final int lastDigit = rqst.indexOf( "\n", firstDigit );
            return Integer.parseInt( rqst.substring( firstDigit, lastDigit ).trim() );
        }

        return 0;
    }

    private int getLengthAfterContent( final String rqst )
    {
        int firstDigit = rqst.indexOf( "CONTENT-LENGTH:" ) + 16;

        if( -1 != firstDigit )
        {
            return rqst.length() - firstDigit;
        }

        return 0;
    }
}

