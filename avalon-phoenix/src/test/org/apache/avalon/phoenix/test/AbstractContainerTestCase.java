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

package org.apache.avalon.phoenix.test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.phoenix.components.deployer.PhoenixProfileBuilder;
import org.apache.avalon.phoenix.containerkit.profile.PartitionProfile;
import org.apache.avalon.phoenix.interfaces.ContainerConstants;
import org.apache.avalon.phoenix.tools.configuration.ConfigurationBuilder;
import org.xml.sax.InputSource;

/**
 * Abstract class which TestCases can extend.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.11 $ $Date: 2003/04/05 11:21:10 $
 */
public class AbstractContainerTestCase
    extends TestCase
{
    public AbstractContainerTestCase( String name )
    {
        super( name );
    }

    protected PartitionProfile assembleSar( final String config )
        throws Exception
    {
        final PhoenixProfileBuilder assembler = new PhoenixProfileBuilder();
        assembler.enableLogging( new ConsoleLogger() );
        final URL resource = getClass().getResource( config );
        assertNotNull( "Config resource: " + config, resource );
        final Configuration assembly =
            ConfigurationBuilder.build( new InputSource( resource.toExternalForm() ),
                                        ConfigurationBuilder.ASSEMBLY_SCHEMA,
                                        new ConsoleLogger() );
        final Map parameters = new HashMap();
        parameters.put( ContainerConstants.ASSEMBLY_NAME, "test" );
        parameters.put( ContainerConstants.ASSEMBLY_CONFIG, assembly );
        parameters.put( ContainerConstants.ASSEMBLY_CLASSLOADER, getClass().getClassLoader() );
        return assembler.buildProfile( parameters );
    }

}
