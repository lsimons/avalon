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
 * @version CVS $Id: TraxErrorHandler.java,v 1.2 2002/05/02 10:04:05 cziegeler Exp $
 */
public class TraxErrorHandler implements ErrorListener {

    private Logger logger = null;

    public TraxErrorHandler( Logger logger ) {
        this.logger = logger;
    }

    public void warning( TransformerException exception )
            throws TransformerException {
        final String message = getMessage( exception );
        if ( this.logger != null ) {
            this.logger.warn( message, exception );
        } else {
            System.out.println( "WARNING: " + message );
        }
    }

    public void error( TransformerException exception )
            throws TransformerException {
        final String message = getMessage( exception );
        if ( this.logger != null ) {
            this.logger.error( message, exception );
        } else {
            System.out.println( "ERROR: " + message );
        }
    }

    public void fatalError( TransformerException exception )
            throws TransformerException {
        final String message = getMessage( exception );
        if ( this.logger != null ) {
            this.logger.fatalError( message, exception );
        } else {
            System.out.println( "FATAL-ERROR: " + message );
        }
        throw exception;
    }

    private String getMessage( TransformerException exception ) {
        SourceLocator locator = exception.getLocator();

        if ( null != locator ) {
            // System.out.println("Parser fatal error: "+exception.getMessage());
            String id = ( locator.getPublicId() != locator.getPublicId() )
                    ? locator.getPublicId()
                    : ( null != locator.getSystemId() )
                    ? locator.getSystemId() : "SystemId Unknown";
            return new StringBuffer( "Error in TraxTransformer: " )
                    .append( id ).append( "; Line " ).append( locator.getLineNumber() )
                    .append( "; Column " ).append( locator.getColumnNumber() )
                    .append( "; " ).toString();
        }
        return "Error in TraxTransformer: " + exception;
    }
}
