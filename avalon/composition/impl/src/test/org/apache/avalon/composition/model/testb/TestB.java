

package org.apache.avalon.composition.model.testb;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AbstractLogEnabled;

public class TestB extends AbstractLogEnabled
  implements Initializable, B
{
    public void initialize() throws Exception
    {
        getLogger().info( "hello from B" );
    }
}
