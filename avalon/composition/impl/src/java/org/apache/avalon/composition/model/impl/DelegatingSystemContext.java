/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.composition.model.impl;

import java.io.File;
import java.net.URL;

import org.apache.avalon.composition.logging.LoggingManager;
import org.apache.avalon.composition.model.ModelFactory;
import org.apache.avalon.composition.model.SystemContext;
import org.apache.avalon.repository.Repository;
import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.parameters.Parameters;

import org.apache.avalon.composition.data.CategoryDirective;


/**
 * Implementation of a system context the delegates operations to a 
 * a parent system context.  This implementation enables the seperation
 * of a system context assigned to an application as distinct from a 
 * system context supplied to internal facilities within which 
 * supplimentary context entries can be added.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1.2.1 $ $Date: 2004/01/07 12:42:49 $
 */
public class DelegatingSystemContext extends DefaultContext 
  implements SystemContext
{
    //--------------------------------------------------------------
    // immutable state
    //--------------------------------------------------------------

    private final SystemContext m_parent;

    //--------------------------------------------------------------
    // constructor
    //--------------------------------------------------------------

   /**
    * Creation of a new delegating system context.
    *
    * @param parent the parent system context
    */
    public DelegatingSystemContext( SystemContext parent )
    {
        super( parent );
        if( parent == null )
        {
            throw new NullPointerException( "parent" );
        }
        m_parent = parent;
    }

    //--------------------------------------------------------------
    // SystemContext
    //--------------------------------------------------------------

   /**
    * Return the model factory.
    *
    * @return the factory
    */
    public ModelFactory getFactory()
    {
        return m_parent.getFactory();
    }

   /**
    * Return the base directory from which relative classloader 
    * references may be resolved.
    *
    * @return the base directory
    */
    public File getBaseDirectory()
    {
        return m_parent.getBaseDirectory();
    }

   /**
    * Return the working directory from which containers may 
    * establish persistent content.
    *
    * @return the working directory
    */
    public File getHomeDirectory()
    {
        return m_parent.getHomeDirectory();
    }

   /**
    * Return the temporary directory from which a container 
    * may use to establish a transient content directory. 
    *
    * @return the temporary directory
    */
    public File getTempDirectory()
    {
        return m_parent.getTempDirectory();
    }

   /**
    * Return the system wide repository from which resource 
    * directives can be resolved.
    *
    * @return the repository
    */
    public Repository getRepository()
    {
        return m_parent.getRepository();
    }

   /**
    * Return the system classloader. This classloader is equivalent to the
    * API classloader.
    *
    * @return the system classloader
    */
    public ClassLoader getCommonClassLoader()
    {
        return m_parent.getCommonClassLoader();
    }

   /**
    * Return the system classloader.  This classloader is equivalent to the
    * SPI privileged classloader.
    *
    * @return the system classloader
    */
    public ClassLoader getSystemClassLoader()
    {
        return m_parent.getSystemClassLoader();
    }

   /**
    * Return the system trace flag.
    *
    * @return the trace flag
    */
    public boolean isTraceEnabled()
    {
        return m_parent.isTraceEnabled();
    }

   /**
    * Return the logging manager.
    *
    * @return the logging manager.
    */
    public LoggingManager getLoggingManager()
    {
        return m_parent.getLoggingManager();
    }

   /**
    * Return the system logging channel.
    *
    * @return the system logging channel
    */
    public Logger getLogger()
    {
        return m_parent.getLogger();
    }

   /** 
    * Returns the configurable kernel parameters.
    *
    * @return a Parameters object populated with the system
    * parameters.
    */
    public Parameters getSystemParameters()
    {
        return m_parent.getSystemParameters();
    }

}
