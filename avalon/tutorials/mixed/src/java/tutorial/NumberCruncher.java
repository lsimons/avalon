
package tutorial;

/**
 * A demonstration class that that we will instantiate via 
 * context directives within the component declaration.
 */
public class NumberCruncher
{
    private final int m_primary;
    private final float m_secondary;

    public NumberCruncher( Integer primary, Double secondary )
    {
        m_primary = primary.intValue();
        m_secondary = secondary.floatValue();
    }
 
   /**
    * Multiply the supplied constructor arguments together and 
    * return the value.
    */
    public float crunch()
    {
       return ( m_secondary * m_primary );
    }
}
