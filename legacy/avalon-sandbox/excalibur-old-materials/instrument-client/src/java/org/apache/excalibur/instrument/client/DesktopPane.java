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
package org.apache.excalibur.instrument.client;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/**
 * This class was build to make it possible to use some of the JDK1.3
 *  features work in 1.2.2. Taken from JDK1.3 source to make it work.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.5 $ $Date: 2003/03/22 12:46:36 $
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
