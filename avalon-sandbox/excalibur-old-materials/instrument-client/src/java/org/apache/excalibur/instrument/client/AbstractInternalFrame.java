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
    public void internalFrameOpened( InternalFrameEvent event )
    {
    }

    public void internalFrameClosing( InternalFrameEvent event )
    {
        // Select the new top frame
        JDesktopPane desktop = m_frame.getDesktopPane();
        JInternalFrame[] frames = desktop.getAllFrames();
        // Find the first frame other than the one being hidden and select and move it to the front
        m_nextFrame = null;
        for( int i = 0; i < frames.length; i++ )
        {
            JInternalFrame frame = frames[ i ];
            if( frame != this )
            {
                m_nextFrame = frame;

                // Break out
                i = frames.length;
            }
        }
    }

    public void internalFrameClosed( InternalFrameEvent event )
    {
        // On closing Swing will bring forward the window at the bottom,
        //	rather than the next window.  So we need to move it back and show the correct one.
        if( m_nextFrame != null )
        {
            // The getSelectedFrame method was added in JDK1.3, so it may not yet exist.
            // Cast this to our workaround DesktopPane to work around this.
            DesktopPane desktop = (DesktopPane)m_frame.getDesktopPane();
            JInternalFrame top = desktop.getSelectedFrame();

            if( top != null )
            {
                if( top != m_nextFrame )
                {
                    try
                    {
                        m_nextFrame.setSelected( true );
                        desktop.moveToFront( m_nextFrame );
                        desktop.moveToBack( top );
                    }
                    catch( java.beans.PropertyVetoException e )
                    {
                    }
                }
            }
        }
    }

    public void internalFrameIconified( InternalFrameEvent event )
    {
    }

    public void internalFrameDeiconified( InternalFrameEvent event )
    {
        // Swing always activates a frame when it is deiconified, but it down't
        //  always move it to the front
        JDesktopPane desktop = m_frame.getDesktopPane();
        desktop.moveToFront( this );
    }

    public void internalFrameActivated( InternalFrameEvent event )
    {
        m_active = true;
        m_frame.setStatusMessage( getTitle() );
    }

    public void internalFrameDeactivated( InternalFrameEvent event )
    {
        m_active = false;
        m_frame.setStatusMessage( "" );
    }
}

