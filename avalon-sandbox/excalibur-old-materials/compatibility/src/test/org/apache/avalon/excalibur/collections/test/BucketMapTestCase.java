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
public final class BucketMapTestCase
    extends TestCase
{

    private static class TestInteger
    {
        int i;

        public TestInteger( int i )
        {
            this.i = i;
        }

        public boolean equals( Object o )
        {
            return this == o;
        }

        public int hashCode()
        {
            return i;
        }

        public String toString()
        {
            return "TestInteger " + i + " @" + System.identityHashCode( this );
        }
    }

    private static final TestInteger VAL1 = new TestInteger( 5 );
    private static final TestInteger VAL2 = new TestInteger( 5 );
    private static final TestInteger VAL3 = new TestInteger( 5 );
    private static final TestInteger VAL4 = new TestInteger( 5 );
    private static final TestInteger VAL5 = new TestInteger( 5 );
    private static final TestInteger VAL6 = new TestInteger( 5 );
    private static final TestInteger VAL7 = new TestInteger( 5 );

    public BucketMapTestCase()
    {
        this( "Bucket Map Test Case" );
    }

    public BucketMapTestCase( String name )
    {
        super( name );
    }

    public void testBucket()
    {
        final BucketMap map = new BucketMap();

        map.put( VAL1, VAL1 );
        assertTrue( map.size() == 1 );
        assertTrue( VAL1 == map.get( VAL1 ) );

        map.put( VAL2, VAL2 );
        assertTrue( map.size() == 2 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );

        map.put( VAL3, VAL3 );
        assertTrue( map.size() == 3 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );
        assertTrue( VAL3 == map.get( VAL3 ) );

        map.put( VAL4, VAL4 );
        assertTrue( map.size() == 4 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );
        assertTrue( VAL3 == map.get( VAL3 ) );
        assertTrue( VAL4 == map.get( VAL4 ) );

        map.put( VAL5, VAL5 );
        assertTrue( map.size() == 5 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );
        assertTrue( VAL3 == map.get( VAL3 ) );
        assertTrue( VAL4 == map.get( VAL4 ) );
        assertTrue( VAL5 == map.get( VAL5 ) );

        map.put( VAL6, VAL6 );
        assertTrue( map.size() == 6 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );
        assertTrue( VAL3 == map.get( VAL3 ) );
        assertTrue( VAL4 == map.get( VAL4 ) );
        assertTrue( VAL5 == map.get( VAL5 ) );
        assertTrue( VAL6 == map.get( VAL6 ) );

        map.put( VAL7, VAL7 );
        assertTrue( map.size() == 7 );
        assertTrue( VAL1 == map.get( VAL1 ) );
        assertTrue( VAL2 == map.get( VAL2 ) );
        assertTrue( VAL3 == map.get( VAL3 ) );
        assertTrue( VAL4 == map.get( VAL4 ) );
        assertTrue( VAL5 == map.get( VAL5 ) );
        assertTrue( VAL6 == map.get( VAL6 ) );
        assertTrue( VAL7 == map.get( VAL7 ) );

        map.remove( VAL1 );
        assertTrue( map.size() == 6 );
        assertTrue( map.get( VAL1 ) == null );

        map.remove( VAL7 );
        assertTrue( map.size() == 5 );
        assertTrue( map.get( VAL7 ) == null );

        map.remove( VAL4 );
        assertTrue( map.size() == 4 );
        assertTrue( map.get( VAL4 ) == null );
    }

    public void testReplace1()
    {
        final BucketMap map = new BucketMap();

        map.put( VAL1, VAL1 );
        map.put( VAL1, VAL2 );
        assertTrue( map.size() == 1 );
        assertTrue( map.get( VAL1 ) == VAL2 );
    }

    public void testReplace2()
    {
        final BucketMap map = new BucketMap();

        map.put( VAL1, VAL1 );
        map.put( VAL2, VAL2 );
        map.put( VAL1, VAL3 );
        assertTrue( map.size() == 2 );
        assertTrue( map.get( VAL1 ) == VAL3 );
    }

    public void testReplace3()
    {
        final BucketMap map = new BucketMap();

        map.put( VAL1, VAL1 );
        map.put( VAL2, VAL2 );
        map.put( VAL3, VAL3 );
        map.put( VAL3, VAL4 );
        assertTrue( map.size() == 3 );
        assertTrue( map.get( VAL3 ) == VAL4 );
    }

    public void testReplace4()
    {
        final BucketMap map = new BucketMap();

        map.put( VAL1, VAL1 );
        map.put( VAL2, VAL2 );
        map.put( VAL3, VAL3 );
        map.put( VAL4, VAL4 );
        map.put( VAL3, VAL5 );
        assertTrue( map.size() == 4 );
        assertTrue( map.get( VAL3 ) == VAL5 );
    }
}
