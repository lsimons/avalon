/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 * This class was build to make it possible to use some of the JDK1.3
 *  features work in 1.2.2. Taken from JDK1.3 source to make it work.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/14 14:58:22 $
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
