/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.util.system;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.excalibur.util.CPUParser;

/**
 * Parses the Windows XP environment.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.3 $ $Date: 2003/02/25 16:28:38 $
 */
public final class WindowsXP implements CPUParser
{
    private final int m_processors;
    private final String m_cpuInfo;

    /**
     * Create this instance of CPUParser and gather information from
     * the Windows XP system.
     */
    public WindowsXP()
    {
        int procs = 1;
        String info = "";

        try
        {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec( "cmd.exe /C echo %NUMBER_OF_PROCESSORS%" );
            BufferedReader reader = new BufferedReader( new InputStreamReader(
                proc.getInputStream() ) );
            String numProcs = reader.readLine();

            proc = rt.exec( "cmd.exe /C echo %PROCESSOR_IDENTIFIER%" );
            reader = new BufferedReader( new InputStreamReader( proc.getInputStream() ) );
            info = reader.readLine();

            procs = Integer.parseInt( numProcs );
        }
        catch( Exception e )
        {
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
}

