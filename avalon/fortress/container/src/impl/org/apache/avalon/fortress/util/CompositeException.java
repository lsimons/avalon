package org.apache.avalon.fortress.util;

/**
 * This is an exception made up of one or more subexceptions.
 */
public class CompositeException extends Exception
{
    private final Exception[] m_ex;
    private final String m_message;

    public CompositeException( Exception[] ex )
    {
        this( ex, null );
    }

    public CompositeException( Exception[] ex, String message )
    {
        m_ex = ex;
        if( ex == null || ex.length < 1 )
        {
            throw new IllegalArgumentException( "you must specify a contained exception!" );
        }
        if( message == null )
        {
            final StringBuffer msg = new StringBuffer();
            for( int i = 0; i < ex.length; i++ )
            {
                msg.append( ex[ i ].getMessage() );
            }
            m_message = msg.toString();
        }
        else
            m_message = message;
    }

    public Exception[] getExceptions()
    {
        return m_ex;
    }

    public String toString()
    {
        return m_message;
    }
}
