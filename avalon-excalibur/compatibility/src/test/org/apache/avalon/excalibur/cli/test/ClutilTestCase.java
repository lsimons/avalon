/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

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

 4. The names "Jakarta", "Avalon", "Excalibur" and "Apache Software Foundation"
    must not be used to endorse or promote products derived from this  software
    without  prior written permission. For written permission, please contact
    apache@apache.org.

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
package org.apache.avalon.excalibur.cli.test;

import java.util.List;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.cli.AbstractParserControl;
import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;
import org.apache.avalon.excalibur.cli.ParserControl;

/**
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @deprecated Toolkit deprecated and replaced by http://spice.sourceforge.net/cli/
 */
public final class ClutilTestCase
    extends TestCase
{
    private static final String[] ARGLIST1 = new String[]
    {
        "--you", "are", "--all", "-cler", "kid"
    };

    private static final String[] ARGLIST2 = new String[]
    {
        "-Dstupid=idiot", "are", "--all", "here", "-d"
    };

    private static final String[] ARGLIST3 = new String[]
    {
        //duplicates
        "-Dstupid=idiot", "are", "--all", "--all", "here"
    };

    private static final String[] ARGLIST4 = new String[]
    {
        //incompatable (blee/all)
        "-Dstupid=idiot", "are", "--all", "--blee", "here"
    };

    private static final String[] ARGLIST5 = new String[]
    {
        "-f", "myfile.txt"
    };

    private static final int DEFINE_OPT = 'D';
    private static final int CASE_CHECK_OPT = 'd';
    private static final int YOU_OPT = 'y';
    private static final int ALL_OPT = 'a';
    private static final int CLEAR1_OPT = 'c';
    private static final int CLEAR2_OPT = 'l';
    private static final int CLEAR3_OPT = 'e';
    private static final int CLEAR5_OPT = 'r';
    private static final int BLEE_OPT = 'b';
    private static final int FILE_OPT = 'f';
    private static final int TAINT_OPT = 'T';

    private static final CLOptionDescriptor DEFINE =
        new CLOptionDescriptor( "define",
                                CLOptionDescriptor.ARGUMENTS_REQUIRED_2,
                                DEFINE_OPT,
                                "define" );
    private static final CLOptionDescriptor CASE_CHECK =
        new CLOptionDescriptor( "charCheck",
                                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                CASE_CHECK_OPT,
                                "check character case sensitivity" );
    private static final CLOptionDescriptor YOU =
        new CLOptionDescriptor( "you", CLOptionDescriptor.ARGUMENT_DISALLOWED, YOU_OPT, "you" );

    private static final CLOptionDescriptor CLEAR1 =
        new CLOptionDescriptor( "c", CLOptionDescriptor.ARGUMENT_DISALLOWED, CLEAR1_OPT, "c" );
    private static final CLOptionDescriptor CLEAR2 =
        new CLOptionDescriptor( "l", CLOptionDescriptor.ARGUMENT_DISALLOWED, CLEAR2_OPT, "l" );
    private static final CLOptionDescriptor CLEAR3 =
        new CLOptionDescriptor( "e", CLOptionDescriptor.ARGUMENT_DISALLOWED, CLEAR3_OPT, "e" );
    private static final CLOptionDescriptor CLEAR5 =
        new CLOptionDescriptor( "r", CLOptionDescriptor.ARGUMENT_DISALLOWED, CLEAR5_OPT, "r" );
    private static final CLOptionDescriptor BLEE =
        new CLOptionDescriptor( "blee",
                                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                BLEE_OPT,
                                "blee" );

    private static final CLOptionDescriptor ALL =
        new CLOptionDescriptor( "all",
                                CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                ALL_OPT,
                                "all",
                                new CLOptionDescriptor[]{BLEE} );

    private static final CLOptionDescriptor FILE =
        new CLOptionDescriptor( "file",
                                CLOptionDescriptor.ARGUMENT_REQUIRED,
                                FILE_OPT,
                                "the build file." );
    private static final CLOptionDescriptor TAINT =
        new CLOptionDescriptor( "taint",
                                CLOptionDescriptor.ARGUMENT_OPTIONAL,
                                TAINT_OPT,
                                "turn on tainting checks (optional level)." );

    public ClutilTestCase()
    {
        this( "Command Line Interpreter Test Case" );
    }

    public ClutilTestCase( String name )
    {
        super( name );
    }

    public static void main( final String[] args )
    {
        final ClutilTestCase test = new ClutilTestCase();
        test.testShortOptArgUnenteredBeforeOtherOpt();
    }

    public void testOptionalArgWithSpace()
    {
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            ALL, TAINT
        };

        final String[] args = new String[]{"-T", "param", "-a"};

        final CLArgsParser parser = new CLArgsParser( args, options );

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( "Option count", 3, size );

        final CLOption option0 = (CLOption)clOptions.get( 0 );
        assertEquals( "Option Code: " + option0.getDescriptor().getId(), TAINT_OPT, option0.getDescriptor().getId() );
        assertEquals( "Option Arg: " + option0.getArgument( 0 ),
                      null, option0.getArgument( 0 ) );

        final CLOption option1 = (CLOption)clOptions.get( 1 );
        assertEquals( option1.getDescriptor().getId(), CLOption.TEXT_ARGUMENT );
        assertEquals( option1.getArgument( 0 ), "param" );

        final CLOption option2 = (CLOption)clOptions.get( 2 );
        assertEquals( option2.getDescriptor().getId(), ALL_OPT );
        assertEquals( option2.getArgument( 0 ), null );
    }

    public void testShortOptArgUnenteredBeforeOtherOpt()
    {
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            ALL, TAINT
        };

        final String[] args = new String[]{"-T", "-a"};

        final CLArgsParser parser = new CLArgsParser( args, options );

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( "Option count", 2, size );

        final CLOption option0 = (CLOption)clOptions.get( 0 );
        assertEquals( "Option Code: " + option0.getDescriptor().getId(), TAINT_OPT, option0.getDescriptor().getId() );
        assertEquals( "Option Arg: " + option0.getArgument( 0 ), null, option0.getArgument( 0 ) );

        final CLOption option1 = (CLOption)clOptions.get( 1 );
        assertEquals( option1.getDescriptor().getId(), ALL_OPT );
        assertEquals( option1.getArgument( 0 ), null );
    }

    public void testOptionalArgsWithArgShortBeforeOtherOpt()
    {
        //"-T3","-a"
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            ALL, TAINT
        };

        final String[] args = new String[]{"-T3", "-a"};

        //System.out.println("[before parsing]");

        final CLArgsParser parser = new CLArgsParser( args, options );

        //System.out.println("[after parsing]");

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( size, 2 );
        final CLOption option0 = (CLOption)clOptions.get( 0 );
        assertEquals( option0.getDescriptor().getId(), TAINT_OPT );
        assertEquals( option0.getArgument( 0 ), "3" );

        final CLOption option1 = (CLOption)clOptions.get( 1 );
        assertEquals( ALL_OPT, option1.getDescriptor().getId() );
        assertEquals( null, option1.getArgument( 0 ) );
    }

    public void testOptionalArgsNoArgShortBeforeOtherOpt()
    {
        //"-T","-a"
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            ALL, TAINT
        };

        final String[] args = new String[]{"-T", "-a"};

        //System.out.println("[before parsing]");
        final CLArgsParser parser = new CLArgsParser( args, options );

        //System.out.println("[after parsing]");

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( size, 2 );
        final CLOption option0 = (CLOption)clOptions.get( 0 );
        assertEquals( TAINT_OPT, option0.getDescriptor().getId() );
        assertEquals( null, option0.getArgument( 0 ) );

        final CLOption option1 = (CLOption)clOptions.get( 1 );
        assertEquals( ALL_OPT, option1.getDescriptor().getId() );
        assertEquals( null, option1.getArgument( 0 ) );
    }

    public void testFullParse()
    {
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            YOU, ALL, CLEAR1, CLEAR2, CLEAR3, CLEAR5
        };

        final CLArgsParser parser = new CLArgsParser( ARGLIST1, options );

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( size, 8 );
        assertEquals( ( (CLOption)clOptions.get( 0 ) ).getDescriptor().getId(), YOU_OPT );
        assertEquals( ( (CLOption)clOptions.get( 1 ) ).getDescriptor().getId(), 0 );
        assertEquals( ( (CLOption)clOptions.get( 2 ) ).getDescriptor().getId(), ALL_OPT );
        assertEquals( ( (CLOption)clOptions.get( 3 ) ).getDescriptor().getId(), CLEAR1_OPT );
        assertEquals( ( (CLOption)clOptions.get( 4 ) ).getDescriptor().getId(), CLEAR2_OPT );
        assertEquals( ( (CLOption)clOptions.get( 5 ) ).getDescriptor().getId(), CLEAR3_OPT );
        assertEquals( ( (CLOption)clOptions.get( 6 ) ).getDescriptor().getId(), CLEAR5_OPT );
        assertEquals( ( (CLOption)clOptions.get( 7 ) ).getDescriptor().getId(), 0 );
    }

    public void testDuplicateOptions()
    {
        //"-Dstupid=idiot","are","--all","--all","here"
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            DEFINE, ALL, CLEAR1
        };

        final CLArgsParser parser = new CLArgsParser( ARGLIST3, options );

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( size, 5 );
        assertEquals( ( (CLOption)clOptions.get( 0 ) ).getDescriptor().getId(), DEFINE_OPT );
        assertEquals( ( (CLOption)clOptions.get( 1 ) ).getDescriptor().getId(), 0 );
        assertEquals( ( (CLOption)clOptions.get( 2 ) ).getDescriptor().getId(), ALL_OPT );
        assertEquals( ( (CLOption)clOptions.get( 3 ) ).getDescriptor().getId(), ALL_OPT );
        assertEquals( ( (CLOption)clOptions.get( 4 ) ).getDescriptor().getId(), 0 );
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

        assertEquals( size, 5 );
        assertEquals( ( (CLOption)clOptions.get( 0 ) ).getDescriptor().getId(), DEFINE_OPT );
        assertEquals( ( (CLOption)clOptions.get( 1 ) ).getDescriptor().getId(), 0 );
        assertEquals( ( (CLOption)clOptions.get( 2 ) ).getDescriptor().getId(), ALL_OPT );
        assertEquals( ( (CLOption)clOptions.get( 3 ) ).getDescriptor().getId(), BLEE_OPT );
        assertEquals( ( (CLOption)clOptions.get( 4 ) ).getDescriptor().getId(), 0 );
    }

    public void testSingleArg()
    {
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            FILE
        };

        final CLArgsParser parser = new CLArgsParser( ARGLIST5, options );

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( size, 1 );
        assertEquals( ( (CLOption)clOptions.get( 0 ) ).getDescriptor().getId(), FILE_OPT );
        assertEquals( ( (CLOption)clOptions.get( 0 ) ).getArgument(), "myfile.txt" );
    }

    public void test2ArgsParse()
    {
        //"-Dstupid=idiot","are","--all","here"
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            DEFINE, ALL, CLEAR1, CASE_CHECK
        };

        final CLArgsParser parser = new CLArgsParser( ARGLIST2, options );

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( size, 5 );
        assertEquals( ( (CLOption)clOptions.get( 0 ) ).getDescriptor().getId(), DEFINE_OPT );
        assertEquals( ( (CLOption)clOptions.get( 1 ) ).getDescriptor().getId(), 0 );
        assertEquals( ( (CLOption)clOptions.get( 2 ) ).getDescriptor().getId(), ALL_OPT );
        assertEquals( ( (CLOption)clOptions.get( 3 ) ).getDescriptor().getId(), 0 );
        assertEquals( ( (CLOption)clOptions.get( 4 ) ).getDescriptor().getId(), CASE_CHECK_OPT );

        final CLOption option = (CLOption)clOptions.get( 0 );
        assertEquals( "stupid", option.getArgument( 0 ) );
        assertEquals( "idiot", option.getArgument( 1 ) );
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
                return ( lastOptionCode == YOU_OPT );
            }
        };

        final CLArgsParser parser = new CLArgsParser( ARGLIST1, options, control );

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( size, 1 );
        assertEquals( ( (CLOption)clOptions.get( 0 ) ).getDescriptor().getId(), YOU_OPT );
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
                return ( lastOptionCode == YOU_OPT );
            }
        };

        final CLArgsParser parser1 = new CLArgsParser( ARGLIST1, options1, control1 );

        assertNull( parser1.getErrorString(), parser1.getErrorString() );

        final List clOptions1 = parser1.getArguments();
        final int size1 = clOptions1.size();

        assertEquals( size1, 1 );
        assertEquals( ( (CLOption)clOptions1.get( 0 ) ).getDescriptor().getId(), YOU_OPT );

        final CLArgsParser parser2 =
            new CLArgsParser( parser1.getUnparsedArgs(), options2 );

        assertNull( parser2.getErrorString(), parser2.getErrorString() );

        final List clOptions2 = parser2.getArguments();
        final int size2 = clOptions2.size();

        assertEquals( size2, 7 );
        assertEquals( ( (CLOption)clOptions2.get( 0 ) ).getDescriptor().getId(), 0 );
        assertEquals( ( (CLOption)clOptions2.get( 1 ) ).getDescriptor().getId(), ALL_OPT );
        assertEquals( ( (CLOption)clOptions2.get( 2 ) ).getDescriptor().getId(), CLEAR1_OPT );
        assertEquals( ( (CLOption)clOptions2.get( 3 ) ).getDescriptor().getId(), CLEAR2_OPT );
        assertEquals( ( (CLOption)clOptions2.get( 4 ) ).getDescriptor().getId(), CLEAR3_OPT );
        assertEquals( ( (CLOption)clOptions2.get( 5 ) ).getDescriptor().getId(), CLEAR5_OPT );
        assertEquals( ( (CLOption)clOptions2.get( 6 ) ).getDescriptor().getId(), 0 );
    }

    public void test2PartPartialParse()
    {
        final CLOptionDescriptor[] options1 = new CLOptionDescriptor[]
        {
            YOU, ALL, CLEAR1
        };

        final CLOptionDescriptor[] options2 = new CLOptionDescriptor[]{};

        final ParserControl control1 = new AbstractParserControl()
        {
            public boolean isFinished( final int lastOptionCode )
            {
                return ( lastOptionCode == CLEAR1_OPT );
            }
        };

        final CLArgsParser parser1 = new CLArgsParser( ARGLIST1, options1, control1 );

        assertNull( parser1.getErrorString(), parser1.getErrorString() );

        final List clOptions1 = parser1.getArguments();
        final int size1 = clOptions1.size();

        assertEquals( size1, 4 );
        assertEquals( ( (CLOption)clOptions1.get( 0 ) ).getDescriptor().getId(), YOU_OPT );
        assertEquals( ( (CLOption)clOptions1.get( 1 ) ).getDescriptor().getId(), 0 );
        assertEquals( ( (CLOption)clOptions1.get( 2 ) ).getDescriptor().getId(), ALL_OPT );
        assertEquals( ( (CLOption)clOptions1.get( 3 ) ).getDescriptor().getId(), CLEAR1_OPT );

        assertTrue( parser1.getUnparsedArgs()[ 0 ].equals( "ler" ) );

        final CLArgsParser parser2 =
            new CLArgsParser( parser1.getUnparsedArgs(), options2 );

        assertNull( parser2.getErrorString(), parser2.getErrorString() );

        final List clOptions2 = parser2.getArguments();
        final int size2 = clOptions2.size();

        assertEquals( size2, 2 );
        assertEquals( ( (CLOption)clOptions2.get( 0 ) ).getDescriptor().getId(), 0 );
        assertEquals( ( (CLOption)clOptions2.get( 1 ) ).getDescriptor().getId(), 0 );
    }

    public void testDuplicatesFail()
    {
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            YOU, ALL, CLEAR1, CLEAR2, CLEAR3, CLEAR5
        };

        final CLArgsParser parser = new CLArgsParser( ARGLIST1, options );

        assertNull( parser.getErrorString(), parser.getErrorString() );
    }

    public void testIncomplete2Args()
    {
        //"-Dstupid="
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            DEFINE
        };

        final CLArgsParser parser = new CLArgsParser( new String[]{"-Dstupid="}, options );

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( size, 1 );
        final CLOption option = (CLOption)clOptions.get( 0 );
        assertEquals( option.getDescriptor().getId(), DEFINE_OPT );
        assertEquals( option.getArgument( 0 ), "stupid" );
        assertEquals( option.getArgument( 1 ), "" );
    }

    public void testIncomplete2ArgsMixed()
    {
        //"-Dstupid=","-c"
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            DEFINE, CLEAR1
        };

        final String[] args = new String[]{"-Dstupid=", "-c"};

        final CLArgsParser parser = new CLArgsParser( args, options );

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( size, 2 );
        assertEquals( ( (CLOption)clOptions.get( 1 ) ).getDescriptor().getId(), CLEAR1_OPT );
        final CLOption option = (CLOption)clOptions.get( 0 );
        assertEquals( option.getDescriptor().getId(), DEFINE_OPT );
        assertEquals( option.getArgument( 0 ), "stupid" );
        assertEquals( option.getArgument( 1 ), "" );
    }

    public void fail_testIncomplete2ArgsMixedNoEq()
    {
        //"-Dstupid","-c"
        final CLOptionDescriptor[] options = new CLOptionDescriptor[]
        {
            DEFINE, CLEAR1
        };

        final String[] args = new String[]{"-Dstupid", "-c"};

        final CLArgsParser parser = new CLArgsParser( args, options );

        assertNull( parser.getErrorString(), parser.getErrorString() );

        final List clOptions = parser.getArguments();
        final int size = clOptions.size();

        assertEquals( size, 2 );
        assertEquals( ( (CLOption)clOptions.get( 1 ) ).getDescriptor().getId(), CLEAR1_OPT );
        final CLOption option = (CLOption)clOptions.get( 0 );
        assertEquals( option.getDescriptor().getId(), DEFINE_OPT );
        assertEquals( option.getArgument( 0 ), "stupid" );
        assertEquals( option.getArgument( 1 ), "" );
    }

    /**
     * Test the getArgumentById and getArgumentByName lookup methods.
     */
    public void testArgumentLookup()
    {
        final String[] args = {"-f", "testarg"};
        final CLOptionDescriptor[] options = {FILE};
        final CLArgsParser parser = new CLArgsParser( args, options );

        CLOption optionById = parser.getArgumentById( FILE_OPT );
        assertNotNull( optionById );
        assertEquals( FILE_OPT, optionById.getDescriptor().getId() );

        CLOption optionByName = parser.getArgumentByName( FILE.getName() );
        assertNotNull( optionByName );
        assertEquals( FILE_OPT, optionByName.getDescriptor().getId() );
    }

    /**
     * Test that you can have null long forms.
     */
    public void testNullLongForm()
    {
        final CLOptionDescriptor test =
            new CLOptionDescriptor( null,
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    'n',
                                    "test null long form" );

        final String[] args = {"-n", "testarg"};
        final CLOptionDescriptor[] options = {test};
        final CLArgsParser parser = new CLArgsParser( args, options );

        final CLOption optionByID = parser.getArgumentById( 'n' );
        assertNotNull( optionByID );
        assertEquals( 'n', optionByID.getDescriptor().getId() );

        final CLOption optionByName = parser.getArgumentByName( FILE.getName() );
        assertNull( "Looking for non-existent option by name", optionByName );
    }

    /**
     * Test that you can have null descriptions.
     */
    public void testNullDescription()
    {
        final CLOptionDescriptor test =
            new CLOptionDescriptor( "nulltest",
                                    CLOptionDescriptor.ARGUMENT_DISALLOWED,
                                    'n',
                                    null );

        final String[] args = {"-n", "testarg"};
        final CLOptionDescriptor[] options = {test};
        final CLArgsParser parser = new CLArgsParser( args, options );

        final CLOption optionByID = parser.getArgumentById( 'n' );
        assertNotNull( optionByID );
        assertEquals( 'n', optionByID.getDescriptor().getId() );

        final StringBuffer sb = CLUtil.describeOptions( options );
        final String lineSeparator = System.getProperty( "line.separator" );
        assertEquals( "Testing display of null description",
                      "\t-n, --nulltest" + lineSeparator,
                      sb.toString() );
    }
}
