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

package org.apache.metro.transit;

import java.io.File;

/**
 * Contract defining a repository monitor.  The contract defines a set of operations
 * dealing with the notification of debug, info and error messages, together with 
 * and operation supporting the construction of a monitor dedicated to a dowload
 * action.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id: Artifact.java 30977 2004-07-30 08:57:54Z niclas $
 */
public final class Policy
{
    //---------------------------------------------------------------------
    // static
    //---------------------------------------------------------------------

    //private static final int FAST_INDEX = 0;
    //private static final int SNAPSHOT_INDEX = 1;
    //private static final int TIMESTAMP_INDEX = 2;
    //private static final int OVERWRITE_INDEX = 3;

    private static final String FAST_VALUE = "fast";
    private static final String SNAPSHOT_VALUE = "snapshot";
    private static final String TIMESTAMP_VALUE = "timestamp";
    private static final String OVERWRITE_VALUE = "overwrite";

    public static final Policy FAST = new Policy();
    public static final Policy SNAPSHOT = new Policy();
    public static final Policy TIMESTAMP = new Policy();
    public static final Policy OVERWRITE = new Policy();

    public static Policy createPolicy( String key ) throws PolicyException
    {
         if( null == key )
         {
             return FAST;
         }
         else if( FAST_VALUE.equals( key ) )
         {
             return FAST;
         }
         else if( SNAPSHOT_VALUE.equals( key ) )
         {
             return SNAPSHOT;
         }
         else if( TIMESTAMP_VALUE.equals( key ) )
         {
             return TIMESTAMP;
         }
         else if( OVERWRITE_VALUE.equals( key ) )
         {
             return OVERWRITE;
         }
         else
         {
             final String error = 
               "Invalid policy key: " + key;
             throw new PolicyException( error );
         }
    }

    //---------------------------------------------------------------------
    // constructor
    //---------------------------------------------------------------------

    private Policy()
    {
    }

   /**
    * Relative to this policy, return TRUE if a download action is required,
    * or FALSE if no downloading is implied under the policy.  If the supplied
    * file does not exist, then processing is required, otherwise processing 
    * is implied if the policy is TIMESTAMP, or if the file is a SNAPSHOT and 
    * SNAPSHOT filtering policy is enabled.
    * 
    * @param file the destination file to process
    * @return TRUE if the file should be processed
    */
    public boolean isaCandidate( File file )
    {
        if( OVERWRITE == this )
        {
            return true;
        }
        else if( !file.exists() )
        {
            return true;
        }
        else
        {
            if( FAST == this )
            {
                return false; // use the cached copy
            }
            else if( SNAPSHOT == this )
            {
                return !isSnapshot( file ); // use cached copy if its not a snapshot
            }
            else
            {
                return true;
            }
        }
    }

    public boolean equals( Object other )
    {
        return this == other;
    }

    public String toString()
    {
        if( FAST == this )
        {
            return FAST_VALUE;
        }
        else if( SNAPSHOT == this )
        {
            return SNAPSHOT_VALUE;
        }
        else if( TIMESTAMP == this )
        {
            return TIMESTAMP_VALUE;
        }
        else
        {
            return OVERWRITE_VALUE;
        }
    }

    private boolean isSnapshot( final File file )
    {
        if( file == null )
            return false;
        String name = file.getName();
        int posSS = name.indexOf( "-SNAPSHOT" );
        int posDot = name.indexOf( ".", posSS + 8 );
        if( posDot > -1 )
        {
             String sub = name.substring( 0, posDot );
             return sub.endsWith( "-SNAPSHOT" );
        }
        return name.endsWith( "-SNAPSHOT" );
    }
}


