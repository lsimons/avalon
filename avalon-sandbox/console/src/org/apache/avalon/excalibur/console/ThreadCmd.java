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

import java.io.BufferedReader;
import java.io.BufferedWriter;

public class ThreadCmd
    implements ConsoleCommand
{
    public void execute( BufferedReader input, BufferedWriter output, String[] arguments )
        throws Exception
    {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        ThreadGroup root = tg;
        while( tg != null )
        {
            root = tg;
            tg = tg.getParent();
        }
        int size = root.activeCount();
        Thread[] threads = new Thread[size+10];
        int created = root.enumerate( threads, true );
        for( int i=0 ; i < created ; i++ )
        {
            output.write( threads[i].toString() );
            output.newLine();
        }
        output.flush();
    }
}
