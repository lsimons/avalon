/* ==================================================================== 
 * The Apache Software License, Version 1.1 
 * 
 * Copyright (c) 2002 The Apache Software Foundation. All rights 
 * reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software 
 *    itself, if and wherever such third-party acknowledgments  
 *    normally appear.
 *
 * 4. The names "Jakarta", "Avalon", and "Apache Software Foundation" 
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see 
 * <http://www.apache.org/>.
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
 * @version $Revision: 1.1 $ $Date: 2003/09/24 09:34:07 $
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
