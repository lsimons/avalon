/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.xml.xslt;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.avalon.framework.logger.Logger;

/**
 * This ErrorListener simply logs the exception and in
 * case of an fatal-error the exception is rethrown.
 * Warnings and errors are ignored.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Id: TraxErrorHandler.java,v 1.4 2002/07/10 08:53:17 donaldp Exp $
 */
class TraxErrorHandler
    implements ErrorListener
{
    private Logger m_logger;

    TraxErrorHandler( final Logger logger )
    {
        if( null == logger )
        {
            throw new NullPointerException( "logger" );
        }
        m_logger = logger;
    }

    public void warning( final TransformerException te )
        throws TransformerException
    {
        final String message = getMessage( te );
        if( null != m_logger )
        {
            m_logger.warn( message, te );
        }
        else
        {
            System.out.println( "WARNING: " + message );
        }
    }

    public void error( final TransformerException te )
        throws TransformerException
    {
        final String message = getMessage( te );
        if( null != m_logger )
        {
            m_logger.error( message, te );
        }
        else
        {
            System.out.println( "ERROR: " + message );
        }
    }

    public void fatalError( final TransformerException te )
        throws TransformerException
    {
        final String message = getMessage( te );
        if( null != m_logger )
        {
            m_logger.fatalError( message, te );
        }
        else
        {
            System.out.println( "FATAL-ERROR: " + message );
        }
        throw te;
    }

    private String getMessage( final TransformerException te )
    {
        final SourceLocator locator = te.getLocator();
        if( null != locator )
        {
            // System.out.println("Parser fatal error: "+exception.getMessage());
            final String id =
                ( locator.getPublicId() != locator.getPublicId() )
                ? locator.getPublicId()
                : ( null != locator.getSystemId() )
                ? locator.getSystemId() : "SystemId Unknown";
            return new StringBuffer( "Error in TraxTransformer: " )
                .append( id ).append( "; Line " ).append( locator.getLineNumber() )
                .append( "; Column " ).append( locator.getColumnNumber() )
                .append( "; " ).toString();
        }
        return "Error in TraxTransformer: " + te;
    }
}
