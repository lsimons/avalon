/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.cli;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Vector;

/**
 * Parser for command line arguments.
 *
 * This parses command lines according to the standard (?) of
 * gnu utilities.
 *
 * Note: This is still used in 1.1 libraries so do not add 1.2+ dependancies.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class CLArgsParser
{
    protected class Token
    {
        protected final int                m_type;
        protected final String             m_value;

        public Token( final int type, final String value )
        {
            m_type = type;
            m_value = value;
        }

        public String getValue()
        {
            return m_value;
        }

        public int getType()
        {
            return m_type;
        }

        public String toString()
        {
            return "" + m_type + ":" + m_value;
        }
    }

    private final static int                 STATE_NORMAL           = 0;
    private final static int                 STATE_REQUIRE_2ARGS    = 1;
    private final static int                 STATE_REQUIRE_ARG      = 2;
    private final static int                 STATE_OPTIONAL_ARG     = 3;
    private final static int                 STATE_NO_OPTIONS       = 4;
    private final static int                 STATE_OPTION_MODE      = 5;

    protected final static int               TOKEN_SEPERATOR        = 0;
    protected final static int               TOKEN_STRING           = 1;


    protected final static char[]            ARG2_SEPERATORS        =
        new char[] { (char)0, '=', '-' };

    protected final static char[]            ARG_SEPERATORS         =
        new char[] { (char)0, '=' };

    protected final static char[]            NULL_SEPERATORS        =
        new char[] { (char)0 };

    protected final CLOptionDescriptor[]     m_optionDescriptors;
    protected final Vector                   m_options;
    protected final ParserControl            m_control;

    protected String                         m_errorMessage;
    protected String[]                       m_unparsedArgs         = new String[] {};

    //variables used while parsing options.
    protected char                           ch;
    protected String[]                       args;
    protected boolean                        isLong;
    protected int                            argIndex;
    protected int                            stringIndex;
    protected int                            stringLength;

    //cached character == Integer.MAX_VALUE when invalid
    protected final static int               INVALID         = Integer.MAX_VALUE;
    protected int                            m_lastChar      = INVALID;

    protected int                            m_lastOptionId;
    protected CLOption                       m_option;
    protected int                            m_state         = STATE_NORMAL;

    public String[] getUnparsedArgs()
    {
        return m_unparsedArgs;
    }

    /**
     * Retrieve a list of options that were parsed from command list.
     *
     * @return the list of options
     */
    public Vector getArguments()
    {
        //System.out.println( "Arguments: " + m_options );
        return m_options;
    }

    /**
     * Get Descriptor for option id.
     *
     * @param id the id
     * @return the descriptor
     */
    private CLOptionDescriptor getDescriptorFor( final int id )
    {
        for( int i = 0; i < m_optionDescriptors.length; i++ )
        {
            if( m_optionDescriptors[i].getId() == id )
            {
                return m_optionDescriptors[i];
            }
        }

        return null;
    }

    /**
     * Retrieve a descriptor by name.
     *
     * @param name the name
     * @return the descriptor
     */
    private CLOptionDescriptor getDescriptorFor( final String name )
    {
        for( int i = 0; i < m_optionDescriptors.length; i++ )
        {
            if( m_optionDescriptors[i].getName().equals( name ) )
            {
                return m_optionDescriptors[i];
            }
        }

        return null;
    }

    /**
     * Retrieve an error message that occured during parsing if one existed.
     *
     * @return the error string
     */
    public String getErrorString()
    {
        //System.out.println( "ErrorString: " + m_errorMessage );
        return m_errorMessage;
    }

    /**
     * Requier state to be placed in for option.
     *
     * @param descriptor the Option Descriptor
     * @return the state
     */
    private int getStateFor( final CLOptionDescriptor descriptor )
    {
        int flags = descriptor.getFlags();
        if( ( flags & CLOptionDescriptor.ARGUMENTS_REQUIRED_2 ) ==
            CLOptionDescriptor.ARGUMENTS_REQUIRED_2 )
        {
            return STATE_REQUIRE_2ARGS;
        }
        else if( ( flags & CLOptionDescriptor.ARGUMENT_REQUIRED ) ==
                 CLOptionDescriptor.ARGUMENT_REQUIRED )
        {
            return STATE_REQUIRE_ARG;
        }
        else if( ( flags & CLOptionDescriptor.ARGUMENT_OPTIONAL ) ==
                 CLOptionDescriptor.ARGUMENT_OPTIONAL )
        {
            return STATE_OPTIONAL_ARG;
        }
        else
        {
            return STATE_NORMAL;
        }
    }

    /**
     * Create a parser that can deals with options and parses certain args.
     *
     * @param args[] the args
     * @param optionDescriptors[] the option descriptors
     */
    public CLArgsParser( final String[] args,
                         final CLOptionDescriptor[] optionDescriptors,
                         final ParserControl control )
    {
        m_optionDescriptors = optionDescriptors;
        m_control = control;
        m_options = new Vector();
        this.args = args;

        try
        {
            parse();
            checkIncompatabilities( m_options );
        }
        catch( final ParseException pe )
        {
            m_errorMessage = pe.getMessage();
        }

        //System.out.println( "Built : " + m_options );
        //System.out.println( "From : " + Arrays.asList( args ) );
    }

    /**
     * Check for duplicates of an option.
     * It is an error to have duplicates unless appropriate flags is set in descriptor.
     *
     * @param arguments the arguments
     */
    protected void checkIncompatabilities( final Vector arguments )
        throws ParseException
    {
        final int size = arguments.size();

        for( int i = 0; i < size; i++ )
        {
            final CLOption option = (CLOption)arguments.elementAt( i );
            final int id = option.getId();
            final CLOptionDescriptor descriptor = getDescriptorFor( id );

            //this occurs when id == 0 and user has not supplied a descriptor
            //for arguments
            if( null == descriptor ) continue;

            final int[] incompatable = descriptor.getIncompatble();

            checkIncompatable( arguments, incompatable, i );
        }
    }

    protected void checkIncompatable( final Vector arguments,
                                      final int[] incompatable,
                                      final int original )
        throws ParseException
    {
        final int size = arguments.size();

        for( int i = 0; i < size; i++ )
        {
            if( original == i ) continue;

            final CLOption option = (CLOption)arguments.elementAt( i );
            final int id = option.getId();
            final CLOptionDescriptor descriptor = getDescriptorFor( id );

            for( int j = 0; j < incompatable.length; j++ )
            {
                if( id == incompatable[ j ] )
                {
                    final CLOption originalOption = (CLOption)arguments.elementAt( original );
                    final int originalId = originalOption.getId();

                    String message = null;

                    if( id == originalId )
                    {
                        message =
                            "Duplicate options for " + describeDualOption( originalId ) +
                            " found.";
                    }
                    else
                    {
                        message = "Incompatable options -" +
                            describeDualOption( id ) + " and " +
                            describeDualOption( originalId ) + " found.";
                    }
                    throw new ParseException( message, 0 );
                }
            }
        }
    }

    protected String describeDualOption( final int id )
    {
        final CLOptionDescriptor descriptor = getDescriptorFor( id );
        if( null == descriptor ) return "<parameter>";
        else
        {
            final StringBuffer sb = new StringBuffer();
            boolean hasCharOption = false;

            if( Character.isLetter( (char)id ) )
            {
                sb.append( '-' );
                sb.append( (char)id );
                hasCharOption = true;
            }

            final String longOption = descriptor.getName();
            if( null != longOption )
            {
                if( hasCharOption ) sb.append( '/' );
                sb.append( "--" );
                sb.append( longOption );
            }

            return sb.toString();
        }
    }

    /**
     * Create a parser that can deals with options and parses certain args.
     *
     * @param args[] the args
     * @param optionDescriptors[] the option descriptors
     */
    public CLArgsParser( final String[] args,
                         final CLOptionDescriptor[] optionDescriptors )
    {
        this( args, optionDescriptors, null );
    }

    /**
     * Create a string array that is subset of input array.
     * The sub-array should start at array entry indicated by index. That array element
     * should only include characters from charIndex onwards.
     *
     * @param array[] the original array
     * @param index the cut-point in array
     * @param charIndex the cut-point in element of array
     * @return the result array
     */
    protected String[] subArray( final String[] array,
                                 final int index,
                                 final int charIndex )
    {
        final int remaining = array.length - index;
        final String[] result = new String[ remaining ];

        if( remaining > 1 )
        {
            System.arraycopy( array, index + 1, result, 1, remaining - 1 );
        }

        result[0] = array[ index ].substring( charIndex - 1 );

        return result;
    }

    /**
     * Actually parse arguments
     *
     * @param args[] arguments
     */
    protected void parse()
        throws ParseException
    {
        if( 0 == args.length ) return;

        stringLength = args[ argIndex ].length();

        //ch = peekAtChar();

        while( true )
        {
            ch = peekAtChar();

            if( argIndex >= args.length ) break;

            if( null != m_control && m_control.isFinished( m_lastOptionId ) )
            {
                //this may need mangling due to peeks
                m_unparsedArgs = subArray( args, argIndex, stringIndex );
                return;
            }

            //System.out.println( "State=" + m_state );
            //System.out.println( "Char=" + (char)ch );

            if( STATE_OPTION_MODE == m_state )
            {
                //if get to an arg barrier then return to normal mode
                //else continue accumulating options
                if( 0 == ch )
                {
                    getChar(); //strip the null
                    m_state = STATE_NORMAL;
                }
                else parseShortOption();
            }
            else if( STATE_NORMAL == m_state )
            {
                parseNormal();
            }
            else if( STATE_NO_OPTIONS == m_state )
            {
                //should never get to here when stringIndex != 0
                addOption( new CLOption( args[ argIndex++ ] ) );
            }
            else if( STATE_OPTIONAL_ARG == m_state && '-' == ch )
            {
                m_state = STATE_NORMAL;
                addOption( m_option );
            }
            else
            {
                parseArguments();
            }
        }

        if( m_option != null )
        {
            if( STATE_OPTIONAL_ARG == m_state )
            {
                m_options.addElement( m_option );
            }
            else if( STATE_REQUIRE_ARG == m_state )
            {
                final CLOptionDescriptor descriptor = getDescriptorFor( m_option.getId() );
                final String message =
                    "Missing argument to option " + getOptionDescription( descriptor );
                throw new ParseException( message, 0 );
            }
            else if( STATE_REQUIRE_2ARGS == m_state )
            {
                if( 1 == m_option.getArgumentCount() )
                {
                    m_option.addArgument( "" );
                    m_options.addElement( m_option );
                }
                else
                {
                    final CLOptionDescriptor descriptor = getDescriptorFor( m_option.getId() );
                    final String message =
                        "Missing argument to option " + getOptionDescription( descriptor );
                    throw new ParseException( message, 0 );
                }
            }
            else
            {
                throw new ParseException( "IllegalState " + m_state + ": " + m_option, 0 );
            }
        }
    }

    protected final String getOptionDescription( final CLOptionDescriptor descriptor )
    {
        if( isLong ) return "--" + descriptor.getName();
        else return "-" + (char)descriptor.getId();
    }

    protected final char peekAtChar()
    {
        if( INVALID == m_lastChar ) m_lastChar = readChar();
        return (char)m_lastChar;
    }

    protected final char getChar()
    {
        if( INVALID != m_lastChar )
        {
            final char result = (char)m_lastChar;
            m_lastChar = INVALID;
            return result;
        }
        else return readChar();
    }

    private final char readChar()
    {
        if( stringIndex >= stringLength )
        {
            argIndex++;
            stringIndex = 0;

            if( argIndex < args.length ) stringLength = args[ argIndex ].length();
            else stringLength = 0;

            return 0;
        }

        if( argIndex >= args.length ) return 0;

        return args[ argIndex ].charAt( stringIndex++ );
    }

    protected final Token nextToken( final char[] seperators )
    {
        ch = getChar();

        if( isSeperator( ch, seperators ) )
        {
            ch = getChar();
            return new Token( TOKEN_SEPERATOR, null );
        }

        final StringBuffer sb = new StringBuffer();

        do
        {
            sb.append( ch );
            ch = getChar();
        }
        while( !isSeperator( ch, seperators ) );

        return new Token( TOKEN_STRING, sb.toString() );
    }

    private final boolean isSeperator( final char ch, final char[] seperators )
    {
        for( int i = 0; i < seperators.length; i++ )
        {
            if( ch == seperators[ i ] ) return true;
        }

        return false;
    }

    protected void addOption( final CLOption option )
    {
        m_options.addElement( option );
        m_lastOptionId = option.getId();
        m_option = null;
    }

    protected void parseOption( final CLOptionDescriptor descriptor,
                                final String optionString )
        throws ParseException
    {
        if( null == descriptor )
        {
            throw new ParseException( "Unknown option " + optionString, 0 );
        }

        m_state = getStateFor( descriptor );
        m_option = new CLOption( descriptor.getId() );

        if( STATE_NORMAL == m_state ) addOption( m_option );
    }

    protected void parseShortOption()
        throws ParseException
    {
        ch = getChar();
        final CLOptionDescriptor descriptor = getDescriptorFor( (int)ch );
        isLong = false;
        parseOption( descriptor, "-" + ch );

        if( STATE_NORMAL == m_state ) m_state = STATE_OPTION_MODE;
    }

    protected boolean parseArguments()
        throws ParseException
    {
        if( STATE_REQUIRE_ARG == m_state )
        {
            if( '=' == ch || 0 == ch ) getChar();

            final Token token = nextToken( NULL_SEPERATORS );
            m_option.addArgument( token.getValue() );

            addOption( m_option );
            m_state = STATE_NORMAL;
        }
        else if( STATE_REQUIRE_2ARGS == m_state )
        {
            if( 0 == m_option.getArgumentCount() )
            {
                final Token token = nextToken( ARG_SEPERATORS );

                if( TOKEN_SEPERATOR == token.getType() )
                {
                    final CLOptionDescriptor descriptor = getDescriptorFor( m_option.getId() );
                    final String message =
                        "Unable to parse first argument for option " +
                        getOptionDescription( descriptor );
                    throw new ParseException( message, 0 );
                }
                else
                {
                    m_option.addArgument( token.getValue() );
                }
            }
            else //2nd argument
            {
                final StringBuffer sb = new StringBuffer();

                ch = getChar();
                if( '-' == ch ) m_lastChar = ch;

                while( !isSeperator( ch, ARG2_SEPERATORS ) )
                {
                    sb.append( ch );
                    ch = getChar();
                }

                final String argument = sb.toString();

                //System.out.println( "Arguement:" + argument );

                m_option.addArgument( argument );
                addOption( m_option );
                m_option = null;
                m_state = STATE_NORMAL;
            }
        }

        return true;
    }

    /**
     * Parse Options from Normal mode.
     */
    protected void parseNormal()
        throws ParseException
    {
        if( '-' != ch )
        {
            //Parse the arguments that are not options
            final String argument = nextToken( NULL_SEPERATORS ).getValue();
            addOption( new CLOption( argument ) );
            m_state = STATE_NORMAL;
        }
        else
        {
            getChar(); // strip the -

            if( 0 == peekAtChar() )
            {
                throw new ParseException( "Malformed option -", 0 );
            }
            else
            {
                ch = peekAtChar();

                //if it is a short option then parse it else ...
                if( '-' != ch ) parseShortOption();
                else
                {
                    getChar(); // strip the -
                    //-- sequence .. it can either mean a change of state
                    //to STATE_NO_OPTIONS or else a long option

                    if( 0 == peekAtChar() )
                    {
                        getChar();
                        m_state = STATE_NO_OPTIONS;
                    }
                    else
                    {
                        //its a long option
                        final String optionName = nextToken( ARG_SEPERATORS ).getValue();
                        final CLOptionDescriptor descriptor = getDescriptorFor( optionName );
                        isLong = true;
                        parseOption( descriptor, "--" + optionName );
                    }
                }
            }
        }
    }
}


