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
            pool3.put( a1 );
            pool3.put( a2 );
            pool3.put( a3 );
            pool3.put( a4 );
            pool3.put( a5 );
            pool3.put( a6 );
            pool3.put( a7 );
            pool3.put( a8 );
            pool3.put( a9 );
            pool3.put( a10 );
            pool3.put( a11 );
            pool3.put( a12 );
            pool3.put( a13 );
            pool3.put( a14 );
            pool3.put( a15 );
        }
        final long pool3End = System.currentTimeMillis();
        final long pool3Duration = pool3End - pool3Start;

        System.gc();
        System.gc();
        Thread.currentThread().sleep( 2 );

        if( logger.isDebugEnabled() ) logger.debug( "FreeMem post 3: " + Runtime.getRuntime().freeMemory() );

        final DefaultPool pool4 = new DefaultPool( C.class, 5, 10 );
        pool4.enableLogging( poolLogger );
        final long pool4Start = System.currentTimeMillis();
        final int pool4Factor = 20;
        final int pool4Loops = TEST_SIZE / pool4Factor;

        for( int i = 0; i < pool4Loops; i++ )
        {
            final Poolable a1 = pool4.get();
            final Poolable a2 = pool4.get();
            final Poolable a3 = pool4.get();
            final Poolable a4 = pool4.get();
            final Poolable a5 = pool4.get();
            final Poolable a6 = pool4.get();
            final Poolable a7 = pool4.get();
            final Poolable a8 = pool4.get();
            final Poolable a9 = pool4.get();
            final Poolable a10 = pool4.get();
            final Poolable a11 = pool4.get();
            final Poolable a12 = pool4.get();
            final Poolable a13 = pool4.get();
            final Poolable a14 = pool4.get();
            final Poolable a15 = pool4.get();
            final Poolable a16 = pool4.get();
            final Poolable a17 = pool4.get();
            final Poolable a18 = pool4.get();
            final Poolable a19 = pool4.get();
            final Poolable a20 = pool4.get();
            pool4.put( a1 );
            pool4.put( a2 );
            pool4.put( a3 );
            pool4.put( a4 );
            pool4.put( a5 );
            pool4.put( a6 );
            pool4.put( a7 );
            pool4.put( a8 );
            pool4.put( a9 );
            pool4.put( a10 );
            pool4.put( a11 );
            pool4.put( a12 );
            pool4.put( a13 );
            pool4.put( a14 );
            pool4.put( a15 );
            pool4.put( a16 );
            pool4.put( a17 );
            pool4.put( a18 );
            pool4.put( a19 );
            pool4.put( a20 );
        }
        final long pool4End = System.currentTimeMillis();
        final long pool4Duration = pool4End - pool4Start;

        if( logger.isDebugEnabled() ) logger.debug( "FreeMem post 4: " + Runtime.getRuntime().freeMemory() );

        final long createStart = System.currentTimeMillis();
        for( int i = 0; i < TEST_SIZE; i++ )
        {
            final Poolable a1 = new C();
        }
        final long createEnd = System.currentTimeMillis();
        final long createDuration = createEnd - createStart;

        if( logger.isDebugEnabled() ) logger.debug( "FreeMem post create: " + Runtime.getRuntime().freeMemory() );

        final double pool1Efficiency = (double)createDuration / (double)pool1Duration * 100.0;
        final double pool2Efficiency = (double)createDuration / (double)pool2Duration * 100.0;
        final double pool3Efficiency = (double)createDuration / (double)pool3Duration * 100.0;
        final double pool4Efficiency = (double)createDuration / (double)pool4Duration * 100.0;

        if( logger.isDebugEnabled() ) logger.debug( "Pool Efficiency for 100% hits: " + pool1Efficiency + "% " );
        if( logger.isInfoEnabled() ) logger.info( "Time Saved for 100% hits: " + ( createDuration - pool1Duration ) + "ms " );

        if( logger.isDebugEnabled() ) logger.debug( "Pool Efficiency for 100% hits and saturated: " + pool2Efficiency + "% " );
        if( logger.isInfoEnabled() ) logger.info( "Time Saved for 100% hits: " + ( createDuration - pool2Duration ) + "ms " );

        if( logger.isDebugEnabled() ) logger.debug( "Pool Efficiency for 60% hits: " + pool3Efficiency + "% " );
        if( logger.isInfoEnabled() ) logger.info( "Time Saved for 60% hits: " + ( createDuration - pool3Duration ) + "ms " );

        if( logger.isDebugEnabled() ) logger.debug( "Pool Efficiency for 50% hits: " + pool4Efficiency + "% " );
        if( logger.isInfoEnabled() ) logger.info( "Time Saved for 50% hits: " + ( createDuration - pool4Duration ) + "ms " );
    }
}
