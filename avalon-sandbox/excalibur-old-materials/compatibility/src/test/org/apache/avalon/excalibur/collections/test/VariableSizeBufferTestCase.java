package org.apache.avalon.excalibur.collections.test;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.collections.VariableSizeBuffer;

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

