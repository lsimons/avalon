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
package org.apache.avalon.excalibur.monitor;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.thread.ThreadSafe;

/**
 * The PassiveMonitor is used to passively check a set of resources to see if they have
 * changed.  It will be implemented as a Component, that can be retrieved from
 * the ComponentLocator.  It defaults to checking every 1 minute.  The configuration
 * looks like this:
 *
 * <pre>
 *   &lt;passive-monitor&gt;
 *     &lt;init-resources&gt;
 *       &lt;-- This entry can be repeated for every resource you want to register immediately --&gt;
 *
 *       &lt;resource key="<i>file:./myfile.html</i>" class="<i>org.apache.avalon.excalibur.monitor.FileMonitor</i>"/&gt;
 *     &lt;/init-resources&gt;
 *   &lt;/pasive-monitor&gt;
 * </pre>
 *
 * @avalon.component name="passive-monitor" lifestyle="singleton"
 * @avalon.service type="org.apache.avalon.excalibur.monitor.Monitor" version="1.0"
 * @x-avalon.info name=passive-monitor
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version $Id: PassiveMonitor.java,v 1.17 2003/09/24 05:39:40 mcconnell Exp $
 */
public class PassiveMonitor
    extends org.apache.avalon.excalibur.monitor.impl.PassiveMonitor
    implements LogEnabled, Configurable, ThreadSafe
{
    private Logger m_logger;

    public void enableLogging( final Logger logger )
    {
        m_logger = logger;
    }

    public final void configure( final Configuration config )
        throws ConfigurationException
    {
        final Configuration[] initialResources =
            config.getChild( "init-resources" ).getChildren( "resource" );
        final Resource[] resources =
            MonitorUtil.configureResources( initialResources, m_logger );
        addResources( resources );
    }
}
