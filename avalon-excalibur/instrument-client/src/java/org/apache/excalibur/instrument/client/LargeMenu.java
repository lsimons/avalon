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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 * The default JMenu class does not work correctly when the popup menu contains
 *  large numbers of elements.
 *
 * @author <a href="mailto:leif@tanukisoftware.com">Leif Mortenson</a>
 * @version CVS $Revision: 1.1 $ $Date: 2003/11/09 16:36:49 $
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
     *      menu which should be used as the origin
     *      of the <code>JMenu</code>'s popup menu
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
