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

package org.apache.avalon.composition.data;

import java.io.Serializable;


/**
 * Description of a set of categories.
 *
 * @see CategoryDirective
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2003/10/28 12:53:48 $
 */
public class CategoriesDirective extends CategoryDirective implements Serializable
{
    /**
     * The root category hierachy.
     */
    private CategoryDirective[] m_categories;

    /**
     * Create a CategoriesDirective instance.
     */
    public CategoriesDirective()
    {
        this( "" );
    }

    /**
     * Create a CategoriesDirective instance.
     *
     * @param name the base category name
     */
    public CategoriesDirective( final String name )
    {
        this( name, null, null, new CategoryDirective[0] );
    }

    /**
     * Create a CategoriesDirective instance.
     *
     * @param categories the categories to include in the directive
     */
    public CategoriesDirective( CategoryDirective[] categories )
    {
        this( "", null, null, categories );
    }


    /**
     * Create a CategoriesDirective instance.
     *
     * @param name the base category name
     * @param priority the default logging priority
     * @param target the default logging target
     * @param categories the logging category descriptors
     */
    public CategoriesDirective( final String name,
                                 final String priority,
                                 final String target,
                                 final CategoryDirective[] categories )
    {
        super( name, priority, target );
        if( categories == null )
        {
            m_categories = new CategoryDirective[ 0 ]; 
        }
        else
        {
            m_categories = categories;
        }
    }

    /**
     * Return the set of logging categories.
     *
     * @return the set of category declarations
     */
    public CategoryDirective[] getCategories()
    {
        return m_categories;
    }

    /**
     * Return a named category.
     *
     * @param name the category name
     * @return the category declaration
     */
    public CategoryDirective getCategoryDirective( String name )
    {
        for( int i = 0; i < m_categories.length; i++ )
        {
            final CategoryDirective category = m_categories[ i ];
            if( category.getName().equalsIgnoreCase( name ) )
            {
                return category;
            }
        }
        return null;
    }

   /**
    * Test this object for equality with the suppplied object.
    *
    * @return TRUE if this object equals the supplied object
    *   else FALSE
    */
    public boolean equals( Object other )
    {
        boolean isEqual = other instanceof CategoriesDirective;
        if ( isEqual ) isEqual = super.equals( other );

        if ( isEqual )
        {
            CategoriesDirective cat = (CategoriesDirective) other;
            if ( isEqual ) isEqual = m_categories.length == cat.m_categories.length;
            if ( isEqual )
            {
                for ( int i = 0; i < m_categories.length && isEqual; i++ )
                {
                    isEqual = m_categories[i].equals( cat.m_categories[i] );
                }
            }
        }
        return isEqual;
    }

   /**
    * Return the hashcode for the object.
    * @return the cashcode
    */
    public int hashCode()
    {
        int hash = super.hashCode();
        for ( int i = 0; i < m_categories.length; i++ )
        {
            hash >>>= 1;
            hash ^= m_categories[i].hashCode();
        }
        return hash;
    }


}
