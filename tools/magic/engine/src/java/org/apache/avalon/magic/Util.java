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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;

import java.net.URL;
import java.net.URLConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util 
{
    static public URL[] getURLs( Artifact[] artifacts )
        throws IOException
    {
        URL[] jars = new URL[ artifacts.length ];
        for( int i=0 ; i < artifacts.length ; i++ )
        {
            File file = artifacts[i].toLocalFile();
            jars[i] = file.toURL();
        }
        return jars;
    }

    static public void download( Artifact artifact, File dest )
        throws IOException
    {
        URL url = artifact.toRemoteURL();
        download( url, dest );
    }
    
    static public void download( URL url, File dest )
        throws IOException
    {
        FileOutputStream out = null;
        BufferedOutputStream bos = null;
        InputStream in = null;
        BufferedInputStream bis = null;
        File dlFile = File.createTempFile( "~magic", ".tmp", dest.getParentFile() );
        dlFile.deleteOnExit();
        try
        {
            out = new FileOutputStream( dlFile );
            bos = new BufferedOutputStream( out );
            URLConnection conn = url.openConnection();
            conn.connect();
            in = conn.getInputStream();
            bis = new BufferedInputStream( in );
            int b;
            int counter = 0;
            int kbs = 0;
            while( ( b = bis.read() ) != -1 )
            {
                counter++;
                if( counter >= 1000 )
                {
                    counter = 0;
                    kbs++;
                    System.out.print( "." );
                }
                bos.write( b );
            }
            bos.flush();
            System.out.println( "\nDownloaded: " + (counter + kbs * 1000) + " bytes." );
            dlFile.renameTo( dest );            
        } finally
        {
            if( bos != null )
                bos.close();
            if( out != null )
                out.close();
            if( bis != null )
                bis.close();
            if( in != null )
                in.close();
            dlFile.delete();
        }
    }

    static public boolean verify( File content, File checksum )
        throws IOException
    {
        byte[] chk1 = digest( content );
        byte[] chk2 = readChecksumFile( checksum );
        return MessageDigest.isEqual( chk1, chk2 );
    }
    
    static public void checksum( File inFile )
        throws IOException
    {
        byte[] checksum = digest( inFile );

        String outFilename = inFile.getAbsolutePath() + ".md5";
        File outFile = new File( outFilename );

        FileOutputStream fos = new FileOutputStream( outFile );
        PrintStream out = new PrintStream( fos );

        for( int i=0 ; i < checksum.length ; i++ )
        {
            int b = checksum[i];
            if( b < 0 )
                b = 256 + b;
            String hex = Integer.toHexString( b );
            if( hex.length() == 1 )
                hex = "0" + hex;
            out.print( hex );
        }
        out.println();
        out.flush();
        if( fos != null )
            fos.close();
        if( out != null )
            out.close();
    }
    
    static private byte[] digest( File content )
        throws IOException
    {
        MessageDigest digest = null;
        try
        {
            digest = MessageDigest.getInstance( "MD5" );
        } catch( NoSuchAlgorithmException e )
        {} // can't happen.
        
        FileInputStream fis = null;
            
        fis = new FileInputStream( content );
        byte[] data = new byte[10000];
        while( true )  // read all the bytes
        {
            int available = fis.available();
            int length = fis.read( data, 0, 10000 );
            if( length == -1 )
                break;
            digest.update( data, 0, length );
        }
        if( fis != null )
            fis.close();
        return digest.digest();
    }
    
    static private byte[] readChecksumFile( File checksumFile )
        throws IOException
    {
        try
        {
            FileInputStream fis = new FileInputStream( checksumFile );
            BufferedReader in = new BufferedReader( new InputStreamReader(fis) );
            
            String line = in.readLine().trim();
            byte[] data = new byte[ line.length() / 2 ];
            int pos = 0;
            for( int i=0 ; i < line.length() ; i += 2 )
            {
                String hex = line.substring( i, i+2 );
                int value = Integer.valueOf( hex, 16 ).intValue();
                if( value > 127 )
                    value = value - 256;
                data[pos++] = (byte) value;
            }
            System.out.println();
            fis.close();
            return data;
        } catch( Exception e )
        {
            return null;
        }
    }
}
