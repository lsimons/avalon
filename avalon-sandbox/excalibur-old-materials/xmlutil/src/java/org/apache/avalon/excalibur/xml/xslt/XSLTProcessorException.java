/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml.xslt;

import org.apache.avalon.framework.CascadingException;

/**
 * This exception is thrown by the XSLTProcessor. It will wrap any exceptions thrown throughout the processing process.
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @author <a href="mailto:proyal@managingpartners.com">Peter Royal</a>
 */
public class XSLTProcessorException
    extends CascadingException
{
    public XSLTProcessorException( final String message )
    {
        super( message );
    }

    public XSLTProcessorException( final String message,
                                   final Throwable throwable )
    {
        super( message, throwable );
    }
}
