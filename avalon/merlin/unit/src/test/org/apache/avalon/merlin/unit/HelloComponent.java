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

package org.apache.avalon.merlin.unit;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.Configuration;

/**
 * A sample component.  This component implements a number 
 * of lifecycle interface.  Each lifecycle interface is a stage
 * that is processed by a container during the deployment of 
 * the component.  The lifecycle stages demonstrated here include
 * LogEnabled (association of a logging channel), Initializable
 * (initialization of the component), Executable (component
 * execution), and Disposable (componet disposal).  PLease note 
 * that all lifecycle stages are optional.
 *
 * @avalon.component version="1.0" name="hello"
 */
public class HelloComponent 
  implements LogEnabled, Configurable, Initializable, Disposable, Hello
{

   //------------------------------------------------------------
   // state
   //------------------------------------------------------------

   /**
    * Internal reference to the logging channel supplied to us 
    * by the container. 
    */
    private Logger m_logger;

    private String m_message;

   //------------------------------------------------------------
   // Hello
   //------------------------------------------------------------

   /**
    * Return the hello message.
    * @return the message
    */
    public String getMessage()
    {
        return m_message;
    }

   //------------------------------------------------------------
   // lifecycle
   //------------------------------------------------------------

   /**
    * Supply of a logging channel by the container.
    *
    * @param logger the logging channel for this component
    */
    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
        getLogger().info( "logging" );
    }

   /**
    * Configuration of the component by the container.
    * @exception Exception if a configuration error occurs
    */
    public void configure( Configuration config ) throws ConfigurationException
    {
        getLogger().info( "configuration" );
        m_message = config.getChild( "message" ).getValue( "unknown" );
    }

   /**
    * Initialization of the component by the container.
    * @exception Exception if an initialization error occurs
    */
    public void initialize() throws Exception
    {
        getLogger().info( "initialization" );
    }

   /**
    * Component disposal trigger by the container during which
    * the component will release consumed resources.
    */
    public void dispose()
    {
        getLogger().info( "disposal" );
        m_logger = null;
    }

   /**
    * Return the logging channel assigned to us by the container.
    * @return the logging channel
    */
    private Logger getLogger()
    {
        return m_logger;
    }

}
