package org.apache.excalibur.configuration.validation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <a href="proyal@pace2020.com">peter royal</a>
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
