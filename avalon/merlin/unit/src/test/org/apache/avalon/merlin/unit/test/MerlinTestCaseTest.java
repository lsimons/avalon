/* 
 * Copyright 2004 Apache Software Foundation
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

package org.apache.avalon.merlin.unit.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Enumeration;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.apache.avalon.repository.Artifact;
import org.apache.avalon.repository.provider.Builder;
import org.apache.avalon.repository.provider.InitialContext;
import org.apache.avalon.repository.provider.Factory;
import org.apache.avalon.repository.RepositoryException;
import org.apache.avalon.repository.main.DefaultInitialContext;
import org.apache.avalon.repository.main.DefaultBuilder;

import org.apache.avalon.util.env.Env;
import org.apache.avalon.util.exception.ExceptionHelper;

import org.apache.avalon.merlin.unit.AbstractMerlinTestCase;

/**
 * Test case that usages the repository builder to deploy the 
 * Merlin default application factory.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.3 $
 */
public class MerlinTestCaseTest extends AbstractMerlinTestCase
{
    //----------------------------------------------------------
    // constructor
    //----------------------------------------------------------

    /**
     * Constructor for MerlinEmbeddedTest.
     * @param name the name of the testcase
     */
    public MerlinTestCaseTest( String name )
    {
        super( name );
    }

    //----------------------------------------------------------
    // testcase
    //----------------------------------------------------------

    public void testHelloAquisition() throws Exception
    {
        Object hello = super.resolve( "/tutorial/hello" );
        assertNotNull( "hello", hello );
    }

    public void testHelloAquisitionAgain() throws Exception
    {
        Object hello = super.resolve( "/tutorial/hello" );
        assertNotNull( "hello-2", hello );
    }

}
