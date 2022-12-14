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

package org.apache.avalon.phoenix.interfaces;

import java.io.File;
import java.io.InputStream;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.phoenix.containerkit.profile.PartitionProfile;
import org.apache.excalibur.instrument.InstrumentManager;

/**
 * Manage the "context" in which Applications operate.
 *
 * @author Peter Donald
 */
public interface ApplicationContext
{
    String ROLE = ApplicationContext.class.getName();

    File getHomeDirectory();

    PartitionProfile getPartitionProfile();

    /**
     * A application can request that it be be shutdown. In most cases
     * the kernel will schedule the shutdown to occur in another thread.
     */
    void requestShutdown();

    /**
     * Export specified object into management system.
     * The object is exported using specifed interface
     * and using the specified name.
     *
     * @param name the name of object to export
     * @param interfaceClasses the interface of object with which to export
     * @param object the actual object to export
     */
    void exportObject( String name, Class[] interfaceClasses, Object object )
        throws Exception;

    /**
     * Unexport specified object from management system.
     *
     * @param name the name of object to unexport
     */
    void unexportObject( String name )
        throws Exception;

    /**
     * Get ClassLoader for the current application.
     *
     * @return the ClassLoader
     */
    ClassLoader getClassLoader();

    /**
     * This method grants access to a named ClassLoader. The ClassLoaders
     * for an application are declared in the <tt>environment.xml</tt>
     * descriptor. See the Specification for details.
     */
    ClassLoader getClassLoader( String name )
        throws Exception;

    /**
     * Retrieve a resource from the SAR file. The specified
     * name is relative the root of the archive. So you could
     * use it to retrieve a html page from within sar by loading
     * the resource named "data/main.html" or similar.
     */
    InputStream getResourceAsStream( String name );

    /**
     * Get logger with category for application.
     * Note that this name may not be the absolute category.
     *
     * @param name the name of logger
     * @return the Logger
     */
    Logger getLogger( String name )
        throws Exception;

    /**
     * Get the instrument manager to use for this application
     *
     * @return the InstrumentManager
     */
    InstrumentManager getInstrumentManager();

    /**
     * Get the name to use for the instrumentables for the specified component
     *
     * @param component the component
     * @return the name to use for Instrumentables
     */
    String getInstrumentableName( String component );
}
