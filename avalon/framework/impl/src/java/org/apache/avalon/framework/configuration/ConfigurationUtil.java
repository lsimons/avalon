/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
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

package org.apache.avalon.framework.configuration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This class has a bunch of utility methods to work
 * with configuration objects.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.11 $ $Date: 2003/02/11 15:58:38 $
 */
public class ConfigurationUtil
{
    /**
     * Private constructor to block instantiation.
     */
    private ConfigurationUtil()
    {
    }

    /**
     * Convert a configuration tree into a DOM Element tree.
     *
     * @param configuration the configuration object
     * @return the DOM Element
     */
    public static Element toElement( final Configuration configuration )
    {
        try
        {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.newDocument();

            return createElement( document, configuration );
        }
        catch( final ParserConfigurationException pce )
        {
            throw new IllegalStateException( pce.toString() );
        }
    }

    /**
     * Test to see if two Configuration's can be considered the same. Name, value, attributes
     * and children are test. The <b>order</b> of children is not taken into consideration
     * for equality.
     *
     * @param c1 Configuration to test
     * @param c2 Configuration to test
     * @return true if the configurations can be considered equals
     */
    public static boolean equals( final Configuration c1, final Configuration c2 )
    {
        return c1.getName().equals( c2.getName() )
            && areValuesEqual( c1, c2 )
            && areAttributesEqual( c1, c2 )
            && areChildrenEqual( c1, c2 );
    }

    /**
     * Return true if the children of both configurations are equal.
     *
     * @param c1 configuration1
     * @param c2 configuration2
     * @return true if the children of both configurations are equal.
     */
    private static boolean areChildrenEqual( final Configuration c1,
                                             final Configuration c2 )
    {
        final Configuration[] kids1 = c1.getChildren();
        final ArrayList kids2 = new ArrayList( Arrays.asList( c2.getChildren() ) );
        if( kids1.length != kids2.size() )
        {
            return false;
        }

        for( int i = 0; i < kids1.length; i++ )
        {
            if( !findMatchingChild( kids1[ i ], kids2 ) )
            {
                return false;
            }
        }

        return kids2.isEmpty() ? true : false;
    }

    /**
     * Return true if find a matching child and remove child from list.
     *
     * @param c the configuration
     * @param matchAgainst the list of items to match against
     * @return true if the found.
     */
    private static boolean findMatchingChild( final Configuration c,
                                              final ArrayList matchAgainst )
    {
        final Iterator i = matchAgainst.iterator();
        while( i.hasNext() )
        {
            if( equals( c, (Configuration)i.next() ) )
            {
                i.remove();
                return true;
            }
        }

        return false;
    }

    /**
     * Return true if the attributes of both configurations are equal.
     *
     * @param c1 configuration1
     * @param c2 configuration2
     * @return true if the attributes of both configurations are equal.
     */
    private static boolean areAttributesEqual( final Configuration c1,
                                               final Configuration c2 )
    {
        final String[] names1 = c1.getAttributeNames();
        final String[] names2 = c2.getAttributeNames();
        if( names1.length != names2.length )
        {
            return false;
        }

        for( int i = 0; i < names1.length; i++ )
        {
            final String name = names1[ i ];
            final String value1 = c1.getAttribute( name, null );
            final String value2 = c2.getAttribute( name, null );
            if( !value1.equals( value2 ) )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Return true if the values of two configurations are equal.
     *
     * @param c1 configuration1
     * @param c2 configuration2
     * @return true if the values of two configurations are equal.
     */
    private static boolean areValuesEqual( final Configuration c1,
                                           final Configuration c2 )
    {
        final String value1 = c1.getValue( null );
        final String value2 = c2.getValue( null );
        return ( value1 == null && value2 == null ) 
            || ( value1 != null && value1.equals( value2 ) );
    }

    /**
     * Create an DOM {@link Element} from a {@link Configuration}
     * object.
     *
     * @param document the DOM document
     * @param configuration the configuration to convert
     * @return the DOM Element
     */
    private static Element createElement( final Document document,
                                          final Configuration configuration )
    {
        final Element element = document.createElement( configuration.getName() );

        final String content = configuration.getValue( null );
        if( null != content )
        {
            final Text child = document.createTextNode( content );
            element.appendChild( child );
        }

        final String[] names = configuration.getAttributeNames();
        for( int i = 0; i < names.length; i++ )
        {
            final String name = names[ i ];
            final String value = configuration.getAttribute( name, null );
            element.setAttribute( name, value );
        }
        final Configuration[] children = configuration.getChildren();
        for( int i = 0; i < children.length; i++ )
        {
            final Element child = createElement( document, children[ i ] );
            element.appendChild( child );
        }
        return element;
    }
}
