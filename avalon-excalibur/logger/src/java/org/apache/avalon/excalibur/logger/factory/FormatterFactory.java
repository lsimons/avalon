/*
 * FormatterFactory.java
 *
 * Created on August 29, 2001, 10:43 PM
 */

package org.apache.avalon.excalibur.logger.factory;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.logger.AvalonFormatter;
import org.apache.log.format.ExtendedPatternFormatter;
import org.apache.log.format.Formatter;
import org.apache.log.format.PatternFormatter;
import org.apache.log.format.RawFormatter;
import org.apache.log.format.SyslogFormatter;
import org.apache.log.format.XMLFormatter;

/**
 * Factory for Formatter-s.
 */
public class FormatterFactory
{

    //Format of default formatter
    private static final String FORMAT =
        "%7.7{priority} %5.5{time}   [%8.8{category}] (%{context}): %{message}\\n%{throwable}";

    public Formatter createFormatter( final Configuration conf )
    {
        final String type = conf.getAttribute( "type", "pattern" );
        final String format = conf.getValue( FORMAT );

        if( "avalon".equals( type ) )
        {
            int depth = conf.getAttributeAsInteger( "depth", AvalonFormatter.DEFAULT_STACK_DEPTH );
            boolean printCascading = conf.getAttributeAsBoolean( "cascading", AvalonFormatter.DEFAULT_PRINT_CASCADING );
            return new AvalonFormatter( format, depth, printCascading );
        }

        if( "extended".equals( type ) )
        {
            return new ExtendedPatternFormatter( format );
        }

        if( "raw".equals( type ) )
        {
            return new RawFormatter();
        }

        if( "xml".equals( type ) )
        {
            return new XMLFormatter();
        }

        if( "syslog".equals( type ) )
        {
            return new SyslogFormatter();
        }

        // default formatter
        return new PatternFormatter( format );
    }

}
