
import org.apache.avalon.framework.CascadingRuntimeException;
import org.apache.excalibur.event.DefaultQueue;
import org.apache.excalibur.event.Queue;
import org.apache.excalibur.event.QueueElement;
import org.apache.excalibur.event.Sink;
import org.apache.excalibur.event.SinkException;
import org.apache.excalibur.event.Source;

/**
 * Simple test to expose the thread queue bug
 *
 * @author <a href="mailto:proyal@managingpartners.com">Peter Royal</a>
 * @version VSS $Revision: 1.3 $ $Date: 2002/05/13 12:17:41 $
 */
public class QueueTest
{
    private QueueStart start;
    private QueueEnd end;

    private Queue queue;

    private Thread[] stages;

    public static void main( String[] args ) throws Exception
    {
        QueueTest qt = new QueueTest();

        qt.initialize( Integer.parseInt( args[ 0 ] ) );
        qt.start();
    }

    public void initialize( int count ) throws Exception
    {
        this.stages = new Thread[ 2 ];

        this.queue = new DefaultQueue();

        this.start = new QueueStart( count );
        this.start.setSink( this.queue );
        this.stages[ 0 ] = new Thread( this.start );

        this.end = new QueueEnd();
        this.end.setSource( this.queue );
        this.stages[ 1 ] = new Thread( this.end );
    }

    public void start() throws Exception
    {
        System.out.println( "Starting test" );

        for( int i = 0; i < this.stages.length; i++ )
        {
            this.stages[ i ].start();
        }

        stop();
    }

    public void stop() throws Exception
    {
        for( int i = 0; i < this.stages.length; i++ )
        {
            try
            {
                this.stages[ i ].join();
            }
            catch( InterruptedException e )
            {
                throw new CascadingRuntimeException( "Stage unexpectedly interrupted", e );
            }
        }

        System.out.println( "Test complete" );

        System.out.println( "Enqueue: " + this.start.getCount() );
        System.out.println( "Dequeue: " + this.end.getCount() );
    }

    private class QueueInteger implements QueueElement
    {
        private int integer;

        public QueueInteger( int integer )
        {
            this.integer = integer;
        }

        public int getInteger()
        {
            return integer;
        }
    }

    private class QueueStart implements Runnable
    {
        private Sink sink;
        private int queueCount;
        private int count;

        public QueueStart( int queueCount )
        {
            this.queueCount = queueCount;
        }

        protected void setSink( Sink sink )
        {
            this.sink = sink;
        }

        public int getCount()
        {
            return count;
        }

        public void run()
        {
            for( int i = 0; i < this.queueCount; i++ )
            {
                try
                {
                    this.sink.enqueue( new QueueInteger( i ) );
                    this.count++;
                }
                catch( SinkException e )
                {
                    System.out.println( "Unable to queue: " + e.getMessage() );
                }
            }

            try
            {
                this.sink.enqueue( new QueueInteger( -1 ) );
            }
            catch( SinkException e )
            {
                System.out.println( "Unable to queue stop" );
            }
        }
    }

    private class QueueEnd implements Runnable
    {
        private Source source;
        private int count;

        protected void setSource( Source source )
        {
            this.source = source;
        }

        public int getCount()
        {
            return count;
        }

        public void run()
        {
            while( true )
            {
                QueueElement qe = this.source.dequeue();

                if( qe == null )
                {

                }
                else if( qe instanceof QueueInteger )
                {
                    QueueInteger qi = (QueueInteger)qe;

                    if( qi.getInteger() == -1 )
                    {
                        break;
                    }
                    else
                    {
                        this.count++;
                    }
                }
            }
        }
    }
}
