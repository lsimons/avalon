/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1997-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Avalon", "Phoenix" and "Apache Software Foundation"
    must  not be  used to  endorse or  promote products derived  from this
    software without prior written permission. For written permission, please
    contact apache@apache.org.

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

package org.apache.avalon.phoenix.tools.punit.test;

import org.apache.avalon.phoenix.tools.punit.PUnitTestCase;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.configuration.Configuration;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class PUnitTestCaseTestCase extends PUnitTestCase
{

    DefaultConfigurationBuilder m_defaultConfigurationBuilder = new DefaultConfigurationBuilder();


    public PUnitTestCaseTestCase(String name)
    {
        super(name);
    }

    public void testBasicBlock() throws Exception
    {
        TestBlock block = new TestBlock();
        Configuration configuration = m_defaultConfigurationBuilder.build(
                new InputSource(new StringReader("<hi>Hi</hi>")));
        addBlock("bl","block", block, configuration);
        startup();
        // check lifecycle run thru
        assertNotNull("Configuration null", block.m_configuration);
        assertNotNull("Context null", block.m_context);
        assertNotNull("Logger null", block.m_logger);
        assertNotNull("ServiceManager null", block.m_serviceManager);
        assertTrue("Not Initialized", block.m_initialized);
        // check lifecycle events logged
        assertTrue("Service Not logged", super.logHasEntry("I:service"));
        assertTrue("Initialize Not logged", super.logHasEntry("W:initialize"));
        assertTrue("Contextualize Not logged", super.logHasEntry("E:contextualize"));
        assertTrue("Configure Not logged", super.logHasEntry("F:configure"));
        shutdown();
    }

}
