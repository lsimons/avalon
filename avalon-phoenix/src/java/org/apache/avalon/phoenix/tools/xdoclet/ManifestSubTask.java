/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.apache.avalon.phoenix.tools.xdoclet;

import java.io.File;
import java.net.URL;
import xdoclet.TemplateSubTask;
import xdoclet.XDocletException;
import xjavadoc.XClass;

/**
 * Generates Manifest file for Blocks
 *
 * @author <a href="mailto:vinay_chandran@users.sourceforge.net">Vinay Chandrasekharan</a>
 * @author <a href="mailto:peter@apache.org">Peter Donald</a>
 * @version $Revision: 1.7 $ $Date: 2002/07/26 09:49:23 $
 */
public class ManifestSubTask
    extends TemplateSubTask
{
    public static final String SUBTASK_NAME = "manifest";

    private static final String GENERATED_FILE_NAME = "manifest.mf";

    private static final String DEFAULT_TEMPLATE_FILE =
        "/org/apache/avalon/phoenix/tools/xdoclet/manifest.j";

    private static String c_classPattern;

    private String m_manifestFile;

    public ManifestSubTask()
    {
        final URL resource = getClass().getResource( DEFAULT_TEMPLATE_FILE );
        setTemplateURL( resource );
        setDestinationFile( GENERATED_FILE_NAME );
        setOfType( "org.apache.avalon.phoenix.Block" );

        final TemplateSubTask.ExtentTypes extent = new TemplateSubTask.ExtentTypes();
        extent.setValue( "hierarchy" );
        setExtent( extent );
    }

    public void setTemplatePath( final String templatePath )
    {
        setTemplateFile( new File( templatePath ) );
    }

    public void setManifestFile( final String manifestFile )
    {
        m_manifestFile = manifestFile;
        setDestinationFile( m_manifestFile );
    }

    public static String getClassPattern()
    {
        return c_classPattern;
    }

    public String getSubTaskName()
    {
        return SUBTASK_NAME;
    }

    public void setPattern( final String classPattern )
    {
        c_classPattern = classPattern;
    }

    /**
     * Called to validate configuration parameters.
     */
    public void validateOptions()
        throws XDocletException
    {
        super.validateOptions();

        if( null == m_manifestFile )
        {
            throw new XDocletException( "'manifestFile' attribute is missing ." );
        }

        if( null == getDestinationFile()
            || getDestinationFile().trim().equals( "" ) )
        {
            throw new XDocletException( "Error with the 'manifestFile' attribute." );
        }

        if( null == getClassPattern()
            || getClassPattern().trim().equals( "" ) )
        {
            throw new XDocletException( "'pattern' parameter missing or empty." );
        }

        if( -1 == getClassPattern().indexOf( "{0}" ) )
        {
            throw new XDocletException( "'pattern' parameter does not have a '{0}' in it. " );
        }
    }

    protected boolean matchesGenerationRules( final XClass clazz )
        throws XDocletException
    {
        if( !super.matchesGenerationRules( clazz ) )
        {
            return false;
        }
        else if( clazz.isAbstract() )
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
