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

package org.apache.avalon.phoenix.components.classloader;

import java.io.File;
import java.net.URL;
import java.security.Policy;
import java.util.Map;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.phoenix.components.util.ResourceUtil;
import org.realityforge.xmlpolicy.builder.PolicyResolver;

/**
 * A basic resolver that resolves Phoenix specific features.
 * (like remapping URLs).
 *
 * @author Peter Donald
 * @version $Revision: 1.11 $ $Date: 2003/12/05 15:14:35 $
 */
class SarPolicyResolver
    extends AbstractLogEnabled
    implements PolicyResolver
{
    private final File m_baseDirectory;
    private final File m_workDirectory;

    SarPolicyResolver( final File baseDirectory,
                       final File workDirectory )
    {
        m_workDirectory = workDirectory;
        m_baseDirectory = baseDirectory;
    }

    public Policy createPolicy( final Map grants )
        throws Exception
    {
        final SarPolicy sarPolicy = new SarPolicy( grants );
        ContainerUtil.enableLogging( sarPolicy, getLogger() );
        ContainerUtil.initialize( sarPolicy );
        return sarPolicy;
    }

    public URL resolveLocation( String location )
        throws Exception
    {
        if( null == location )
        {
            return null;
        }
        else
        {
            location = ResourceUtil.expandSarURL( location,
                                                  m_baseDirectory,
                                                  m_workDirectory );
            return new URL( location );
        }
    }
}
