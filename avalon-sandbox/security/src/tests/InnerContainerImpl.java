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

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

public class InnerContainerImpl
    implements InnerContainer
{
    private Component m_Component;
    private AccessControlContext m_AccContext;
    
    public InnerContainerImpl()
        throws Exception
    {
        ClassLoader parentCL = getClass().getClassLoader();
        File f = new File( System.getProperty( "user.dir" ), "component.jar" );
        CodeSource cs = new CodeSource( f.toURL(), null );        
        
        Permissions p = new Permissions();
        
        Permission perm = new RuntimePermission( "doProtected" );
        p.add( perm );
        
        ProtectionDomain pd = new ProtectionDomain( cs, p );
        ProtectionDomain[] domains = new ProtectionDomain[] { pd };
        
        m_AccContext = new AccessControlContext( domains );    
        
        m_Component = (Component) Main.instantiate( f, "ComponentImpl", parentCL );
    }
    
    public void doThatMethod() throws Exception
    {
        String s = System.getProperty( "java.home" );
        System.out.println( "Phase 0 OK.      <--  Container reads an allowed Property, and passed." );
        AccessController.doPrivileged( new PrivilegedExceptionAction()
        {
            public Object run() throws Exception
            {
                m_Component.doProtectedMethod();
                return null;
            }
        }, m_AccContext );
        
        try
        {
            s = System.getProperty( "java.vendor" );
            System.out.println( "Java Vendor=" + s );
        } catch( SecurityException e )
        {
            System.out.println( "Phase 3 OK.      <--  Container reads a non-allowed Property, and is denied." );
        }
    }
}
