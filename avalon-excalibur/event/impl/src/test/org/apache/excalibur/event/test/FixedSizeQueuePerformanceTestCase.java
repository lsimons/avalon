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
package org.apache.excalibur.event.test;

import org.apache.excalibur.event.impl.FixedSizeQueue;

/**
 * The default queue implementation is a variabl size queue.
 *
 * @author <a href="mailto:bloritsch@apache.org">Berin Loritsch</a>
 */
public final class FixedSizeQueuePerformanceTestCase extends AbstractQueueTestCase
{
    public FixedSizeQueuePerformanceTestCase( String name )
    {
        super( name );
    }

    public void testMillionIterationOneElement()
        throws Exception
    {
        this.performMillionIterationOneElement( new FixedSizeQueue( 32 ) );
    }

    public void testMillionIterationTenElements()
        throws Exception
    {
        this.performMillionIterationTenElements( new FixedSizeQueue( 32 ) );
    }
}
