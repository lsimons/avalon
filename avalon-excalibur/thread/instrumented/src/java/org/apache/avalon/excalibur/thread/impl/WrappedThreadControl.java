/*
 * Created on Mar 4, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.excalibur.thread.ThreadControl;

/**
 * @author bloritsch
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WrappedThreadControl implements ThreadControl
{
    private final org.apache.excalibur.thread.ThreadControl m_control;
    
    public WrappedThreadControl(org.apache.excalibur.thread.ThreadControl control)
    {
        m_control = control;
    }
    
    /* (non-Javadoc)
     * @see org.apache.excalibur.thread.ThreadControl#join(long)
     */
    public void join(long milliSeconds)
        throws IllegalStateException, InterruptedException
    {
        m_control.join(milliSeconds);
    }
    /* (non-Javadoc)
     * @see org.apache.excalibur.thread.ThreadControl#interrupt()
     */
    public void interrupt() throws IllegalStateException, SecurityException
    {
        m_control.interrupt();
    }
    /* (non-Javadoc)
     * @see org.apache.excalibur.thread.ThreadControl#isFinished()
     */
    public boolean isFinished()
    {
        return m_control.isFinished();
    }
    /* (non-Javadoc)
     * @see org.apache.excalibur.thread.ThreadControl#getThrowable()
     */
    public Throwable getThrowable()
    {
        return m_control.getThrowable();
    }
}
