/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.cornerstone.services.soapification;

/**
 * A general execption wrapper for SOAPification type services.
 *
 * @author <a href="mailto:Paul_Hammant@yahoo.com">Paul Hammant</a>
 */
public class SOAPificationException
    extends Exception
{
    private Throwable mContained;
    private String mReason;

    public SOAPificationException( String reason )
    {
        this( reason, null );
    }

    public SOAPificationException( String reason, Throwable contained )
    {
        mContained = contained;
        mReason = reason;
    }
}
