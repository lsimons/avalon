/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.util;

/**
 * This interface is for CPUParser objects that are automagically loaded, and
 * perform architecture dependant processing for determining the number of CPUs,
 * and the generic infomation about them.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/09/25 14:52:28 $
 */
public interface CPUParser
{
    /**
     * Return the number of processors available on the machine
     */
    int numProcessors();

    /**
     * Return the cpu info for the processors (assuming symetric multiprocessing
     * which means that all CPUs are identical).  The format is:
     *
     * ${arch} Family ${family} Model ${model} Stepping ${stepping}, ${vendor_id}
     */
    String cpuInfo();
}

