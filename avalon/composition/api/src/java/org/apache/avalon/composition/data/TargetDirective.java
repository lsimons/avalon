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

import java.io.Serializable;

import org.apache.avalon.framework.configuration.Configuration;

import org.apache.avalon.logging.data.CategoriesDirective;
import org.apache.avalon.logging.data.CategoryDirective;

/**
 * <p>A target is a tagged configuration fragment.  The tag is a path
 * seperated by "/" charaters qualifying the component that the target
 * configuration is to be applied to.</p>
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $ $Date: 2004/01/24 23:25:24 $
 */
public class TargetDirective implements Serializable
{
    //========================================================================
    // immutable state
    //========================================================================

    /**
     * The path.
     */
    private final String m_path;

    /**
     * The configuration.
     */
    private final Configuration m_config;

    /**
     * The configuration.
     */
    private final CategoriesDirective m_categories;

    //========================================================================
    // constructors
    //========================================================================

    /**
     * Create a new null Target instance.
     *
     * @param path target path
     */
    public TargetDirective( final String path )
    {
        this( path, null );
    }

    /**
     * Create a new Target instance.
     *
     * @param path target path
     * @param configuration the configuration 
     */
    public TargetDirective( final String path, final Configuration configuration )
    {
        this( path, configuration, null );
    }

    /**
     * Create a new Target instance.
     *
     * @param path target path
     * @param configuration the configuration 
     * @param categories the logging category directives 
     */
    public TargetDirective( 
      final String path, 
      final Configuration configuration, 
      final CategoriesDirective categories )
    {
        m_path = path;
        m_config = configuration;
        m_categories = categories;
    }

    //========================================================================
    // implementation
    //========================================================================

    /**
     * Return the target path.
     *
     * @return the target path
     */
    public String getPath()
    {
        return m_path;
    }

    /**
     * Return the target configuration.
     *
     * @return the target configuration
     */
    public Configuration getConfiguration()
    {
        return m_config;
    }

    /**
     * Return the logging categories directive.
     *
     * @return the logging categories (possibly null)
     */
    public CategoriesDirective getCategoriesDirective()
    {
        return m_categories;
    }

    /**
     * Return a string representation of the target.
     * @return a string representing the target instance
     */
    public String toString()
    {
        return "[target: " + getPath() + ", " 
          + (getConfiguration() != null ) + ", " 
          + (getCategoriesDirective() != null ) + ", " 
          + " ]";
    }

}
