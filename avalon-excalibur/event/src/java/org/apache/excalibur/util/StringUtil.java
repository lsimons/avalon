/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.util;

import java.util.StringTokenizer;

/**
 * A set of utility operations that work on or create strings.
 *
 * @author <a href="mailto:peter at apache.org">Peter Donald</a>
 * @version CVS $Revision: 1.1 $ $Date: 2002/09/25 15:01:43 $
 */
public final class StringUtil
{
    /**
     * the line separator for this OS
     */
    public static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    private static final String SPACE_16 = "                ";
    private static final String SPACE_8 = "        ";
    private static final String SPACE_4 = "    ";
    private static final String SPACE_2 = "  ";
    private static final String SPACE_1 = " ";

    ///Private Constructor to block instantiation
    private StringUtil()
    {
    }

    /**
     * Convenience utility to concatenate three strings together.
     */
    public static final String concat( final String first,
                                       final String second,
                                       final String third )
    {
        return new StringBuffer( first ).append( second ).append( third ).toString();
    }

    /**
     * Convenience utility to concatenate an array of strings together.
     */
    public static final String concat( final String[] list )
    {
        final StringBuffer buffer = new StringBuffer();

        for( int i = 0; i < list.length; i++ )
        {
            buffer.append( list[ i ] );
        }

        return buffer.toString();
    }

    /**
     * Replace substrings of one string with another string and return altered string.
     *
     * @param original input string
     * @param oldString the substring section to replace
     * @param newString the new substring replacing old substring section
     * @return converted string
     */
    public static final String replaceSubString( final String original,
                                                 final String oldString,
                                                 final String newString )
    {
        final StringBuffer sb = new StringBuffer();

        int end = original.indexOf( oldString );
        int start = 0;
        final int stringSize = oldString.length();

        while( end != -1 )
        {
            sb.append( original.substring( start, end ) );
            sb.append( newString );
            start = end + stringSize;
            end = original.indexOf( oldString, start );
        }

        end = original.length();
        sb.append( original.substring( start, end ) );

        return sb.toString();
    }

    /**
     * Display bytes in hex format.
     * <p>The display puts hex display on left and then writes out
     * textual representation on right. The text replaces any
     * non-printing character with a '.'</p>
     *
     * <p>Note that this code was based on work done by Barry Peterson
     * on the Q2Java project</p>
     *
     * @param data the bytes to display
     * @param length the number of bytes to display per line
     * @return The display string
     */
    public static final String hexDisplay( final byte[] data, final int length )
    {
        final StringBuffer sb = new StringBuffer();

        for( int i = 0; i < length; i += 16 )
        {
            //int lineEnd = Math.min( i+16, fArrayLength );
            int lineSize = Math.min( 16, length - i );
            final int lineEnd = lineSize + i;

            for( int j = i; j < lineEnd; j++ )
            {
                final int value = data[ j ] & 0xFF;

                if( value < 16 ) sb.append( '0' );
                sb.append( Integer.toHexString( value ) );
                sb.append( ' ' );
            }

            int padcount = 16 - lineSize;
            while( padcount > 0 )
            {
                padcount--;
                sb.append( "   " );
            }

            sb.append( "  " );

            for( int j = i; j < lineEnd; j++ )
            {
                final int value = data[ j ] & 0xFF;

                //Shouldn't 255 be lower????????
                if( ( value < 32 ) || ( value > 255 ) )
                    sb.append( '.' );
                else
                    sb.append( (char)value );
            }

            sb.append( '\n' );
        }

        return sb.toString();
    }

    /**
     * Truncate a string to maximum length;
     *
     * @param string the string
     * @param length the length
     * @return a truncated string or original string if it is short enough
     */
    public static final String truncate( final String string, final int length )
    {
        if( length >= string.length() )
            return string;
        else
            return string.substring( 0, length );
    }

    /**
     * Truncate a string in a nice manner.
     * The method will attempt to truncate the string
     * on whitespace and append "..." to the end.
     *
     * @author <a href="mailto:nate@protomatter.com">Nate Sammons</a>
     */
    public static final String truncateNicely( final String string, final int length )
    {
        if( length >= string.length() )
            return string;
        else if( 3 >= length )
        {
            final StringBuffer sb = new StringBuffer( length );
            for( int i = 0; i < length; i++ ) sb.append( '.' );
            return sb.toString();
        }

        final StringBuffer sb = new StringBuffer( length );
        getIndexOfBreak( sb, string, 0, length - 3, true );
        sb.append( "..." );

        return sb.toString();
    }

    /**
     * Wordwrap string to specified column.
     * if force is true then words that exceed column length will be
     * cut otherwise each word will exist on a line by itself.
     *
     * @param string string to word-wrap
     * @param column the column at which to wrap
     * @param force true if string should be force split at column
     * @return The word-wrapped string
     */
    public static final String wordWrap( final String string, final int column, final boolean force )
    {
        final int length = string.length();
        final StringBuffer sb = new StringBuffer();

        int start = 0;
        int end = getIndexOfBreak( sb, string, start, column, force );

        while( length != end )
        {
            //TODO: Make this EOL parameterizable
            sb.append( "\n" );

            start = end;

            end = getIndexOfBreak( sb, string, start, column, force );
        }

        return sb.toString();
    }

    /**
     * Splits the string on every token into an array of strings.
     *
     * @param string the string
     * @param onToken the token
     * @return the resultant array
     */
    public static final String[] split( final String string, final String onToken )
    {
        final StringTokenizer tokenizer = new StringTokenizer( string, onToken );
        final String[] result = new String[ tokenizer.countTokens() ];

        for( int i = 0; i < result.length; i++ )
        {
            result[ i ] = tokenizer.nextToken();
        }

        return result;
    }

    /**
     * Removes all the whitespace in a string
     */
    public static final String stripWhitespace( final String string )
    {
        return concat( split( string, " \t\r\n\b" ) );
    }

    /**
     * Joins the string array using specified separator.
     *
     * @param strings the array of strings to join
     * @param separator the separator to use when joining
     * @return the joined string
     */
    public static final String join( final String[] strings, final String separator )
    {
        final StringBuffer sb = new StringBuffer();
        for( int i = 0; i < strings.length; i++ )
        {
            sb.append( strings[ i ] );
            sb.append( separator );
        }

        return sb.toString();
    }

    /**
     * Utility to format a string given a set of constraints.
     * TODO: Think of a better name than format!!!! ;)
     *
     * @param minSize the minimum size of output (0 to ignore)
     * @param maxSize the maximum size of output (0 to ignore)
     * @param rightJustify true if the string is to be right justified in it's box.
     * @param string the input string
     */
    public static final String format( final int minSize,
                                       final int maxSize,
                                       final boolean rightJustify,
                                       final String string )
    {
        final StringBuffer sb = new StringBuffer( maxSize );
        format( sb, minSize, maxSize, rightJustify, string );
        return sb.toString();
    }

    /**
     * Utility to format a string given a set of constraints.
     * TODO: Think of a better name than format!!!! ;)
     * Note this was thieved from the logkit project.
     *
     * @param sb the StringBuffer
     * @param minSize the minimum size of output (0 to ignore)
     * @param maxSize the maximum size of output (0 to ignore)
     * @param rightJustify true if the string is to be right justified in it's box.
     * @param string the input string
     */
    public static final void format( final StringBuffer sb,
                                     final int minSize,
                                     final int maxSize,
                                     final boolean rightJustify,
                                     final String string )
    {
        final int size = string.length();

        if( size < minSize )
        {
            //assert( minSize > 0 );
            if( rightJustify )
            {
                appendWhiteSpace( sb, minSize - size );
                sb.append( string );
            }
            else
            {
                sb.append( string );
                appendWhiteSpace( sb, minSize - size );
            }
        }
        else if( maxSize > 0 && maxSize < size )
        {
            if( rightJustify )
            {
                sb.append( string.substring( size - maxSize ) );
            }
            else
            {
                sb.append( string.substring( 0, maxSize ) );
            }
        }
        else
        {
            sb.append( string );
        }
    }

    /**
     * Append a certain number of whitespace characters to a StringBuffer.
     *
     * @param sb the StringBuffer
     * @param length the number of spaces to append
     */
    public static final void appendWhiteSpace( final StringBuffer sb, int length )
    {
        while( length >= 16 )
        {
            sb.append( SPACE_16 );
            length -= 16;
        }

        if( length >= 8 )
        {
            sb.append( SPACE_8 );
            length -= 8;
        }

        if( length >= 4 )
        {
            sb.append( SPACE_4 );
            length -= 4;
        }

        if( length >= 2 )
        {
            sb.append( SPACE_2 );
            length -= 2;
        }

        if( length >= 1 )
        {
            sb.append( SPACE_1 );
            length -= 1;
        }
    }

    /**
     * Get index of good place to break line.
     * The index is the last whitespace character.
     *
     * @param string the input string
     * @param start the start index of line
     * @param maxCount the max size of the line
     * @return the index to break line
     */
    private static final int getIndexOfBreak( final StringBuffer sb,
                                              final String string,
                                              final int start,
                                              final int maxCount,
                                              final boolean forceBreak )
    {

        final int end = string.length() - start;
        int max = Math.min( maxCount, end );

        int base = 0;
        for( int i = 0; i < max; i++ )
        {
            final int index = start + i;
            final char ch = string.charAt( index );
            if( !Character.isWhitespace( ch ) ) break;

            base = i + 1;
        }

        max += base;

        int breakIndex = -1;
        for( int i = base; i < max; i++ )
        {
            final int index = start + i;
            final char ch = string.charAt( index );

            if( Character.isWhitespace( ch ) ) breakIndex = index;
        }

        if( -1 != breakIndex )
        {
            final String part = string.substring( start + base, breakIndex );
            sb.append( part );
            return breakIndex + 1;
        }
        else
        {
            if( forceBreak )
            {
                final String part = string.substring( start + base, start + max );
                sb.append( part );

                return start + max;
            }
            else
            {
                return getIndexOfBreak( sb, string, start, Integer.MAX_VALUE, true );
            }
        }
    }
}
