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

package org.apache.avalon.composition.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.avalon.composition.data.ExcludeDirective;
import org.apache.avalon.composition.data.IncludeDirective;

/**
 * <p>Specification of a fileset model from which a 
 * a set of included and excluded file directives can be resolved
 * into set of fully qualifed filenames for purposes of establishing
 * a classpath.</p>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2004/04/19 19:53:24 $
 */
public interface FilesetModel {

    /**
     * Establishes the base directory anchor for the fileset resolution.
     * 
     * @param anchor the base directory anchor
     */
    void setBaseDirectory(File anchor);
    /**
     * Establishes the set of <code>IncludeDirective</code> objects
     * to use during fileset resolution.
     * 
     * @param includes array of <code>IncludeDirective</code> objects
     */
    void setIncludeDirectives(IncludeDirective[] includes);
    /**
     * Establishes the set of <code>ExcludeDirective</code> objects
     * to use during fileset resolution.
     * 
     * @param excludes array of <code>ExcludeDirectives</code>
     */
    void setExcludeDirectives(ExcludeDirective[] excludes);
    /**
     * Establishes a set of default includes to use during fileset
     * resolution in lieu of an explicit specification of a set
     * of <code>IncludeDirective</code> objects.
     * 
     * @param defaultIncludes array of <code>String</code> objects
     * representing a set of default fileset includes
     */
    void setDefaultIncludes(String[] defaultIncludes);
    /**
     * Establishes a set of default excludes to use during fileset
     * resolution in lieu of an explicit specification of a set
     * of <code>ExcludeDirective</code> objects.
     * 
     * @param defaultExcludes array of <code>String</code> objects
     * representing a set of default fileset excludes
     */
    void setDefaultExcludes(String[] defaultExcludes);
    /**
     * Returns a set of <code>File</code> objects representing the
     * results of the fileset resolution.  This array will contain
     * fully qualified filenames based on the base directory anchor.
     * 
     * @return an array list of <code>File</code> objects
     * to include in the classpath
     */
    ArrayList getIncludes();
    /**
     * Resolves the specified include and exclude directives from
     * the base directory anchor and produces an array of files
     * to include in the classpath.
     */
    void resolveFileset() throws IOException, IllegalStateException;
}
