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
package org.apache.avalon.fortress.examples.viewer;

import org.apache.avalon.fortress.impl.DefaultContainerManager;

/**
 * Fortress container example allowing you to perform lookups on components
 * via a simple swing gui.
 *
 * @author <a href="mailto:crafterm@apache.org">Marcus Crafter</a>
 * @version $Id: Main.java,v 1.7 2003/04/11 07:36:21 donaldp Exp $
 */
public final class Main
{
    /**
     * @param args a <code>String[]</code> array of command line arguments
     * @exception java.lang.Exception if an error occurs
     */
    public static final void main( String[] args )
        throws Exception
    {
        org.apache.avalon.fortress.ContainerManager cm = null;

        try
        {
            final org.apache.avalon.fortress.util.FortressConfig config = new org.apache.avalon.fortress.util.FortressConfig();
            config.setContainerClass( "org.apache.avalon.fortress.examples.viewer.ComponentViewer" );
            config.setContextDirectory( "./" );
            config.setWorkDirectory( "./" );
            config.setContainerConfiguration( "resource://org/apache/avalon/fortress/examples/viewer/ComponentViewer.xconf" );
            config.setLoggerManagerConfiguration( "resource://org/apache/avalon/fortress/examples/viewer/ComponentViewer.xlog" );
            config.setRoleManagerConfiguration( "resource://org/apache/avalon/fortress/examples/viewer/ComponentViewer.roles" );
            config.setInstrumentManagerConfiguration( "resource://org/apache/avalon/fortress/examples/viewer/ComponentViewer.instruments" );

            cm = new DefaultContainerManager( config.getContext() );
            org.apache.avalon.framework.container.ContainerUtil.initialize( cm );

            ( (org.apache.avalon.fortress.examples.viewer.ComponentViewer)cm.getContainer() ).run();
        }
        catch( org.apache.avalon.framework.CascadingException e )
        {
            e.printStackTrace();

            Throwable t = e.getCause();

            while( t != null )
            {
                t.printStackTrace();

                if( t instanceof org.apache.avalon.framework.CascadingException )
                    t = ( (org.apache.avalon.framework.CascadingException)t ).getCause();
                else
                    t = null;
            }
        }
        finally
        {
            org.apache.avalon.framework.container.ContainerUtil.dispose( cm );
        }
    }
}

