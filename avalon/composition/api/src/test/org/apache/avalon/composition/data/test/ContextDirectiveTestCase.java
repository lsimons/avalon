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
package org.apache.avalon.composition.data.test;

import junit.framework.TestCase;
import org.apache.avalon.composition.data.ImportDirective;
import org.apache.avalon.composition.data.EntryDirective;
import org.apache.avalon.composition.data.ContextDirective;

/**
 * ContextDirectiveTestCase does XYZ
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $ Revision: 1.1 $
 */
public class ContextDirectiveTestCase extends TestCase
{
    private String m_source = "../xxx";

    public ContextDirectiveTestCase( String name )
    {
        super( name );
    }

    public void testConstructor()
    {
        try
        {
            new ContextDirective( null );
        }
        catch(NullPointerException npe)
        {
            fail("NullPointerException should not be thrown - null indicates default");
        }

        try
        {
            new ContextDirective( null, new EntryDirective[0] );
        }
        catch(NullPointerException npe)
        {
            fail("NullPointerException should not be thrown - null indicates default");
        }

        try
        {
            new ContextDirective( null, null );
        }
        catch ( NullPointerException npe )
        {
            fail("NullPointerException should not be thrown - null indicated default");
        }
    }

    public void testContextDirective()
    {
        EntryDirective[] entries = new EntryDirective[0];
        ContextDirective cd = 
          new ContextDirective( getClass().getName(), entries, m_source );

        assertEquals( "classname", getClass().getName(), cd.getClassname());
        assertEquals( "source", m_source, cd.getSource());
        assertEquals( "entries", entries, cd.getEntryDirectives());
        assertEquals( "length", entries.length, cd.getEntryDirectives().length);
    }

    public void testGetEntry()
    {
        String key = "key";
        String val = "val";
        ImportDirective imp = new ImportDirective( key, "xxx" );
        EntryDirective[] entries = 
          new EntryDirective[]{ imp };
        ContextDirective cd = new ContextDirective( entries );

        assertNull( cd.getClassname() );
        assertEquals( entries, cd.getEntryDirectives() );
        assertEquals( entries.length, cd.getEntryDirectives().length );
        assertEquals( entries[0], cd.getEntryDirective( key ) );
        assertNull( cd.getEntryDirective( val ) );
    }
}
