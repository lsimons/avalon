/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.demos.httpproxy;

/**
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class HttpRequestValidationException
    extends Exception
{
    /**
     * Constructor HttpRequestValidationException
     * Thrown when somethod failed during the handlers validation stage.
     */
    public HttpRequestValidationException( final String message )
    {
        super( message );
    }
}
