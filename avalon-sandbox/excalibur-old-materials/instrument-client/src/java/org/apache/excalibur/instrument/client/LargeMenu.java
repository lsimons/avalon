/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.instrument.client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * The default JMenu class does not work correctly when the popup menu contains
 *  large numbers of elements.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/08/14 14:58:22 $
 * @since 4.1
 */
public class LargeMenu
    extends JMenu
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Constructs a new <code>JMenu</code> with no text.
     */
    public LargeMenu()
    {
        super();
    }

    /**
     * Constructs a new <code>JMenu</code> with the supplied string
     * as its text.
     *
     * @param s  the text for the menu label
     */
    public LargeMenu( String s )
    {
        super( s );
    }

    /**
     * Constructs a menu whose properties are taken from the
     * <code>Action</code> supplied.
     * @param a an <code>Action</code>
     */
    public LargeMenu( Action a )
    {
        super( a );
    }

    /**
     * Constructs a new <code>JMenu</code> with the supplied string as
     * its text and specified as a tear-off menu or not.
     *
     * @param s the text for the menu label
     * @param b can the menu be torn off (not yet implemented)
     */
    public LargeMenu( String s, boolean b )
    {
        super( s, b );
    }

    /*---------------------------------------------------------------
     * JMenu Methods
     *-------------------------------------------------------------*/
    /**
     * Computes the origin for the <code>JMenu</code>'s popup menu.
     * <p>
     * Code is copied from JDK1.3 source, but has been patched.
     *
     * @return a <code>Point</code> in the coordinate space of the
     *		menu which should be used as the origin
     * 		of the <code>JMenu</code>'s popup menu
     */
    protected Point getPopupMenuOrigin()
    {
        int x = 0;
        int y = 0;
        JPopupMenu pm = getPopupMenu();
        // Figure out the sizes needed to caclulate the menu position
        Dimension screenSize =Toolkit.getDefaultToolkit().getScreenSize();
        Dimension s = getSize();
        Dimension pmSize = pm.getSize();
        // For the first time the menu is popped up,
        // the size has not yet been initiated
        if (pmSize.width==0)
        {
            pmSize = pm.getPreferredSize();
        }
        Point position = getLocationOnScreen();

        Container parent = getParent();
        if (parent instanceof JPopupMenu)
        {
            // We are a submenu (pull-right)

            // Can not call SwingUtilities.isLeftToRight(this) from here, so assume true.
            // First determine x:
            if (position.x+s.width + pmSize.width < screenSize.width)
            {
                x = s.width;         // Prefer placement to the right
            }
            else
            {
                x = 0-pmSize.width;  // Otherwise place to the left
            }

            // Then the y:
            if (position.y+pmSize.height < screenSize.height)
            {
                y = 0;                       // Prefer dropping down
            }
            else
            {
                // ****************
                // This code was patched.
                // ****************
                // Old Code:
                // y = s.height-pmSize.height;  // Otherwise drop 'up'

                // New Code:
                if ( position.y + s.height - pmSize.height >= 0 )
                {
                    // Fits in the screen when dripped up.
                    y = s.height - pmSize.height;
                }
                else
                {
                    // Does not fit, so show it starting at the top of the screen.
                    // This is an offset.
                    y = 0 - position.y;
                }
            }
        } else {
            // We are a toplevel menu (pull-down)

            // Can not call SwingUtilities.isLeftToRight(this) from here, so assume true.
            // First determine the x:
            if (position.x+pmSize.width < screenSize.width) {
                x = 0;                     // Prefer extending to right
            } else {
                x = s.width-pmSize.width;  // Otherwise extend to left
            }

            // Then the y:
            if (position.y+s.height+pmSize.height < screenSize.height) {
                y = s.height;          // Prefer dropping down
            } else {
                // ****************
                // This code was patched.
                // ****************
                // Old Code:
                //y = 0-pmSize.height;   // Otherwise drop 'up'

                // New Code:
                if ( position.y - pmSize.height >= 0 )
                {
                    // Fits in the screen when dripped up.
                    y = 0 - pmSize.height;
                }
                else
                {
                    // Does not fit, so show it starting at the top of the screen.
                    // This is an offset.
                    y = 0 - position.y;
                }
            }
        }
        return new Point(x,y);
    }
}
