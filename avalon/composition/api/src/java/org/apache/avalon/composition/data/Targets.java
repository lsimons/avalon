/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.avalon.composition.data;

import java.util.ArrayList;

/**
 * <p>A target is a tagged configuration fragment.  The tag is a path
 * seperated by "/" charaters qualifying the component that the target
 * configuration is to be applied to.</p>
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:31:13 $
 */
public class Targets
{
    //========================================================================
    // state
    //========================================================================

    /**
     * The set of targets.
     */
    private final TargetDirective[] m_targets;

    //========================================================================
    // constructors
    //========================================================================

    /**
     * Create an empty Targets instance.
     */
    public Targets()
    {
        m_targets = new TargetDirective[0];
    }

    /**
     * Create a new Targets instance.
     *
     * @param targets the set of targets
     */
    public Targets( final TargetDirective[] targets )
    {
        m_targets = targets;
    }

    //========================================================================
    // implementation
    //========================================================================

    /**
     * Return all targets.
     *
     * @return all the targets in this targets instance.
     */
    public TargetDirective[] getTargets()
    {
        return m_targets;
    }

    /**
     * Return a matching target.
     *
     * @param path the target path to lookup
     * @return the target or null if no matching target
     */
    public TargetDirective getTarget( String path )
    {
        final String key = getKey( path );

        for( int i=0; i<m_targets.length; i++ )
        {
            TargetDirective target = m_targets[i];
            if( target.getPath().equals( key ) )
            {
                return target;
            }
        }
        return null;
    }

    /**
     * Return a set of targets relative to the supplied path.
     *
     * @param path the base path to match against
     * @return the set of relative targets
     */
    public Targets getTargets( String path )
    {
        final String key = getKey( path );
        ArrayList list = new ArrayList();
        for( int i=0; i<m_targets.length; i++ )
        {
            TargetDirective target = m_targets[i];
            if( target.getPath().startsWith( key ) )
            {
                String name = target.getPath().substring( key.length() );
                if( name.length() > 0 )
                {
                    list.add( 
                       new TargetDirective( 
                          getKey( name ), 
                          target.getConfiguration(),
                          target.getCategoriesDirective() ) );
                }
            }
        }

        return new Targets( 
          (TargetDirective[]) list.toArray( new TargetDirective[0] ) );
    }

    /**
     * Convert the supplied path to a valid path.
     * @param path the path to convert
     * @return a good path value
     */
    private String getKey( final String path ) throws IllegalArgumentException
    {
        if( !path.startsWith("/") )
        {
            return "/" + path;
        }
        return path;
    }

    /**
     * Return a string representation of the target.
     * @return a string representing the target instance
     */
    public String toString()
    {
        StringBuffer buffer = new StringBuffer( "[targets: " );
        for( int i=0; i<m_targets.length; i++ )
        {
            buffer.append( m_targets[i] );
            if( i < ( m_targets.length -1 ) )
            {
               buffer.append( ", " );
            }
        }
        buffer.append( " ]" );
        return buffer.toString();
    }
}
