/*
 * Copyright  The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.excalibur.util.test;

import junit.framework.TestCase;
import org.apache.excalibur.util.StringUtil;

/**
 * This is used to test StringUtil for correctness.
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 */
public final class StringUtilTestCase
    extends TestCase
{
    private static final String S1 = "TestMe";
    private static final String S2 = "TestMeTestMe";
    private static final String S3 = "TestMeTestMeTestMe";
    private static final String S4 = "MeeeMer";
    private static final String S5 = "MeeeeMer";

    private static final String P11 = "eT";
    private static final String P21 = "eT";
    private static final String P31 = "eT";
    private static final String P41 = "ee";
    private static final String P51 = "ee";

    private static final String P12 = "Te";
    private static final String P22 = "Te";
    private static final String P32 = "Te";
    private static final String P42 = "ff";
    private static final String P52 = "ff";

    private static final String SR1 = "TestMe";
    private static final String SR2 = "TestMTeestMe";
    private static final String SR3 = "TestMTeestMTeestMe";
    private static final String SR4 = "MffeMer";
    private static final String SR5 = "MffffMer";

    private static final String ST1 = "TestMe";
    private static final String ST2 = "TestMeT";
    private static final String ST3 = "TestMeT";
    private static final String ST4 = "MeeeMer";
    private static final String ST5 = "MeeeeMe";

    private static final String STN1 = "TestMe";
    private static final String STN2 = "Test...";
    private static final String STN3 = "Test...";
    private static final String STN4 = "MeeeMer";
    private static final String STN5 = "Meee...";

    private static final String[] SS1 = new String[]{"T", "stM"};
    private static final String[] SS2 = new String[]{"T", "stM", "T", "stM"};
    private static final String[] SS3 = new String[]{"T", "stM", "T", "stM", "T", "stM"};
    private static final String[] SS4 = new String[]{"M", "M", "r"};
    private static final String[] SS5 = new String[]{"M", "M", "r"};

    private static final String SP = " ";

    private static final String WU1 = S1 + SP + S2 + SP + S3 + SP + S4 + SP + S5;
    private static final String WW1 =
        "TestMe\n" +
        "TestMeTe\n" +
        "stMe\n" +
        "TestMeTe\n" +
        "stMeTest\n" +
        "Me\n" +
        "MeeeMer\n" +
        "MeeeeMer";
    private static final String WW2 =
        "TestMe\n" +
        "TestMeT\n" +
        "estMe\n" +
        "TestMeT\n" +
        "estMeTe\n" +
        "stMe\n" +
        "MeeeMer\n" +
        "MeeeeMe\n" +
        "r";

    public StringUtilTestCase( final String name )
    {
        super( name );
    }

    public void testReplaceSubString()
        throws Exception
    {
        final String result1 = StringUtil.replaceSubString( S1, P11, P12 );
        final String result2 = StringUtil.replaceSubString( S2, P21, P22 );
        final String result3 = StringUtil.replaceSubString( S3, P31, P32 );
        final String result4 = StringUtil.replaceSubString( S4, P41, P42 );
        final String result5 = StringUtil.replaceSubString( S5, P51, P52 );

        assertEquals( "ReplaceSubString SR1", SR1, result1 );
        assertEquals( "ReplaceSubString SR2", SR2, result2 );
        assertEquals( "ReplaceSubString SR3", SR3, result3 );
        assertEquals( "ReplaceSubString SR4", SR4, result4 );
        assertEquals( "ReplaceSubString SR5", SR5, result5 );
    }

    public void testWordWrap()
        throws Exception
    {
        assertEquals( "WordWrap S1", WW1, StringUtil.wordWrap( WU1, 8, true ) );
        assertEquals( "WordWrap S1", WW2, StringUtil.wordWrap( WU1, 7, true ) );
    }

    public void testTruncate()
        throws Exception
    {
        assertEquals( "Truncate S1", ST1, StringUtil.truncate( S1, 7 ) );
        assertEquals( "Truncate S2", ST2, StringUtil.truncate( S2, 7 ) );
        assertEquals( "Truncate S3", ST3, StringUtil.truncate( S3, 7 ) );
        assertEquals( "Truncate S4", ST4, StringUtil.truncate( S4, 7 ) );
        assertEquals( "Truncate S5", ST5, StringUtil.truncate( S5, 7 ) );
    }

    public void testTruncateNicely()
        throws Exception
    {
        assertEquals( "Truncate Nicely S1", STN1, StringUtil.truncateNicely( S1, 7 ) );
        assertEquals( "Truncate Nicely S2", STN2, StringUtil.truncateNicely( S2, 7 ) );
        assertEquals( "Truncate Nicely S3", STN3, StringUtil.truncateNicely( S3, 7 ) );
        assertEquals( "Truncate Nicely S4", STN4, StringUtil.truncateNicely( S4, 7 ) );
        assertEquals( "Truncate Nicely S5", STN5, StringUtil.truncateNicely( S5, 7 ) );

        assertEquals( "Truncate Nicely 1", ".", StringUtil.truncateNicely( S5, 1 ) );
        assertEquals( "Truncate Nicely 2", "..", StringUtil.truncateNicely( S5, 2 ) );
        assertEquals( "Truncate Nicely 3", "...", StringUtil.truncateNicely( S5, 3 ) );
    }

    public void testSplitString()
        throws Exception
    {
        assertEqualArrays( SS1, StringUtil.split( S1, "e" ) );
        assertEqualArrays( SS2, StringUtil.split( S2, "e" ) );
        assertEqualArrays( SS3, StringUtil.split( S3, "e" ) );
        assertEqualArrays( SS4, StringUtil.split( S4, "e" ) );
        assertEqualArrays( SS5, StringUtil.split( S5, "e" ) );
    }

    public void testConcatStrings()
        throws Exception
    {
        assertEquals( "TestMeTruly", StringUtil.concat( "Test", "Me", "Truly" ) );
        assertEquals( "<prefix:name/>", StringUtil.concat( new String[]{
            "<",
            "prefix",
            ":",
            "name",
            "/>"
        } ) );
    }

    public void testStripWhiteSpace()
    {
        assertEquals( "TestMeTruly", StringUtil.stripWhitespace( " Test Me Truly" ) );
    }

    private void assertEqualArrays( final String[] s1, final String[] s2 )
    {
        assertEquals( "Array Length Equality", s1.length, s2.length );
        assertEquals( "Array Type Equality",
                      s1.getClass().getComponentType(),
                      s2.getClass().getComponentType() );

        for( int i = 0; i < s1.length; i++ )
        {
            assertEquals( "Array Element " + i, s1[ i ], s2[ i ] );
        }
    }
}
