package org.apache.merlin.magic;


public class CreationException extends Exception
{
    public CreationException()
    {
    }

    public CreationException( String message )
    {
        super( message );
    }

    public CreationException( String message, Throwable cause )
    {
        super( message, cause );
    }
} 
