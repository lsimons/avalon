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
package org.apache.avalon.fortress;

/**
 * The ContainerManager is a single point of contact to manage your Container
 * resources.  It takes care of creating the other managers that a Container
 * needs to use, as well as initializing the Container.  It is designed to be
 * directly instantiated by whatever class needs to initialize your system.
 *
 * <p>
 *   The ContainerManager provides some constants used in the initial
 *   <code>Parameters</code> passed into the ContainerManager.  The
 *   ContainerManager uses these values to create all the pieces necessary
 *   for the Container. Below is a table that describes what those options
 *   are.
 * </p>
 *
 * <p>You can think of a ContainerManager is a pocket universe for a impl and its
 * components.</p>
 *
 * <p><b>Case 1: Use by a servlet or other "root" entity</b></p>
 *
 * <pre>
 * <code>
 *    FortressConfig config = new FortressConfig();
 *    config.setContainerClass( Thread.currentThread().getContextClassLoader().loadClass( "org.apache.avalon.fortress.test.TestContainer" ) );
 *    config.setContextDirectory( "./" );
 *    config.setWorkDirectory( "./" );
 *    config.setContainerConfiguration( "resource://org.apache.avalon.fortress/test/ContainerProfile.xconf" );
 *    config.setLoggerManagerConfiguration( "resource://org.apache.avalon.fortress/test/ContainerProfile.xlog" );
 *
 *    ContainerManager cm = new DefaultContainerManager( config.getContext() );
 *    ContainerUtil.initialize( cm );
 * </code>
 * </pre>
 *
 * Then, for example, wait for a request and pass it on to the impl:
 *
 * <pre>
 * <code>
 *    TestContainer impl = (TestContainer) cm.getContainer();
 *    impl.handleRequest( ... );
 * </code>
 * </pre>
 *
 * When done, dispose of the managers.
 *
 * <pre>
 * <code>
 *    ContainerUtil.dispose( containerManager );
 *    ContainerUtil.dispose( contextManager );
 * </code>
 * </pre>
 *
 * @author <a href="mailto:dev@avalon.apache.org">The Avalon Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/03/22 12:46:32 $
 * @see ContainerManagerConstants for the contract surrounding the ContainerManager context
 */
public interface ContainerManager
{
    /**
     * Get a reference to the managed Container.  This instance is typically cast to
     * the interface used to interact with the impl.
     */
    Object getContainer();
}
