package org.apache.avalon.fortress.util;

/**
 * This is an exception made up of one or more subexceptions.
 */
public final class CompositeException extends Exception
{
    private final Exception[] m_ex;
    private final String m_message;

    public CompositeException( final Exception[] ex )
    {
        this( ex, null );
    }

    public CompositeException( final Exception[] ex, final String message )
    {
        m_ex = ex;
        if ( ex == null || ex.length < 1 )
        {
            throw new IllegalArgumentException( "you must specify a contained exception!" );
        }
        if ( message == null )
        {
            final StringBuffer msg = new StringBuffer();
            for ( int i = 0; i < ex.length; i++ )
            {
                if (i > 0) msg.append('\n');
                msg.append( ex[i].getMessage() );
            }
            m_message = msg.toString();
        }
        else
            m_message = message;
    }

    public String getMessage()
    {
        return m_message;
    }

    public Exception[] getExceptions()
    {
        return m_ex;
    }
}
