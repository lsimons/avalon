/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.components.kernel.beanshell;

import bsh.Interpreter;
import bsh.EvalError;
import bsh.util.JConsole;
import org.apache.avalon.phoenix.interfaces.Kernel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


/**
 * Class BeanShellGUI
 *
 *
 * @author Paul Hammant <a href="mailto:Paul_Hammant@yahoo.com">Paul_Hammant@yahoo.com</a>
 * @version $Revision: 1.1 $
 */
public class BeanShellGUI extends JPanel implements ActionListener
{

    private JConsole m_jConsole;
    private Interpreter m_interpreter;
    private Thread m_thread;
    private JFrame m_frame;

    /**
     * Construct a BeanShellGUI with a handle on the Kernel.
     */
    public BeanShellGUI(Kernel kernel)
    {

        this.setPreferredSize(new Dimension(600, 480));

        m_jConsole = new JConsole();

        this.setLayout(new BorderLayout());
        this.add(m_jConsole, BorderLayout.CENTER);

        m_interpreter = new Interpreter(m_jConsole);
        try
        {
            m_interpreter.set("phoenix-kernel", kernel);
        }
        catch (EvalError ee)
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

        m_frame = new JFrame("BeanShell - Phoenix management");
        m_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        m_frame.getContentPane().add(this,BorderLayout.CENTER);

        JMenuBar menubar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem mi = new JMenuItem("Close");

        mi.addActionListener(this);
        menu.add(mi);
        menubar.add(menu);

        m_frame.setJMenuBar(menubar);

        m_thread = new Thread(m_interpreter);

        m_thread.start();
        m_frame.setVisible(true);
        m_frame.pack();
    }


    /**
     * Method actionPerformed by the menu options.
     *
     * @param e the action event.
     *
     */
    public void actionPerformed(ActionEvent e)
    {

        String com = e.getActionCommand();

        if (com.equals("Close"))
        {
            m_thread.interrupt();
            m_frame.dispose();
        }
    }

}
