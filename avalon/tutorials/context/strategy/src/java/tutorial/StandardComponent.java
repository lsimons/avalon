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

package tutorial;

import java.io.File;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.activity.Executable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

/**
 * This is a minimal demonstration component that implements the
 * <code>BasicService</code> interface and has no dependencies.
 *
 * @avalon.component name="standard" lifestyle="singleton"
 * @avalon.service type="tutorial.StandardService"
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class StandardComponent extends AbstractLogEnabled
    implements Contextualizable, Initializable, Executable, Disposable, StandardService
{
    private File m_home;
    private File m_work;
    private String m_name;
    private String m_partition;
    private String m_message;
    private StandardContext m_context;

    //=======================================================================
    // Contextualizable
    //=======================================================================

   /**
    * Supply of the the component context to the component type.
    *
    * @param context the context value
    *
    * @avalon.context strategy="tutorial.Contextualizable"
    * @avalon.entry key="urn:avalon:name" 
    * @avalon.entry key="urn:avalon:partition"
    * @avalon.entry key="urn:avalon:home" type="java.io.File"
    * @avalon.entry key="urn:avalon:temp" type="java.io.File"
    */
    public void contextualize( StandardContext context )
    {
        m_context = context;
        m_home = context.getHomeDirectory();
        m_work = context.getWorkingDirectory();
        m_name = context.getName();
        m_partition = context.getPartitionName();
    }

    //=======================================================================
    // Initializable
    //=======================================================================

    /**
     * Initialization of the component type by its container.
     */
    public void initialize() throws Exception
    {
        m_message =
          "  strategy: " + Contextualizable.class.getName()
          + "\n  context: " + m_context.getClass().getName()
          + "\n  home: " + m_home
          + "\n  work: " + m_work
          + "\n  name: " + m_name
          + "\n  partition: " + m_partition;
    }

    //=======================================================================
    // Disposable
    //=======================================================================

    /**
     * Dispose of the component.
     */
    public void dispose()
    {
        getLogger().debug( "dispose" );
    }

    //=======================================================================
    // Executable
    //=======================================================================

    /**
     * Execute the component.
     */
    public void execute()
    {
        printMessage();
    }

    //=======================================================================
    // BasicService
    //=======================================================================

    /**
     * Service interface implementation.
     */
    public void printMessage()
    {
        getLogger().info( "contextualization using a custom strategy\n\n"
         + m_message + "\n");
    }
}
