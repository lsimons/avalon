package org.apache.merlin.magic;

import java.io.File;

public class Main
{
    static private Builder m_Application;

    static public void main( String[] args )
        throws Exception
    {
        long t0 = System.currentTimeMillis();
        try
        {
            m_Application = new Builder( args, getProjectDir() );
            m_Application.execute();
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
} 
