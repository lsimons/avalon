/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.metagenerate;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import java.io.IOException;
import java.io.File;
import org.apache.avalon.framework.tools.ant.FormatEnum;
import org.apache.tools.ant.BuildException;

/**
 * MetaInfo Generation Ant Taskdef
 * @author Paul Hammant
 */
public class MetaGenerateTask
    extends org.apache.avalon.framework.tools.ant.MetaGenerateTask
{
    //private boolean m_inheritance = true;

    /**
     * Inheritence : should parent classes of blocks be queried too?
     * @param inheritance
     */
    public void setInheritance( boolean inheritance )
    {
        //TODO: Do inheritance based on markup rather than based on task run
        //m_inheritance = inheritance;
    }

    public void setDest( File destDir )
    {
        super.setDestDir( destDir );
    }

    public void execute()
        throws BuildException
    {
        final FormatEnum format = new FormatEnum();
        format.setValue( "legacy" );
        setFormat( format);
        super.execute();
        outputClasses();
    }

    /**
     * Output the classes
     *
     * @throws BuildException If a problem writing output
     */
    private void outputClasses()
        throws BuildException
    {
        final int size = allClasses.size();
        for( int i = 0; i < size; i++ )
        {
            final JavaClass javaClass = (JavaClass)allClasses.get( i );
            final DocletTag topic = javaClass.getTagByName( "phoenix:mx-topic" );
            if( topic != null )
            {
                final MxinfoFactory factory =
                    new MxinfoFactory( getDestDir(), javaClass );
                try
                {
                    factory.generate();
                }
                catch( final IOException ioe )
                {
                    throw new BuildException( ioe.getMessage(), ioe );
                }
            }
        }
    }
}
