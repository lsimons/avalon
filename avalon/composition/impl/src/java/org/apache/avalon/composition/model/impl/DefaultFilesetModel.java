/*
 * Copyright  2002-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.apache.avalon.composition.model.impl;

import java.io.File;
import java.util.ArrayList;

import org.apache.avalon.composition.data.ExcludeDirective;
import org.apache.avalon.composition.data.IncludeDirective;
import org.apache.avalon.composition.model.FilesetModel;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;


/**
 * Implementation of a <code>FilesetModel</code> in which a set
 * of <code>IncludeDirective</code> objects, a set of
 * <code>ExcludeDirective</code> objects, a set of default
 * includes and excludes, and a base directory anchor are used
 * to resolve and build a set of files specified by a
 * <code>FilesetDirective</code>.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.1 $ $Date: 2004/04/17 19:05:14 $
 */
public class DefaultFilesetModel extends AbstractLogEnabled
    implements FilesetModel
{
    /**
     * Base directory anchor from which to begin filset resolution.
     */
    private File m_anchor = null;
    /**
     * Array of <code>IncludeDirective</code> objects to use during
     * fileset resolution.
     */
    private IncludeDirective[] m_includes = null;
    /**
     * Array of <code>ExcludeDirective</code> objects to use during
     * fileset resolution.
     */
    private ExcludeDirective[] m_excludes = null;
    /**
     * Array of <code>String</code> objects to use as a default set
     * of fileset includes.
     */
    private String[] m_defaultIncludes = null;
    /**
     * Array of <code>String</code> objects to use as a default set
     * of fileset excludes.
     */
    private String[] m_defaultExcludes = null;
    /**
     * Array of <code>File</code> objects representing the results
     * of fileset resolution.
     */
    private ArrayList m_list = null;
    /**
     * Container-supplied logger instance.
     */
    private final Logger m_logger;
    
    /**
     * Constructs a new default fileset model.
     * 
     * @param anchor base directory anchor from which to begin
     * fileset resolution
     * @param includes array of <code>IncludeDirective</code> objects
     * to use during fileset resolution
     * @param excludes array of <code>ExcludeDirective</code> objects
     * to use during fileset resolution
     * @param defaultIncludes array of <code>String</code> objects
     * to use as a default set of fileset includes
     * @param defaultExcludes array of <code>String</code> objects
     * to use as a default set of fileset excludes
     */
    public DefaultFilesetModel(File anchor, IncludeDirective[] includes,
            ExcludeDirective[] excludes, String[] defaultIncludes,
            String[] defaultExcludes, Logger logger)
    {
        m_logger = logger;
        m_list = new ArrayList();
        setBaseDirectory( anchor );
        setIncludeDirectives( includes );
        setExcludeDirectives( excludes );
        setDefaultIncludes( defaultIncludes );
        setDefaultExcludes( defaultExcludes );
    }
    
    /**
     * Establishes the base directory anchor for the fileset resolution.
     * 
     * @param anchor the base directory anchor
     */
    public void setBaseDirectory(File anchor) {
        m_anchor = anchor;
    }

    /**
     * Establishes the set of <code>IncludeDirective</code> objects
     * to use during fileset resolution.
     * 
     * @param includes array of <code>IncludeDirective</code> objects
     */
    public void setIncludeDirectives(IncludeDirective[] includes) {
        m_includes = includes;
    }

    /**
     * Establishes the set of <code>ExcludeDirective</code> objects
     * to use during fileset resolution.
     * 
     * @param excludes array of <code>ExcludeDirectives</code>
     */
    public void setExcludeDirectives(ExcludeDirective[] excludes) {
        m_excludes = excludes;
    }

    /**
     * Establishes a set of default includes to use during fileset
     * resolution in lieu of an explicit specification of a set
     * of <code>IncludeDirective</code> objects.
     * 
     * @param defaultIncludes array of <code>String</code> objects
     * representing a set of default fileset includes
     */
    public void setDefaultIncludes(String[] defaultIncludes) {
        if (defaultIncludes == null) {
            m_defaultIncludes = new String[1];
            m_defaultIncludes[0] = "*.jar";
        } else {
            m_defaultIncludes = defaultIncludes;
        }
    }

    /**
     * Establishes a set of default excludes to use during fileset
     * resolution in lieu of an explicit specification of a set
     * of <code>ExcludeDirective</code> objects.
     * 
     * @param defaultExcludes array of <code>String</code> objects
     * representing a set of default fileset excludes
     */
    public void setDefaultExcludes(String[] defaultExcludes) {
        if (defaultExcludes == null) {
            m_defaultExcludes = new String[0];
        } else {
            m_defaultExcludes = defaultExcludes;
        }
    }

    /**
     * Returns a set of <code>File</code> objects representing the
     * results of the fileset resolution.  This array will contain
     * fully qualified filenames based on the base directory anchor.
     * 
     * @return an array of files to include in the classpath
     */
    public ArrayList getIncludes() {
        return m_list;
    }

    /**
     * Resolves the specified include and exclude directives from
     * the base directory anchor and produces an array of files
     * to include in the classpath.
     */
    public void resolveFileset() throws IllegalStateException {
        if( m_includes.length > 0 ) {
            for( int j=0; j<m_includes.length; j++ ) {
                File file = new File( m_anchor, m_includes[j].getPath() );
                m_list.add( file );
            }
        } else {
            m_list.add( m_anchor );
        }
    }
}
