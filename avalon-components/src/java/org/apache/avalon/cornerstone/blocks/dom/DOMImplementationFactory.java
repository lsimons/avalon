/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.avalon.cornerstone.blocks.dom;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.phoenix.Block;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

public class DOMImplementationFactory 
    extends AbstractLoggable 
    implements Block, Configurable, DOMImplementation
{
    protected DOMImplementation m_domImplementation;

    public void dispose()
    {
        m_domImplementation = null;
    }

    public void configure( Configuration configuration ) 
        throws ConfigurationException 
    {
        final String domClass = 
            configuration.getChild("domimplementation-class-name").getValue();
        try 
        {
            m_domImplementation = 
                (DOMImplementation)Class.forName(domClass).newInstance();
        } 
        catch( final ClassNotFoundException cnfe )
        {
            throw new ConfigurationException( "ClassNotFoundException for DOM " + 
                                              "implementaion factory",
                                              cnfe );
        } 
        catch( final InstantiationException ie )
        {
            throw new ConfigurationException( "InstantiationException for DOM " + 
                                              "implementaion factory", 
                                              ie );
        } 
        catch( final IllegalAccessException ie )
        {
            throw new ConfigurationException( "IllegalAccessException for DOM " + 
                                              "implementaion factory", 
                                              ie );
        }
    }

    // Methods from DOMImplementaion interface.  We're acting as a proxy here.
    public Document createDocument( final String s, 
                                    final String s1, 
                                    final DocumentType type ) 
        throws DOMException
    {
        return m_domImplementation.createDocument( s, s1, type );
    }

    public DocumentType createDocumentType( final String s, 
                                            final String s1, 
                                            final String s2 ) 
        throws DOMException 
    {
        return m_domImplementation.createDocumentType( s, s1, s2 );
    }

    public boolean hasFeature( final String s, final String s1 ) 
    {
        return m_domImplementation.hasFeature( s, s1 );
    }
}

