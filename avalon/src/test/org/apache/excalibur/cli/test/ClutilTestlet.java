/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.excalibur.cli.test;

import java.util.List;
import org.apache.excalibur.cli.AbstractParserControl;
import org.apache.excalibur.cli.CLArgsParser;
import org.apache.excalibur.cli.CLOption;
import org.apache.excalibur.cli.CLOptionDescriptor;
import org.apache.excalibur.cli.ParserControl;
import org.apache.testlet.AbstractTestlet;

/**
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class ClutilTestlet
    extends AbstractTestlet
{ 
    protected final static String[] ARGLIST1 = 
    {
        "--you","are","--all","-cler","kid"
    };

    protected final static String[] ARGLIST2 = 
    {
        "-Dstupid=idiot","are","--all","here"
    };
    
    protected final static String[] ARGLIST3 = 
    {
        //duplicates
        "-Dstupid=idiot","are","--all","--all","here"
    };
    
    protected final static String[] ARGLIST4 = 
    {
        //incompatable (blee/all)
        "-Dstupid=idiot","are","--all","--blee","here"
    };

    protected final static String[] ARGLIST5 = 
    {
        "-f","myfile.txt"
    };

    private static final int                DEFINE_OPT        = 'D';
    private static final int                YOU_OPT           = 'y';
    private static final int                ALL_OPT           = 'a';
    private static final int                CLEAR1_OPT        = 'c';
    private static final int                CLEAR2_OPT        = 'l';
    private static final int                CLEAR3_OPT        = 'e';
    private static final int                CLEAR5_OPT        = 'r';
    private static final int                BLEE_OPT          = 'b';
    private static final int                FILE_OPT          = 'f';

    protected final static CLOptionDescriptor DEFINE          =
        new CLOptionDescriptor( "define", 
                                CLOptionDescriptor.ARGUMENTS_REQUIRED_2, 
                                DEFINE_OPT, 
                                "define" );
    protected final static CLOptionDescriptor YOU             =
        new CLOptionDescriptor( "you", CLOptionDescriptor.ARGUMENT_DISALLOWED, YOU_OPT, "you" );

    protected final static CLOptionDescriptor ALL             =
        new CLOptionDescriptor( "all", 
                                CLOptionDescriptor.ARGUMENT_DISALLOWED, 
                                ALL_OPT, 
                                "all",
                                new int[] { BLEE_OPT } );

    protected final static CLOptionDescriptor CLEAR1          =
        new CLOptionDescriptor( "c", CLOptionDescriptor.ARGUMENT_DISALLOWED, CLEAR1_OPT, "c" );
    protected final static CLOptionDescriptor CLEAR2          =
        new CLOptionDescriptor( "l", CLOptionDescriptor.ARGUMENT_DISALLOWED, CLEAR2_OPT, "l" );
    protected final static CLOptionDescriptor CLEAR3          =
        new CLOptionDescriptor( "e", CLOptionDescriptor.ARGUMENT_DISALLOWED, CLEAR3_OPT, "e" );
    protected final static CLOptionDescriptor CLEAR5          =
        new CLOptionDescriptor( "r", CLOptionDescriptor.ARGUMENT_DISALLOWED, CLEAR5_OPT, "r" );
    protected final static CLOptionDescriptor BLEE            =
        new CLOptionDescriptor( "blee", 
                                CLOptionDescriptor.ARGUMENT_DISALLOWED, 
                                BLEE_OPT, 
                                "blee" );
    protected final static CLOptionDescriptor FILE            =
        new CLOptionDescriptor( "file",
                                CLOptionDescriptor.ARGUMENT_REQUIRED,
                                FILE_OPT,
                                "the build file." );

    public void testFullParse()
    {
        final CLOptionDescriptor[] options = new CLOptionDescriptor[] 
        {
            YOU, ALL, CLEAR1, CLEAR2, CLEAR3, CLEAR5
        };
        
        final CLArgsParser parser = new CLArgsParser( ARGLIST1, options );
        
        assertNull( parser.getErrorString() );
        
        final List clOptions = parser.getArguments();
        final int size = clOptions.size();
        
        assertEquality( size, 8 );
        assertEquality( ((CLOption)clOptions.get( 0 )).getId(), YOU_OPT );
        assertEquality( ((CLOption)clOptions.get( 1 )).getId(), 0 );
        assertEquality( ((CLOption)clOptions.get( 2 )).getId(), ALL_OPT );
        assertEquality( ((CLOption)clOptions.get( 3 )).getId(), CLEAR1_OPT );
        assertEquality( ((CLOption)clOptions.get( 4 )).getId(), CLEAR2_OPT );
        assertEquality( ((CLOption)clOptions.get( 5 )).getId(), CLEAR3_OPT );
        assertEquality( ((CLOption)clOptions.get( 6 )).getId(), CLEAR5_OPT );
        assertEquality( ((CLOption)clOptions.get( 7 )).getId(), 0 );
    }

    public void testDuplicateOptions()
    {
        //"-Dstupid=idiot","are","--all","--all","here"
        final CLOptionDescriptor[] options = new CLOptionDescriptor[] 
        {
            DEFINE, ALL, CLEAR1
        };

        final CLArgsParser parser = new CLArgsParser( ARGLIST3, options );

        assertNull( parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquality( size, 5 );
        assertEquality( ((CLOption)clOptions.get( 0 )).getId(), DEFINE_OPT );
        assertEquality( ((CLOption)clOptions.get( 1 )).getId(), 0 );
        assertEquality( ((CLOption)clOptions.get( 2 )).getId(), ALL_OPT );
        assertEquality( ((CLOption)clOptions.get( 3 )).getId(), ALL_OPT );
        assertEquality( ((CLOption)clOptions.get( 4 )).getId(), 0 );
    }
    
    public void testIncompatableOptions()
    {
        final CLOptionDescriptor[] options = new CLOptionDescriptor[] 
        {
            DEFINE, ALL, CLEAR1, BLEE
        };
        
        final CLArgsParser parser = new CLArgsParser( ARGLIST4, options );
        
        assertNotNull( parser.getErrorString() );
        
        final List clOptions = parser.getArguments();
        final int size = clOptions.size();
        
        assertEquality( size, 5 );
        assertEquality( ((CLOption)clOptions.get( 0 )).getId(), DEFINE_OPT );
        assertEquality( ((CLOption)clOptions.get( 1 )).getId(), 0 );
        assertEquality( ((CLOption)clOptions.get( 2 )).getId(), ALL_OPT );
        assertEquality( ((CLOption)clOptions.get( 3 )).getId(), BLEE_OPT );
        assertEquality( ((CLOption)clOptions.get( 4 )).getId(), 0 );
    }        

    public void testSingleArg()
    {
        final CLOptionDescriptor[] options = new CLOptionDescriptor[] 
        {
            FILE
        };
        
        final CLArgsParser parser = new CLArgsParser( ARGLIST5, options );
        
        assertNull( parser.getErrorString() );
        
        final List clOptions = parser.getArguments();
        final int size = clOptions.size();
        
        assertEquality( size, 1 );
        assertEquality( ((CLOption)clOptions.get( 0 )).getId(), FILE_OPT );
        assertEquality( ((CLOption)clOptions.get( 0 )).getArgument(), "myfile.txt" );
    }        

    public void test2ArgsParse()
    {
        //"-Dstupid=idiot","are","--all","here"
        final CLOptionDescriptor[] options = new CLOptionDescriptor[] 
        {
            DEFINE, ALL, CLEAR1
        };
        
        final CLArgsParser parser = new CLArgsParser( ARGLIST2, options );
        
        assertNull( parser.getErrorString() );
        
        final List clOptions = parser.getArguments();
        final int size = clOptions.size();
        
        assertEquality( size, 4 );
        assertEquality( ((CLOption)clOptions.get( 0 )).getId(), DEFINE_OPT );
        assertEquality( ((CLOption)clOptions.get( 1 )).getId(), 0 );
        assertEquality( ((CLOption)clOptions.get( 2 )).getId(), ALL_OPT );
        assertEquality( ((CLOption)clOptions.get( 3 )).getId(), 0 );
        
        final CLOption option = (CLOption)clOptions.get( 0 );
        assertEquality( "stupid", option.getArgument( 0 ) );
        assertEquality( "idiot", option.getArgument( 1 ) );
    }

    public void testPartParse()
    {
        final CLOptionDescriptor[] options = new CLOptionDescriptor[] 
        {
            YOU
        };

        final ParserControl control = new AbstractParserControl() 
        {
            public boolean isFinished( int lastOptionCode )
            {
                return (lastOptionCode == YOU_OPT);
            }
        };

        final CLArgsParser parser = new CLArgsParser( ARGLIST1, options, control );

        assertNull( parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquality( size, 1 );
        assertEquality( ((CLOption)clOptions.get( 0 )).getId(), YOU_OPT );
    }

    public void test2PartParse()
    {
        final CLOptionDescriptor[] options1 = new CLOptionDescriptor[] 
        {
            YOU
        };

        final CLOptionDescriptor[] options2 = new CLOptionDescriptor[] 
        {
            ALL, CLEAR1, CLEAR2, CLEAR3, CLEAR5
        };

        final ParserControl control1 = new AbstractParserControl() 
        {
            public boolean isFinished( int lastOptionCode )
            {
                return (lastOptionCode == YOU_OPT);
            }
        };

        final CLArgsParser parser1 = new CLArgsParser( ARGLIST1, options1, control1 );

        assertNull( parser1.getErrorString() );

        final List clOptions1 = parser1.getArguments();
        final int size1 = clOptions1.size();

        assertEquality( size1, 1 );
        assertEquality( ((CLOption)clOptions1.get( 0 )).getId(), YOU_OPT );

        final CLArgsParser parser2 = 
            new CLArgsParser( parser1.getUnparsedArgs(), options2 );

        assertNull( parser2.getErrorString() );

        final List clOptions2 = parser2.getArguments();
        final int size2 = clOptions2.size();

        assertEquality( size2, 7 );
        assertEquality( ((CLOption)clOptions2.get( 0 )).getId(), 0 );
        assertEquality( ((CLOption)clOptions2.get( 1 )).getId(), ALL_OPT );
        assertEquality( ((CLOption)clOptions2.get( 2 )).getId(), CLEAR1_OPT );
        assertEquality( ((CLOption)clOptions2.get( 3 )).getId(), CLEAR2_OPT );
        assertEquality( ((CLOption)clOptions2.get( 4 )).getId(), CLEAR3_OPT );
        assertEquality( ((CLOption)clOptions2.get( 5 )).getId(), CLEAR5_OPT );
        assertEquality( ((CLOption)clOptions2.get( 6 )).getId(), 0 );
    }

    public void test2PartPartialParse()
    {
        final CLOptionDescriptor[] options1 = new CLOptionDescriptor[] 
        {
            YOU, ALL, CLEAR1
        };

        final CLOptionDescriptor[] options2 = new CLOptionDescriptor[] {};

        final ParserControl control1 = new AbstractParserControl() 
        {
            public boolean isFinished( final int lastOptionCode )
            {
                return (lastOptionCode == CLEAR1_OPT);
            }
        };

        final CLArgsParser parser1 = new CLArgsParser( ARGLIST1, options1, control1 );

        assertNull( parser1.getErrorString() );

        final List clOptions1 = parser1.getArguments();
        final int size1 = clOptions1.size();

        assertEquality( size1, 4 );
        assertEquality( ((CLOption)clOptions1.get( 0 )).getId(), YOU_OPT );
        assertEquality( ((CLOption)clOptions1.get( 1 )).getId(), 0 );
        assertEquality( ((CLOption)clOptions1.get( 2 )).getId(), ALL_OPT );
        assertEquality( ((CLOption)clOptions1.get( 3 )).getId(), CLEAR1_OPT );

        assert( parser1.getUnparsedArgs()[0].equals("ler") );
        
        final CLArgsParser parser2 = 
            new CLArgsParser( parser1.getUnparsedArgs(), options2 );

        assertNull( parser2.getErrorString() );

        final List clOptions2 = parser2.getArguments();
        final int size2 = clOptions2.size();

        assertEquality( size2, 2 );
        assertEquality( ((CLOption)clOptions2.get( 0 )).getId(), 0 );
        assertEquality( ((CLOption)clOptions2.get( 1 )).getId(), 0 );
    }

    
    public void testDuplicatesFail()
    {
        final CLOptionDescriptor[] options = new CLOptionDescriptor[] 
        {
            YOU, ALL, CLEAR1, CLEAR2, CLEAR3, CLEAR5
        };

        //duplicate as
        final String[] DUPLICATE_ARGLIST = 
        {
            "--you","are","--all","-clear","kid"
        };
        
        final CLArgsParser parser = new CLArgsParser( ARGLIST1, options );
        
        assertNull( parser.getErrorString() );
    }

    public void testIncomplete2Args()
    {
        //"-Dstupid="
        final CLOptionDescriptor[] options = new CLOptionDescriptor[] 
        {
            DEFINE
        };

        final CLArgsParser parser = new CLArgsParser( new String[] { "-Dstupid=" }, options );

        assertNull( parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquality( size, 1 );
        final CLOption option = (CLOption)clOptions.get( 0 );
        assertEquality( option.getId(), DEFINE_OPT );
        assertEquality( option.getArgument( 0 ), "stupid" );
        assertEquality( option.getArgument( 1 ), "" );
    }
    
    public void testIncomplete2ArgsMixed()
    {
        //"-Dstupid=","-c"
        final CLOptionDescriptor[] options = new CLOptionDescriptor[] 
        {
            DEFINE, CLEAR1
        };

        final String[] args = new String[] { "-Dstupid=", "-c" };

        final CLArgsParser parser = new CLArgsParser( args, options );
        
        assertNull( parser.getErrorString() );
        
        final List clOptions = parser.getArguments();
        final int size = clOptions.size();
        
        assertEquality( size, 2 );
        assertEquality( ((CLOption)clOptions.get( 1 )).getId(), CLEAR1_OPT );
        final CLOption option = (CLOption)clOptions.get( 0 );
        assertEquality( option.getId(), DEFINE_OPT );
        assertEquality( option.getArgument( 0 ), "stupid" );
        assertEquality( option.getArgument( 1 ), "" );
    }

    public void fail_testIncomplete2ArgsMixedNoEq()
    {
        //"-Dstupid","-c"
        final CLOptionDescriptor[] options = new CLOptionDescriptor[] 
        {
            DEFINE, CLEAR1
        };
        
        final String[] args = new String[] { "-Dstupid", "-c" };
        
        final CLArgsParser parser = new CLArgsParser( args, options );
        
        assertNull( parser.getErrorString() );
        
        final List clOptions = parser.getArguments();
        final int size = clOptions.size();
        
        assertEquality( size, 2 );
        assertEquality( ((CLOption)clOptions.get( 1 )).getId(), CLEAR1_OPT );
        final CLOption option = (CLOption)clOptions.get( 0 );
        assertEquality( option.getId(), DEFINE_OPT );
        assertEquality( option.getArgument( 0 ), "stupid" );
        assertEquality( option.getArgument( 1 ), "" );
    }
}
