/*
 * Copyright 2004 Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.examples.fortress;

import org.apache.avalon.fortress.impl.DefaultContainerManager;
import org.apache.avalon.fortress.util.FortressConfig;
import org.apache.avalon.fortress.ContainerManager;
import org.apache.avalon.framework.container.ContainerUtil;

/**
 * Fortress container example.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: FortressSwingRunner.java,v 1.1 2004/02/29 08:41:41 farra Exp $
 */
public final class FortressSwingRunner
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

        String configDir = "src/conf";

        // Set up all the preferences for Fortress
        final FortressConfig config = new FortressConfig();
        config.setContainerClass( SwingContainer.class );
        config.setContainerConfiguration( configDir+"/system.xconf" );
        config.setRoleManagerConfiguration( configDir+"/system.roles" );
        config.setLoggerManagerConfiguration( configDir+"/logkit.xconf");

        // Get the root container initialized
        ContainerManager cm = new DefaultContainerManager( config.getContext() );
        ContainerUtil.initialize( cm );

        /* Special containers such as Swing applications run in a different
         * thread in the background.  This is only one method of handling
         * a Swing based container.  Another alternative is to have the root
         * JFrame/JWindow/JDialog at this level, and hand a reference of the
         * container or its ServiceManager to the Swing class.  That will allow
         * you to defer proper shutdown of Fortress resources when your
         * application is closed.
         */
        ( (SwingContainer)cm.getContainer() ).run();

        // Properly clean up when we are done
        ContainerUtil.dispose( cm );
    }
}

