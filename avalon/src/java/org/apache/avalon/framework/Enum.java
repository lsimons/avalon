/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.framework;

import java.util.Map;

/**
 * Basic enum class for type-safe enums. Should be used as an abstract base. For example:
 *
 * <pre>
 * import org.apache.avalon.framework.Enum;
 *
 * public final class Color extends Enum {
 *   public static final Color RED = new Color( "Red" );
 *   public static final Color GREEN = new Color( "Green" );
 *   public static final Color BLUE = new Color( "Blue" );
 *
 *   private Color( final String color )
 *   {
 *     super( color );
 *   }
 * }
 * </pre>
 *
 * If further operations, such as iterating over all items, are required, the
 * {@link #Enum(String, Map)} constructor can be used to populate a <code>Map</code>, from which
 * further functionality can be derived:
 * <pre>
 * public final class Color extends Enum {
 *   static final Map map = new HashMap();
 *
 *   public static final Color RED = new Color( "Red", map );
 *   public static final Color GREEN = new Color( "Green", map );
 *   public static final Color BLUE = new Color( "Blue", map );

 *   private Color( final String color, final Map map )
 *   {
 *     super( color, map );
 *   }
 *
 *   public static Iterator iterator()
 *   {
 *     return map.values().iterator();
 *   }
 * }
 * </pre>
 *
 * <p>
 * <em>NOTE:</em> between 4.0 and 4.1, the constructors' access has been changed
 * from <code>public</code> to <code>protected</code>. This is to prevent users
 * of the Enum breaking type-safety by defining new Enum items. All Enum items
 * should be defined in the Enum class, as shown above.
 * </p>
 *
 *
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @author <a href="mailto:jefft@apache.org">Jeff Turner</a>
 * @version 1.0
 */
public abstract class Enum
{
    /**
     * The string representation of the Enum.
     */
    private final String        m_name;

    /**
     * Constructor to add a new named item.
     * <p>
     * <em>Note:</em> access changed from <code>public</code> to
     * <code>protected</code> after 4.0. See class description.
     * </p>
     *
     * @param name Name of the item.
     */
    protected Enum( final String name )
    {
        this( name, null );
    }

    /**
     * Constructor to add a new named item.
     * <p>
     * <em>Note:</em> access changed from <code>public</code> to
     * <code>protected</code> after 4.0. See class description.
     * </p>
     *
     * @param name Name of the item.
     * @param map A <code>Map</code>, to which will be added a pointer to the newly constructed
     * object.
     */
    protected Enum( final String name, final Map map )
    {
        m_name = name;
        if( null != map )
        {
            map.put( name, this );
        }
    }

    /**
     * Retrieve the name of this Enum item, set in the constructor.
     * @return the name <code>String</code> of this Enum item
     */
    public final String getName()
    {
        return m_name;
    }

    /**
     * Human readable description of this Enum item. For use when debugging.
     * @return String in the form <code>type[name]</code>, eg.:
     * <code>Color[Red]</code>.
     */
    public String toString()
    {
        return getClass().getName() + "[" + m_name + "]";
    }
}
