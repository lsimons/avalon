

package org.apache.avalon.composition.model.testa;

import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.excalibur.configuration.ConfigurationUtil;

public class ConfigurableComponent extends AbstractLogEnabled
  implements Configurable
{
    public void configure( Configuration config ) throws ConfigurationException
    {
        getLogger().info( "setting config to:" 
         + ConfigurationUtil.list( config ) );
    }
}
