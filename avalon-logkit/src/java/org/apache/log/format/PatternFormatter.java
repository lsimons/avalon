/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.log.format;

import java.io.StringWriter;
import java.util.Stack;
import org.apache.log.*;

/**
 * This formater formats the LogEntries according to a input pattern 
 * string.
 *
 * The format of each pattern element can be %[+|-]#.#{field:subformat}
 *
 * The +|- indicates left or right justify.
 * The #.# indicates the minimum and maximum size of output.
 * 'field' indicates which field is to be output and must be one of
 *  proeprties of LogEntry
 * 'subformat' indicates a particular subformat and is currently unused.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class PatternFormatter 
    implements Formatter 
{
    protected final static int         TYPE_TEXT            = 1;
    protected final static int         TYPE_CATEGORY        = 2;
    protected final static int         TYPE_CONTEXT         = 3;
    protected final static int         TYPE_MESSAGE         = 4;
    protected final static int         TYPE_TIME            = 5;
    protected final static int         TYPE_THROWABLE       = 6;
    protected final static int         TYPE_PRIORITY        = 7;

    protected final static String      TYPE_CATEGORY_STR    = "category";
    protected final static String      TYPE_CONTEXT_STR     = "context";
    protected final static String      TYPE_MESSAGE_STR     = "message";
    protected final static String      TYPE_TIME_STR        = "time";
    protected final static String      TYPE_THROWABLE_STR   = "throwable";
    protected final static String      TYPE_PRIORITY_STR    = "priority";

    protected final static String      SPACE_16             = "                ";
    protected final static String      SPACE_8              = "        ";
    protected final static String      SPACE_4              = "    ";
    protected final static String      SPACE_2              = "  ";
    protected final static String      SPACE_1              = " ";

    protected static class PatternRun
    {
        String             m_data;
        boolean            m_rightJustify;
        int                m_minSize;
        int                m_maxSize;
        int                m_type;
        String             m_format;
    }

    protected PatternRun                      m_formatSpecification[];

    /**
     * Extract and build a pattern from input string.
     *
     * @param stack the stack on which to place patterns
     * @param pattern the input string
     * @param index the start of pattern run
     * @return the number of characters in pattern run
     */
    protected int addPatternRun( final Stack stack, 
                                 final char pattern[], 
                                 int index )
    {
        final PatternRun run = new PatternRun();
        final int start = index++;

        //first check for a +|- sign
        if( '+' == pattern[ index ] ) index++;
        else if( '-' == pattern[ index ] )
        {
            run.m_rightJustify = true;
            index++;
        }

        if( Character.isDigit( pattern[ index ] ))
        {
            int total = 0;
            while( Character.isDigit( pattern[ index ] ) )
            {
                total = total * 10 + (pattern[ index ] - '0');
                index++;
            }
            run.m_minSize = total;
        }

        //check for . sign indicating a maximum is to follow
        if( index < pattern.length && '.' == pattern[ index ] ) 
        {
            index++;

            if( Character.isDigit( pattern[ index ] ))
            {
                int total = 0;
                while( Character.isDigit( pattern[ index ] ) )
                {
                    total = total * 10 + (pattern[ index ] - '0');
                    index++;
                }
                run.m_maxSize = total;
            }
        }

        if( index >= pattern.length || '{' != pattern[ index ] )
        {
            throw 
                new IllegalArgumentException( "Badly formed pattern at character " +
                                              index );
        }

        int typeStart = index;

        while( index < pattern.length && 
               pattern[ index ]!= ':' && pattern[ index ] != '}' ) 
        {
            index++;
        }

        int typeEnd = index - 1;

        final String type = 
            new String( pattern, typeStart + 1, typeEnd - typeStart );

        run.m_type = getTypeIdFor( type );

        if( index < pattern.length && pattern[ index ] == ':' )
        {
            index++;
            while( index < pattern.length && pattern[ index ] != '}' ) index++;

            final int length = index - typeEnd - 2;

            if( 0 != length )
            {
                run.m_format = new String( pattern, typeEnd + 2, length );
            }
        }

        if( index >= pattern.length || '}' != pattern[ index ] )
        {
            throw new 
                IllegalArgumentException("Unterminated type in pattern at character "
                                         + index );
        }

        index++;

        stack.push( run );

        return index - start;
    }

    /**
     * Extract and build a text run  from input string.
     * It does special handling of '\n' and '\t' replaceing
     * them with newline and tab.
     *
     * @param stack the stack on which to place runs
     * @param pattern the input string
     * @param index the start of the text run
     * @return the number of characters in run
     */
    protected int addTextRun( final Stack stack, 
                              final char pattern[], 
                              int index )
    {
        final PatternRun run = new PatternRun();
        final int start = index;
        boolean escapeMode = false;

        if( '%' == pattern[ index ] ) index++;

        final StringBuffer sb = new StringBuffer();

        while( index < pattern.length && pattern[ index ] != '%' )
        {
            if( escapeMode )
            {
                if( 'n' == pattern[ index ] ) sb.append('\n');
                else if( 't' == pattern[ index ] ) sb.append('\t');
                else sb.append( pattern[ index ] );
                escapeMode = false;
            }
            else if( '\\' == pattern[ index ] ) escapeMode = true;
            else sb.append( pattern[ index ] );
            index++;
        }

        run.m_data = sb.toString();
        run.m_type = TYPE_TEXT;

        stack.push( run );

        return index - start;
    }

    /**
     * Utility to append a string to buffer given certain constraints.
     *
     * @param sb the StringBuffer
     * @param minSize the minimum size of output (0 to ignore)
     * @param maxSize the maximum size of output (0 to ignore)
     * @param rightJustify true if the string is to be right justified in it's box.
     * @param output the input string
     */
    protected void append( final StringBuffer sb, 
                           final int minSize, 
                           final int maxSize, 
                           final boolean rightJustify, 
                           final String output )
    {
        final int size = output.length();
    
        if( size < minSize )
        {
            //assert( minSize > 0 );
            if( rightJustify )
            {
                appendWhiteSpace( sb, minSize - size );
                sb.append( output );
            }
            else
            {
                sb.append( output );
                appendWhiteSpace( sb, minSize - size );
            }
        }
        else if( maxSize > 0 && maxSize < size )
        {
            sb.append( output.substring( 0, maxSize ) );
        }
        else
        {
            sb.append( output );
        }
    }

    /**
     * Append a certain number of whitespace characters to a StringBuffer.
     *
     * @param sb the StringBuffer
     * @param length the number of spaces to append
     */
    protected void appendWhiteSpace( final StringBuffer sb, int length )
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
     * Format the entry according to the pattern.
     *
     * @param entry the entry
     * @return the formatted output
     */
    public String format( final LogEntry entry )
    {
        final StringBuffer sb = new StringBuffer();

        String str = null;

        for( int i = 0; i < m_formatSpecification.length; i++ )
        {
            final PatternRun run = m_formatSpecification[ i ];

            switch( run.m_type )
            {
            //treat text differently as it doesn't need min/max padding
            case TYPE_TEXT: sb.append( run.m_data ); continue;

            case TYPE_TIME: 
                str = getTime( entry.getTime(), run.m_format ); 
                break;

            case TYPE_THROWABLE: 
                str = getStackTrace( entry.getThrowable(), run.m_format ); 
                break;

            case TYPE_MESSAGE: 
                str = getMessage( entry.getMessage(), run.m_format ); 
                break;

            case TYPE_CONTEXT: 
                str = getContext( entry.getContextStack(), run.m_format ); 
                break;

            case TYPE_CATEGORY:
                str = getCategory( entry.getCategory().getName(), run.m_format );
                break;

            case TYPE_PRIORITY: 
                str = getPriority( entry.getPriority(), run.m_format ); 
                break;

            default:
                LogKit.log( "Unknown Pattern specification." + run.m_type );
                continue;
            }

            append( sb, run.m_minSize, run.m_maxSize, run.m_rightJustify, str );
        }

        return sb.toString();
    }

    /**
     * Utility method to format category.
     *
     * @param category the category string
     * @param format ancilliary format parameter - allowed to be null
     * @return the formatted string
     */
    protected String getCategory( final String category, final String format )
    {
        return category;
    }

    protected String getPriority( final Priority.Enum priority, final String format )
    {
        return priority.getName();
    }

    /**
     * Utility method to format context.
     *
     * @param context the context string
     * @param format ancilliary format parameter - allowed to be null
     * @return the formatted string
     */
    protected String getContext( final ContextStack stack, final String format )
    {
        //TODO: Retrieve StringBuffers from a cache
        final StringBuffer sb = new StringBuffer();
        final int size = stack.getSize();

        int sizeSpecification = Integer.MAX_VALUE;

        if( null != format )
        {
            try { sizeSpecification = Integer.parseInt( format ); }
            catch( final NumberFormatException nfe ) { nfe.printStackTrace(); }
        }

        final int end = size - 1;
        final int start = Math.max( end - sizeSpecification + 1, 0 );

        for( int i = start; i < end; i++ )
        {
            sb.append( stack.get( i ) );
            sb.append('.');
        }

        sb.append( stack.get( end ) );

        return sb.toString();
    }

    /**
     * Utility method to format message.
     *
     * @param message the message string
     * @param format ancilliary format parameter - allowed to be null
     * @return the formatted string
     */
    protected String getMessage( final String message, final String format )
    {
        return message;
    }

    /**
     * Utility method to format stack trace.
     *
     * @param throwable the throwable instance
     * @param format ancilliary format parameter - allowed to be null
     * @return the formatted string
     */
    protected String getStackTrace( final Throwable throwable, final String format )
    {
        if( null == throwable ) return "";
        final StringWriter sw = new StringWriter();
        throwable.printStackTrace( new java.io.PrintWriter( sw ) );
        return sw.toString();
    }

    /**
     * Utility method to format time.
     *
     * @param time the time
     * @param format ancilliary format parameter - allowed to be null
     * @return the formatted string
     */
    protected String getTime( final long time, final String format )
    {
        return Long.toString( time );
    }
  
    /**
     * Retrieve the type-id for a particular string.
     *
     * @param type the string
     * @return the type-id
     */
    protected int getTypeIdFor( final String type )
    {
        if( type.equalsIgnoreCase( TYPE_CATEGORY_STR ) ) return TYPE_CATEGORY;
        else if( type.equalsIgnoreCase( TYPE_CONTEXT_STR ) ) return TYPE_CONTEXT;
        else if( type.equalsIgnoreCase( TYPE_MESSAGE_STR ) ) return TYPE_MESSAGE;
        else if( type.equalsIgnoreCase( TYPE_PRIORITY_STR ) ) return TYPE_PRIORITY;
        else if( type.equalsIgnoreCase( TYPE_TIME_STR ) ) return TYPE_TIME;
        else if( type.equalsIgnoreCase( TYPE_THROWABLE_STR ) ) 
        {
            return TYPE_THROWABLE;
        }
        else
        {
            throw new IllegalArgumentException( "Unknown Type in pattern - " + 
                                                type );
        }
    }

    /**
     * Parse the input pattern and build internal data structures.
     *
     * @param patternString the pattern
     */
    protected void parse( final String patternString )
    {
        final Stack stack = new Stack();
        final int size = patternString.length();
        final char pattern[] = new char[ size ];
        int index = 0;

        patternString.getChars( 0, size, pattern, 0 );

        while( index < size )
        {
            if( pattern[ index ] == '%' && 
                !( index != size - 1 && pattern[ index + 1 ] == '%' ) )
            {
                index += addPatternRun( stack, pattern, index );
            }
            else
            {
                index +=  addTextRun( stack, pattern, index );
            }
        }

        final int elementCount = stack.size();

        m_formatSpecification = new PatternRun[ elementCount ];

        for( int i = 0; i < elementCount; i++ )
        {
            m_formatSpecification[ i ] = (PatternRun) stack.elementAt( i );
        }
    }

    /**
     * Set the string description that the format is extracted from.
     *
     * @param format the string format
     */
    public void setFormat( final String format )
    {
        parse( format );
    }
}
