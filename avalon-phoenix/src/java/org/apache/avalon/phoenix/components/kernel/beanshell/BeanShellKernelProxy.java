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

package org.apache.avalon.phoenix.components.kernel.beanshell;

import java.io.File;
import java.util.Map;
import org.apache.avalon.phoenix.containerkit.profile.PartitionProfile;
import org.apache.avalon.phoenix.interfaces.Application;
import org.apache.avalon.phoenix.interfaces.Kernel;
import org.realityforge.loggerstore.LoggerStore;

/**
 * @author Paul Hammant
 * @author David Gray
 */
public class BeanShellKernelProxy
    implements Kernel
{
    private final transient Kernel m_kernel;

    /**
     * Construct a Proxy to the Kernel that does not implement all methods.
     */
    public BeanShellKernelProxy( final Kernel kernel )
    {
        m_kernel = kernel;
    }

    public void addApplication( final PartitionProfile profile,
                                final File homeDirectory, final File workDirectory,
                                final ClassLoader classLoader,
                                final LoggerStore store,
                                final Map classloaders )
        throws Exception
    {
        throw new UnsupportedOperationException( "This is not supported for non-kernel visitors" );
    }

    public void removeApplication( final String name )
        throws Exception
    {
        m_kernel.removeApplication( name );
    }

    public Application getApplication( final String name )
    {
        return m_kernel.getApplication( name );
    }

    public String[] getApplicationNames()
    {
        return m_kernel.getApplicationNames();
    }

    public void lock() {
        m_kernel.lock();
    }

    public void unlock() {
        m_kernel.unlock();
    }

}