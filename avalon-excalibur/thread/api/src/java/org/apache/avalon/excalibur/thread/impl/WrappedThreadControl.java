/* 
 * Copyright 2002-2004 Apache Software Foundation
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
package org.apache.avalon.excalibur.thread.impl;

import org.apache.avalon.excalibur.thread.ThreadControl;

/**
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
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
