/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.log.test;

import junit.framework.TestCase;
import org.apache.log.Hierarchy;
import org.apache.log.Logger;
import org.apache.log.LogEvent;
import org.apache.log.LogTarget;
import org.apache.log.output.AbstractWrappingTarget;
import org.apache.log.util.Closeable;

/**
 * Test suite for wrapping targets.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public final class WrappingTargetTestCase
    extends TestCase
{
    
    static private class DummyTarget implements LogTarget
    {
        private boolean closed = false;
        
        public void close()
        {
            closed = true;
        }
        
        public boolean isClosed()
        {
            return closed;
        }
        
        public void processEvent( LogEvent event )
        {
            // Do nothing
        }
    }
    
    static private class CloseableDummyTarget extends DummyTarget implements Closeable
    {
    }
    
    static private class DummyTargetWrapper extends AbstractWrappingTarget
    {
        public DummyTargetWrapper( final LogTarget logTarget )
        {
            super( logTarget );
        }
        
        public DummyTargetWrapper( final LogTarget logTarget, final boolean closeWrappedTarget )
        {
            super( logTarget, closeWrappedTarget );
        }
        
        public void doProcessEvent( LogEvent event )
        {
            // Do nothing
        }
    }
    
    public void testNonCloseable()
    {
        DummyTarget dummyTargetNonClose = new DummyTarget();
        DummyTarget dummyTargetNonClose2 = new DummyTarget();
        DummyTarget dummyTargetClose = new DummyTarget();
        
        DummyTargetWrapper wrapperNonClose = new DummyTargetWrapper(dummyTargetNonClose, false);
        DummyTargetWrapper wrapperNonClose2 = new DummyTargetWrapper(dummyTargetNonClose2); // should default to false
        DummyTargetWrapper wrapperClose = new DummyTargetWrapper(dummyTargetClose, true);
        
        assertTrue( !dummyTargetNonClose.isClosed() );
        assertTrue( !dummyTargetNonClose2.isClosed() );
        assertTrue( !dummyTargetClose.isClosed() );
        
        wrapperNonClose.close();
        wrapperNonClose2.close();
        wrapperClose.close();
        
        // The close() should have no effect, since neither target implements closeable.
        
        assertTrue( !dummyTargetNonClose.isClosed() );
        assertTrue( !dummyTargetNonClose2.isClosed() );
        assertTrue( !dummyTargetClose.isClosed() );        
    }
    
    public void testCloseable()
    {
        DummyTarget dummyTargetNonClose = new CloseableDummyTarget();
        DummyTarget dummyTargetNonClose2 = new CloseableDummyTarget();
        DummyTarget dummyTargetClose = new CloseableDummyTarget();
        
        DummyTargetWrapper wrapperNonClose = new DummyTargetWrapper(dummyTargetNonClose, false);
        DummyTargetWrapper wrapperNonClose2 = new DummyTargetWrapper(dummyTargetNonClose2); // should default to false
        DummyTargetWrapper wrapperClose = new DummyTargetWrapper(dummyTargetClose, true);
        
        assertTrue( !dummyTargetNonClose.isClosed() );
        assertTrue( !dummyTargetNonClose2.isClosed() );
        assertTrue( !dummyTargetClose.isClosed() );
        
        wrapperNonClose.close();
        wrapperNonClose2.close();
        wrapperClose.close();
        
        // Only the target that was wrapped with the closeWrapped parameter
        // set to true should be closed.
        
        assertTrue( !dummyTargetNonClose.isClosed() );
        assertTrue( !dummyTargetNonClose2.isClosed() );
        assertTrue( dummyTargetClose.isClosed() );        
    }
}
