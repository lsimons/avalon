/* 
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.avalon.excalibur.collections.test;

import org.apache.avalon.excalibur.collections.VariableSizeBuffer;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public class VariableSizeBufferTestCase
        extends TestCase
{
    public VariableSizeBufferTestCase( final String name )
    {
        super( name );
    }

    /**
     * Triggers a situation when m_tail < m_head during buffer
     * extension, so copying will be wrapping around the end of
     * the buffer.
     */
    public void testGrowthWrapAround()
            throws Exception
    {
        VariableSizeBuffer buf = new VariableSizeBuffer( 1 );
        buf.add( "1" );
        assertEquals( "Got 1 that just added", "1", buf.remove() );
        buf.add( "2" );
        buf.add( "3" );
        assertEquals( "After 3 puts and 1 remove buffer size must be 2",
                2, buf.size() );
        assertEquals( "Got 2", "2", buf.remove() );
        assertEquals( "Got 3", "3", buf.remove() );
        assertTrue( "Buffer is empty", buf.isEmpty() );
    }

    /**
     * Extension is done when m_head = 0 and m_tail = m_buffer.length - 1.
     */
    public void testGrowthCopyStartToEnd()
    {
        VariableSizeBuffer buf = new VariableSizeBuffer( 1 );
        buf.add( "1" );
        buf.add( "2" );
        assertEquals( "After 2 puts buffer size must be 2",
                2, buf.size() );
        assertEquals( "Got 1", "1", buf.remove() );
        assertEquals( "Got 2", "2", buf.remove() );
        assertTrue( "Buffer is empty", buf.isEmpty() );
    }
}

