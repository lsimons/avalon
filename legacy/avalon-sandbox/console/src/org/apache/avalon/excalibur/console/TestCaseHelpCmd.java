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

public class TestCaseHelpCmd extends TestCase
{
    public TestCaseHelpCmd( String name )
    {
        super( name );
    }

    public void testHelp()
        throws Exception
    {
        CharArrayReader car = new CharArrayReader( new char[0] );
        BufferedReader in = new BufferedReader( car );

        CharArrayWriter caw = new CharArrayWriter();
        BufferedWriter out = new BufferedWriter( caw );

        ConsoleCommand cc = new HelpCmd();

        cc.execute( in, out, new String[0] );

        String result = caw.toString();

        assertEquals( m_Expected, result );
    }


    static private final String m_Expected =
    "\n" +
    "Available commands:\n" +
    "===================\n" +
    "dispose\n" +
    "echo\n" +
    "exit\n" +
    "help\n" +
    "list\n" +
    "load\n" +
    "login\n" +
    "perf\n" +
    "report\n" +
    "shutdown\n" +
    "start\n" +
    "stop\n" +
    "threads\n" +
    "\n";
}
