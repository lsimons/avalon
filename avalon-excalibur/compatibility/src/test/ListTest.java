
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.avalon.excalibur.collections.FixedSizeBuffer;
import org.apache.avalon.excalibur.collections.VariableSizeBuffer;

public class ListTest
{
    public static void main( String[] args )
    {
        int lInitialSize = Integer.parseInt( args[ 0 ] );
        int lIterations = Integer.parseInt( args[ 1 ] );
        ArrayList lArrayList = new ArrayList( lInitialSize + 1 );
        LinkedList lLinkedList = new LinkedList();
        VariableSizeBuffer lVariableSizeBuffer = new VariableSizeBuffer( lInitialSize + 1 );
        FixedSizeBuffer lFixedSizeBuffer = new FixedSizeBuffer( lInitialSize + 1 );
        long lBegin, lEnd;

        for( int i = 0; i < lInitialSize; i++ )
        {
            lArrayList.add( new Integer( i ) );
            lLinkedList.add( new Integer( i ) );
            lVariableSizeBuffer.add( new Integer( i ) );
            lFixedSizeBuffer.add( new Integer( i ) );
        }

        lBegin = System.currentTimeMillis();
        for( int i = 0; i < lIterations; i++ )
        {
            lArrayList.add( 0, new Integer( i ) );  // Add to the head
            lArrayList.remove( lInitialSize );  // Remove from the tail
        }
        lEnd = System.currentTimeMillis();
        System.out.println( "Time: " + ( lEnd - lBegin ) );

        lBegin = System.currentTimeMillis();
        for( int i = 0; i < lIterations; i++ )
        {
            lLinkedList.addFirst( new Integer( i ) );  // Add to the head
            lLinkedList.removeLast();  // Remove from the tail
        }
        lEnd = System.currentTimeMillis();
        System.out.println( "Time: " + ( lEnd - lBegin ) );

        lBegin = System.currentTimeMillis();
        for( int i = 0; i < lIterations; i++ )
        {
            lVariableSizeBuffer.add( new Integer( i ) );  // Add to the head
            lVariableSizeBuffer.remove();  // Remove from the tail
        }
        lEnd = System.currentTimeMillis();
        System.out.println( "Time: " + ( lEnd - lBegin ) );

        lBegin = System.currentTimeMillis();
        for( int i = 0; i < lIterations; i++ )
        {
            lFixedSizeBuffer.add( new Integer( i ) );  // Add to the head
            lFixedSizeBuffer.remove();  // Remove from the tail
        }
        lEnd = System.currentTimeMillis();
        System.out.println( "Time: " + ( lEnd - lBegin ) );
    }
}
