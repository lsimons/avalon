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
package org.apache.avalon.fortress.util.test;

import junit.framework.TestCase;
import org.apache.avalon.fortress.InitializationException;
import org.apache.avalon.fortress.util.CompositeException;

/**
 * CompositeExceptionTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class CompositeExceptionTestCase extends TestCase
{
    private Exception[] m_exceptions;

    public CompositeExceptionTestCase( String name )
    {
        super( name );
    }

    public void setUp()
    {
        m_exceptions = new Exception[2];
        m_exceptions[0] = new RuntimeException("Test1");
        m_exceptions[1] = new RuntimeException("Test2");
    }

    public void testRegularCreation()
    {
        CompositeException exc = new CompositeException( m_exceptions );
        assertNotNull( exc );
        assertNotNull( exc.getMessage() );
        assertTrue( null == exc.getCause() );
        assertNotNull( exc.getExceptions() );

        final StringBuffer msg = new StringBuffer();
        for ( int i = 0; i < m_exceptions.length; i++ )
        {
            msg.append( m_exceptions[i].getMessage() );
        }
        final String message = msg.toString();

        assertEquals( message, exc.getMessage() );

        Exception[] exceptions = exc.getExceptions();
        assertEquals( m_exceptions.length, exceptions.length );

        for (int i = 0; i < exceptions.length; i++)
        {
            assertEquals( m_exceptions[i], exceptions[i]);
        }
    }

    public void testNestedCreation()
    {
        final String message = "Message";
        CompositeException exc = new CompositeException( m_exceptions, message );
        assertNotNull( exc );
        assertNotNull( exc.getMessage() );
        assertTrue( null == exc.getCause() );
        assertNotNull( exc.getExceptions() );

        assertEquals( message, exc.getMessage() );

        Exception[] exceptions = exc.getExceptions();
        assertEquals( m_exceptions.length, exceptions.length );

        for ( int i = 0; i < exceptions.length; i++ )
        {
            assertEquals( m_exceptions[i], exceptions[i] );
        }
    }

    public void testIllegalArgument()
    {
        try
        {
            new CompositeException(null);
            fail("Did not throw an IllegalArgumentException");
        }
        catch(IllegalArgumentException iae)
        {
            // SUCCESS!!
        }
        catch(Exception e)
        {
            fail("Threw the wrong exception: " + e.getClass().getName());
        }

        try
        {
            new CompositeException( new Exception[] {} );
            fail( "Did not throw an IllegalArgumentException" );
        }
        catch ( IllegalArgumentException iae )
        {
            // SUCCESS!!
        }
        catch ( Exception e )
        {
            fail( "Threw the wrong exception: " + e.getClass().getName() );
        }
    }
}