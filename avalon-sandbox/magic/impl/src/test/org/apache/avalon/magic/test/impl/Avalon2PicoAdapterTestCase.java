/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1997-2003 The Apache Software Foundation. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *    "This product includes software developed by the
 *    Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software
 *    itself, if and wherever such third-party acknowledgments
 *    normally appear.
 *
 * 4. The names "Avalon", and "Apache Software Foundation"
 *    must not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation. For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.avalon.magic.test.impl;

import java.io.File;

import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.ConsoleLogger;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.DefaultContext;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.apache.avalon.magic.impl.Avalon2PicoAdapter;

import junit.framework.TestCase;

/**
 *
 *
 * @author <a href="mail at leosimons dot com">Leo Simons</a>
 * @version $Id: Avalon2PicoAdapterTestCase.java,v 1.2 2003/08/21 20:58:31 leosimons Exp $
 */
public class Avalon2PicoAdapterTestCase extends TestCase
{
    public void testLogEnabledPicoComponent() throws Exception
    {
        Tweety tweety = (Tweety)Avalon2PicoAdapter.getProxy( TweetyImpl.class );

        AssertionLogger logger = new AssertionLogger( this );
        ContainerUtil.enableLogging( tweety, logger );
        ContainerUtil.contextualize( tweety, new DefaultContext() );
        ContainerUtil.service( tweety, new DefaultServiceManager() );
        ContainerUtil.configure( tweety, new DefaultConfiguration( "blah" ) );
        //ContainerUtil.parameterize( tweety, new Parameters() ); -- from config
        ContainerUtil.initialize( tweety );
        ContainerUtil.start( tweety );

        tweety.chilp();
        assertTrue( "The Logger was not properly passed to the proxied pico-style component", logger.isInfoCalled() );
    }

    public void testContextualizablePicoComponent() throws Exception
    {
        Tweety tweety = (Tweety)Avalon2PicoAdapter.getProxy( TweetyImpl2.class );

        Logger logger = new ConsoleLogger();
        ContainerUtil.enableLogging( tweety, logger );

        AssertionContext context = new AssertionContext();
        context.put( File.class.getName(), new File(".") );
        context.makeReadOnly();
        ContainerUtil.contextualize( tweety, context );
        ContainerUtil.service( tweety, new DefaultServiceManager() );
        ContainerUtil.configure( tweety, new DefaultConfiguration( "blah" ) );
        //ContainerUtil.parameterize( tweety, new Parameters() ); -- from config
        ContainerUtil.initialize( tweety );
        ContainerUtil.start( tweety );

        tweety.chilp();
        assertTrue( "The homedir was not passed by retrieving it from the context", context.isGetCalled() );
    }

    public void testServicablePicoComponent() throws Exception
    {
        Tweety tweety = (Tweety)Avalon2PicoAdapter.getProxy( TweetyImpl2.class );

        Logger logger = new ConsoleLogger();
        ContainerUtil.enableLogging( tweety, logger );

        DefaultContext context = new DefaultContext();
        context.makeReadOnly();
        ContainerUtil.contextualize( tweety, context );

        AssertionServiceManager sm = new AssertionServiceManager();
        sm.put( File.class.getName(), new File(".") );
        sm.makeReadOnly();
        ContainerUtil.service( tweety, sm );
        ContainerUtil.configure( tweety, new DefaultConfiguration( "blah" ) );
        //ContainerUtil.parameterize( tweety, new Parameters() ); -- from config
        ContainerUtil.initialize( tweety );
        ContainerUtil.start( tweety );

        tweety.chilp();
        assertTrue( "The homedir was not passed by retrieving it from the servicemanager", sm.isLookupCalled() );
    }

    public void testConfigurablePicoComponent() throws Exception
    {
        Tweety tweety = (Tweety)Avalon2PicoAdapter.getProxy( TweetyImpl3.class );

        Logger logger = new ConsoleLogger();
        ContainerUtil.enableLogging( tweety, logger );

        DefaultContext context = new DefaultContext();
        context.makeReadOnly();
        ContainerUtil.contextualize( tweety, context );

        DefaultServiceManager sm = new DefaultServiceManager();
        sm.makeReadOnly();
        ContainerUtil.service( tweety, sm );

        AssertionConfiguration conf = new AssertionConfiguration( "blah" );
        conf.setAttribute( "message", "dummy message");
        conf.makeReadOnly();
        ContainerUtil.configure( tweety, conf );
        //ContainerUtil.parameterize( tweety, new Parameters() );
        ContainerUtil.initialize( tweety );
        ContainerUtil.start( tweety );

        tweety.chilp();
        assertTrue( "The message was not passed by retrieving it from the configuration", conf.isGetAttributeCalled() );
    }

    public void testParameterizedPicoComponent() throws Exception
    {
        // note the difference: TweetyImpl4 is 'parameterizable',
        // not configurable
        Tweety tweety = (Tweety)Avalon2PicoAdapter.getProxy( TweetyImpl4.class );

        Logger logger = new ConsoleLogger();
        ContainerUtil.enableLogging( tweety, logger );

        DefaultContext context = new DefaultContext();
        context.makeReadOnly();
        ContainerUtil.contextualize( tweety, context );

        DefaultServiceManager sm = new DefaultServiceManager();
        sm.makeReadOnly();
        ContainerUtil.service( tweety, sm );

        AssertionConfiguration conf = new AssertionConfiguration( "blah" );
        AssertionConfiguration conf2 = new AssertionConfiguration( "parameter" );
        conf2.setAttribute( "name", "message");
        conf2.setAttribute( "value", "empty message");
        conf2.makeReadOnly();
        conf.addChild( conf2 );
        conf.makeReadOnly();
        ContainerUtil.configure( tweety, conf );
        //ContainerUtil.parameterize( tweety, new Parameters() );
        ContainerUtil.initialize( tweety );
        ContainerUtil.start( tweety );

        tweety.chilp();
        assertTrue( "The message was not passed by retrieving it from the configuration", conf2.isGetAttributeCalled() );
    }

    public void testServicedLoggerOverridesLogEnabledLogger() throws Exception
    {
        Tweety tweety = (Tweety)Avalon2PicoAdapter.getProxy( TweetyImpl.class );

        AssertionLogger firstlogger = new AssertionLogger( this );
        ContainerUtil.enableLogging( tweety, firstlogger );
        ContainerUtil.contextualize( tweety, new DefaultContext() );

        AssertionLogger secondlogger = new AssertionLogger( this );
        DefaultServiceManager sm = new DefaultServiceManager();
        sm.put( Logger.class.getName(), secondlogger );
        sm.makeReadOnly();
        ContainerUtil.service( tweety, sm );
        ContainerUtil.configure( tweety, new DefaultConfiguration( "blah" ) );
        //ContainerUtil.parameterize( tweety, new Parameters() ); -- from config
        ContainerUtil.initialize( tweety );
        ContainerUtil.start( tweety );

        tweety.chilp();
        assertFalse( "The first Logger was used by the proxied pico-style component", firstlogger.isInfoCalled() );
        assertTrue( "The second Logger was not used by the proxied pico-style component", secondlogger.isInfoCalled() );
    }

    public void testServicedFileOverridesContextualizedFile() throws Exception
    {
        Tweety tweety = (Tweety)Avalon2PicoAdapter.getProxy( TweetyImpl2.class );

        ConsoleLogger logger = new ConsoleLogger();
        ContainerUtil.enableLogging( tweety, logger );

        AssertionContext context = new AssertionContext();
        context.put( File.class.getName(), new File(".") );
        context.makeReadOnly();
        ContainerUtil.contextualize( tweety, context );

        AssertionServiceManager sm = new AssertionServiceManager();
        sm.put( File.class.getName(), new File(".") );
        sm.makeReadOnly();
        ContainerUtil.service( tweety, sm );
        ContainerUtil.configure( tweety, new DefaultConfiguration( "blah" ) );
        //ContainerUtil.parameterize( tweety, new Parameters() ); -- from config
        ContainerUtil.initialize( tweety );
        ContainerUtil.start( tweety );

        tweety.chilp();
        assertFalse( "The first File was used by the proxied pico-style component", context.isGetCalled() );
        assertTrue( "The second File was not used by the proxied pico-style component", sm.isLookupCalled() );

    }
}
