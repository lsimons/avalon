

package org.apache.avalon.composition.model.testa;

import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

public class DefaultFacade implements Facade
{
    private Context m_context;

    public DefaultFacade( Context context )
    {
        m_context = context;
    }

    //------------------------------------------------------------
    // Context
    //------------------------------------------------------------

    public Object get( final Object key ) throws ContextException
    {
        return m_context.get( key );
    }

    //------------------------------------------------------------
    // Extra
    //------------------------------------------------------------
    
    public String getName()
    {
        try
        {
            return (String) m_context.get( "urn:avalon:name" );
        }
        catch( ContextException e )
        {
            throw new RuntimeException( e.toString() );
        }
    }
}
