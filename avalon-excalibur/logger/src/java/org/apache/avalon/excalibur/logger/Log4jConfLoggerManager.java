/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.logger;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.log4j.xml.DOMConfigurator;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * A LoggerManager for Log4j that will configure the Log4j subsystem
 * using specified configuration.
 *
 * @author <a href="mailto:Ole.Bulbuk at ebp.de">Ole Bulbuk</a>
 * @version $Revision: 1.1 $ $Date: 2002/10/28 00:36:03 $
 */
public class Log4jConfLoggerManager
    extends Log4JLoggerManager
    implements Configurable
{
    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        final Element element = ConfigurationUtil.toElement( configuration );
        final Document document = element.getOwnerDocument();
        final Element newElement = document.createElement( "log4j:configuration" );
        final NodeList childNodes = element.getChildNodes();
        final int length = childNodes.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Node node = childNodes.item( i );
            newElement.appendChild( node.cloneNode( true ) );
        }

        document.appendChild( newElement );
        DOMConfigurator.configure( newElement );
    }
}
