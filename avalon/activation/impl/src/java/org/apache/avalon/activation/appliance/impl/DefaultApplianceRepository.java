/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2002 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Jakarta", "Apache Avalon", "Avalon Framework" and
    "Apache Software Foundation"  must not be used to endorse or promote
    products derived  from this  software without  prior written
    permission. For written permission, please contact apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.avalon.activation.appliance.impl;

import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.avalon.activation.appliance.Appliance;
import org.apache.avalon.activation.appliance.ApplianceRepository;

import org.apache.avalon.framework.logger.Logger;

import org.apache.avalon.meta.info.DependencyDescriptor;
import org.apache.avalon.meta.info.StageDescriptor;


/**
 * The appliance repository interface declares operations through which 
 * clients may resolve registered appliance instances relative to
 * a stage or service dependencies.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.3 $ $Date: 2003/12/22 09:06:41 $
 */
class DefaultApplianceRepository implements ApplianceRepository
{
    //------------------------------------------------------------------
    // immutable state
    //------------------------------------------------------------------

    /**
     * The parent appliance repository.
     */
    private ApplianceRepository m_parent;
    
    private Logger m_Logger;
    
    /**
     * Table of registered appliance instances keyed by name.
     */
    private final Map m_appliances = new Hashtable();

    //------------------------------------------------------------------
    // constructor
    //------------------------------------------------------------------

    public DefaultApplianceRepository()
    {
        this( null );
    }

    public DefaultApplianceRepository( ApplianceRepository parent )
    {
        m_parent = parent;
    }
    
    public void enableLogging( Logger logger )
    {
        m_Logger = logger;
    }

    //------------------------------------------------------------------
    // ApplianceRepository
    //------------------------------------------------------------------

    /**
     * Locate an appliance meeting the supplied criteria.
     *
     * @param dependency a component service dependency
     * @return the appliance
     */
    public Appliance getAppliance( DependencyDescriptor dependency )
    {
        //
        // attempt to locate a solution locally
        //

        Iterator iterator = m_appliances.values().iterator();
        while( iterator.hasNext() )
        {
            Appliance appliance = (Appliance) iterator.next();
            if( appliance.getModel().isaCandidate( dependency ) )
            {
                return appliance;
            }
        }

        //
        // attempt to locate a solution from the parent
        //

        if( m_parent != null )
        {
            return m_parent.getAppliance( dependency );
        }

        return null;
    }

    /**
     * Locate an appliance meeting the supplied criteria.
     *
     * @param stage a component stage dependency
     * @return the appliance
     */
    public Appliance getAppliance( StageDescriptor stage )
    {
        Iterator iterator = m_appliances.values().iterator();
        while( iterator.hasNext() )
        {
            Appliance appliance = (Appliance) iterator.next();

            if( appliance.isEnabled()
              && appliance.getModel().isaCandidate( stage ) )
            {
                return appliance;
            }
        }

        if( m_parent != null )
        {
            return m_parent.getAppliance( stage );
        }

        return null;
    }

    //------------------------------------------------------------------
    // implementation
    //------------------------------------------------------------------

    /**
     * Add an appliance to the repository.
     *
     * @param appliance the appliance to add
     */
    protected void addAppliance( Appliance appliance )
    {
        m_appliances.put( appliance.getModel().getName(), appliance );
    }

    /**
     * Locate an appliance meeting the supplied criteria.
     *
     * @param dependency a component service dependency
     * @return the appliance
     */
    public Appliance[] getAppliances()
    {
        return (Appliance[]) m_appliances.values().toArray( new Appliance[0] );
    }

    /**
     * Locate an appliance matching the supplied name.
     *
     * @param dependency a component service dependency
     * @return the appliance
     */
    public Appliance getLocalAppliance( String name )
    {
        Appliance appl = (Appliance) m_appliances.get( name );
        if( appl == null && m_Logger != null )
            m_Logger.debug( "Can't find '" + name + "' in appliance repository: " + m_appliances );
        return appl;
    }
}
