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
package org.apache.avalon.phoenix.components.application;

import java.io.File;
import java.io.InputStream;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.BlockContext;
import org.apache.avalon.phoenix.interfaces.ApplicationContext;

/**
 * Context via which Blocks communicate with container.
 *
 * @author Peter Donald
 */
final class DefaultBlockContext
    implements BlockContext
{
    private final String m_name;
    private final ApplicationContext m_applicationContext;

    DefaultBlockContext( final String name,
                         final ApplicationContext frame )
    {
        m_name = name;
        m_applicationContext = frame;
    }

    public Object get( final Object key )
        throws ContextException
    {
        if( BlockContext.APP_NAME.equals( key ) )
        {
            return m_applicationContext.getPartitionProfile().getMetaData().getName();
        }
        else if( BlockContext.APP_HOME_DIR.equals( key ) )
        {
            return m_applicationContext.getHomeDirectory();
        }
        else if( BlockContext.NAME.equals( key ) )
        {
            return m_name;
        }
        else
        {
            throw new ContextException( "Unknown key: " + key );
        }
    }

    /**
     * Base directory of .sar application.
     *
     * @return the base directory
     */
    public File getBaseDirectory()
    {
        return m_applicationContext.getHomeDirectory();
    }

    /**
     * Retrieve name of block.
     *
     * @return the name of block
     */
    public String getName()
    {
        return m_name;
    }

    public void requestShutdown()
    {
        m_applicationContext.requestShutdown();
    }

    public InputStream getResourceAsStream( final String name )
    {
        return m_applicationContext.getResourceAsStream( name );
    }

    public Logger getLogger( final String name )
    {
        try
        {
            return m_applicationContext.getLogger( getName() ).getChildLogger( name );
        }
        catch( Exception e )
        {
            final String message =
                "Unable to aquire logger " + name + " due to " + e;
            throw new IllegalStateException( message );
        }
    }

    public ClassLoader getClassLoader( final String name )
        throws Exception
    {
        return m_applicationContext.getClassLoader( name );
    }
}
