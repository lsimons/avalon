/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework.test;

import junit.framework.TestCase;
import org.apache.avalon.framework.Enum;

/**
 * TestCase for Enum.
 *
 * @author <a href="mailto:leo.sutic@insprieinfrastructure.com">Leo Sutic</a>
 */
public class EnumTestCase
    extends TestCase
{
    private final static class Color extends Enum 
    {
        public static final Color RED = new Color( "Red" );
        public static final Color GREEN = new Color( "Green" );
        public static final Color BLUE = new Color( "Blue" );
        
        private Color( final String color )
        {
            super( color );
        }
    }
    
    private final static class OtherColor extends Enum 
    {
        public static final OtherColor RED = new OtherColor( "Red" );
        public static final OtherColor GREEN = new OtherColor( "Green" );
        public static final OtherColor BLUE = new OtherColor( "Blue" );
        
        private OtherColor( final String color )
        {
            super( color );
        }
    }
    
    public EnumTestCase( final String name )
    {
        super( name );
    }
    
    public void testEquality ()
    {
        assertTrue( Color.RED.equals( Color.RED ) );
        assertTrue( Color.GREEN.equals( Color.GREEN ) );
        assertTrue( Color.BLUE.equals( Color.BLUE ) );
        
        assertTrue( !OtherColor.RED.equals( Color.RED ) );
        assertTrue( !OtherColor.GREEN.equals( Color.GREEN ) );
        assertTrue( !OtherColor.BLUE.equals( Color.BLUE ) );
        
        assertTrue( !Color.RED.equals( OtherColor.RED ) );
        assertTrue( !Color.GREEN.equals( OtherColor.GREEN ) );
        assertTrue( !Color.BLUE.equals( OtherColor.BLUE ) );
        
        assertTrue( !Color.RED.equals( Color.GREEN ) );
        assertTrue( !Color.GREEN.equals( Color.BLUE ) );
        assertTrue( !Color.BLUE.equals( Color.RED ) );
    }
}
