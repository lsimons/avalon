/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.components.kernel.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import org.apache.avalon.phoenix.interfaces.Kernel;

/**
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.6 $
 */
public class BeanShellGUI
    extends JPanel
    implements ActionListener
{
    private final JConsole m_jConsole;

    private final Interpreter m_interpreter;

    private Thread m_thread;

    private JFrame m_frame;

    /**
     * Construct a BeanShellGUI with a handle on the Kernel.
     */
    public BeanShellGUI( final Kernel kernel )
    {
        setPreferredSize( new Dimension( 600, 480 ) );

        m_jConsole = new JConsole();

        this.setLayout( new BorderLayout() );
        this.add( m_jConsole, BorderLayout.CENTER );

        m_interpreter = new Interpreter( m_jConsole );
        try
        {
            m_interpreter.set( "phoenix-kernel", kernel );
        }
        catch( EvalError ee )
        {
            ee.printStackTrace();
        }
    }

    /**
     * Initialize after construction.
     *
     */
    public void init()
    {
        m_frame = new JFrame( "BeanShell - Phoenix management" );
        m_frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        m_frame.getContentPane().add( this, BorderLayout.CENTER );

        final JMenuBar menubar = new JMenuBar();
        final JMenu menu = new JMenu( "File" );
        final JMenuItem mi = new JMenuItem( "Close" );

        mi.addActionListener( this );
        menu.add( mi );
        menubar.add( menu );

        m_frame.setJMenuBar( menubar );

        m_thread = new Thread( m_interpreter );

        m_thread.start();
        m_frame.setVisible( true );
        m_frame.pack();
    }

    /**
     * Method actionPerformed by the menu options.
     *
     * @param event the action event.
     *
     */
    public void actionPerformed( final ActionEvent event )
    {
        final String command = event.getActionCommand();

        if( command.equals( "Close" ) )
        {
            m_thread.interrupt();
            m_frame.dispose();
        }
    }
}
