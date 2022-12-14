/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/
package org.apache.avalon.phoenix.components.logger;

import java.util.HashMap;
import java.util.Map;
import org.apache.avalon.excalibur.logger.Log4JLoggerManager;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.ConfigurationUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.log4j.xml.DOMConfigurator;
import org.realityforge.configkit.PropertyExpander;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A LoggerManager for Log4j that will configure the Log4j subsystem
 * using specified configuration.
 *
 * @author <a href="mailto:Ole.Bulbuk at ebp.de">Ole Bulbuk</a>
 * @author Peter Donald
 * @version $Revision: 1.3 $ $Date: 2003/12/05 15:14:36 $
 */
public class ExtendedLog4jLoggerManager
    extends Log4JLoggerManager
    implements Contextualizable, Configurable, Initializable
{
    private final PropertyExpander m_expander = new PropertyExpander();
    private final Map m_data = new HashMap();
    private Element m_element;

    public void contextualize( Context context )
        throws ContextException
    {
        extractData( context, BlockContext.APP_HOME_DIR );
        extractData( context, BlockContext.APP_NAME );
        //extractData( context, "phoenix.home" );
    }

    private void extractData( Context context, final String key ) throws ContextException
    {
        m_data.put( key, context.get( key ) );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        m_element = ConfigurationUtil.toElement( configuration );
    }

    public void initialize()
        throws Exception
    {
        m_expander.expandValues( m_element, m_data );
        final Document document = m_element.getOwnerDocument();
        final Element newElement = document.createElement( "log4j:configuration" );
        final NodeList childNodes = m_element.getChildNodes();
        final int length = childNodes.getLength();
        for( int i = 0; i < length; i++ )
        {
            final Node node = childNodes.item( i );
            final Node newNode = node.cloneNode( true );
            newElement.appendChild( newNode );
        }

        document.appendChild( newElement );
        DOMConfigurator.configure( newElement );
    }
}

