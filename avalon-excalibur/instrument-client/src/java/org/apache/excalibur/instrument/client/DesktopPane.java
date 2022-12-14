/* 
 * Copyright 2002-2004 The Apache Software Foundation
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

package org.apache.excalibur.instrument.client;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 * This class was build to make it possible to use some of the JDK1.3
 *  features work in 1.2.2. Taken from JDK1.3 source to make it work.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:24 $
 * @since 4.1
 */
class DesktopPane extends JDesktopPane
{
    private transient JInternalFrame selectedFrame = null;

    /**
     * Used to indicate you wish to see the entire contents of the item being
     * dragged inside the desktop pane.
     *
     * @see #OUTLINE_DRAG_MODE
     * @see #setDragMode
     */
    public static int LIVE_DRAG_MODE = 0;

    /**
     * Used to indicate you wish to see only an outline of the item being
     * dragged inside the desktop pane.
     *
     * @see #LIVE_DRAG_MODE
     * @see #setDragMode
     */
    public static int OUTLINE_DRAG_MODE = 1;

    private int dragMode = LIVE_DRAG_MODE;

    /**
     * Set the "dragging style" used by the desktop pane.  You may want to change
     * to one mode or another for performance or aesthetic reasons.
     *
     * @param dragMode the style of drag to use for items in the Desktop
     *
     * @beaninfo
     *        bound: true
     *  description: Dragging style for internal frame children.
     *         enum: LIVE_DRAG_MODE JDesktopPane.LIVE_DRAG_MODE
     *               OUTLINE_DRAG_MODE JDesktopPane.OUTLINE_DRAG_MODE
     */
    public void setDragMode( int dragMode )
    {
        /* if (!(dragMode == LIVE_DRAG_MODE || dragMode == OUTLINE_DRAG_MODE)) {
        throw new IllegalArgumentException("Not a valid drag mode");
        }*/
        firePropertyChange( "dragMode", this.dragMode, dragMode );
        this.dragMode = dragMode;
    }

    /**
     * Get the current "dragging style" used by the desktop pane.
     * @see #setDragMode
     */
    public int getDragMode()
    {
        return dragMode;
    }

    /** return the currently active JInternalFrame in this JDesktopPane, or
     * null if no JInternalFrame is currently active.
     *
     * @return the currently active JInternalFrame or null
     * @since 1.3
     */
    public JInternalFrame getSelectedFrame()
    {
        return selectedFrame;
    }

    /** set the currently active JInternalFrame in this JDesktopPane.
     *
     * @param f The internal frame that's currently selected
     * @since 1.3
     */

    public void setSelectedFrame( JInternalFrame f )
    {
        selectedFrame = f;
    }
}
