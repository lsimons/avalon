/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.util.system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.excalibur.util.CPUParser;

/**
 * Parses the Linux environment--Uses the proc filesystem to determine all the
 * CPU information.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2002/09/26 12:46:39 $
 */
public final class Linux implements CPUParser
{
    private final int m_processors;
    private final String m_cpuInfo;

    public Linux()
    {
        int procs = 1;
        String info = "";

        try
        {
            BufferedReader reader = new BufferedReader( new FileReader( "/proc/cpuinfo" ) );
            procs = 0;

            Properties props = new Properties();
            String line = null;

            while( ( line = reader.readLine() ) != null )
            {
                String[] args = split( line, ":\t" );

                if( args.length > 1 )
                {
                    props.setProperty( args[ 0 ].trim(), args[ 1 ].trim() );
                    if( args[ 0 ].trim().equals( "processor" ) )
                    {
                        procs++;
                    }
                }
            }

            StringBuffer buf = new StringBuffer();
            buf.append( props.getProperty( "model name" ) );
            buf.append( " Family " );
            buf.append( props.getProperty( "cpu family" ) );
            buf.append( " Model " );
            buf.append( props.getProperty( "model" ) );
            buf.append( " Stepping " );
            buf.append( props.getProperty( "stepping" ) );
            buf.append( ", " );
            buf.append( props.getProperty( "vendor_id" ) );

            info = buf.toString();
        }
        catch( Exception e )
        {
            procs = 1;
            e.printStackTrace();
        }

        m_processors = procs;
        m_cpuInfo = info;
    }

    /**
     * Return the number of processors available on the machine
     */
    public int numProcessors()
    {
        return m_processors;
    }

    /**
     * Return the cpu info for the processors (assuming symetric multiprocessing
     * which means that all CPUs are identical).  The format is:
     *
     * ${arch} family ${family} Model ${model} Stepping ${stepping}, ${identifier}
     */
    public String cpuInfo()
    {
        return m_cpuInfo;
    }

    /**
     * Splits the string on every token into an array of strings.
     *
     * @param string the string
     * @param onToken the token
     * @return the resultant array
     */
    private static final String[] split( final String string, final String onToken )
    {
        final StringTokenizer tokenizer = new StringTokenizer( string, onToken );
        final String[] result = new String[ tokenizer.countTokens() ];

        for( int i = 0; i < result.length; i++ )
        {
            result[ i ] = tokenizer.nextToken();
        }

        return result;
    }
}

