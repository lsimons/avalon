/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avalon.fortress.impl;

import org.apache.avalon.fortress.MetaInfoEntry;
import org.apache.avalon.fortress.impl.ComponentHandlerMetaData;
import org.apache.avalon.fortress.impl.DefaultContainer;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * Customize the Fortress container to handle ECM compatibility
 *
 * @author <a href="mailto:dev@avalon.apache.org">The Avalon Team</a>
 * @version CVS $ Revision: 1.1 $
 */
public class DefaultECMContainer extends DefaultContainer {
    
    /**
     * Retrieve the classname for component configuration.
     *
     * @param config the component configuration
     * @return the class name
     */
    private String getClassname( final Configuration config )
    throws ConfigurationException {
        final String className;

        if ( "component".equals( config.getName() ) ) {
            className = config.getAttribute( "class" );
        } else {
            final MetaInfoEntry roleEntry = m_metaManager.getMetaInfoForShortName( config.getName() );
            if ( null == roleEntry )
            {
                
                final String message = "No class found matching configuration name " +
                    "[name: " + config.getName() + ", location: " + config.getLocation() + "]";
                throw new ConfigurationException( message );
            }

            className = roleEntry.getComponentClass().getName();
        }

        if ( getLogger().isDebugEnabled() ) {
            getLogger().debug( "Configuration processed for: " + className );
        }

        return className;
    }
    
    /**
     * Provide some validation for the core Cocoon components
     *
     * @param conf The configuration
     * @throws ConfigurationException if the coniguration is invalid
     */
    public void configure( Configuration conf ) 
    throws ConfigurationException {
        this.interpretProxy( conf.getAttribute("proxy-type", "none") );

        final Configuration[] elements = conf.getChildren();
        for ( int i = 0; i < elements.length; i++ )
        {
            final Configuration element = elements[i];
            String hint = element.getAttribute( "id", null );
            if ( null == hint ) {
                // Fortress requires a hint, so we just give it one :)
                hint = element.getLocation();
            }
            //final String role = getRole( element );
            final String className = getClassname( element );
            
            final int activation = ComponentHandlerMetaData.ACTIVATION_BACKGROUND;
            final ComponentHandlerMetaData metaData =
                new ComponentHandlerMetaData( hint, className, element, activation );

            try {
                addComponent( metaData );
            } catch ( Exception e ) {
                throw new ConfigurationException( "Could not add component", e );
            }
        }
    }

}
