/*
 * Copyright 1997-2004 Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.excalibur.console;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.CharArrayReader;

import junit.framework.TestCase;

public class TestCaseEchoCmd extends TestCase
{
    public TestCaseEchoCmd( String name )
    {
        super( name );
    }

    public void testEcho()
        throws Exception
    {
        CharArrayReader car = new CharArrayReader( new char[0] );
        BufferedReader in = new BufferedReader( car );

        CharArrayWriter caw = new CharArrayWriter();
        BufferedWriter out = new BufferedWriter( caw );

        ConsoleCommand cc = new EchoCmd();

        String[] arguments = new String[]
        {
            "abc",
            "def", 
            "ghi",
            "I think, therefor I am...... I think"
        };

        cc.execute( in, out, arguments );

        String result = caw.toString();

        assertEquals( m_Expected, result );
    }


    static private final String m_Expected =
    "\n" +
    "echo:  abc def ghi I think, therefor I am...... I think \n";
}

