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

package org.apache.avalon.tools.model;

import org.apache.tools.ant.BuildException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Utility class supporting DOM content handling.
 * 
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Revision: 1.2 $ $Date: 2004/03/17 10:30:09 $
 */
public class ElementHelper
{
    public static Element getRootElement( final File definition )
      throws BuildException
    {
        if( !definition.exists() ) 
        {
            throw new BuildException( 
              new FileNotFoundException( definition.toString() ) );
        }

        if( !definition.isFile() ) 
        {
            final String error = 
              "Source is not a file: " + definition;
            throw new BuildException( 
              new IllegalArgumentException( error ) );
        }

        try 
        {
            final DocumentBuilderFactory factory =
              DocumentBuilderFactory.newInstance();
            factory.setValidating( false );
            factory.setNamespaceAware( false );
            final Document document =
              factory.newDocumentBuilder().parse( definition );
            return document.getDocumentElement();
        }
        catch( SAXException sxe )
        {
            // Error generated during parsing
            if( sxe.getException() != null) 
            {
                throw new BuildException( sxe.getException() );
            }
            throw new BuildException( sxe );
        }
        catch( ParserConfigurationException pce ) 
        {
            // Parser with specified options can't be built
            throw new BuildException( pce );
        }
        catch( IOException ioe ) 
        {
            // I/O error
            throw new BuildException(ioe);
        }
    }

    public static Element getRootElement( final InputStream input )
      throws Exception
    {
        try
        {
            final DocumentBuilderFactory factory =
              DocumentBuilderFactory.newInstance();
            factory.setValidating( false );
            factory.setNamespaceAware( false );
            final Document document =
              factory.newDocumentBuilder().parse( input );
            return document.getDocumentElement();
        }
        catch( ParserConfigurationException pce ) 
        {
            // Parser with specified options can't be built
            throw new BuildException( pce );
        }
        catch( IOException ioe ) 
        {
            // I/O error
            throw new BuildException(ioe);
        }
    }

    public static Element getChild( final Element root, final String name )
    {
        if( null == root ) return null;
        final NodeList list = root.getElementsByTagName( name );
        final int n = list.getLength();
        if( n < 1 ) return null;
        return (Element) list.item( 0 );
    }

    public static Element[] getChildren( final Element root, final String name )
    {
        if( null == root ) return new Element[0];
        final NodeList list = root.getElementsByTagName( name );
        final int n = list.getLength();
        final ArrayList result = new ArrayList();
        for( int i=0; i<n; i++ )
        {
            final Node item = list.item( i );
            if( item instanceof Element )
            {
                result.add( item );
            }
        }
        return (Element[]) result.toArray( new Element[0] );
    }

    public static Element[] getChildren( final Element root )
    {
        if( null == root ) return new Element[0];
        final NodeList list = root.getChildNodes();
        final int n = list.getLength();
        if( n < 1 ) return new Element[0];
        final ArrayList result = new ArrayList();
        for( int i=0; i<n; i++ )
        {
            final Node item = list.item( i );
            if( item instanceof Element )
            {
                result.add( item );
            }
        }
        return (Element[]) result.toArray( new Element[0] );
    }

    public static String getValue( final Element node )
    {
        if( null == node ) return null;
        if( node.getChildNodes().getLength() > 0 )
        {
            return node.getFirstChild().getNodeValue();
        }
        return node.getNodeValue(); 
    }

    public static String getAttribute( final Element node, final String key )
    {
        return getAttribute( node, key, null );
    }

    public static String getAttribute( final Element node, final String key, final String def )
    {
        if( null == node ) return def;
        final String value = node.getAttribute( key );
        if( null == value ) return def;
        return value;
    }

    public static boolean getBooleanAttribute( final Element node, final String key )
    {
        return getBooleanAttribute( node, key, false );
    }

    public static boolean getBooleanAttribute( final Element node, final String key, final boolean def )
    {
        if( null == node ) return def;
        final String value = node.getAttribute( key );
        if( null == value ) return def;
        if( value.equals( "" ) ) return def;
        if( value.equals( "true" ) ) return true;
        if( value.equals( "false" ) ) return false;
        final String error = 
          "Boolean argument [" + value + "] not recognized.";
        throw new BuildException( error );
    }
}
