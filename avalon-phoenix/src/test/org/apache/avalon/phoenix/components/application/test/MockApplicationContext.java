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

package org.apache.avalon.phoenix.components.application.test;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.containerkit.profile.PartitionProfile;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;
import org.apache.excalibur.threadcontext.ThreadContext;
import org.apache.excalibur.threadcontext.impl.DefaultThreadContextPolicy;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.manager.NoopInstrumentManager;

/**
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version $Revision: 1.9 $ $Date: 2003/03/22 12:07:17 $
 */
class MockApplicationContext
    implements ApplicationContext
{
    private final ThreadContext m_threadContext = new ThreadContext( new DefaultThreadContextPolicy(), new HashMap() );
    private final PartitionProfile m_sarMetaData;
    private final Logger m_logger;

    public MockApplicationContext( final PartitionProfile sarMetaData,
                                   final Logger logger )
    {
        m_sarMetaData = sarMetaData;
        m_logger = logger;
    }

    public PartitionProfile getPartitionProfile()
    {
        return m_sarMetaData;
    }

    public ThreadContext getThreadContext()
    {
        return m_threadContext;
    }

    public void requestShutdown()
    {
        //ignore
    }

    public void exportObject( String name, Class[] interfaceClasses, Object object )
        throws Exception
    {
        //ignore
    }

    public void unexportObject( String name )
        throws Exception
    {
        //ignore
    }

    public ClassLoader getClassLoader()
    {
        return getClass().getClassLoader();
    }

    public InputStream getResourceAsStream( final String name )
    {
        return getClassLoader().getResourceAsStream( name );
    }

    public Configuration getConfiguration( String component )
        throws ConfigurationException
    {
        throw new ConfigurationException( "I can't do that dave!" );
    }

    public File getHomeDirectory()
    {
        return new File( "." );
    }

    public ClassLoader getClassLoader( String name )
        throws Exception
    {
        throw new Exception( "I can't do that dave!" );
    }

    public Logger getLogger( String name )
    {
        return m_logger;
    }

    public InstrumentManager getInstrumentManager()
    {
        return new NoopInstrumentManager();
    }

    public String getInstrumentableName( String component )
    {
        return component;
    }
}
