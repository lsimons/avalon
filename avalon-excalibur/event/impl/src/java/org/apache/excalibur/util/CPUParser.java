/* 
 * Copyright 1999-2004 Apache Software Foundation
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
package org.apache.excalibur.util;

/**
 * This interface is for CPUParser objects that are automagically loaded, and
 * perform architecture dependant processing for determining the number of CPUs,
 * and the generic infomation about them.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 * @version CVS $Revision: 1.2 $ $Date: 2004/02/24 14:45:56 $
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

