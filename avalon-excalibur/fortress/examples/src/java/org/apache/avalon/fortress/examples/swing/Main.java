/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) @year@ The Apache Software Foundation. All rights reserved.

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
package org.apache.avalon.fortress.examples.swing;

import org.apache.avalon.fortress.impl.DefaultContainerManager;

/**
 * Fortress container example.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version $Id: Main.java,v 1.2 2003/02/25 16:28:46 bloritsch Exp $
 */
public final class Main
{
    /**
     * Start the show. Creates a <code>SwingContainer</code>.
     *
     * @param args a <code>String[]</code> array of command line arguments
     * @exception java.lang.Exception if an error occurs
     */
    public static void main( String[] args )
        throws Exception
    {
        // Set up all the preferences for Fortress
        final org.apache.avalon.fortress.util.FortressConfig config = new org.apache.avalon.fortress.util.FortressConfig();
        config.setContainerClass( "org.apache.avalon.fortress.examples.swing.SwingContainer" );
        config.setContextDirectory( "./" );
        config.setWorkDirectory( "./" );
        config.setContainerConfiguration( "resource://org/apache/avalon/fortress/examples/swing/SwingContainer.xconf" );
        config.setLoggerManagerConfiguration( "resource://org/apache/avalon/fortress/examples/swing/SwingContainer.xlog" );
        config.setRoleManagerConfiguration( "resource://org/apache/avalon/fortress/examples/swing/SwingContainer.roles" );
        config.setInstrumentManagerConfiguration( "resource://org/apache/avalon/fortress/examples/swing/SwingContainer.instruments" );

        // Get the root container initialized
        org.apache.avalon.fortress.ContainerManager cm = new DefaultContainerManager( config.getContext() );
        org.apache.avalon.framework.container.ContainerUtil.initialize( cm );

        /* Special containers such as Swing applications run in a different
         * thread in the background.  This is only one method of handling
         * a Swing based container.  Another alternative is to have the root
         * JFrame/JWindow/JDialog at this level, and hand a reference of the
         * container or its ServiceManager to the Swing class.  That will allow
         * you to defer proper shutdown of Fortress resources when your
         * application is closed.
         */
        ((SwingContainer)cm.getContainer()).run();

        // Properly clean up when we are done
        org.apache.avalon.framework.container.ContainerUtil.dispose( cm );
    }
}

