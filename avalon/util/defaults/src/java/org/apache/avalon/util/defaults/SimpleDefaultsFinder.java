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

package org.apache.avalon.util.defaults ;


import java.util.Properties ;


/**
 * Attempts to discover defaults using an array of Properties as value sources.
 * 
 * @author <a href="mailto:aok123@bellsouth.net">Alex Karasulu</a>
 * @author $Author: mcconnell $
 * @version $Revision: 1.1 $
 */
public class SimpleDefaultsFinder implements DefaultsFinder
{
    /** Properties array to use for discovery */
    private Properties [] m_sources ;
    /** halt on first finding flag */
    private boolean m_haltOnDiscovery = true ;
    
    
    /**
     * Creates a simple defaults finder that searches a single source Properties
     * instance for default values.
     * 
     * @param source single source Properties to discover values in
     */
    public SimpleDefaultsFinder( Properties source )
    {
        m_sources = new Properties [] { source } ;
        m_haltOnDiscovery = false ;
    }
    
    
    /**
     * Creates a simple defaults filder that searches a set of source Properties
     * for default values.
     * 
     * @param sources the source Properties to discover values in
     * @param haltOnDiscovery true to halt search when first value is 
     * discovered, false to continue search overriding values until the last 
     * value is discovered.
     */
    public SimpleDefaultsFinder( Properties [] sources, 
                                   boolean haltOnDiscovery )
    {
        m_sources = sources ;
        m_haltOnDiscovery = haltOnDiscovery ;
    }
    
    
    /**
     * Applies default discovery using properties in array of properties.
     * 
     * @see org.apache.avalon.util.defaults.DefaultsFinder#find(
     * org.apache.avalon.util.defaults.Defaults)
     */
    public void find( Defaults a_defaults )
    {
        Defaults.discover( a_defaults, m_sources, m_haltOnDiscovery ) ;
    }
}
