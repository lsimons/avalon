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
package org.apache.avalon.fortress.impl.extensions.test;

import junit.framework.TestCase;
import junit.framework.Assert;
import org.apache.avalon.lifecycle.Creator;
import org.apache.avalon.fortress.impl.extensions.InstrumentableCreator;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.excalibur.instrument.Instrumentable;
import org.apache.excalibur.instrument.InstrumentManager;
import org.apache.excalibur.instrument.Instrument;
import org.apache.excalibur.instrument.InstrumentManageable;

/**
 * InstrumentableCreatorTestCase does XYZ
 *
 * @author <a href="bloritsch.at.apache.org">Berin Loritsch</a>
 * @version CVS $ Revision: 1.1 $
 */
public class InstrumentableCreatorTestCase extends TestCase
{
    private Instrumentable m_instrumentable;
    private DefaultContext m_context;
    private InstrumentManager m_instrumentManager;
    private boolean m_isActive = false;

    public InstrumentableCreatorTestCase( String name )
    {
        super( name );
    }

    public void setUp()
    {
        m_instrumentable = new TestInstrumentable();
        m_instrumentManager = new TestInstrumentManager();
        m_context = new DefaultContext();
        m_context.put("component.name", "component1");
        m_context.makeReadOnly();
    }

    public void testNoInstrumentManager() throws Exception
    {
        Creator creator = new InstrumentableCreator(null);

        creator.create(m_instrumentable, m_context);
        creator.destroy( m_instrumentable, m_context );
    }

    public void testInstrumentManager() throws Exception
    {
        Creator creator = new InstrumentableCreator( m_instrumentManager );
        m_isActive = true;

        creator.create( m_instrumentable, m_context );
        creator.destroy( m_instrumentable, m_context );
    }

    class TestInstrumentManager implements InstrumentManager
    {
        public void registerInstrumentable( Instrumentable instrumentable, String instrumentableName ) throws Exception
        {
            String name = instrumentable.getInstrumentableName();
            assertNotNull(name);

            name = "registered:" + instrumentableName;
            instrumentable.setInstrumentableName(name);
            assertEquals(name, instrumentable.getInstrumentableName());

            assertNotNull(instrumentable.getChildInstrumentables());
            assertNotNull(instrumentable.getInstruments());
        }
    }

    class TestInstrumentable implements Instrumentable, InstrumentManageable
    {
        private static final String DEFAULT_NAME = "test";
        private String m_name = DEFAULT_NAME;
        private String m_assigned;

        public void setInstrumentableName( String name )
        {
            assertTrue( m_isActive );
            assertNotNull(name);
            m_name = name;
            m_assigned = m_name;
        }

        public String getInstrumentableName()
        {
            assertTrue( m_isActive );
            assertNotNull(m_name);

            if ( null == m_assigned )
            {
                assertEquals(DEFAULT_NAME, m_name);
            }
            else
            {
                assertEquals(m_assigned, m_name);
            }

            return m_name;
        }

        public Instrument[] getInstruments()
        {
            assertTrue(m_isActive);
            return new Instrument[0];
        }

        public Instrumentable[] getChildInstrumentables()
        {
            assertTrue( m_isActive );
            return new Instrumentable[0];
        }

        public void setInstrumentManager( InstrumentManager instrumentManager )
        {
            assertTrue( m_isActive );
        }
    }
}