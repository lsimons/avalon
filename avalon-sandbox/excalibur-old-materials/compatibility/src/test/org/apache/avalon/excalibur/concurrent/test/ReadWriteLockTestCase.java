/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.excalibur.concurrent.test;

import junit.framework.TestCase;

import org.apache.avalon.excalibur.concurrent.ReadWriteLock;

/**
 * Test harness for the ReadWriteLock.
 *
 * @author <a href="mailto:leo.sutic@inspireinfrastructure.com">Leo Sutic</a>
 */
public class ReadWriteLockTestCase
    extends TestCase
{
    /**
     * Worker thread that attempts to
     * aquire a write lock. Start it, wait a little while
     * and call the hasSuccess() method.
     */
    static class TriesWriteLock
        extends Thread
    {
        protected ReadWriteLock m_lock;
        protected boolean m_success = false;

        public TriesWriteLock( ReadWriteLock lock )
        {
            m_lock = lock;
        }

        public boolean hasSuccess()
        {
            return m_success;
        }

        public void run()
        {
            try
            {
                m_lock.aquireWrite();
                m_success = true;
            }
            catch( Exception e )
            {
                // Ignore. Errors are handled by the testlet
                // monitoring hasSuccess().
            }
        }
    }

    /**
     * Worker thread that attempts to
     * aquire a read lock. Start it, wait a little while
     * and call the hasSuccess() method.
     */
    static class TriesReadLock
        extends Thread
    {
        protected ReadWriteLock m_lock;
        protected boolean m_success = false;

        public TriesReadLock( final ReadWriteLock lock )
        {
            m_lock = lock;
        }

        public boolean hasSuccess()
        {
            return m_success;
        }

        public void run()
        {
            try
            {
                m_lock.aquireRead();
                m_success = true;
            }
            catch( final Exception e )
            {
                // Ignore. Errors are handled by the testlet
                // monitoring hasSuccess().
            }
        }
    }

    public ReadWriteLockTestCase( String name )
    {
        super( name );
    }

    /**
     * Attempt to aquire and release read and write locks from
     * different threads.
     */
    public void testRWLock()
        throws Exception
    {
        final ReadWriteLock lock = new ReadWriteLock();
        final TriesWriteLock wl = new TriesWriteLock( lock );
        final TriesReadLock rl = new TriesReadLock( lock );

        rl.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( "Attempted to aquire read lock.", rl.hasSuccess() );

        wl.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( "Attempted to aquire write lock.", !wl.hasSuccess() );

        lock.release();

        Thread.currentThread().sleep( 100 );
        assertTrue( "Attempted to aquire write lock after releasing read lock.",
                    wl.hasSuccess() );

        lock.release();

        //
        // And see that the write lock is released properly.
        //
        final TriesReadLock r2 = new TriesReadLock( lock );
        r2.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( "Attempted to aquire read lock.", r2.hasSuccess() );

        lock.release();
    }

    /**
     * Test that the lock throws an IllegalStateException when
     * one attempts to release an already released lock.
     */
    public void testIllegalState() throws Exception
    {
        ReadWriteLock lock = new ReadWriteLock();
        try
        {
            lock.release();
            fail( "ReadWriteLock did *not* signal illegal state when an attempt was made to release an unlocked lock." );
        }
        catch( IllegalStateException ise )
        {
            // OK, we should receive this one.
        }
    }

    /**
     * Tests that attempts to aquire a write lock
     * are given higher priority than attempts
     * at aquiring a read lock.
     */
    public void testMultipleWriters() throws Exception
    {
        ReadWriteLock lock = new ReadWriteLock();
        TriesReadLock rla = new TriesReadLock( lock );
        TriesReadLock rlb = new TriesReadLock( lock );
        TriesWriteLock wla = new TriesWriteLock( lock );
        TriesWriteLock wlb = new TriesWriteLock( lock );

        rla.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( "Attempted to aquire read lock.", rla.hasSuccess() );

        wla.start();
        wlb.start();

        //
        // Give the write lock threads some time to attempt
        // to aquire a lock.
        //
        Thread.currentThread().sleep( 100 );

        rlb.start();
        Thread.currentThread().sleep( 100 );

        //
        // Two write locks queued up, one read lock queued up.
        //
        //      rla holds read lock
        // wlb, wla -> (read lock is held)
        //      rlb -> (has waiting write locks)
        //
        assertTrue( "Attempted to aquire write lock, and succeeded even though it shouldn't be possible (rla has the lock).",
                    !wla.hasSuccess() && !wlb.hasSuccess() && !rlb.hasSuccess() );

        //
        // Upon releasing the lock, the write lock attempt should succeed, while the read
        // lock should still be waiting.
        //
        lock.release();
        Thread.currentThread().sleep( 100 );

        //
        // One write lock queued up, one read lock queued up.
        //
        //      wla or wlb holds write lock
        // wlb || wla -> (write lock is held)
        //        rlb -> (has waiting write lock)
        //
        assertTrue( "Attempted to aquire write lock after releasing read lock.",
                    ( wla.hasSuccess() || wlb.hasSuccess() ) && !rlb.hasSuccess()
                    && !( wla.hasSuccess() && wlb.hasSuccess() ) );

        //
        // Release write lock again. The other one of wla and wlb should grab the lock.
        //
        lock.release();
        Thread.currentThread().sleep( 100 );

        //
        // Two write locks queued up, one read lock queued up.
        //
        //      wla or wlb holds write lock
        //        rlb -> (write lock is held)
        //
        assertTrue( "Attempted to aquire write lock after releasing read lock.",
                    wla.hasSuccess() && wlb.hasSuccess() && !rlb.hasSuccess() );

        //
        // Release the lock - the waiting read lock should grab it.
        //
        lock.release();
        Thread.currentThread().sleep( 100 );
        assertTrue( "Attempted to aquire write lock after releasing read lock.",
                    wla.hasSuccess() && wlb.hasSuccess() && rlb.hasSuccess() );
    }

    /**
     * Tests that the lock behaves correctly when
     * multiple read locks are obtained.
     */
    public void testMultipleReaders() throws Exception
    {
        ReadWriteLock lock = new ReadWriteLock();
        TriesReadLock rla = new TriesReadLock( lock );
        TriesReadLock rlb = new TriesReadLock( lock );
        TriesWriteLock wla = new TriesWriteLock( lock );

        rla.start();
        rlb.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( "Attempted to aquire read multiple read locks.",
                    rla.hasSuccess() && rlb.hasSuccess() );

        wla.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( "Write lock aquired even though read locks are held.", !wla.hasSuccess() );

        lock.release();
        Thread.currentThread().sleep( 100 );
        assertTrue( "Write lock aquired even though read locks are held. (There should be one read lock left)",
                    !wla.hasSuccess() );

        lock.release();
        Thread.currentThread().sleep( 100 );
        assertTrue( "Write lock not aquired even though lock should be released.",
                    wla.hasSuccess() );
    }

    /**
     * Tests the tryAquireXXX methods.
     */
    public void testTrying() throws Exception
    {
        ReadWriteLock lock = new ReadWriteLock();
        TriesReadLock rla = new TriesReadLock( lock );
        TriesReadLock rlb = new TriesReadLock( lock );
        TriesWriteLock wla = new TriesWriteLock( lock );
        TriesWriteLock wlb = new TriesWriteLock( lock );

        //
        // Grab a read lock, try to aquire one more (should work),
        // and try aquiring a write lock (should not work).
        //
        rla.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( "Could not aquire a read lock.", rla.hasSuccess() );

        assertTrue( "Could not aquire a read lock, even though only a read lock is held.",
                    lock.tryAquireRead() );

        assertTrue( "Could aquire a write lock.", !lock.tryAquireWrite() );

        //
        // Release both locks.
        //
        lock.release();
        lock.release();

        //
        // Try aquiring a write lock (should work), a read
        // lock (should fail) and another write lock (should fail).
        //
        assertTrue( "Could not aquire a write lock.", lock.tryAquireWrite() );
        assertTrue( "Could aquire a read lock.", !lock.tryAquireRead() );
        assertTrue( "Could aquire a write lock.", !lock.tryAquireWrite() );

        //
        // Release the write lock.
        //
        lock.release();

        assertTrue( "Could not aquire a write lock after releasing the lock.",
                    lock.tryAquireWrite() );
    }

    /**
     * Tests a condition pointed out to me (L.Sutic) by Avi Drissman
     * <a href="drissman@acm.org">drissman@acm.org</a>. If you hold
     * a read lock, and a thread waiting for a write lock is interrupted,
     * there is no way to aquire a read lock again.
     *
     * (This condition is fixed, 2001-10-31.)
     */
    public void testDeadLock() throws Exception
    {
        ReadWriteLock lock = new ReadWriteLock();
        TriesReadLock rla = new TriesReadLock( lock );
        TriesReadLock rlb = new TriesReadLock( lock );
        TriesWriteLock wla = new TriesWriteLock( lock );
        TriesWriteLock wlb = new TriesWriteLock( lock );

        //
        // Grab a read lock.
        //
        rla.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( rla.hasSuccess() );

        //
        // Try to grab a write lock. (The attempt stalls,
        // because we are holding a read lock.)
        //
        wla.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( !wla.hasSuccess() );

        //
        // Interupt the thread waiting for the write lock...
        //
        wla.interrupt();

        //
        // ...and release the read lock.
        //
        lock.release();

        //
        // Right, we are in the condition described by Drissman.
        // Now try to aquire, in turn, a read and a write lock.
        // Before the fix, the assertion immediately below
        // would fail.
        //
        rlb.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( rlb.hasSuccess() );
        lock.release();

        wlb.start();
        Thread.currentThread().sleep( 100 );
        assertTrue( wlb.hasSuccess() );
        lock.release();
    }
}


