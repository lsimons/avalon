/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

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

package org.apache.avalon.activation.appliance.impl;

import org.apache.avalon.activation.appliance.Block;
import org.apache.avalon.composition.model.ContainmentModel;

/**
 * The DefaultBlockThread provides support for the execution of 
 * a block.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:30:31 $
 */
public class BlockThread extends Thread 
{
    //-------------------------------------------------------------------
    // immmutable state
    //-------------------------------------------------------------------

    private final Block m_block;

    //-------------------------------------------------------------------
    // state
    //-------------------------------------------------------------------

    private Throwable m_error;

    private boolean m_started = false;

    private boolean m_stopped = false;

    private boolean m_terminate = false;

    //-------------------------------------------------------------------
    // constructor
    //-------------------------------------------------------------------

   /**
    * Creation of a block thread.
    *
    * @param block the block
    */
    public BlockThread( Block block )
      throws Exception
    {
        if( block == null ) throw new NullPointerException( "block" );

        ContainmentModel model = (ContainmentModel) block.getModel();
        ClassLoader classloader = model.getClassLoaderModel().getClassLoader();
        setContextClassLoader( classloader );
        m_block = block;
    }

   /**
    * Thread execution during which the component managed by 
    * the block will be deployed.
    */
    public void run()
    {
        try
        {
            m_block.deploy();
        } 
        catch( Throwable e )
        {
            m_error = e;
        }
        finally
        {
            m_started = true;
        }

        while( !m_terminate )
        {
            if( null != m_error ) m_terminate = true;

            try
            {
                Thread.sleep( 300 );
            }
            catch( Throwable wakeup )
            {
                // return
            }
        }

        m_block.decommission();
        m_stopped = true;
    }

   /**
    * Set the termination flag to true.
    */
    public void decommission()
    {
        m_terminate = true;
    }

   /**
    * Return the started state of the block thread.
    */
    public boolean started()
    {
        return m_started;
    }

   /**
    * Return the stoppped state of the block thread.
    */
    public boolean stopped()
    {
        return m_stopped;
    }

   /**
    * Returns an error contition that may occur during startup.
    * @return the error or null if no error encountered
    */
    public Throwable getError()
    {
        return m_error;
    }
}
