/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE file.
 */
package org.apache.cornerstone.demos.httpproxy;

import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.avalon.configuration.Configuration;
import org.apache.avalon.configuration.ConfigurationException;
import org.apache.log.Logger;

/**
 * @author  Paul Hammant <Paul_Hammant@yahoo.com>
 * @version 1.0
 */
public class DefaultHttpFilteringProxyServer
    extends AbstractHttpProxyServer
    implements HttpFilteringProxyServer
{
    // a site may be x.y.z.p.p.hello.com, only check the last two and three parts
    protected HashSet     blockedTwoPartDomains             = new HashSet();
    protected HashSet     blockedThreePartDomains           = new HashSet();
    protected HashSet     cookiesAllowedForTwoPartDomains   = new HashSet();
    protected HashSet     cookiesAllowedForThreePartDomains = new HashSet();

    /**
     * Constructor DefaultHttpFilteringProxyServer
     * This is A proxy server that will filter requests.
     */
    public DefaultHttpFilteringProxyServer()
    {
        super( "Filtering" );
    }

    public void configure( final Configuration configuration )
        throws ConfigurationException
    {
        super.configure( configuration );

        // block listed domains
        final Configuration blockedDomains = configuration.getChild( "blocked-domains" );
        configureBlockedDomains( blockedDomains.getChildren( "blocked-domain" ) );

        // mark some domains as having no cookies sent.
        final Configuration cookieDomains = configuration.getChild( "cookies-allowed-domains" );
        configureCookieDomains( cookieDomains.getChildren("cookies-allowed-domain") );
    }

    protected void configureBlockedDomains( final Configuration[] domains )
        throws ConfigurationException
    {
        for( int i = 0; i < domains.length; i++ )
        {
            blockAllContentFrom( domains[ i ].getAttribute( "domain-name" ), true );
        }
    }

    protected void configureCookieDomains( final Configuration[] domains )
        throws ConfigurationException
    {
        for( int i = 0; i < domains.length; i++ )
        {
            allowCookiesFrom( domains[ i ].getAttribute( "domain-name" ), true );
        }
    }

    public boolean domainAllowed( final String domainName )
    {
        return !isDomainInList( domainName,
                                blockedTwoPartDomains,
                                blockedThreePartDomains );
    }

    public boolean cookieAllowed( final String domainName )
    {
        return isDomainInList( domainName,
                               cookiesAllowedForTwoPartDomains,
                               cookiesAllowedForThreePartDomains );
    }

    private boolean isDomainInList( String domainName,
                                    final HashSet twoPart,
                                    final HashSet threePart )
    {
        domainName = domainName.toLowerCase();

        int dCount = dotCount( domainName );

        if( 0 == dCount )
        {
            // no dot, must mean it's inside the intranet.
            return false;
        }
        else
        {
            String domainName2p = domainName;

            if( dCount >= 2 )
            {
                // three or more parts to the domain name.
                String domainName3p = trimToThreePartDomainName(domainName, dCount);

                if( threePart.contains(domainName3p) )
                {
                    return true; // it's in the three part list
                }

                // get rid of first part to make a two part domain name.
                domainName2p = domainName3p.substring( domainName3p.indexOf('.') + 1,
                                                       domainName3p.length() );
            }

            if( twoPart.contains( domainName2p ) )
            {
                return true; // it's in the two part list.
            }
        }

        return false; // default (Homer's two favorite words).
    }

    /**
     * Method createHttpProxyHandler
     * Factory method, overriding that of parent, to create the right handler for
     * an individual request.
     */
    protected HttpProxyHandler newHttpProxyHandler()
    {
        return new HttpFilteringProxyHandler( this, m_forwardToAnotherProxy );
    }

    public void blockAllContentFrom( final String domainName, final boolean onOff )
    {
        addOrRemoveFromList( domainName,
                             onOff,
                             blockedTwoPartDomains,
                             blockedThreePartDomains );
        getLogger().debug( "blockAllContentFrom " + domainName );
    }

    public void allowCookiesFrom( final String domainName,
                                  final boolean onOff )
    {
        addOrRemoveFromList( domainName,
                             onOff,
                             cookiesAllowedForTwoPartDomains,
                             cookiesAllowedForThreePartDomains );
        getLogger().debug( "allowCookiesFrom " + domainName );
    }

    private void addOrRemoveFromList( String domainName,
                                      final boolean onOff,
                                      final HashSet twoPart,
                                      final HashSet threePart )
    {
        domainName = domainName.toLowerCase();

        int dCount = dotCount( domainName );

        if( onOff )
        {
            if( 1 == dCount ) twoPart.add(domainName);
            else if( dCount >= 2 )
            {
                threePart.add( trimToThreePartDomainName( domainName, dCount ) );
            }
        }
        else
        {
            if( 1 == dCount ) twoPart.remove( domainName );
            else if( dCount >= 2 )
            {
                threePart.remove( trimToThreePartDomainName( domainName, dCount ) );
            }
        }
    }

    private int dotCount( final String domainName )
    {
        int count = 0;
        int ix = domainName.indexOf('.', 0);

        while( -1 != ix )
        {
            count++;
            ix = domainName.indexOf( '.', ix + 1 );
        }

        return count;
    }

    private String trimToThreePartDomainName( final String domainName, int dotCount )
    {
        if( 2 == dotCount ) return domainName;
        else
        {
            int bIx = 0;

            while( dotCount-- > 2 )
            {
                bIx = domainName.indexOf('.', bIx) + 1;
            }

            return domainName.substring( bIx, domainName.length() );
        }
    }

    public String[] getBlockedDomains()
    {
        return new String[] {"TODO"}; // TODO PH
    }

    /**
     * List the domains for which cookies are not sent.
     */
    public String[] getCookieSuppressedDomains()
    {
        return new String[] {"TODO"}; // TODO PH
    }

}
