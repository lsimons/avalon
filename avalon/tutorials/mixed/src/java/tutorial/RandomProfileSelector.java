/* 
 * Copyright 2004 Apache Software Foundation
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

package tutorial;

import org.apache.avalon.assembly.engine.profile.ProfileSelector;
import org.apache.avalon.composition.data.Mode;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.meta.info.StageDescriptor;
import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.assembly.data.Profile;

/**
 * Select one profile from the multiple profile provided.
 *
 * @author <a href="mailto:mcconnell@apache.org">Stephen McConnell</a>
 * @version $Revision: 1.2 $ $Date: 2004/02/21 13:27:03 $
 */
public class RandomProfileSelector extends AbstractLogEnabled
    implements ProfileSelector
{

    /**
     * Returns the preferred profile form an available selection of 
     * candidate profiles.
     * @param profiles the set of candidate profiles
     * @param dependency the service dependency
     * @return the preferred profile or null if no satisfactory provider can be established
     */
    public Profile select( Profile[] profiles, DependencyDescriptor dependency )
    {
        if( profiles.length == 0 )
        {
            return null;
        }

        for( int i=0; i<profiles.length; i++ )
        {
            Profile profile = profiles[i];
            getLogger().info( "available profile: " + profile.getName() );
        }

        //
        // select the profile witgh the highest seed value
        //

        long seed = 0;
        Profile selection = profiles[0];
        for( int i=0; i<profiles.length; i++ )
        {
            Profile profile = profiles[i];
            long value = profile.getConfiguration().getChild( "seed" ).getValueAsLong( 0 );
            if( value > seed )
            {
                seed = value;
                selection = profile;
            }
        }
        return selection;
    }

    /**
     * Returns the preferred profile form an available selection of 
     * candidate profiles.
     * @param profiles the set of candidate profiles
     * @param stage the service stage depedency
     * @return the prefered profile or null if no satisfactory provider can be established
     */
    public Profile select( Profile[] profiles, StageDescriptor stage )
    {
        return null;
    }
}
