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
package org.apache.excalibur.source;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.BitSet;
import java.util.Iterator;

import org.apache.avalon.framework.parameters.Parameters;

/**
 *
 * Utility class for source resolving.
 *
 * @author <a href="mailto:cziegeler@apache.org">Carsten Ziegeler</a>
 * @version CVS $Revision: 1.3 $ $Date: 2002/12/15 11:56:48 $
 */
public final class SourceUtil
{
    private static final char[] alphabet = new char[]
    {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', // 0 to 7
        'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', // 8 to 15
        'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', // 16 to 23
        'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', // 24 to 31
        'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', // 32 to 39
        'o', 'p', 'q', 'r', 's', 't', 'u', 'v', // 40 to 47
        'w', 'x', 'y', 'z', '0', '1', '2', '3', // 48 to 55
        '4', '5', '6', '7', '8', '9', '+', '/'}; // 56 to 63

    /**
     * Append parameters to the uri.
     * Each parameter is appended to the uri with "parameter=value",
     * the parameters are separated by "&".
     */
    public static String appendParameters( String uri,
                                           Parameters parameters )
    {
        if( parameters != null )
        {
            StringBuffer buffer = new StringBuffer( uri );
            String[] keys = parameters.getNames();
            String current;
            char separator = ( uri.indexOf( "?" ) == -1 ? '?' : '&' );

            if( keys != null )
            {
                for( int i = 0; i < keys.length; i++ )
                {
                    current = keys[ i ];
                    buffer.append( separator )
                        .append( current )
                        .append( '=' )
                        .append( SourceUtil.encode( parameters.getParameter( current, null ) ) );
                    separator = '&';
                }
            }
            return buffer.toString();
        }

        return uri;
    }

    /**
     * Append parameters to the uri
     * Each parameter is appended to the uri with "parameter=value",
     * the parameters are separated by "&".
     */
    public static String appendParameters( String uri,
                                           SourceParameters parameters )
    {
        if( parameters != null )
        {
            StringBuffer buffer = new StringBuffer( uri );
            Iterator keys = parameters.getParameterNames();
            String current;
            char separator = ( uri.indexOf( "?" ) == -1 ? '?' : '&' );
            Iterator values;

            while( keys.hasNext() == true )
            {
                current = (String)keys.next();
                values = parameters.getParameterValues( current );
                while( values.hasNext() == true )
                {
                    buffer.append( separator )
                        .append( current )
                        .append( '=' )
                        .append( SourceUtil.encode( (String)values.next() ) );
                    separator = '&';
                }
            }
            return buffer.toString();
        }

        return uri;
    }

    /**
     * BASE 64 encoding.
     * See also RFC 1421
     */
    public static String encodeBASE64( String s )
    {
        return encodeBASE64( s.getBytes() );
    }

    /**
     * BASE 64 encoding.
     * See also RFC 1421
     */
    public static String encodeBASE64( byte[] octetString )
    {
        int bits24;
        int bits6;

        char[] out = new char[ ( ( octetString.length - 1 ) / 3 + 1 ) * 4 ];

        int outIndex = 0;
        int i = 0;

        while( ( i + 3 ) <= octetString.length )
        {
            // store the octets
            bits24 = ( octetString[ i++ ] & 0xFF ) << 16;
            bits24 |= ( octetString[ i++ ] & 0xFF ) << 8;
            bits24 |= ( octetString[ i++ ] & 0xFF ) << 0;

            bits6 = ( bits24 & 0x00FC0000 ) >> 18;
            out[ outIndex++ ] = alphabet[ bits6 ];
            bits6 = ( bits24 & 0x0003F000 ) >> 12;
            out[ outIndex++ ] = alphabet[ bits6 ];
            bits6 = ( bits24 & 0x00000FC0 ) >> 6;
            out[ outIndex++ ] = alphabet[ bits6 ];
            bits6 = ( bits24 & 0x0000003F );
            out[ outIndex++ ] = alphabet[ bits6 ];
        }

        if( octetString.length - i == 2 )
        {
            // store the octets
            bits24 = ( octetString[ i ] & 0xFF ) << 16;
            bits24 |= ( octetString[ i + 1 ] & 0xFF ) << 8;

            bits6 = ( bits24 & 0x00FC0000 ) >> 18;
            out[ outIndex++ ] = alphabet[ bits6 ];
            bits6 = ( bits24 & 0x0003F000 ) >> 12;
            out[ outIndex++ ] = alphabet[ bits6 ];
            bits6 = ( bits24 & 0x00000FC0 ) >> 6;
            out[ outIndex++ ] = alphabet[ bits6 ];

            // padding
            out[ outIndex++ ] = '=';
        }
        else if( octetString.length - i == 1 )
        {
            // store the octets
            bits24 = ( octetString[ i ] & 0xFF ) << 16;

            bits6 = ( bits24 & 0x00FC0000 ) >> 18;
            out[ outIndex++ ] = alphabet[ bits6 ];
            bits6 = ( bits24 & 0x0003F000 ) >> 12;
            out[ outIndex++ ] = alphabet[ bits6 ];

            // padding
            out[ outIndex++ ] = '=';
            out[ outIndex++ ] = '=';
        }

        return new String( out );
    }

    /** A BitSet defining the characters which don't need encoding */
    static BitSet charactersDontNeedingEncoding;
    static final int characterCaseDiff = ( 'a' - 'A' );

    /** Initialize the BitSet */
    static
    {
        charactersDontNeedingEncoding = new BitSet( 256 );
        int i;
        for( i = 'a'; i <= 'z'; i++ )
        {
            charactersDontNeedingEncoding.set( i );
        }
        for( i = 'A'; i <= 'Z'; i++ )
        {
            charactersDontNeedingEncoding.set( i );
        }
        for( i = '0'; i <= '9'; i++ )
        {
            charactersDontNeedingEncoding.set( i );
        }
        charactersDontNeedingEncoding.set( '-' );
        charactersDontNeedingEncoding.set( '_' );
        charactersDontNeedingEncoding.set( '.' );
        charactersDontNeedingEncoding.set( '*' );
        charactersDontNeedingEncoding.set( '"' );
    }

    /**
     * Translates a string into <code>x-www-form-urlencoded</code> format.
     *
     * @param   s   <code>String</code> to be translated.
     * @return  the translated <code>String</code>.
     */
    public static String encode( String s )
    {
        final StringBuffer out = new StringBuffer( s.length() );
        final ByteArrayOutputStream buf = new ByteArrayOutputStream( 32 );
        final OutputStreamWriter writer = new OutputStreamWriter( buf );
        for( int i = 0; i < s.length(); i++ )
        {
            int c = (int)s.charAt( i );
            if( charactersDontNeedingEncoding.get( c ) )
            {
                out.append( (char)c );
            }
            else
            {
                try
                {
                    writer.write( c );
                    writer.flush();
                }
                catch( IOException e )
                {
                    buf.reset();
                    continue;
                }
                byte[] ba = buf.toByteArray();
                for( int j = 0; j < ba.length; j++ )
                {
                    out.append( '%' );
                    char ch = Character.forDigit( ( ba[ j ] >> 4 ) & 0xF, 16 );
                    // converting to use uppercase letter as part of
                    // the hex value if ch is a letter.
                    if( Character.isLetter( ch ) )
                    {
                        ch -= characterCaseDiff;
                    }
                    out.append( ch );
                    ch = Character.forDigit( ba[ j ] & 0xF, 16 );
                    if( Character.isLetter( ch ) )
                    {
                        ch -= characterCaseDiff;
                    }
                    out.append( ch );
                }
                buf.reset();
            }
        }

        return out.toString();
    }

    /**
     * Return a <code>File</code> object associated with the <code>Source</code> object.
     *
     * @return The corresponding <code>File</code> object or null if the
     *         <code>Source</code> object does not point to a file URI.
     */
    public static File getFile( Source source )
    {
        final String systemId = source.getSystemId();
        if( systemId.startsWith( "file:" ) )
        {
            return new File( systemId.substring( 5 ) );
        }
        return null;
    }
}
