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

package org.apache.avalon.phoenix.framework.tools.infobuilder.test.data;

import java.io.Serializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service2;
import org.apache.avalon.phoenix.framework.tools.infobuilder.test.data.otherpkg.Service3;

/**
 * A simple avalon component to test QDox loading of info etc.
 *
 * @author Peter Donald
 * @version $Revision: 1.6 $ $Date: 2003/12/05 15:14:39 $
 * @phoenix.component
 * @phoenix.service type="Service1"
 * @phoenix.service type="Service2"
 * @phoenix.service type="Service3"
 */
public class QDoxComponent1
    extends AbstractLogEnabled
    implements Serializable, Service1, Service2, Service3, Serviceable, Contextualizable, Configurable
{
    /**
     * @phoenix.logger
     * @phoenix.logger name="foo"
     */
    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
    }

    /**
     * @phoenix.context type="Context"
     * @phoenix.entry key="foo" type="ClassLoader"
     * @phoenix.entry key="bar" type="Logger"
     * @phoenix.entry key="baz" type="java.io.File"
     */
    public void contextualize( Context context )
        throws ContextException
    {
    }

    /**
     * @phoenix.dependency key="foo" type="Service3"
     * @phoenix.dependency type="Service3"
     * @phoenix.dependency type="Service2" optional="true"
     */
    public void service( ServiceManager manager )
        throws ServiceException
    {
    }

    /**
     * @phoenix.configuration type="http://relaxng.org/ns/structure/1.0"
     */
    public void configure( Configuration configuration )
        throws ConfigurationException
    {
    }
}
