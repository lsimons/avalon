/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.cache.validator;

import org.apache.avalon.excalibur.cache.CacheValidator;

/**
 * @author <a href="mailto:colus@apache.org">Eung-ju Park</a>
 */
public class AndValidator
    implements CacheValidator
{
    private CacheValidator[] m_validators;

    public AndValidator( final CacheValidator validator1,
                         final CacheValidator validator2 )
    {
        m_validators = new CacheValidator[ 2 ];
        m_validators[ 0 ] = validator1;
        m_validators[ 1 ] = validator2;
    }

    public AndValidator( final CacheValidator[] validators )
    {
        m_validators = validators;
    }

    public boolean validate( final Object key, final Object value )
    {
        for( int i = 0; i < m_validators.length; i++ )
        {
            if( !m_validators[ i ].validate( key, value ) )
            {
                return false;
            }
        }

        return true;
    }
}
