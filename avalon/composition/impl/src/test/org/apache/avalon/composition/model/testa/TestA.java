

package org.apache.avalon.composition.model.testa;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

public class TestA extends AbstractLogEnabled
  implements Contextualizable, Initializable, A
{
    public void contextualize( Context context ) throws ContextException
    {
        getLogger().info( "name: " + context.get( "urn:avalon:name" ) );
        getLogger().info( "partition: " + context.get( "urn:avalon:partition" ) );
        getLogger().info( "classloader: " + context.get( "urn:avalon:classloader" ) );
        getLogger().info( "work: " + context.get( "urn:avalon:home" ) );
        getLogger().info( "temp: " + context.get( "urn:avalon:temp" ) );
    }

    public void initialize() throws Exception
    {
        getLogger().info( "hello" );
    }
}
