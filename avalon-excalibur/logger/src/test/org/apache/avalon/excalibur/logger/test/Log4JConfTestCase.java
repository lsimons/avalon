/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================
 
 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.
 
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
 
 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"  
    must not be used to endorse or promote products derived from this  software 
    without  prior written permission. For written permission, please contact 
    apache@apache.org.
 
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
package org.apache.avalon.excalibur.logger.test;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.logger.Log4JConfLoggerManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.logger.Logger;
import org.xml.sax.SAXException;

/**
 *
 * @author Peter Donald
 * @version $Revision: 1.6 $ $Date: 2003/12/05 15:13:53 $
 */
public class Log4JConfTestCase
    extends TestCase
{
    public Log4JConfTestCase( final String name )
    {
        super( name );
    }

    public void testWrite()
        throws Exception
    {
        final Log4JConfLoggerManager manager = getManager( "log4j.xml" );
        final Logger logger = manager.getDefaultLogger();
        logger.warn( "Some random message" );
    }

    private Log4JConfLoggerManager getManager( final String resourceName )
        throws Exception
    {
        final Configuration configuration = loadConfiguration( resourceName );
        final Log4JConfLoggerManager manager = new Log4JConfLoggerManager();
        ContainerUtil.enableLogging(manager, new ConsoleLogger());
        ContainerUtil.configure( manager, configuration );
        return manager;
    }

    private Configuration loadConfiguration( final String resourceName ) throws SAXException, IOException, ConfigurationException
    {
        final InputStream resource = getResource( resourceName );
        final DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
        final Configuration configuration = builder.build( resource );
        return configuration;
    }

    private InputStream getResource( final String resourceName )
    {
        final InputStream resource = getClass().getResourceAsStream( resourceName );
        if( null == resource )
        {
            throw new NullPointerException( "resource" );
        }
        return resource;
    }
}