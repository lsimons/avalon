/* 
 * Copyright 2002-2004 The Apache Software Foundation
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
package org.apache.excalibur.configuration.validation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public final class ValidationResult
{
    private final List m_warnings = new ArrayList( 16 );
    private final List m_errors = new ArrayList( 16 );
    private boolean m_valid;
    private boolean m_readOnly;

    public void addWarning( final String warning )
    {
        checkWriteable();

        m_warnings.add( warning );
    }

    public void addError( final String error )
    {
        checkWriteable();

        m_errors.add( error );
    }

    public void setResult( final boolean valid )
    {
        checkWriteable();

        m_valid = valid;
        m_readOnly = true;
    }

    public List getWarnings()
    {
        return m_warnings;
    }

    public List getErrors()
    {
        return m_errors;
    }

    public boolean isValid()
    {
        return m_valid;
    }

    protected final void checkWriteable()
        throws IllegalStateException
    {
        if( m_readOnly )
        {
            throw new IllegalStateException( "ValidationResult is read only "
                                             + "and can not be modified" );
        }
    }
}
