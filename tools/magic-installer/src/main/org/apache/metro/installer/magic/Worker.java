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

package org.apache.metro.installer.magic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.Enumeration;

import java.util.jar.JarFile;
import java.util.jar.JarEntry;

public class Worker
{
    private static final int BUFFER_SIZE = 10000;
    
    private File m_MagicHome;
    private File m_AntLibDir;
    
    public Worker( File magicHome, File antLib )
    {
        m_MagicHome = magicHome;
        m_AntLibDir = antLib;
    }
    
    public void start( ProgressIndicator indicator )
        throws Exception
    {
        /* Not sure what to do with these yet.
        File md5File = download( "http://www.dpml.net/avalon/tools/bars/avalon-tools-magic.bar.md5" );
        File ascFile = download( "http://www.dpml.net/avalon/tools/bars/avalon-tools-magic.bar.asc" );
        */
        
        String url = "http://www.dpml.net/avalon/tools/bars/avalon-tools-magic.bar";
        File barFile = download( url, indicator );
        unjar( barFile, m_MagicHome, indicator );
        
        File toolsJar = new File( m_MagicHome, "jars/avalon-tools-magic.jar" );
        
        copy( toolsJar, m_AntLibDir, indicator );
        indicator.message( "Cleaning up." );
        File jarsDir = toolsJar.getParentFile();
        toolsJar.delete();
        jarsDir.delete();
    }
    
    private void unjar( File barFile, File toDir, ProgressIndicator indicator )
        throws IOException
    {
        indicator.message( "Unzipping " + barFile );
        indicator.start();
        JarFile jar = new JarFile( barFile );
        Enumeration entries = jar.entries();
        while( entries.hasMoreElements() )
        {
            JarEntry entry = (JarEntry) entries.nextElement();
            String name = entry.getName();
            InputStream in = jar.getInputStream( entry );
            File file = new File( toDir, name );
            FileOutputStream out = new FileOutputStream( file );
            long size = entry.getSize();
            copy( in, out, indicator, size );
            in.close();
            out.close();
        }
        indicator.finished();
    }
    
    private File download( String url, ProgressIndicator indicator )
        throws IOException
    {        
        URL download = new URL( url );
        indicator.message( "Connecting to " + download.getHost() );

        URLConnection conn = download.openConnection();
        conn.connect();
        int size = conn.getContentLength();
        
        indicator.message( "Downloading " + url );
        indicator.start();
        InputStream in = conn.getInputStream();
        
        File tmp = File.createTempFile( "magic", null );
        tmp.deleteOnExit();
        
        FileOutputStream out = new FileOutputStream( tmp );
        
        copy( in, out, indicator, size );
        in.close();
        out.close();
        indicator.finished();
        return tmp;   
    }
    
    
    private void copy( InputStream from, OutputStream to, 
                       ProgressIndicator indicator, long size )
        throws IOException
    {
        BufferedOutputStream out = new BufferedOutputStream( to );
        BufferedInputStream in = new BufferedInputStream( from, BUFFER_SIZE );
        int bytesRead = 0;
        int counter = 0;
        do
        {
            byte[] data = new byte[ BUFFER_SIZE ];
            bytesRead = in.read( data, 0, BUFFER_SIZE );
            counter = counter + bytesRead;
            if( size != 0 )
                indicator.progress( (int) ( ( counter * 100 ) / size ) );
            if( bytesRead != -1 )
                out.write( data, 0, bytesRead );
        } while( bytesRead != -1 );
        out.flush();
    }
    
    private void copy( File file, File toDir, ProgressIndicator indicator )
        throws IOException
    {
        indicator.message( "Copying " + file + " to " + file );
        indicator.start();
        toDir.mkdirs();
        String name = file.getName();
        File destFile = new File( toDir, name );
        
        FileInputStream in = new FileInputStream( file );
        FileOutputStream out = new FileOutputStream( destFile );
        
        long size = file.length();
        copy( in, out, indicator, size );
        
        in.close();
        out.close();
        indicator.finished();
    }
}
