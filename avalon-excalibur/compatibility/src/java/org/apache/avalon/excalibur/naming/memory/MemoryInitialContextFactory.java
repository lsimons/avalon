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
package org.apache.avalon.excalibur.naming.memory;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

import org.apache.avalon.excalibur.naming.DefaultNameParser;
import org.apache.avalon.excalibur.naming.DefaultNamespace;

/**
 * Initial context factory for memorycontext.
 *
 * <p><b>WARNING:</b> This class should never be use used in a real
 * system. It is is just a class that demonstrates how to write a
 * basic <code>InitialContextFactory</code> for MemeoryContext.
 * However this factory creates a new Context every time which is
 * rarely desired behaviour.</p>
 *
 * <p>In a real application you may want the policy of Context
 * creation to be specific application. Some strategies include.</p>
 * <ul>
 *   <li>ClassLoader-wide. ie Every user who is in same ClassLoader
 *       or loaded from a Child ClassLoader will see same JNDI tree.
 *       In this case the InitialContextFactory should cache root
 *       context in a static variable.</li>
 *   <li>Thread-specific. ie Give out initial context based on which
 *       thread the caller is in. In this case the InitialContextFactory
 *       should cache root context in a [Inheritable]ThreadLocal
 *       variable.</li>
 *   <li>Parameter-specific. ie Give out initial context based on
 *       a parameter passed in. The parameter could be passed in as
 *       PROVIDER_URL or another standard context property. In this
 *       case the InitialContextFactory should cache root context(s)
 *       in a static map variable that maps between parameter and
 *       context.</li>
 * </ul>
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.1 $
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/jndikit/
 */
public class MemoryInitialContextFactory
    implements InitialContextFactory
{
    public Context getInitialContext( final Hashtable environment )
        throws NamingException
    {
        final DefaultNameParser parser = new DefaultNameParser();
        final DefaultNamespace namespace = new DefaultNamespace( parser );
        return new MemoryContext( namespace, environment, null );
    }
}

