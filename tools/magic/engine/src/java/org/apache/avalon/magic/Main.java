/*
Copyright 2004 The Apache Software Foundation
Licensed  under the  Apache License,  Version 2.0  (the "License");
you may not use  this file  except in  compliance with the License.
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed  under the  License is distributed on an "AS IS" BASIS,
WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
implied.

See the License for the specific language governing permissions and
limitations under the License.
*/

package org.apache.avalon.magic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main
{
    static private Builder m_Application;

    static public void main( String[] args )
        throws Exception
    {
        long t0 = System.currentTimeMillis();
        try
        {
            File projectDir = getProjectDir();
            process( args, projectDir );
        } finally
        {
            long t1 = System.currentTimeMillis();
            System.out.println( "Build Time: " + (t1 - t0) + " ms." );
        }
    }
    
    static private File getProjectDir()
    {
        String cwd = System.getProperty( "user.dir" );
        File f = new File( cwd );
        return f.getAbsoluteFile();
    }
    
    static private void process( String[] args, File dir )
        throws Exception
    {
        if( args.length == 0 )
        {
            String[] jobs = sequence( dir );
            for( int i = 0 ; i < jobs.length ; i++ )
            {
                doJob( jobs[i], dir );
            }
        }
        else
        {   
            m_Application = new Builder( args, dir );
            m_Application.execute();
        }
    }

    static String[] sequence( File projDir )
        throws Exception
    {
        File sequenceFile = new File( projDir, "build.sequence" );
        if( ! sequenceFile.exists() )
            return new String[0];
        FileReader reader = null;
        BufferedReader br = null;
        
        ArrayList result = new ArrayList();
        try
        {        
            reader = new FileReader( sequenceFile );
            br = new BufferedReader( reader );
            String line;
            while( (line = br.readLine() ) != null )
            {
                result.add( line.trim() );
            }
            String[] retVal = new String[ result.size() ];
            result.toArray( retVal );
            return retVal;
        } finally
        {
            if( reader != null )
                reader.close();
            if( br != null )
                br.close();
        }
    }
    
    static private void doJob( String descriptor, File dir )
        throws Exception
    {
        // Check for empty line.
        if( "".equals( descriptor ) )
            return;
            
        StringTokenizer st = new StringTokenizer( descriptor, " ,", true );
        
        String subdir = st.nextToken();
        ArrayList methods = new ArrayList();
        
        while( st.hasMoreTokens() )
        {
            String method = st.nextToken().trim();
            if( ! "".equals( method ) )
                methods.add( method );
        }
        String[] result = new String[ methods.size() ];
        methods.toArray( result );
        
        // recurse the methods into main() having user.dir set to the
        // requested dir.
        File newProjectDir = new File( dir, subdir );
        process( result, newProjectDir );
    }
} 
